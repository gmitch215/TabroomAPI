package dev.gmitch215.tabroom.util

import dev.gmitch215.tabroom.api.user.getCurrentUser
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals

class TestLogin {

    @Test
    fun testLogin() = runTest { try {
        val username = System.getenv("TABROOM_USERNAME")
        val password = System.getenv("TABROOM_PASSWORD")

        if (username == null || password == null) {
            println("Warning: TABROOM_USERNAME and TABROOM_PASSWORD must be set in the environment.")
            return@runTest
        }

        login(username, password)

        val user = getCurrentUser()
        assertFalse { user.email.isEmpty() }
        assertFalse { user.firstName.isEmpty() }
        assertFalse { user.lastName.isEmpty() }
        assertNotEquals("(000) 000-0000", user.phoneNumber)
        assertNotEquals("Unknown", user.timeZone)
        assertNotEquals("Unknown", user.state)
        assertNotEquals("Unknown", user.country)
        assertNotEquals(0, user.zipCode)
    } finally { logout() }}

}