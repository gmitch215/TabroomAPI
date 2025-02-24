package xyz.gmitch215.tabroom.api

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class Ballot(
    /**
     * The number of ballots lost.
     */
    @SerialName("ballots_lost")
    val ballotsLost: Int = 0,
    /**
     * The number of ballots won.
     */
    @SerialName("ballots_won")
    val ballotsWon: Int = 0,
    /**
     * The decision as a string.
     */
    @SerialName("decision_str")
    val decision: String = "L",
    /**
     * Whether this round is an elimination round.
     */
    @SerialName("elim")
    @Serializable(with = ByteToBooleanSerializer::class)
    val isElimination: Boolean,
    /**
     * The level of the debate.
     */
    @SerialName("event_level")
    val level: DebateLevel = DebateLevel.OPEN,
    /**
     * The name of the event.
     */
    @SerialName("event_name")
    val eventName: String = "Unknown",
    /**
     * The name of the judge.
     */
    @SerialName("judge_raw")
    val judge: String = "Unknown",
    /**
     * The name of the opponent.
     */
    val opponent: String? = "Unknown",
    /**
     * The round number.
     */
    @SerialName("round_name")
    val round: Int = 0,
    /**
     * The speaker points of the first speaker, or 0 if not available.
     */
    @SerialName("speaker1_pts")
    @Serializable(with = SpeakerDeserializer::class)
    val speaker1Points: Double = 0.0,
    /**
     * The speaker points of the second speaker, or 0 if not available.
     */
    @SerialName("speaker2_pts")
    @Serializable(with = SpeakerDeserializer::class)
    val speaker2Points: Double = 0.0,
    /**
     * The name of the tournament.
     */
    @SerialName("tourn")
    val tournament: String,
    /**
     * The date of the tournament.
     */
    @SerialName("tourn_start")
    val tournamentDate: String
)

private object SpeakerDeserializer : KSerializer<Double> {
    override val descriptor = PrimitiveSerialDescriptor("SpeakerPoints", PrimitiveKind.DOUBLE)

    override fun deserialize(decoder: Decoder): Double {
        return try {
            decoder.decodeString().trim().toDouble()
        } catch (e: Exception) {
            0.0
        }
    }

    override fun serialize(encoder: Encoder, value: Double) {
        encoder.encodeDouble(value)
    }
}

private object ByteToBooleanSerializer : KSerializer<Boolean> {
    override val descriptor = PrimitiveSerialDescriptor("Boolean", PrimitiveKind.BOOLEAN)

    override fun deserialize(decoder: Decoder): Boolean {
        return decoder.decodeByte() == 1.toByte()
    }

    override fun serialize(encoder: Encoder, value: Boolean) {
        encoder.encodeByte(if (value) 1 else 0)
    }
}