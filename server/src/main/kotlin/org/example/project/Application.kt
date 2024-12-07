package com.example.project

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.routing.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.engine.cio.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import io.ktor.http.HttpStatusCode


fun main() {
    embeddedServer(Netty, port = 8080) {
        module()
    }.start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    install(Authentication) {
        oauth("soundcloud") {
            urlProvider = { "http://localhost:8080/callback" }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "soundcloud",
                    authorizeUrl = "https://soundcloud.com/connect",
                    accessTokenUrl = "https://api.soundcloud.com/oauth2/token",
                    clientId = "z1zS0nvwlaPi0dO4ySOWO1UsOaZn9j7I", // Replace with your SoundCloud client ID
                    clientSecret = "1zjaXB8a1bIy7wRkEnHWCLpSTD6ImjvX", // Replace with your SoundCloud client secret
                    defaultScopes = listOf("non-expiring")
                )
            }
            client = HttpClient(CIO) // Use the CIO client engine
        }
    }


    routing {
        authenticate("soundcloud") {
            get("/login") {
                call.respondRedirect("/callback")
            }


                get("/callback") {
                    val principal = call.principal<OAuthAccessTokenResponse.OAuth2>()
                    if (principal?.accessToken != null) {
                        call.respondText("Access Token: ${principal.accessToken}")
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "No access token provided")
                    }
                }
            }


        get("/user/profile") {
            val token = call.request.queryParameters["token"]
            if (token.isNullOrEmpty()) {
                call.respond(HttpStatusCode.BadRequest, "Missing or invalid token")
                return@get
            }
            val profile = fetchSoundCloudProfile(token)
            call.respond(profile)
        }
    }
}

@Serializable
data class UserProfile(val id: String, val username: String)

suspend fun fetchSoundCloudProfile(token: String): UserProfile {
    val client = HttpClient(CIO)
    val response: HttpResponse = client.get("https://api.soundcloud.com/me") {
        header("Authorization", "OAuth $token")
    }
    val json = Json.parseToJsonElement(response.bodyAsText()).jsonObject
    return UserProfile(
        id = json["id"]!!.jsonPrimitive.content,
        username = json["username"]!!.jsonPrimitive.content
    )
}
