package dev.gmitch215.tabroom.util

import io.ktor.client.engine.*
import io.ktor.client.engine.js.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import kotlin.js.Promise

internal actual val engine: HttpClientEngine = Js.create {
    pipelining = true
    dispatcher = Dispatchers.Default
}

actual fun encodeURL(url: String): String = js("encodeURIComponent")(url).unsafeCast<String>()
actual fun decodeURL(url: String): String = js("decodeURIComponent")(url).unsafeCast<String>()

/**
 * Login to Tabroom using the given username and password.
 * @param username The username to log in with.
 * @param password The password to log in with.
 * @return True if the login was successful, false otherwise.
 */
@JsName("login")
@JsExport
@OptIn(DelicateCoroutinesApi::class, ExperimentalJsExport::class)
fun loginAsPromise(username: String, password: String): Promise<Boolean> = GlobalScope.promise {
    login(
        username,
        password
    )
}