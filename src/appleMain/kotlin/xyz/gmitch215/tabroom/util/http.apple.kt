package xyz.gmitch215.tabroom.util

import io.ktor.client.engine.*
import io.ktor.client.engine.darwin.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

internal actual val engine: HttpClientEngine = Darwin.create {
    pipelining = true
    dispatcher = Dispatchers.IO.limitedParallelism(PARALLEL_COUNT)

    configureRequest {
        setAllowsCellularAccess(true)
    }

    configureSession {
        setAllowsCellularAccess(true)
        TLSMinimumSupportedProtocol = platform.Security.tls_protocol_version_TLSv10.toInt()
    }
}