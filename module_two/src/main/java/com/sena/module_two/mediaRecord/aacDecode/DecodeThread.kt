package com.sena.module_two.mediaRecord.aacDecode

import android.content.Context
import android.media.AudioTrack
import android.media.MediaCodec
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer
import kotlin.experimental.and
import kotlin.system.measureTimeMillis


/**
 * FileName: DecodeThread
 * Author: JiaoCan
 * Date: 2022/11/21 16:33
 */

class DecodeThread(
    private val context: Context,
    private val filePath: String,
    private val decoder: MediaCodec,
    private val audioTrack: AudioTrack
): Thread() {

    private var failCount = 0

    private val pcmStream: FileOutputStream

    init {
        pcmStream = FileOutputStream(File(context.externalCacheDir, "convertIn.pcm"))
    }

    override fun run() {
        super.run()
        val time = measureTimeMillis {
            findFrame()
        }
        println("耗时: $time, 失败次数: $failCount")
    }

    private fun findFrame() {
        val inputStream = BufferedInputStream(FileInputStream(filePath))

        var readLen = 0
        var len: Int
        val buffer = ByteArray(1024)

        inputStream.mark(readLen)
        while (inputStream.read(buffer).also { len = it } > 0) {
            for (i in 0 until len - 1) {
                if ((buffer[i] == 0xFF.toByte()) && (buffer[i + 1] and 0xF0.toByte() == 0xF0.toByte())) {
                    // 计算帧长度
                    var size = 0
                    size = size or ((buffer[i + 3].toInt() and 0x03) shl 11)
                    size = size or (buffer[i + 4].toInt() shl 3)
                    size = size or ((buffer[i + 5].toInt() and 0xE0) shr 5)
                    // 读取一帧数据
                    inputStream.reset()
                    val newBuffer = ByteArray(size)
                    inputStream.read(newBuffer)
                    readLen += size
                    inputStream.mark(readLen)
                    // 解码&播放
                    decode(newBuffer)
                    break

                }
            }
        }
    }

    private fun decode(buffer: ByteArray) {

        // 缓冲区有数据的等待时间，-1代表无限等待
        val timeoutUs = 0L

        // 这一段大致是维护了两个队列
        // in队列接收数据，解码器顺序从in队列中取出数据解码，把转换后的数据顺序加入out队列
        // 音频播放器不断从out队列中取出数据播放，直至out队列为空
        try {
            // 解码
            val inputBufferIndex = decoder.dequeueInputBuffer(timeoutUs)
            if (inputBufferIndex >= 0) {
                val newBuffer = decoder.getInputBuffer(inputBufferIndex)!!
                newBuffer.clear()
                newBuffer.put(buffer)
                decoder.queueInputBuffer(inputBufferIndex, 0, buffer.size, 0, 0)
            }

            // 播放
            val info = MediaCodec.BufferInfo()
            var outputBufferIndex = decoder.dequeueOutputBuffer(info, timeoutUs)
            if (outputBufferIndex < 0) {
                failCount++
//                Log.e("", "无数据")
            }
            var outputBuffer: ByteBuffer
            while (outputBufferIndex >= 0) {
                outputBuffer = decoder.getOutputBuffer(outputBufferIndex)!!
                val outData = ByteArray(info.size)
                outputBuffer.get(outData)
                outputBuffer.clear()
                // 可以把aac转换为pcm，但这里的pcm只是原始数据，其他软件识别不了
                // 需要转换为wav，WavUtils.convertPcmToWav(pcmFile, sampleRate, channelCount, 16)
                 pcmStream.write(outData)
                 pcmStream.flush()
                audioTrack.write(outData, 0, info.size)
                decoder.releaseOutputBuffer(outputBufferIndex, false)
                outputBufferIndex = decoder.dequeueOutputBuffer(info, timeoutUs)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}

