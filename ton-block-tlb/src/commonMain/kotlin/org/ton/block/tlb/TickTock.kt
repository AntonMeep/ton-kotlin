package org.ton.block.tlb

import org.ton.block.TickTock
import org.ton.cell.CellBuilder
import org.ton.cell.CellSlice
import org.ton.tlb.TlbCodec
import org.ton.tlb.TlbConstructor

fun TickTock.Companion.tlbCodec(): TlbCodec<TickTock> = TickTockTlbConstructor

private object TickTockTlbConstructor : TlbConstructor<TickTock>(
    schema = "tick_tock\$_ tick:Bool tock:Bool = TickTock;"
) {
    override fun encode(
        cellBuilder: CellBuilder, value: TickTock, param: Int, negativeParam: (Int) -> Unit
    ) = cellBuilder {
        storeBit(value.tick)
        storeBit(value.tock)
    }

    override fun decode(
        cellSlice: CellSlice, param: Int, negativeParam: (Int) -> Unit
    ): TickTock = cellSlice {
        val tick = loadBit()
        val tock = loadBit()
        TickTock(tick, tock)
    }
}
