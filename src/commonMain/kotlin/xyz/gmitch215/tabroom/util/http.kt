package xyz.gmitch215.tabroom.util

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.charsets.*
import kotlinx.io.IOException
import xyz.gmitch215.tabroom.util.html.Document

internal const val PARALLEL_COUNT = 16
internal expect val engine: HttpClientEngine

internal val client
    get() = HttpClient(engine) {
        expectSuccess = false
    }

internal suspend fun String.fetchDocument(): Document {
    val res = client.get(this) {
        headers {
            append("User-Agent", "Ktor HTTP Client, Tabroom API v1")
        }
    }

    if (!res.status.isSuccess()) {
        throw IOException("Failed to fetch document: ${res.status}")
    }

    val text = res.bodyAsText(Charsets.UTF_8)
    return Document(this, text)
}

/**
 * Closes the client. This should be called when the API is no longer needed.
 */
fun closeClient() = client.close()