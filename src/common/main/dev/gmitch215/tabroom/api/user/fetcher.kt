package dev.gmitch215.tabroom.api.user

import dev.gmitch215.tabroom.util.USER_PROFILE
import dev.gmitch215.tabroom.util.fetchDocument
import dev.gmitch215.tabroom.util.html.inputValue
import dev.gmitch215.tabroom.util.html.querySelector
import dev.gmitch215.tabroom.util.html.querySelectorAll
import dev.gmitch215.tabroom.util.isLoggedIn
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.js.JsName
import kotlin.jvm.JvmName

private const val USER_TIME_ZONE = "select[name=\"timezone\"] option[selected]"
private const val USER_STATE = "select[name=\"state\"] option[selected]"
private const val USER_COUNTRY = "select[name=\"country\"] option[selected]"
private const val NSDA_BOX_INFO = "div.odd.smallish > span.half"

/**
 * Fetches the current user from Tabroom.
 * @throws IllegalStateException if not logged in
 */
@JvmName("getCurrentUserAsync")
@JsName("getCurrentUserAsync")
suspend fun getCurrentUser(): User = coroutineScope {
    if (!isLoggedIn) throw IllegalStateException("User is not logged in")

    val doc = USER_PROFILE.fetchDocument()

    val nsda = async {
        val nsdaInfo = doc.querySelectorAll(NSDA_BOX_INFO)
        if (nsdaInfo.isEmpty()) return@async null
        if (nsdaInfo.size < 10) throw IllegalStateException("NSDA info is incomplete: expected 10 elements, got ${nsdaInfo.size}")

        return@async NSDAUser(
            nsdaInfo[1].textContent.trim(),
            nsdaInfo[3].textContent.run { substring(1) }.toIntOrNull() ?: 0,
            nsdaInfo[5].textContent.toIntOrNull() ?: 0,
            nsdaInfo[7].textContent.toIntOrNull() ?: 0,
            nsdaInfo[9].textContent.trim(),
        )
    }

    val email = async { doc.inputValue("email") ?: "" }
    val first = async { doc.inputValue("first") ?: "" }
    val middle = async { doc.inputValue("middle") ?: "" }
    val last = async { doc.inputValue("last") ?: "" }
    val phone = async { doc.inputValue("phone") ?: "(000) 000-0000" }
    val pronouns = async { doc.inputValue("pronoun") ?: "He/Him" }
    val timeZone = async { doc.querySelector(USER_TIME_ZONE)?.textContent?.trim() ?: "Unknown" }
    val address = async { doc.inputValue("street") }
    val city = async { doc.inputValue("city") ?: "" }
    val state = async { doc.querySelector(USER_STATE)?.textContent?.trim() ?: "Unknown" }
    val country = async { doc.querySelector(USER_COUNTRY)?.textContent?.trim() ?: "Unknown" }
    val zip = async { doc.inputValue("zip")?.toIntOrNull() ?: 0 }

    return@coroutineScope User(
        email.await(),
        first.await(),
        middle.await(),
        last.await(),
        phone.await(),
        pronouns.await(),
        timeZone.await(),
        address.await(),
        city.await(),
        state.await(),
        country.await(),
        zip.await(),
        nsda.await()
    )
}