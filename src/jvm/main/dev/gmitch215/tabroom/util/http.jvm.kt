package dev.gmitch215.tabroom.util

import io.ktor.client.engine.*
import io.ktor.client.engine.java.Java
import kotlinx.coroutines.Dispatchers
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

internal actual val engine: HttpClientEngine = Java.create {
    pipelining = true
    dispatcher = Dispatchers.IO.limitedParallelism(PARALLEL_COUNT)
}