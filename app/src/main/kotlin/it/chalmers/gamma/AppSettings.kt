package it.chalmers.gamma

import org.springframework.boot.context.properties.ConfigurationProperties
import java.nio.file.Path

@ConfigurationProperties("application")
data class AppSettings(
    val baseUrl: String = "http://localhost:8081",
    val production: Boolean = true,
    val mocking: Boolean = false,
    val mockDataResource: String = "classpath:/mock/mock.json",
    val adminSetup: Boolean = true,
    val files: ApplicationFileSettings = ApplicationFileSettings(),
    val gotify: GotifySettings = GotifySettings(),
) {
    val publicBaseUrl: String
        get() = baseUrl
}

data class ApplicationFileSettings(
    val path: Path = Path.of("./uploads/"),
)

data class GotifySettings(
    val baseUrl: String = "",
    val apiKey: String = "",
    val from: String = "no-reply@chalmers.it",
)
