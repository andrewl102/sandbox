package ktorexample

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.html.*
import kotlinx.html.*
import freemarker.cache.*
import io.ktor.freemarker.*
import io.ktor.content.*
import io.ktor.http.content.*
import io.ktor.webjars.*
import java.time.*
import com.fasterxml.jackson.databind.*
import com.vladsch.kotlin.jdbc.param
import io.ktor.jackson.*
import io.ktor.features.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }

    install(Webjars) {
        path = "/webjars" //defaults to /webjars
        zone = ZoneId.systemDefault() //defaults to ZoneId.systemDefault()
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        get("/compact") {
            loader.compact()
            call.respondText("DONE", contentType = ContentType.Text.Plain)
        }

        get("/html-dsl") {
            call.respondHtml {
                body {
                    h1 { +"HTML" }
                    ul {
                        for (n in 1..10) {
                            li { +"$n" }
                        }
                    }
                }
            }
        }

        get("/query") {
            val query = call.parameters["query"].orEmpty()
            val doLoad = loader.doLoad(query)
            call.respond(FreeMarkerContent("index.ftl", mapOf("data" to doLoad,"query" to query), ""))
        }

        post("/query") {
            val receiveParameters = call.receiveParameters()
            val query = receiveParameters["query"].orEmpty()
            val doLoad = loader.doLoad(query)
            val shortQuery = if(query.startsWith("*")) query.substring(1) else query
            call.respond(FreeMarkerContent("index.ftl", mapOf("data" to doLoad,"query" to query,"shortQuery" to shortQuery), ""))
        }

        // Static feature. Try to access `/static/ktor_logo.svg`
        static("/static") {
            resources("static")
        }

        get("/webjars") {
            call.respondText("<script src='/webjars/jquery/jquery.js'></script>", ContentType.Text.Html)
        }

        get("/json/jackson") {
            call.respond(mapOf("hello" to "world"))
        }
    }
}

data class IndexData(val items: List<Int>)

