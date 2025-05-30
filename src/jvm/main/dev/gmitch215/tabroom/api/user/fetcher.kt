@file:JvmName("TabroomUser")

package dev.gmitch215.tabroom.api.user

import kotlinx.coroutines.runBlocking

/**
 * Fetches the current user from Tabroom.
 * @throws IllegalStateException if not logged in
 */
@JvmName("getCurrentUser")
fun getCurrentUserSync(): User = runBlocking { getCurrentUser() }

/**
 * Gets the judge paradigm for a judge by their ID.
 * @param judgeId The ID of the judge.
 * @return The judge paradigm.
 * @throws IllegalStateException if not logged in
 */
@JvmName("getJudgeParadigm")
fun getJudgeParadigmSync(judgeId: Int): String = runBlocking { getJudgeParadigm(judgeId) }

/**
 * Gets the round results for a student in a tournament.
 * @param tourneyId The ID of the tournament.
 * @param studentId The ID of the student.
 * @return The round results.
 * @throws IllegalStateException if not logged in
 */
@JvmName("getRoundResults")
fun getRoundResultsSync(tourneyId: Int, studentId: Int): List<Round> = runBlocking { getRoundResults(tourneyId, studentId) }

/**
 * Gets the entry history for the current user.
 * @throws IllegalStateException if not logged in
 */
@JvmName("getEntryHistory")
fun getEntryHistorySync(): List<TournamentEntry> = runBlocking { getEntryHistory() }