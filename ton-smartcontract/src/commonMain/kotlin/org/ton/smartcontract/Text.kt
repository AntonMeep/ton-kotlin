package org.ton.smartcontract

import org.ton.cell.CellBuilder
import org.ton.cell.CellSlice
import org.ton.tlb.*

data class Text(
    val data: SnakeData
) {
    companion object : TlbCodec<Text> by TextCombinator {
        @JvmStatic
        fun tlbCombinator(): TlbCombinator<Text> = TextCombinator
    }
}

private object TextCombinator : TlbCombinator<Text>() {
    override val constructors: List<TlbConstructor<out Text>> = listOf(TextConstructor)

    override fun getConstructor(value: Text): TlbConstructor<out Text> = TextConstructor

    private object TextConstructor : TlbConstructor<Text>(
        schema = "text#_ {n:#} data:(SnakeData ~n) = Text;"
    ) {
        private val snakeDataCodec by lazy { SnakeData.tlbCodec() }

        override fun storeTlb(cellBuilder: CellBuilder, value: Text) {
            cellBuilder.storeTlb(snakeDataCodec, value.data)
        }

        override fun loadTlb(cellSlice: CellSlice): Text = Text(cellSlice.loadTlb(snakeDataCodec))
    }
}
