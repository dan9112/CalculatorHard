package org.example.calculator.data

import app.cash.sqldelight.ColumnAdapter
import org.example.calculator.domain.Operation

object OperationListAdapter : ColumnAdapter<List<Operation>, ByteArray> {
    override fun encode(value: List<Operation>): ByteArray {
        if (value.isEmpty()) return byteArrayOf(0)

        val count = value.size
        val packedSize = 1 + (count + 3) / 4
        val bytes = ByteArray(packedSize)
        bytes[0] = count.toByte()

        for (i in value.indices) {
            val byteIndex = 1 + (i / 4)
            val bitOffset = (i % 4) * 2
            val ordinal = value[i].ordinal
            bytes[byteIndex] = (bytes[byteIndex].toInt() or (ordinal shl bitOffset)).toByte()
        }
        return bytes
    }

    override fun decode(databaseValue: ByteArray): List<Operation> {
        if (databaseValue.isEmpty()) return emptyList()
        val count = databaseValue[0].toInt() and 0xFF
        val result = ArrayList<Operation>(count)

        var i = 0
        var byteIndex = 1
        while (i < count && byteIndex < databaseValue.size) {
            val b = databaseValue[byteIndex].toInt() and 0xFF
            for (bitOffset in 0 until 4) {
                if (i >= count) break
                val ordinal = (b shr (bitOffset * 2)) and 0x03
                result.add(Operation.entries[ordinal])
                i++
            }
            byteIndex++
        }
        return result
    }
}
