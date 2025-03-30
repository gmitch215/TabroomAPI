package dev.gmitch215.tabroom.util

import kotlinx.coroutines.test.runTest
import org.junit.AfterClass
import org.junit.BeforeClass

class TestLogin {

    companion object {
        var loggedIn = false

        @BeforeClass
        @JvmStatic
        fun login() = runTest {
            val username = System.getenv("TABROOM_USERNAME")
            val password = System.getenv("TABROOM_PASSWORD")

            if (username == null || password == null) {
                println("Warning: TABROOM_USERNAME and TABROOM_PASSWORD must be set in the environment.")
                return@runTest
            }

            loggedIn = login(username, password)
        }

        @AfterClass
        @JvmStatic
        fun closeClient() {
            client.close()
        }
    }

}