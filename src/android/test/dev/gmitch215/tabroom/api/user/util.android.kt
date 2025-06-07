package dev.gmitch215.tabroom.api.user

actual fun getEnv(key: String): String? = System.getenv(key)