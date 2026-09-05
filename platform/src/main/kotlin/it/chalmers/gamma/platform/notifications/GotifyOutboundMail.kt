package it.chalmers.gamma.platform.notifications

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class GotifyOutboundMail(
    private val baseUrl: String,
    private val apiKey: String,
    private val from: String = "no-reply@chalmers.it",
    private val client: HttpClient = HttpClient.newHttpClient(),
) : OutboundMail {
    override fun send(message: MailMessage) {
        val request =
            HttpRequest
                .newBuilder(URI.create("${baseUrl.trimEnd('/')}/mail"))
                .header("Authorization", "pre-shared: $apiKey")
                .header("Content-Type", "application/json")
                .POST(
                    HttpRequest.BodyPublishers.ofString(
                        Json.encodeToString(GotifyMailRequest(message.to, from, message.subject, message.body)),
                    ),
                ).build()
        try {
            val response = client.send(request, HttpResponse.BodyHandlers.discarding())
            check(response.statusCode() in 200..299) {
                "Gotify mail delivery returned HTTP ${response.statusCode()}"
            }
        } catch (cause: IOException) {
            throw cause
        } catch (cause: InterruptedException) {
            Thread.currentThread().interrupt()
            throw IOException("Gotify mail delivery was interrupted", cause)
        }
    }
}

@Serializable
private data class GotifyMailRequest(
    val to: String,
    val from: String,
    val subject: String,
    val body: String,
) {
    override fun toString(): String = "GotifyMailRequest(<redacted>)"
}
