@file:Suppress("FunctionName")

package dev.gmitch215.tabroom.util

internal const val HOSTNAME = "www.tabroom.com"

internal class TournamentUrls(
    val tournamentId: Int,
) {

    val home: String
        get() = "https://$HOSTNAME/index/tourn/index.mhtml?tourn_id=$tournamentId"

    val entries: String
        get() = "https://$HOSTNAME/index/tourn/fields.mhtml?tourn_id=$tournamentId"

    val events: String
        get() = "https://$HOSTNAME/index/tourn/events.mhtml?tourn_id=$tournamentId"

    val judges: String
        get() = "https://$HOSTNAME/index/tourn/judges.mhtml?tourn_id=$tournamentId"
}

internal const val USER_LOGIN = "https://$HOSTNAME/user/login/login.mhtml"
internal const val USER_LOGIN_SAVE = "https://$HOSTNAME/user/login/login_save.mhtml"
internal const val USER_LOGOUT = "https://$HOSTNAME/user/login/logout.mhtml"

internal const val USER_PROFILE = "https://$HOSTNAME/user/login/profile.mhtml"
internal const val USER_HOME = "https://$HOSTNAME/user/student/index.mhtml"

internal const val TOURNAMENT_SEARCH = "https://$HOSTNAME/index/search.mhtml"

internal fun USER_RESULTS_HISTORY(tourneyId: Int, studentId: Int) = "https://$HOSTNAME/user/student/history.mhtml?tourn_id=$tourneyId&student_id=$studentId"

internal fun JUDGE_PARADIGM(judgeId: Int) = "https://$HOSTNAME/index/paradigm.mhtml?judge_person_id=$judgeId"