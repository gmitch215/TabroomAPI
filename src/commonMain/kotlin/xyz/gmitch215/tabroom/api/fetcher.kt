package xyz.gmitch215.tabroom.api

import xyz.gmitch215.tabroom.util.TabroomUrls
import xyz.gmitch215.tabroom.util.fetchDocument
import xyz.gmitch215.tabroom.util.getEvents
import xyz.gmitch215.tabroom.util.getTournament

/**
 * Gets a tournament by its ID.
 * @param id The ID of the tournament.
 * @return The tournament.
 */
suspend fun getTournament(id: Int): Tournament {
    val urls = TabroomUrls(id)

    val home = urls.home.fetchDocument()
    val tourney = getTournament(home)

    val entries = urls.entries.fetchDocument()
    val events = getEvents(entries)
    (tourney.events as MutableList).addAll(events)

    return tourney
}