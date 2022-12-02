package com.aitmed.noodlsdk.utilities.encoder

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.nio.ByteBuffer


/**
 * FileName: AacEncoder
 * Author: JiaoCan
 * Date: 2022/12/1 15:05
 *
 * https://www.itdaan.com/blog/2014/12/11/978e7f110d0276ca998f2de7352c5b47.html
 */

class AacEncoder (private val data: EncoderData) : BaseEncoder(data) {

    private lateinit var mEncoder: MediaCodec

    private val timeUs = 0L

    private val adtsHeader = ByteArray(7)

    private val convertCoroutine = CoroutineScope(Dispatchers.Default).launch {
        while (!isFinish) {
            if (bufferQueue.isNotEmpty()) {
                val buffer = bufferQueue.pop()

                val inputBufferIndex = mEncoder.dequeueInputBuffer(timeUs)
                if (inputBufferIndex >= 0) {
                    val newBuffer = mEncoder.getInputBuffer(inputBufferIndex)!!
                    newBuffer.clear()
                    newBuffer.put(buffer)
                    mEncoder.queueInputBuffer(inputBufferIndex, 0, buffer.size, 0, 0)
                }

                val info = MediaCodec.BufferInfo()
                var outputBufferIndex = mEncoder.dequeueOutputBuffer(info, timeUs)
                if (outputBufferIndex < 0) {

                }
                var outputBuffer: ByteBuffer
                while (outputBufferIndex >= 0) {
                    outputBuffer = mEncoder.getOutputBuffer(outputBufferIndex)!!
                    val outData = ByteArray(info.size)
                    createAdtsHeader(info.size + 7)
                    outputStream.write(adtsHeader)
                    outputBuffer.get(outData)
                    outputBuffer.clear()
                    outputStream.write(outData)
                    outputStream.flush()
                    mEncoder.releaseOutputBuffer(outputBufferIndex, false)
                    outputBufferIndex = mEncoder.dequeueOutputBuffer(info, timeUs)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        createDecoder()
        isFinish = false
        if (!convertCoroutine.isActive) {
            convertCoroutine.start()
        }
    }

    override fun onFinish() {
        super.onFinish()
        isFinish = true
        convertCoroutine.cancel()

        mEncoder.stop()
        mEncoder.release()

        try {
            outputStream.flush()
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createDecoder() {
        val mimeType = "audio/mp4a-latm"
        val mediaFormat = MediaFormat()
        mediaFormat.setString(MediaFormat.KEY_MIME, mimeType)
        mediaFormat.setInteger(
            MediaFormat.KEY_AAC_PROFILE,
            MediaCodecInfo.CodecProfileLevel.AACObjectLC
        )
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 64000)
        mediaFormat.setInteger(MediaFormat.KEY_SAMPLE_RATE, 44100)
        mediaFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, data.channelCount)

        mEncoder = MediaCodec.createEncoderByType(mimeType)
        mEncoder.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        mEncoder.start()
    }

    // AAC ADTS Header Config
    // https://wiki.multimedia.cx/index.php/MPEG-4_Audio#Channel_Configurations
    private fun createAdtsHeader(frameSize: Int) {
        val profile = 2  // AAC_LC
        val freqIdx = 4  // 44100Hz
        val chanCfg = 2  // 2 channels
        adtsHeader[0] = 0xFF.toByte()
        adtsHeader[1] = 0xF9.toByte()
        adtsHeader[2] = (((profile - 1) shl 6) + (freqIdx shl 2) + (chanCfg shr 2)).toByte()
        adtsHeader[3] = (((chanCfg and 3) shl 6) + (frameSize shr 11)).toByte()
        adtsHeader[4] = ((frameSize and 0x7FF) shr 3).toByte()
        adtsHeader[5] = (((frameSize and 7) shl 5) + 0x1F).toByte()
        adtsHeader[6] = 0xFC.toByte()
    }
}

