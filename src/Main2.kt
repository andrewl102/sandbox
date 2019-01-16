package ktorexample

import JsonLoader
import com.vladsch.kotlin.jdbc.*
import org.apache.commons.lang3.StringEscapeUtils

//data class TranslatedValue(val key: Array(Byte), val project: String, val transifex: Boolean, val translations: Map(String, Array[Byte]])
data class Mapping(val key: String, val value: String, val transifex: Boolean, val resource:String, val project: String, val language:String)
//data class MappingWithoutValue(val key: String, val transifex: Boolean, val resource:String, val project: String)

data class MappingForDisplay(
    val key: String, val value: String, val english:String, val project: String, val projectId:Long, val language:String, val url:String, val map:Map<String,String>
)
object loader {

    init {
        Class.forName("org.hsqldb.jdbcDriver")

        HikariCP.default("jdbc:hsqldb:file:/home/ec2-user/search/sandbox/hsqldb", "sa", "")
//        HikariCP.default("jdbc:hsqldb:file:/Users/alynch/git/atlassian/moreglo/my-akka-http-project/hsqldb", "sa", "")
        SessionImpl.defaultDataSource = { HikariCP.dataSource() }

    }

    /*fun compact() {
        usingDefault { session ->
            val call = sqlCall("SHUTDOWN COMPACT")
            session.execute(call)
        }
    }*/

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
        val strippedKey = if(key.startsWith("*")) key.substring(1) else key
        return usingDefault { session ->
            // working with the session
            val keyQuery = sqlQuery(
                "SELECT * FROM MAPPING where KEY = ? LIMIT 1000",
                strippedKey
            )
            val keyResults = session.list(keyQuery, toMapping)
            val resultToUse:List<MappingForDisplay> = if(keyResults.isNotEmpty()) {
                val grouped = keyResults.groupBy { it.key }
                grouped.map { e ->
                    e.value.map { l ->
                        val inEnglish = e.value.find { it.language=="en" }?.value?:""
                        toDisplay(l,inEnglish, emptyMap())
                    }
                }.flatten()
            } else {
                val valueMatch = sqlQuery(
                    "SELECT * FROM MAPPING where DATA LIKE ? LIMIT 2000",
                    likeKey
                )
                val valueResults = session.list(valueMatch,toMapping)
                val keys = valueResults.map { it.key }.distinct()
                val englishQuery = sqlQuery(
                    "SELECT * FROM MAPPING where KEY IN(:list) and LANGUAGE = 'en' LIMIT 1000"
                ).inParams("list" to keys)
                val englishResult = session.list(englishQuery,toMapping)
                val englishMap = englishResult.groupBy { it.key }
                valueResults.map { v ->
                    val inEnglish = englishMap.get(v.key).orEmpty().find { it.project == v.project }?.value?:""
                    toDisplay(v,inEnglish, emptyMap())
                }

            }
          resultToUse
        }
    }
}

//fun main(args: Array<String>): Unit {
//}