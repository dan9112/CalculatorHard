package org.example.calculator_hard.data

import app.cash.sqldelight.ColumnAdapter

object FloatListAdapter : ColumnAdapter<List<Float>, ByteArray> {
    override fun encode(value: List<Float>): ByteArray {
        val bytes = ByteArray(value.size * 4)
        for (i in value.indices) {
            val bits = value[i].toBits()
            val offset = i * 4
            bytes[offset] = bits.toByte()
            bytes[offset + 1] = (bits shr 8).toByte()
            bytes[offset + 2] = (bits shr 16).toByte()
            bytes[offset + 3] = (bits shr 24).toByte()
        }
        return bytes
    }

    override fun decode(databaseValue: ByteArray): List<Float> {
        require(databaseValue.size % 4 == 0) { "Invalid BLOB size for Float list: must be multiple of 4" }
        val count = databaseValue.size / 4
        return List(count) { i ->
            val offset = i * 4
            val bits = (databaseValue[offset].toInt() and 0xFF) or
                    ((databaseValue[offset + 1].toInt() and 0xFF) shl 8) or
                    ((databaseValue[offset + 2].toInt() and 0xFF) shl 16) or
                    ((databaseValue[offset + 3].toInt() and 0xFF) shl 24)
            Float.fromBits(bits)
        }
    }
}
