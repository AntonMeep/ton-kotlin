@file:UseSerializers(HexByteArraySerializer::class)

package org.ton.api.tonnode

import io.ktor.utils.io.core.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.ton.crypto.Base64ByteArraySerializer
import org.ton.crypto.HexByteArraySerializer
import org.ton.crypto.base64
import org.ton.tl.TlConstructor
import org.ton.tl.constructors.*

@Serializable
data class TonNodeBlockIdExt(
        val workchain: Int,
        val shard: Long,
        val seqno: Int,
        @SerialName("root_hash")
        @Serializable(Base64ByteArraySerializer::class)
        val rootHash: ByteArray,
        @SerialName("file_hash")
        @Serializable(Base64ByteArraySerializer::class)
        val fileHash: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TonNodeBlockIdExt

        if (workchain != other.workchain) return false
        if (shard != other.shard) return false
        if (seqno != other.seqno) return false
        if (!rootHash.contentEquals(other.rootHash)) return false
        if (!fileHash.contentEquals(other.fileHash)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = workchain
        result = 31 * result + shard.hashCode()
        result = 31 * result + seqno
        result = 31 * result + rootHash.contentHashCode()
        result = 31 * result + fileHash.contentHashCode()
        return result
    }

    override fun toString(): String = buildString {
        append("TonNodeBlockIdExt(workchain=")
        append(workchain)
        append(", shard=")
        append(shard)
        append(", seqno=")
        append(seqno)
        append(", rootHash=")
        append(base64(rootHash))
        append(", fileHash=")
        append(base64(fileHash))
        append(")")
    }

    companion object : TlConstructor<TonNodeBlockIdExt>(
            type = TonNodeBlockIdExt::class,
            schema = "tonNode.blockIdExt workchain:int shard:long seqno:int root_hash:int256 file_hash:int256 = tonNode.BlockIdExt"
    ) {
        override fun decode(input: Input): TonNodeBlockIdExt {
            val workchain = input.readIntTl()
            val shard = input.readLongTl()
            val seqno = input.readIntTl()
            val rootHash = input.readInt256Tl()
            val fileHash = input.readInt256Tl()
            return TonNodeBlockIdExt(workchain, shard, seqno, rootHash, fileHash)
        }

        override fun encode(output: Output, value: TonNodeBlockIdExt) {
            output.writeIntTl(value.workchain)
            output.writeLongTl(value.shard)
            output.writeIntTl(value.seqno)
            output.writeInt256Tl(value.rootHash)
            output.writeInt256Tl(value.fileHash)
        }
    }
}