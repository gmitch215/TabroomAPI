package xyz.gmitch215.tabroom.util

import io.ktor.client.*
import io.ktor.client.engine.*

internal expect val engine: HttpClientEngine

internal val client
    get() = HttpClient(engine) {
        expectSuccess = false
    }