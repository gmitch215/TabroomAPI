@file:JvmName("TabroomAPI")

package dev.gmitch215.tabroom.api

import kotlinx.coroutines.runBlocking

/**
 * Gets a tournament by its ID.
 * @param id The ID of the tournament.
 * @return The tournament.
 */
@JvmName("getTournament")
fun getTournamentSync(id: Int): Tournament = runBlocking { getTournament(id) }

/**
 * Searches for tournaments by name.
 * @param query The query to search for.
 * @return A list of tournaments that match the query.
 */
@JvmName("searchTournaments")
fun searchTournamentsSync(query: String): List<Tournament> = runBlocking { searchTounaments(query) }