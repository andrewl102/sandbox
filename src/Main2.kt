package ktorexample

import com.vladsch.kotlin.jdbc.*

//data class TranslatedValue(val key: Array(Byte), val project: String, val transifex: Boolean, val translations: Map(String, Array[Byte]])
data class Mapping(val key: String, val value: String, val transifex: Boolean, val resource:String, val project: String, val language:String)
data class MappingWithoutValue(val key: String, val transifex: Boolean, val resource:String, val project: String)

data class MappingForDisplay(
    val key: String, val value: String, val english:String, val project: String, val projectId:Long, val language:String, val url:String, val map:Map<String,String>
)
object loader {
    init {
//        HikariCP.default("jdbc:h2:/Users/alynch/git/atlassian/moreglo/my-akka-http-project/db", "sa", "")
        HikariCP.default("jdbc:h2:/home/ec2-user/search/sandbox/db", "sa", "")
//        HikariCP.default("jdbc:hsqldb:file:/Users/alynch/git/atlassian/moreglo/my-akka-http-project/hsqldb", "sa", "")
        SessionImpl.defaultDataSource = { HikariCP.dataSource() }

    }

    fun compact() {
        usingDefault { session ->
            val call = sqlCall("SHUTDOWN COMPACT")
            session.execute(call)
        }
    }

    private fun toDisplay(m:Mapping, toMap:Map<String,String>):MappingForDisplay {
        val f = m.copy(project = "jira-cloud-back-end",resource = "jira_cloud_i18nproperties")
        return MappingForDisplay(f.key,f.value,"",f.project,122528087,"en","https://www.transifex.com/atlassian/${f.project}/viewstrings/#${f.language}/${f.resource}/122528087?q=key%3A${f.key}",toMap)
    }

    fun doLoad(key:String):List<MappingForDisplay>  {
        if(key.isBlank()) {
            return emptyList()
        }
        val toMapping: (Row) -> Mapping = { rs ->
            Mapping(
                rs.string("key"), rs.string("data"), rs.boolean("transifex"), rs.string("project"), rs.string("resource"),rs.string("language")
            )
        }

        val likeKey = if(key.startsWith("*")) "%${key.substring(1)}%" else "$key%"
        return usingDefault { session ->
            // working with the session
            val query = sqlQuery(
                "SELECT * FROM MAPPING where KEY = ? OR DATA LIKE ? LIMIT 2000",
                key,
                likeKey
            )
            query.queryDetails
            val results = session.list(query, toMapping)
            val (valueMatches, keyMatches) = results.partition { it.value == key }
            val (englishKey, translations) = keyMatches.partition { it.language == "en" }
            val distinct = valueMatches.map { mapping -> MappingWithoutValue(mapping.key,mapping.transifex,mapping.resource,mapping.project) }.distinct()


            val result = results.map{ m ->  toDisplay(m, emptyMap())}

            /*val result:List<MappingForDisplay> = if(englishKey.isNotEmpty()) {
                val toMap = translations.map { it.language to it.value }.toMap()

            } else if(valueMatches.isNotEmpty() && distinct.isNotEmpty()){
                *//*val inOthers = distinct.map { m ->
                    val query2 = sqlQuery(
                        "SELECT * FROM MAPPING where KEY = ? AND PROJECT = ? AND RESOURCE = ?",
                        m.key, m.project, m.resource
                    )
                    val list = session.list(query2, toMapping)
                    list
                }

                val toMap = inOthers.flatten().map { it.language to it.value}.toMap()
                inOthers.flatten().map { m ->
                    toDisplay(m, emptyMap())
                }*//*
                val toMap = emptyMap<String,String>()
                valueMatches.map { toDisplay(it,toMap) }
            } else {
                emptyList()
            }*/

            result
        }
    }
}

//fun main(args: Array<String>): Unit {
//}