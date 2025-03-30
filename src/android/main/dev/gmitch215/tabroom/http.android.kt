package dev.gmitch215.tabroom

import android.net.Uri
import dev.gmitch215.tabroom.util.PARALLEL_COUNT
import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*
import kotlinx.coroutines.Dispatchers
import java.util.concurrent.TimeUnit

internal actual val engine: HttpClientEngine = OkHttp.create {
    pipelining = true
    dispatcher = Dispatchers.IO.limitedParallelism(PARALLEL_COUNT)

    config {
        followRedirects(true)
        connectTimeout(30, TimeUnit.SECONDS)
        readTimeout(30, TimeUnit.SECONDS)
        writeTimeout(30, TimeUnit.SECONDS)
    }
}

actual fun encodeURL(url: String): String = Uri.encode(url)
actual fun decodeURL(url: String): String = Uri.decode(url)