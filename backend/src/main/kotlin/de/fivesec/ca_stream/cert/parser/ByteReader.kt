package de.fivesec.ca_stream.cert.parser

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*


object B64 {
    fun decodeStd(s: String): ByteArray = Base64.getDecoder().decode(s)
}


class BEReader(private val buf: ByteBuffer) {
    init {
        buf.order(ByteOrder.BIG_ENDIAN)
    }


    fun u8(): Int = buf.get().toInt() and 0xFF
    fun u16(): Int = buf.short.toInt() and 0xFFFF
    fun u24(): Int {
        val b1 = u8()
        val b2 = u8()
        val b3 = u8()
        return (b1 shl 16) or (b2 shl 8) or b3
    }

    fun u32(): Long = (buf.int.toLong() and 0xFFFFFFFFL)
    fun u64(): Long = buf.long
    fun take(n: Int): ByteArray {
        val out = ByteArray(n); buf.get(out); return out
    }

    fun remaining(): Int = buf.remaining()
}


fun beReader(bytes: ByteArray) = BEReader(ByteBuffer.wrap(bytes))