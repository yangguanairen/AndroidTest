package com.aitmed.noodlsdk.utilities.encoder

import android.media.AudioFormat
import android.media.AudioRecord
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.util.*


/**
 * FileName: BaseEncoder
 * Author: JiaoCan
 * Date: 2022/12/1 14:08
 */

open class BaseEncoder(private val data: EncoderData) {

    protected var isFinish = false

    protected val outputStream: FileOutputStream

    protected val bufferQueue = LinkedList<ByteArray>()

    init {
        try {
            File(data.path).deleteOnExit()
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("BaseEncoder path: ${data.path} is invalid!!")
        }
        outputStream = FileOutputStream(data.path)
    }


    open fun onStart() {
        Log.d("", "BaseEncoder ==> onStart")
    }

    open fun onWrite(buffer: ByteArray) {
        Log.d("", "BaseEncoder ==> onWrite, size ${buffer.size}")
        if (!isFinish) {
            bufferQueue.add(buffer)
        }
    }

    open fun onFinish() {
        Log.d("", "BaseEncoder ==> onFinish")
    }

    fun getOutputPath(): String {
        return data.path
    }

}


data class EncoderData(
    val path: String = "",
    val sampleRate: Int = 48000,
    val channelCount: Int = 2,
    val bitRate: Int = 16,
    val audioFormat: Int = AudioFormat.ENCODING_PCM_16BIT
) {

    val channelConfig = if (channelCount == 2) AudioFormat.CHANNEL_IN_STEREO else AudioFormat.CHANNEL_IN_MONO

    val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

    constructor(mp3Path: String) : this(mp3Path, 48000, 1)
}

