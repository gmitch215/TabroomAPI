package xyz.gmitch215.tabroom.util

import io.ktor.client.engine.*
import io.ktor.client.engine.apache5.*
import kotlinx.coroutines.Dispatchers

internal actual val engine: HttpClientEngine = Apache5.create {
    pipelining = true
    followRedirects = true
    dispatcher = Dispatchers.IO.limitedParallelism(4)
}