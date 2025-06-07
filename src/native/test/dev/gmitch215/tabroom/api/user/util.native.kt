package dev.gmitch215.tabroom.api.user

import kotlinx.cinterop.*
import platform.posix.getenv

@OptIn(ExperimentalForeignApi::class)
actual fun getEnv(key: String): String? {
    val ptr = getenv(key) ?: return null
    val builder = StringBuilder()
    var i = 0
    while (ptr[i] != 0.toByte()) {
        builder.append(ptr[i].toInt().toChar())
        i++
    }

    return builder.toString()
}