package xyz.gmitch215.tabroom.util

import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

internal actual val engine: HttpClientEngine = CIO.create {
    pipelining = true
    dispatcher = Dispatchers.IO.limitedParallelism(4)
}