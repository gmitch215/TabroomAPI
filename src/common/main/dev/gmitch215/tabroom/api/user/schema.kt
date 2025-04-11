@file:OptIn(ExperimentalJsExport::class)

package dev.gmitch215.tabroom.api.user

import dev.gmitch215.tabroom.api.Ballot
import dev.gmitch215.tabroom.api.Judge
import dev.gmitch215.tabroom.api.Tournament
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * Represents a logged-in user on Tabroom.
 */
@JsExport
data class User(
    /**
     * The user's email.
     */
    val email: String,
    /**
     * The user's first name.
     */
    val firstName: String,
    /**
     * The user's middle name.
     */
    val middleName: String?,
    /**
     * The user's last name.
     */
    val lastName: String,
    /**
     * The user's username.
     */
    val phoneNumber: String,
    /**
     * The user's pronouns.
     */
    val pronouns: String,
    /**
     * The user's time zone.
     */
    val timeZone: String,
    /**
     * The user's home address.
     */
    val address: String?,
    /**
     * The user's city.
     */
    val city: String,
    /**
     * The user's state.
     */
    val state: String,
    /**
     * The user's country.
     */
    val country: String,
    /**
     * The user's zip code.
     */
    val zipCode: Int,
    /**
     * The user's NSDA information.
     */
    val nsda: NSDAUser?
)

/**
 * Represents a user connected to a NSDA account.
 */
@JsExport
data class NSDAUser(
    /**
     * The user's full name.
     */
    val fullName: String,
    /**
     * The user's NSDA ID.
     */
    val memberId: Int,
    /**
     * The amount of merit points this user has.
     */
    val meritPoints: Int,
    /**
     * The amount of merit points this user has to earn to get to the next degree.
     */
    val pointsToNextDegree: Int,
    /**
     * The date of the last points update.
     */
    val lastPointsDate: String
)

/**
 * Represents a round in a tournament that a user is competiting in.
 */
@JsExport
data class Round(
    /**
     * The round label name.
     */
    val label: String,
    /**
     * The starting time of the round.
     */
    val start: String,
    /**
     * The room number for the location of round.
     */
    val room: String,
    /**
     * The link for document sharing.
     */
    val docShareLink: String,
    /**
     * The round data associated with this round.
     */
    val data: List<RoundData>
)

/**
 * Represents the result data for a round.
 */
@JsExport
data class RoundData(
    /**
     * A judge for the round.
     */
    val judge: Judge,
    /**
     * The judge's paradigm for the round.
     */
    val judgeParadigm: String,
    /**
     * A ballot for the round by the specified judge.
     */
    val ballot: Ballot?,
    /**
     * The full Reason For Decision (RFD) for the ballot, if available.
     */
    val rfd: String?
)

/**
 * Represents a historical tournament entry for a user.
 */
@JsExport
data class TournamentEntry(
    /**
     * The tournament this entry is for.
     */
    val tournament: Tournament,
    /**
     * The date the tournament is on.
     */
    val date: String,
    /**
     * The name for the user's entry in the tournament.
     */
    val code: String,
    /**
     * The division of the tournament.
     */
    val division: String,
    /**
     * The round this entry is in.
     */
    val rounds: List<Round>
)

/**
 * Represents a session for a user.
 */
@JsExport
data class Session(
    /**
     * The time for the session being last active.
     */
    val lastActiveTime: String?,
    /**
     * The date for the session being last active.
     */
    val lastActiveDate: String?,
    /**
     * The browser identifier for the session.
     */
    val browser: String,
    /**
     * The IP address for the session as a string.
     */
    val ip: String,
    /**
     * The internet service provider for the session, if available.
     */
    val isp: String?,
    /**
     * The location of the session, if available.
     */
    val location: String?,
) {
    /**
     * Whether the provided session is the session for the currently logged-in TabroomAPI Client.
     */
    val isCurrent: Boolean = lastActiveTime != null && lastActiveDate != null
}