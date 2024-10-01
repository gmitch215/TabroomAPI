package xyz.gmitch215.tabroom.api

/**
 * Represents a tournament.
 */
data class Tournament(
    /**
     * The name of the tournament.
     */
    val name: String,
    /**
     * The description of the tournament in HTML format.
     */
    val descriptionHTML: String,
    /**
     * The raw description of the tournament.
     */
    val description: String,
    /**
     * The year the tournament takes place in.
     */
    val year: String
)

/**
 * Represents an event in a tournament.
 */
data class Event(
    /**
     * The name of the event.
     */
    val name: String,
    /**
     * The entries in the event.
     */
    val entries: List<Entry> = emptyList()
)

/**
 * Represents an entry inside a tournament event.
 */
data class Entry(
    /**
     * The name of the school the entry is from.
     */
    val school: String,

    /**
     * The location of the school.
     */
    val location: String,
    /**
     * The person's full name, who entered the tournament.
     */
    val name: String,
    /**
     * The entry code of the entry.
     */
    val code: String
)

/**
 * Represents a Judge in a tournament.
 */
data class Judge(
    /**
     * The first name of the judge.
     */
    val firstName: String,
    /**
     * The last name of the judge.
     */
    val lastName: String,
    /**
     * The name of the school this judge is affiliated with.
     */
    val school: String,
    /**
     * The location of this judge's origin.
     */
    val location: String,
    /**
     * The number of rounds this judge has judged, or 0 if the tournament is still ongoing.
     */
    val rounds: Int
)

// Records

/**
 * The level of a debate.
 */
enum class DebateLevel {
    /**
     * The level is novice, and only those who are in their first year of debate can participate.
     */
    NOVICE,

    /**
     * The level is Junior Varsity, and only those who are in their first or second year of debate can participate.
     */
    JV,

    /**
     * The level is Varsity, and anyone can participate.
     */
    VARSITY,

    /**
     * The level is open and anyone can participate.
     */
    OPEN

    ;

    /**
     * The aliases for the level as the appear in Tabroom.
     * For example, "novice" and "n" are aliases for [NOVICE].
     * @return The list of aliases for the level.
     */
    val aliases: List<String>
        get() = when (this) {
            NOVICE -> listOf("n")
            JV -> listOf("junior varsity", "middle")
            VARSITY -> listOf("v")
            OPEN -> listOf("o")
        } + name.lowercase()

    companion object {
        /**
         * Converts a string to a [DebateLevel].
         * @param string The string to convert.
         * @return The [DebateLevel] that corresponds to the string, or null if the string does not match any level.
         */
        fun fromString(string: String): DebateLevel? {
            val lower = string.lowercase()
            return entries.firstOrNull { it.aliases.contains(lower) }
        }
    }
}

/**
 * Represents the result of a tournament.
 */
data class CurrentSeasonResult(
    /**
     * The division of the debate for the result.
     */
    val division: DebateLevel,
    /**
     * The number of rounds the entry has participated in.
     */
    val count: Int,
    /**
     * The number of wins the entry has.
     */
    val wins: Int
) {
    /**
     * The percentage win rate of the entry.
     */
    val percentage: Double
        get() = wins.toDouble() / count

    /**
     * The number of losses the entry has.
     */
    val losses: Int
        get() = count - wins
}

/**
 * Represents the result of a tournament.
 */
data class TournamentResult(
    /**
     * The name of the tournament where result is from.
     */
    val tournament: String,
    /**
     * The division of the debate for the result.
     */
    val division: DebateLevel,
    /**
     * The date of the tournament in the format "YYYY-MM-DD".
     */
    val date: String,
    /**
     * The results for the preliminary rounds.
     */
    val prelimination: TournamentRecord,
    /**
     * The results for the trial elimination round (64), or null if the entry did not participate in the round.
     */
    val trials: TournamentRecord?,
    /**
     * The results for the double elimination round (32), or null if the entry did not participate in the round.
     */
    val doubles: TournamentRecord?,
    /**
     * The results for the octofinal elimination round (16), or null if the entry did not participate in the round.
     */
    val octofinals: TournamentRecord?,
    /**
     * The results for the quarterfinal elimination round (8), or null if the entry did not participate in the round.
     */
    val quarterfinals: TournamentRecord?,
    /**
     * The results for the semifinal elimination round (4), or null if the entry did not participate in the round.
     */
    val semifinals: TournamentRecord?,
    /**
     * The results for the final elimination round (2), or null if the entry did not participate in the round.
     */
    val finals: TournamentRecord?
) {

    /**
     * The total number of elimination rounds the entry has competed in.
     */
    val eliminations: TournamentRecord
        get() = (trials ?: TournamentRecord.NONE) + doubles + octofinals + quarterfinals + semifinals + finals

    /**
     * The total number of rounds the entry has competed in.
     */
    val total: TournamentRecord
        get() = prelimination + trials + doubles + octofinals + quarterfinals + semifinals + finals
}