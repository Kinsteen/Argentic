package fr.kinsteen.argentic.data

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object RollsSerializer : Serializer<Rolls> {
    override val defaultValue: Rolls = Rolls.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): Rolls {
        try {
            return Rolls.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: Rolls, output: OutputStream) = t.writeTo(output)
}