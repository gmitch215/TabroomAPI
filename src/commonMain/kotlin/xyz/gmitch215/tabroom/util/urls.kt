package xyz.gmitch215.tabroom.util

internal class TabroomUrls(
    val tournamentId: Int,
) {

    val home: String
        get() = "https://www.tabroom.com/index/tourn/index.mhtml?tourn_id=$tournamentId"

    val entries: String
        get() = "https://www.tabroom.com/index/tourn/fields.mhtml?tourn_id=$tournamentId"

}