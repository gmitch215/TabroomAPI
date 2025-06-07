package dev.gmitch215.tabroom.util

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.curl.Curl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

internal actual val engine: HttpClientEngine = Curl.create {
    pipelining = true
    dispatcher = Dispatchers.IO.limitedParallelism(PARALLEL_COUNT)
}