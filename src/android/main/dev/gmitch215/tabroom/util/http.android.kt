package dev.gmitch215.tabroom.util

import android.net.Uri
import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*
import kotlinx.coroutines.Dispatchers
import java.util.concurrent.TimeUnit

internal actual val engine: HttpClientEngine = OkHttp.create {
    pipelining = true
    dispatcher = Dispatchers.IO.limitedParallelism(PARALLEL_COUNT)

    config {
        connectTimeout(30, TimeUnit.SECONDS)
        readTimeout(30, TimeUnit.SECONDS)
        writeTimeout(30, TimeUnit.SECONDS)
    }
}