@file:OptIn(ExperimentalJsExport::class)

package dev.gmitch215.tabroom.util

import dev.gmitch215.tabroom.util.html.Document
import dev.gmitch215.tabroom.util.html.inputValue
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.charsets.*
import kotlinx.io.IOException
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.jvm.JvmName

internal const val PARALLEL_COUNT = 16
internal expect val engine: HttpClientEngine

internal val client
    get() = HttpClient(engine) {
        expectSuccess = false
        followRedirects = false
    }

internal val cache = mutableMapOf<String, Document>()

internal suspend fun String.fetchDocument(useToken: Boolean = true): Document {
    if (this in cache) return cache[this]!!

    val res = client.get(this) {
        headers {
            append("Host", "www.tabroom.com")
            append("User-Agent", "Ktor HTTP Client, Tabroom API v1")

            append("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            append("Accept-Language", "en-US,en;q=0.9")
            append("Connection", "keep-alive")
            append("Upgrade-Insecure-Requests", "1")
        }

        if (isLoggedIn && useToken)
            cookie("TabroomToken", token!!)
    }

    if (!res.status.isSuccess()) throw IOException("Failed to fetch document: ${res.status}\n${res.bodyAsText(Charsets.UTF_8)}")

    val text = res.bodyAsText(Charsets.UTF_8)
    cache[this] = Document(this, text)
    return Document(this, text)
}

/**
 * Closes the client. This should be called when the API is no longer needed.
 */
@JsExport
fun closeClient() = client.close()

/**
 * Clears the document cache.
 */
@JsExport
fun clearCache() = cache.clear()

/**
 * Encodes a URL string for use in a query parameter.
 * @param url The string to encode.
 * @return The encoded string.
 */
expect fun encodeURL(url: String): String

/**
 * Decodes a URL string from a query parameter.
 * @param url The string to decode.
 * @return The decoded string.
 */
expect fun decodeURL(url: String): String

/**
 * Whether the API has currently stored a Session ID.
 */
val isLoggedIn: Boolean
    get() = token != null

/**
 * The current Session ID for the API.
 */
var token: String? = null
    private set

private suspend fun getSaltAndSha(): Pair<String, String> {
    val document = USER_LOGIN.fetchDocument()
    val salt = document.inputValue("salt") ?: throw IllegalStateException("Salt not found in document")
    val sha = document.inputValue("sha") ?: throw IllegalStateException("SHA not found in document")

    return Pair(salt, sha)
}

/**
 * Logs in to Tabroom with the given username and password.
 *
 * This function performs a login request to Tabroom and stores the authentication token in the `token` variable.
 * Tokens represent the Session ID and are valid for 1,024 hours, or roughly 42 days.
 *
 * If the API is already logged in, it will log out and then log in again.
 * @param username The username to log in with.
 * @param password The password to log in with.
 * @return True if the login was successful, false otherwise.
 * @throws IOException If the login request fails.
 */
@JsName("loginAsync")
@JvmName("loginAsync")
suspend fun login(username: String, password: String): Boolean {
    if (isLoggedIn) logout()
    val (salt, sha) = getSaltAndSha()

    val res = client.submitForm(
        USER_LOGIN_SAVE,
        parameters {
            append("tourn_id", "")
            append("key", "")
            append("salt", salt)
            append("sha", sha)
            append("category_id", "")
            append("return", "")
            append("username", username)
            append("password", password)
        }
    ) {
        headers {
            append("User-Agent", "Ktor HTTP Client, Tabroom API v1")
            append("Content-Type", "application/x-www-form-urlencoded")
            append("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
        }
    }

    if (res.status.value != 302) throw IOException("Unexpected status code: ${res.status}")

    val cookie = res.setCookie()
    if (cookie.isEmpty()) return false

    val tabToken = cookie.firstOrNull { it.name == "TabroomToken" }?.value
    if (tabToken == null) return false
    if (tabToken.isEmpty()) throw IllegalStateException("TabroomToken is empty")

    token = decodeURL(tabToken)
    return true
}

/**
 * Logs out of Tabroom, invalidating the stored token. If there is no token, this function does nothing.
 */
suspend fun logout() {
    if (!isLoggedIn) return

    val tabToken = token ?: return
    token = null

    client.get(USER_LOGOUT) {
        headers {
            append("User-Agent", "Ktor HTTP Client, Tabroom API v1")
            append("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            cookie("TabroomToken", tabToken)
        }
    }
}