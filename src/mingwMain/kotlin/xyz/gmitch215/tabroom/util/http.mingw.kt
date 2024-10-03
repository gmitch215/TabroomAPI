package xyz.gmitch215.tabroom.util

import io.ktor.client.engine.*
import io.ktor.client.engine.winhttp.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

internal actual val engine: HttpClientEngine = WinHttp.create {
    pipelining = true
    protocolVersion = HttpProtocolVersion.HTTP_2_0
    securityProtocols = WinHttpSecurityProtocol.Tls11
    dispatcher = Dispatchers.IO.limitedParallelism(4)
}