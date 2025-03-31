@file:OptIn(ExperimentalForeignApi::class, UnsafeNumber::class)

package dev.gmitch215.tabroom.util

import kotlinx.cinterop.*
import platform.posix.sprintf

actual fun encodeURL(url: String): String = url.encodeToByteArray().joinToString("") {
    memScoped {
        val str = allocArray<ByteVar>(4) // 3 for %XX + 1 for null terminator
        sprintf(str, "%%%02X", it)
        str.toString()
    }
}

actual fun decodeURL(url: String): String {
    val decoded = url.replace("%[0-9A-Fa-f]{2}".toRegex()) { match ->
        val hex = match.value.substring(1)
        val byte = hex.toInt(16).toByte()
        byte.toInt().toChar().toString()
    }
    return decoded
}