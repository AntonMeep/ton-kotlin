package org.ton.api.adnl

import io.ktor.utils.io.core.*
import kotlinx.serialization.Serializable
import org.ton.crypto.Base64ByteArraySerializer
import org.ton.crypto.base64
import org.ton.tl.TlConstructor

@Serializable
data class AdnlIdShort(
        @Serializable(Base64ByteArraySerializer::class)
        val id: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AdnlIdShort

        if (!id.contentEquals(other.id)) return false

        return true
    }

    override fun hashCode(): Int {
        return id.contentHashCode()
    }

    override fun toString(): String = buildString {
        append("AdnlIdShort(id=")
        append(base64(id))
        append(")")
    }

    companion object : TlConstructor<AdnlIdShort>(
            type = AdnlIdShort::class,
            schema = "adnl.id.short id:int256 = adnl.id.Short"
    ) {
        override fun decode(input: Input): AdnlIdShort {
            val id = input.readBits256()
            return AdnlIdShort(id)
        }

        override fun encode(output: Output, message: AdnlIdShort) {
            output.writeBits256(message.id)
        }
    }
}