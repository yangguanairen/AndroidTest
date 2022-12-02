package com.aitmed.noodlsdk.utilities.encoder

import android.media.AudioFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder


/**
 * FileName: WavEncoder
 * Author: JiaoCan
 * Date: 2022/12/2 9:29
 */

class WavEncoder(private val data: EncoderData) : BaseEncoder(data) {

    private val bitDepth: Short = when (data.audioFormat) {
        AudioFormat.ENCODING_PCM_8BIT -> 8
        AudioFormat.ENCODING_PCM_16BIT -> 16
        AudioFormat.ENCODING_PCM_FLOAT -> 32
        else -> throw IllegalArgumentException("Unacceptable encoding")
    }

    private val convertCoroutine = CoroutineScope(Dispatchers.Default).launch {
        while (!isFinish) {
            if (bufferQueue.isNotEmpty()) {
                val buffer = bufferQueue.pop()
                outputStream.write(buffer)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        addHeader()
        isFinish = false
        if (!convertCoroutine.isActive) {
            convertCoroutine.start()
        }
    }

    override fun onFinish() {
        super.onFinish()
        isFinish = true
        convertCoroutine.cancel()
        updateHeader()
        try {
            outputStream.flush()
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addHeader() {
        val littleBytes = ByteBuffer.allocate(14)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putShort(data.channelCount.toShort())
            .putInt(data.sampleRate)
            .putInt(data.sampleRate * data.channelCount * (bitDepth / 8))
            .putShort((data.channelCount * (bitDepth / 8)).toShort())
            .putShort(bitDepth)

        outputStream.write(
            // Not necessarily the best, but it's very easy to visualize this way
            byteArrayOf(
                // RIFF header
                'R'.code.toByte(),
                'I'.code.toByte(),
                'F'.code.toByte(),
                'F'.code.toByte(),  // ChunkID
                0,
                0,
                0,
                0,  // ChunkSize (must be updated later)
                'W'.code.toByte(),
                'A'.code.toByte(),
                'V'.code.toByte(),
                'E'.code.toByte(),  // Format
                // fmt subchunk
                'f'.code.toByte(),
                'm'.code.toByte(),
                't'.code.toByte(),
                ' '.code.toByte(),  // Subchunk1ID
                16,
                0,
                0,
                0,  // Subchunk1Size
                1,
                0,  // AudioFormat
                littleBytes[0],
                littleBytes[1],  // NumChannels
                littleBytes[2],
                littleBytes[3],
                littleBytes[4],
                littleBytes[5],  // SampleRate
                littleBytes[6],
                littleBytes[7],
                littleBytes[8],
                littleBytes[9],  // ByteRate
                littleBytes[10],
                littleBytes[11],  // BlockAlign
                littleBytes[12],
                littleBytes[13],  // BitsPerSample
                // data subchunk
                'd'.code.toByte(),
                'a'.code.toByte(),
                't'.code.toByte(),
                'a'.code.toByte(),  // Subchunk2ID
                0,
                0,
                0,
                0
            )
        )
    }

    private fun updateHeader() {
        val file = File(data.path)
        val fileLength = file.length()
        val sizes = ByteBuffer.allocate(8)
            .order(ByteOrder.LITTLE_ENDIAN)
            // There are probably a bunch of different/better ways to calculate
            // these two given your circumstances. Cast should be safe since if the WAV
            // is
            // > 4 GB we've already made a terrible mistake.
            .putInt((fileLength - 8).toInt()) // ChunkSize
            .putInt((fileLength - 44).toInt()) // Subchunk2Size
            .array()

        var accessWave: RandomAccessFile? = null
        //noinspection CaughtExceptionImmediatelyRethrown
        try {
            accessWave = RandomAccessFile(file, "rw")
            // ChunkSize
            accessWave.seek(4)
            accessWave.write(sizes, 0, 4)
            // Subchunk2Size
            accessWave.seek(40)
            accessWave.write(sizes, 4, 4)
        } catch (e: IOException) {
            // Rethrow but we still close accessWave in our finally
            throw e
        } finally {
            try {
                accessWave?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

}

