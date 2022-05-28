package com.example.alphabet

import com.example.alphabet.util.Constants.Companion.sdfISO
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.*

class CalendarSerializer: KSerializer<Calendar> {
    override fun deserialize(decoder: Decoder): Calendar {
        return decoder.decodeString().toCalendar()
    }

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("calendar", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Calendar) {
        encoder.encodeString(sdfISO.format(value.time))
    }
}