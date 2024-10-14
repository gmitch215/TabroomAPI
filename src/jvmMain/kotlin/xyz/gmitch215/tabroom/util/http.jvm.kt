package xyz.gmitch215.tabroom.util

import io.ktor.client.engine.*
import io.ktor.client.engine.jetty.Jetty
import kotlinx.coroutines.Dispatchers

internal actual val engine: HttpClientEngine = Jetty.create {
    pipelining = true
    dispatcher = Dispatchers.IO.limitedParallelism(PARALLEL_COUNT)
}