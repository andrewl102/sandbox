package ktorexample

import com.vladsch.kotlin.jdbc.*
import org.apache.commons.lang3.StringEscapeUtils

//data class TranslatedValue(val key: Array(Byte), val project: String, val transifex: Boolean, val translations: Map(String, Array[Byte]])
data class Mapping(val key: String, val value: String, val transifex: Boolean, val resource:String, val project: String, val language:String)
data class MappingWithoutValue(val key: String, val transifex: Boolean, val resource:String, val project: String)

data class MappingForDisplay(
    val key: String, val value: String, val english:String, val project: String, val projectId:Long, val language:String, val url:String, val map:Map<String,String>
)
object loader {

    init {
        Class.forName("org.hsqldb.jdbcDriver")

//        HikariCP.default("jdbc:h2:/Users/alynch/git/atlassian/moreglo/my-akka-http-project/hsqldb4", "sa", "")
//        HikariCP.default("jdbc:hsqldb:file:/Users/alynch/git/atlassian/moreglo/my-akka-http-project/hsqldb4", "sa", "")
        HikariCP.default("jdbc:hsqldb:file:/home/ec2-user/search/sandbox/hsqldb", "sa", "")
//        HikariCP.default("jdbc:h2:/home/ec2-user/search/sandbox/db", "sa", "")
//        HikariCP.default("jdbc:hsqldb:file:/Users/alynch/git/atlassian/moreglo/my-akka-http-project/hsqldb", "sa", "")
        SessionImpl.defaultDataSource = { HikariCP.dataSource() }

    }

    fun compact() {
        usingDefault { session ->
            val call = sqlCall("SHUTDOWN COMPACT")
            session.execute(call)
        }
    }

    private fun toDisplay(m:Mapping, english: String, toMap:Map<String,String>):MappingForDisplay {
//        val f = m.copy(project = "jira-cloud-back-end",resource = "jira_cloud_i18nproperties")
        val projectId = JsonLoader.loaded.getOrDefault(m.project + "_" + m.resource, "0")
        val english1 = if(m.language == "en") "N/A" else StringEscapeUtils.escapeHtml4(english)
        return MappingForDisplay(m.key,
            StringEscapeUtils.escapeHtml4(m.value),
            english1,m.project,
            projectId.toLong(),m.language,"https://www.transifex.com/atlassian/${m.project}/viewstrings/#${m.language}/${m.resource}/$projectId?q=key%3A${m.key}",toMap)
    }

    fun doLoad(key:String):List<MappingForDisplay>  {
        if(key.isBlank()) {
            return emptyList()
        }
        val toMapping: (Row) -> Mapping = { rs ->
            Mapping(
                rs.string("key"), rs.string("data"), rs.boolean("transifex"), rs.string("resource"), rs.string("project"),rs.string("language")
            )
        }

        val likeKey = if(key.startsWith("*")) "%${key.substring(1)}%" else "$key%"
        return usingDefault { session ->
            // working with the session
            val query = sqlQuery(
                "SELECT DISTINCT(KEY) FROM MAPPING where KEY = ? OR DATA LIKE ? LIMIT 2000",
                key,
                likeKey
            )
            query.queryDetails
            val results = session.list(query) { row -> row.string("KEY") }
//            val results = session.list(query, toMapping)

            val queryAll = sqlQuery(
                "SELECT DISTINCT(*) FROM MAPPING where KEY IN (:list) LIMIT 20000"
            ).inParams("list" to results)
            val withAll = if(results.isNotEmpty()) session.list(queryAll, toMapping) else emptyList()

            val grouped = withAll.groupBy { it.key }

            val mapped =grouped.map {all ->
                val english = all.value.find { it.language == "en" }
                /*val langMapped = all.value.groupBy { it.language }.mapValues { it.value.first().value }
                val shortened = if(key.startsWith("*")) key.substring(1) else key
                val first = all.value.find { it.value.contains(shortened) }
                val orDefault = first ?: all.value.first()
                val r = toDisplay(orDefault,english?.value ?: "", langMapped)
          r*/
                all.value.map { toDisplay(it,english?.value ?: "", emptyMap()) }
            }
            mapped.flatten()


//            val (valueMatches, keyMatches) = results.partition { it.value == key }
//            val (englishKey, translations) = keyMatches.partition { it.language == "en" }

            /*val result = results.map{ m ->
                val english = if(englishKey.isNotEmpty()) englishKey.get(0).value else {
                    val query2 = sqlQuery("SELECT DATA FROM MAPPING WHERE KEY = ? AND LANGUAGE = 'en'", m.key)
                    session.first(query2) { row -> row.string("DATA") }.orEmpty()
                }
                toDisplay(m, english, emptyMap())
            }*/

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


        }
    }
}

//fun main(args: Array<String>): Unit {
//}