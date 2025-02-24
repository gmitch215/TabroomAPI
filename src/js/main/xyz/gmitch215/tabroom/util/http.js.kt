package xyz.gmitch215.tabroom.util

import io.ktor.client.engine.*
import io.ktor.client.engine.js.*
import kotlinx.coroutines.Dispatchers

internal actual val engine: HttpClientEngine = Js.create {
    pipelining = true
    dispatcher = Dispatchers.Default
}