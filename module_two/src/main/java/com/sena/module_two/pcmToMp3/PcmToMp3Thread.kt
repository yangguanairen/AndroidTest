package com.sena.module_two.pcmToMp3

import android.media.AudioRecord
import android.util.Log
import com.sena.audio.LameEncode
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException


/**
 * FileName: PcmToMp3Thread
 * Author: JiaoCan
 * Date: 2022/11/25 17:39
 *
 *  Android LAME库 PCM转MP3
 *  https://juejin.cn/post/6844903922058919950
 *  https://juejin.cn/post/6844903757621248014
 *  https://www.jianshu.com/p/5636d95a45c7
 *
 */

class PcmToMp3Thread(private val data: ConvertData) {

    private val bufferSize = AudioRecord.getMinBufferSize(data.sampleRate, data.channelConfig, data.audioFormat)
    private val mp3Buffer = ByteArray(7200 + (bufferSize * 1.5 * 2 * 2).toInt())  // 不知道为什么这么计算

    private val pcmStream by lazy {
        FileInputStream(data.pcmPath)
    }
    private val mp3Stream by lazy {
        File(data.mp3Path).deleteOnExit()
        FileOutputStream(data.mp3Path)
    }

    fun start() {
        Thread {
            LameEncode.init(data.sampleRate, data.channelCount, data.audioFormat, 2)
            convertMp3()
        }.start()
    }

    // 编码MP3
    private fun convertMp3() {
        val buffer = ByteArray(bufferSize * 2)
        val shortBuffer = ShortArray(bufferSize)
        var len: Int
        while (pcmStream.read(buffer).also { len = it } > 0) {
            /**
             * 注意：这里byte转short必须这么写
             * 必须先把byte转为int进行位操作，否则会出现噪音
             * 错误的写法如下
             * val h = ((pcmBuffer[i * 2] and 0xFF.toByte()).toInt()
             * val l = ((pcmBuffer[i * 2 + 1] and 0xFF.toByte()).toInt()
             */
            for (i in 0 until bufferSize) {
                val l = buffer[i * 2].toInt() and 0xFF
                val h = buffer[i * 2 + 1].toInt() and 0xFF
                val s = l or (h shl 8)
                shortBuffer[i] = s.toShort()
            }
            /**
             * 第二个需要注意的点
             * short两个字节，byte一个字节
             * 所以实际长度为读取的字节数除2
             * 返回值为已转换的字节数
             */
            val mp3Byte = LameEncode.encoder(shortBuffer, mp3Buffer, len / 2)
            if (mp3Byte < 0) {
                Log.e("", "pcm转mp3 ==> 编码失败, $mp3Byte ")
            }
            try {
                mp3Stream.write(mp3Buffer, 0, mp3Byte)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        finishConvert()
    }


    // 刷新缓冲区，写入剩余数据
    private fun flush() {
        val flushByte = LameEncode.flush(mp3Buffer)
        if (flushByte > 0) {
            mp3Stream.write(mp3Buffer, 0, flushByte)
        }
    }

    private fun finishConvert() {
        flush()
        LameEncode.close()
        mp3Stream.flush()
        mp3Stream.close()
        pcmStream.close()

        Log.e("", "pcm转mp3 ==> 完成", )
    }


}

