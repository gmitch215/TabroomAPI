package dev.gmitch215.tabroom.util

import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class TestHttp {

    @Test
    fun testGet() = runTest {
        val res = client.get("https://httpbin.org/get") {
            headers {
                append("User-Agent", USER_AGENT)
            }
        }

        assertTrue { res.status.isSuccess() }
    }

    @Test
    fun testPost() = runTest {
        val res = client.post("https://httpbin.org/post") {
            headers {
                append("User-Agent", USER_AGENT)
            }
        }

        assertTrue { res.status.isSuccess() }
    }

}