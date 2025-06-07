package dev.gmitch215.tabroom.util


import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import platform.Foundation.*
import platform.Security.*

@OptIn(ExperimentalForeignApi::class, UnsafeNumber::class)
internal actual val engine: HttpClientEngine = Darwin.create {
    pipelining = true
    dispatcher = Dispatchers.IO.limitedParallelism(PARALLEL_COUNT)

    configureRequest {
        setAllowsCellularAccess(true)
    }

    configureSession {
        setAllowsCellularAccess(true)
    }

    handleChallenge { session, task, challenge, completionHandler ->
        val serverTrust = challenge.protectionSpace.serverTrust ?: run {
            completionHandler(NSURLSessionAuthChallengeCancelAuthenticationChallenge, null)
            return@handleChallenge
        }

        memScoped {
            val resultVar = alloc<SecTrustResultTypeVar>()
            SecTrustEvaluate(serverTrust, resultVar.ptr)
        }

        val credential = NSURLCredential.credentialForTrust(serverTrust)
        completionHandler(NSURLSessionAuthChallengeUseCredential, credential)
    }
}