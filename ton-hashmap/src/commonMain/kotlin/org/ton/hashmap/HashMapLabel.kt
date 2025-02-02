@file:Suppress("OPT_IN_USAGE")

package org.ton.hashmap

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import org.ton.bitstring.BitString

@Serializable
@JsonClassDiscriminator("@type")
sealed class HashMapLabel

@Serializable
@SerialName("hml_short")
data class HashMapLabelShort(
        val len: Unary,
        val s: BitString
) : HashMapLabel() {
    constructor(s: BitString) : this(Unary(s.length), s)

    override fun toString() = "hml_short(len=$len, s=$s)"
}

@Serializable
@SerialName("hml_long")
data class HashMapLabelLong(
        val n: Int,
        val s: BitString
) : HashMapLabel() {
    constructor(s: BitString) : this(s.length, s)

    override fun toString() = "hml_long(n=$n, s=$s)"
}

@Serializable
@SerialName("hml_same")
data class HashMapLabelSame(
        val v: Boolean,
        val n: Int
) : HashMapLabel() {
    override fun toString() = "hml_same(v=$v, n=$n)"
}