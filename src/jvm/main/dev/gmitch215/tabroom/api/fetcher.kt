@file:JvmName("TabroomAPI")

package dev.gmitch215.tabroom.api

import dev.gmitch215.tabroom.api.user.User
import dev.gmitch215.tabroom.api.user.getCurrentUser
import kotlinx.coroutines.runBlocking

/**
 * Gets a tournament by its ID.
 * @param id The ID of the tournament.
 * @return The tournament.
 */
@JvmName("getTournament")
fun getTournamentSync(id: Int): Tournament = runBlocking { getTournament(id) }

/**
 * Fetches the current user from Tabroom.
 * @throws IllegalStateException if not logged in
 */
@JvmName("getCurrentUser")
fun getCurrentUserSync(): User = runBlocking { getCurrentUser() }