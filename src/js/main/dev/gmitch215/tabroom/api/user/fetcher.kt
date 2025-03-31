@file:OptIn(DelicateCoroutinesApi::class, ExperimentalJsExport::class)

package dev.gmitch215.tabroom.api.user

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import kotlin.js.Promise

/**
 * Fetches the current user from Tabroom.
 * @throws IllegalStateException if not logged in
 */
@JsName("getCurrentUser")
@JsExport
fun getCurrentUserAsPromise(): Promise<User> = GlobalScope.promise { getCurrentUser() }

/**
 * Gets the judge paradigm for a judge by their ID.
 * @param judgeId The ID of the judge.
 * @return The judge paradigm.
 * @throws IllegalStateException if not logged in
 */
@JsName("getJudgeParadigm")
fun getJudgeParadigmAsPromise(judgeId: Int): Promise<String> = GlobalScope.promise { getJudgeParadigm(judgeId) }

/**
 * Gets the round results for a student in a tournament.
 * @param tourneyId The ID of the tournament.
 * @param studentId The ID of the student.
 * @return The round results.
 * @throws IllegalStateException if not logged in
 */
@JsName("getRoundResults")
fun getRoundResultsAsPromise(tourneyId: Int, studentId: Int): Promise<List<Round>> = GlobalScope.promise { getRoundResults(tourneyId, studentId) }

/**
 * Gets the entry history for the current user.
 * @throws IllegalStateException if not logged in
 */
@JsName("getEntryHistory")
fun getEntryHistoryAsPromise(): Promise<List<TournamentEntry>> = GlobalScope.promise { getEntryHistory() }