package dev.gmitch215.tabroom.util

internal class TournamentUrls(
    val tournamentId: Int,
) {

    val home: String
        get() = "https://www.tabroom.com/index/tourn/index.mhtml?tourn_id=$tournamentId"

    val entries: String
        get() = "https://www.tabroom.com/index/tourn/fields.mhtml?tourn_id=$tournamentId"

    val events: String
        get() = "https://www.tabroom.com/index/tourn/events.mhtml?tourn_id=$tournamentId"

    val judges: String
        get() = "https://www.tabroom.com/index/tourn/judges.mhtml?tourn_id=$tournamentId"
}

const val USER_LOGIN = "https://www.tabroom.com/user/login/login.mhtml"
const val USER_LOGIN_SAVE = "https://www.tabroom.com/user/login/login_save.mhtml"
const val USER_LOGOUT = "https://www.tabroom.com/user/login/logout.mhtml"

const val USER_PROFILE = "https://www.tabroom.com/user/login/profile.mhtml"