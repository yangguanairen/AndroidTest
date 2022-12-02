package com.aitmed.noodlsdk.utilities.encoder

import com.aitmed.audio.LameEncode
import com.ecossdk.Other.DebugLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException


/**
 * FileName: Mp3Encoder
 * Author: JiaoCan
 * Date: 2022/12/1 14:11
 */

class Mp3Encoder(private val data: EncoderData) : BaseEncoder(data) {

    private val mp3Quality = 2

    private val mp3Buffer = ByteArray(7200 + (data.bufferSize * 1.5 * 2 * 2).toInt())


    private val coroutine = CoroutineScope(Dispatchers.Default).launch {
        while (!isFinish) {
            if (bufferQueue.isNotEmpty()) {
                val buffer = bufferQueue.pop()
                val realSize = buffer.size / 2
                val shortArray = ShortArray(realSize)
                for (i in 0 until realSize) {
                    val l = buffer[i * 2].toInt() and 0xFF
                    val h = buffer[i * 2 + 1].toInt() and 0xFF
                    val s = l or (h shl 8)
                    shortArray[i] = s.toShort()
                }
                val convertedSize = LameEncode.encoder(shortArray, mp3Buffer, realSize)
                if (convertedSize < 0) {
                    DebugLog.d("Mp3 Encode ==> Error")
                    // 这里不清楚是继续编码还是强行退出，有时候一小段数据的出错可能不影响视听
                    continue
                }
                try {
                    DebugLog.e("Mp3 Encoder ==> Convert data, size: $convertedSize")
                    outputStream.write(mp3Buffer, 0, convertedSize)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }


    override fun onStart() {
        super.onStart()
        LameEncode.init(data.sampleRate, data.channelCount, data.audioFormat, mp3Quality)
        isFinish = false
        if (!coroutine.isActive) {
            coroutine.start()
        }
    }

    override fun onFinish() {
        super.onFinish()
        isFinish = true
        val flushSize = LameEncode.flush(mp3Buffer)
        if (flushSize > 0) {
            outputStream.write(mp3Buffer, 0, flushSize)
        }
        LameEncode.close()
        coroutine.cancel()
        try {
            outputStream.flush()
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}

