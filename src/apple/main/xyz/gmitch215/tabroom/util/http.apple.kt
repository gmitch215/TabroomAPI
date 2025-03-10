package xyz.gmitch215.tabroom.util

import io.ktor.client.engine.*
import io.ktor.client.engine.darwin.*
import kotlinx.coroutines.Dispatchers

internal actual val engine: HttpClientEngine = Darwin.create {
    pipelining = true
    dispatcher = Dispatchers.Default

    configureRequest {
        setAllowsCellularAccess(true)
    }

    configureSession {
        setAllowsCellularAccess(true)
    }
}