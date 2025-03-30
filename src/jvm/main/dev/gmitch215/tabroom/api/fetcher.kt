@file:JvmName("TabroomAPI")

package dev.gmitch215.tabroom.api

import kotlinx.coroutines.runBlocking

/**
 * Gets a tournament by its ID.
 * @param id The ID of the tournament.
 * @return The tournament.
 */
fun getTournamentSync(id: Int): Tournament = runBlocking { getTournament(id) }