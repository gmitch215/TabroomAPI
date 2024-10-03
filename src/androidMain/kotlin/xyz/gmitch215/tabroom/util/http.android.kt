package xyz.gmitch215.tabroom.util

import io.ktor.client.engine.*
import io.ktor.client.engine.android.*
import kotlinx.coroutines.Dispatchers

internal actual val engine: HttpClientEngine = Android.create {
    pipelining = true
    dispatcher = Dispatchers.IO.limitedParallelism(4)
}