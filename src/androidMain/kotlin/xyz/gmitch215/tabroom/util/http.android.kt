package xyz.gmitch215.tabroom.util

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.Android
import kotlinx.coroutines.Dispatchers

internal actual val engine: HttpClientEngine = Android.create {
    pipelining = true
    dispatcher = Dispatchers.IO.limitedParallelism(PARALLEL_COUNT)
    connectTimeout = 0
}