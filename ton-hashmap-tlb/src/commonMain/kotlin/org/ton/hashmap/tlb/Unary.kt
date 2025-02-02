package org.ton.hashmap.tlb

import org.ton.cell.CellBuilder
import org.ton.cell.CellSlice
import org.ton.hashmap.Unary
import org.ton.hashmap.UnarySuccess
import org.ton.hashmap.UnaryZero
import org.ton.tlb.TlbCombinator
import org.ton.tlb.TlbConstructor
import org.ton.tlb.TlbDecoder
import org.ton.tlb.TlbEncoder

object UnaryTlbCombinator : TlbCombinator<Unary>(
        constructors = listOf(UnaryZero.tlbCodec, UnarySuccess.tlbCodec)
) {
    override fun encode(
            cellBuilder: CellBuilder,
            value: Unary,
            typeParam: TlbEncoder<Any>?,
            param: Int,
            negativeParam: ((Int) -> Unit)?
    ) {
        when (value) {
            is UnarySuccess -> {
                cellBuilder.storeBit(true)
                UnarySuccessTlbConstructor.encode(cellBuilder, value, typeParam, param, negativeParam)
            }
            is UnaryZero -> {
                cellBuilder.storeBit(false)
                UnaryZeroTlbConstructor.encode(cellBuilder, value, typeParam, param, negativeParam)
            }
        }
    }

    override fun decode(
            cellSlice: CellSlice,
            typeParam: TlbDecoder<Any>?,
            param: Int,
            negativeParam: ((Int) -> Unit)?
    ): Unary {
        return if (cellSlice.loadBit()) {
            UnarySuccessTlbConstructor.decode(cellSlice, typeParam, param, negativeParam)
        } else {
            UnaryZeroTlbConstructor.decode(cellSlice, typeParam, param, negativeParam)
        }
    }
}

val Unary.Companion.tlbCodec get() = UnaryTlbCombinator

object UnarySuccessTlbConstructor : TlbConstructor<UnarySuccess>(
        schema = "unary_succ\$1 {n:#} x:(Unary ~n) = Unary ~(n + 1);"
) {
    override fun encode(
            cellBuilder: CellBuilder,
            value: UnarySuccess,
            typeParam: TlbEncoder<Any>?,
            param: Int,
            negativeParam: ((Int) -> Unit)?
    ) {
        var n = 0
        UnaryTlbCombinator.encode(cellBuilder, value.x) { n = it }
        negativeParam?.invoke(n + 1)
    }

    override fun decode(
            cellSlice: CellSlice,
            typeParam: TlbDecoder<Any>?,
            param: Int,
            negativeParam: ((Int) -> Unit)?,
    ): UnarySuccess {
        var n = 0
        val x = UnaryTlbCombinator.decode(cellSlice) { n = it }
        negativeParam?.invoke(n + 1)
        return UnarySuccess(x)
    }
}

val UnarySuccess.Companion.tlbCodec get() = UnarySuccessTlbConstructor

object UnaryZeroTlbConstructor : TlbConstructor<UnaryZero>(
        schema = "unary_zero\$0 = Unary ~0;"
) {
    override fun encode(
            cellBuilder: CellBuilder,
            value: UnaryZero,
            typeParam: TlbEncoder<Any>?,
            param: Int,
            negativeParam: ((Int) -> Unit)?
    ) {
        negativeParam?.invoke(0)
    }

    override fun decode(
            cellSlice: CellSlice,
            typeParam: TlbDecoder<Any>?,
            param: Int,
            negativeParam: ((Int) -> Unit)?
    ): UnaryZero {
        negativeParam?.invoke(0)
        return UnaryZero
    }
}

val UnaryZero.tlbCodec get() = UnaryZeroTlbConstructor