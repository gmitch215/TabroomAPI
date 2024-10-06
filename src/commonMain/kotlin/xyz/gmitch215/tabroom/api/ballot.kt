package xyz.gmitch215.tabroom.api

/**
 * Represents a tournament record.
 *
 * For Debate, both [wins] and [losses] are used.
 *
 * For Speech, [wins] represents the current rank, and [losses] is always `-1`.
 *
 * For Congress, [wins] and [losses] are both `0`.
 */
data class TournamentRecord(
    /**
     * The number of wins the entry has.
     */
    val wins: Int,
    /**
     * The number of losses the entry has.
     */
    val losses: Int
) {

    constructor() : this(0, 0)

    /**
     * The total number of rounds the entry has competed in.
     */
    val total: Int get() = wins + losses

    /**
     * The win percentage of the entry.
     */
    val winPercentage: Double get() = wins.toDouble() / total

    /**
     * The loss percentage of the entry.
     */
    val lossPercentage: Double get() = losses.toDouble() / total

    /**
     * The record as a string.
     * @return The record as a string.
     */
    override fun toString(): String {
        if (losses < 0) return "$wins"
        return "$wins-$losses"
    }

    operator fun plus(other: TournamentRecord?): TournamentRecord {
        return TournamentRecord(wins + (other?.wins ?: 0), losses + (other?.losses ?: 0))
    }

    operator fun minus(other: TournamentRecord?): TournamentRecord {
        return TournamentRecord(wins - (other?.wins ?: 0), losses - (other?.losses ?: 0))
    }

    companion object {
        /**
         * A record with no wins or losses.
         */
        val NONE = TournamentRecord(0, 0)

        fun fromString(record: String): TournamentRecord {
            val (wins, losses) = record.split("-")
            return TournamentRecord(wins.toInt(), losses.toInt())
        }
    }

}

/**
 * Parses a win-loss record from a multi-judge ballot string.
 * @param record The record to parse.
 * @return The parsed record.
 */
fun parseMultiJudge(record: String): TournamentRecord {
    val wins = record.count { it == 'W' }
    val losses = record.count { it == 'L' }

    return TournamentRecord(wins, losses)
}