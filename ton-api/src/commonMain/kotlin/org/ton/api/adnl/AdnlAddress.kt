@file:Suppress("OPT_IN_USAGE")

package org.ton.api.adnl

import io.ktor.utils.io.core.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import org.ton.api.pub.PublicKey
import org.ton.crypto.Base64ByteArraySerializer
import org.ton.crypto.base64
import org.ton.tl.TlCombinator
import org.ton.tl.TlConstructor

@JsonClassDiscriminator("@type")
interface AdnlAddress {
    companion object : TlCombinator<AdnlAddress>(
            AdnlAddressUdp,
            AdnlAddressUdp6,
            AdnlAddressTunnel
    )
}

@SerialName("adnl.address.udp")
@Serializable
data class AdnlAddressUdp(
        val ip: Int,
        val port: Int
) : AdnlAddress {
    companion object : TlConstructor<AdnlAddressUdp>(
            type = AdnlAddressUdp::class,
            schema = "adnl.address.udp ip:int port:int = adnl.Address"
    ) {
        override fun encode(output: Output, message: AdnlAddressUdp) {
            output.writeIntLittleEndian(message.ip)
            output.writeIntLittleEndian(message.port)
        }

        override fun decode(input: Input): AdnlAddressUdp {
            val ip = input.readIntLittleEndian()
            val port = input.readIntLittleEndian()
            return AdnlAddressUdp(ip, port)
        }
    }
}

@SerialName("adnl.address.udp6")
@Serializable
data class AdnlAddressUdp6(
        @Serializable(Base64ByteArraySerializer::class)
        val ip: ByteArray,
        val port: Int
) : AdnlAddress {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AdnlAddressUdp6

        if (!ip.contentEquals(other.ip)) return false
        if (port != other.port) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ip.contentHashCode()
        result = 31 * result + port
        return result
    }

    override fun toString(): String = buildString {
        append("AdnlAddressUdp6(ip=")
        append(base64(ip))
        append(", port=")
        append(port)
        append(")")
    }

    companion object : TlConstructor<AdnlAddressUdp6>(
            type = AdnlAddressUdp6::class,
            schema = "adnl.address.udp6 ip:int128 port:int = adnl.Address"
    ) {
        override fun decode(input: Input): AdnlAddressUdp6 {
            val ip = input.readBits256()
            val port = input.readIntLittleEndian()
            return AdnlAddressUdp6(ip, port)
        }

        override fun encode(output: Output, message: AdnlAddressUdp6) {
            output.writeBits256(message.ip)
            output.writeIntLittleEndian(message.port)
        }
    }
}

@SerialName("adnl.address.tunnel")
@Serializable
data class AdnlAddressTunnel(
        @Serializable(Base64ByteArraySerializer::class)
        val to: ByteArray,
        @SerialName("pubkey")
        val pubKey: PublicKey
) : AdnlAddress {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AdnlAddressTunnel

        if (!to.contentEquals(other.to)) return false
        if (pubKey != other.pubKey) return false

        return true
    }

    override fun hashCode(): Int {
        var result = to.contentHashCode()
        result = 31 * result + pubKey.hashCode()
        return result
    }

    override fun toString(): String = buildString {
        append("AdnlAddressTunnel(to=")
        append(base64(to))
        append(", pubKey=")
        append(pubKey)
        append(")")
    }

    companion object : TlConstructor<AdnlAddressTunnel>(
            type = AdnlAddressTunnel::class,
            schema = "adnl.address.tunnel to:int256 pubkey:PublicKey = adnl.Address"
    ) {
        override fun encode(output: Output, message: AdnlAddressTunnel) {
            output.writeBits256(message.to)
            output.writeTl(message.pubKey, PublicKey)
        }

        override fun decode(input: Input): AdnlAddressTunnel {
            val to = input.readBits256()
            val pubKey = input.readTl(PublicKey)
            return AdnlAddressTunnel(to, pubKey)
        }
    }
}