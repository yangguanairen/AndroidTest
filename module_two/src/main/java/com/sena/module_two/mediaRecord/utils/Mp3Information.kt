package com.sena.module_two.mediaRecord.utils

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.MediaExtractor
import com.google.gson.Gson
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import kotlin.experimental.and


/**
 * FileName: Mp3Infomation
 * Author: JiaoCan
 * Date: 2022/11/23 16:42
 */

class Mp3Information(private val path: String) {

    private val file: File by lazy {
        File(path)
    }
    val stream: BufferedInputStream by lazy {
        BufferedInputStream(FileInputStream(file))
    }

    var readLen = 0

    init {

        test()
//        getTagHeader()
//
//        getTagFrame()
    }

    private fun test() {
        try {
            val mediaExtractor = MediaExtractor()
            mediaExtractor.setDataSource(path)
            val trackFormat = mediaExtractor.getTrackFormat(0)
            println(Gson().toJson(trackFormat))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val bufferSize = AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_MP3)
        val audioTrack = AudioTrack(
            AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_MP3, bufferSize, AudioTrack.MODE_STREAM
        )

        Thread {
            audioTrack.play()
            skip(439063L)
            val buffer = ByteArray(1024)
            var len = 0
            while (stream.read(buffer).also { len = it } > 0) {
                audioTrack.write(buffer, 0, len)
            }
            stream.close()
        }.start()

    }

    private fun getTagHeader() {
        stream.mark(readLen)
        val data = ByteArray(10)
        stream.read(data)
        val identification = String(byteArrayOf(data[0], data[1], data[2]))
        val mainVersion = data[3].toInt()
        val viceVersion = data[4].toInt()
        val a = data[5].toInt() shl 7
        val b = data[5].toInt() shl 6
        val c = data[5].toInt() shl 5
        val length = (data[6] and 0x7F) * 0x200000 + (data[7] and 0x7F) * 0x400 + (data[8] and 0x7F) * 0x80 + (data[9] and 0x7F)
        readLen += 10

        val format = "%-12s%-8s"
        println(String.format(format, "名称", "值"))
        println(String.format(format, "标识头", identification))
        println(String.format(format, "版本号", mainVersion.toString()))
        println(String.format(format, "副版本号", viceVersion.toString()))
        println(String.format(format, "标志字节a", a.toString()))
        println(String.format(format, "标志字节b", b.toString()))
        println(String.format(format, "标志字节c", c.toString()))
        println(String.format(format, "标签大小", length.toString()))
    }

    private fun getTagFrame() {
        stream.mark(readLen)
        val header = ByteArray(10)
        stream.read(header)

        val identification = String(byteArrayOf(header[0], header[1], header[2], header[3]))
        val frameContentLen = correcting(header[4]) * 0x10000000 + correcting(header[5]) * 0x10000 + correcting(header[6]) * 0x100 + correcting(header[7])
        val a = (header[8].toInt() shl 7) and 0x01
        val b = (header[8].toInt() shl 6) and 0x01
        val c = (header[8].toInt() shl 5) and 0x01
        val x = (header[9].toInt() shl 7) and 0x01
        val y = (header[9].toInt() shl 6) and 0x01
        val z = (header[9].toInt() shl 5) and 0x01
        skip(frameContentLen.toLong())
        readLen += (10 + frameContentLen)

        val format = "%-12s%-8s"
        println(String.format(format, "名称", "值"))
        println(String.format(format, "帧标识", identification))
        println(String.format(format, "帧内容长度", frameContentLen.toString()))

        stream.mark(readLen)
        val overBuffer = ByteArray(4)
        stream.read(overBuffer)
        val isOver = overBuffer.filter { it.toInt() == 0 }.size == 4
        stream.reset()
        if (!isOver) {
            getTagFrame()
        } else {
            // 跳过填充数据, 不知道是否固定为1024个字节
            skip(1024L)
            readLen += 1024
        }
    }

    private fun getDataFrame() {
        stream.mark(readLen)
        val header = ByteArray(4)

    }

    private fun correcting(byte: Byte): Int {
        val a = byte.toInt()
        return if (a < 0) {
            a + 256
        } else {
            a
        }
    }

    private fun skip(n: Long) {
        val actualSkip = stream.skip(n)
        if (actualSkip == n) return
        val surplusSkip = n - actualSkip
        val count = surplusSkip / 1024
        val buffer = ByteArray(1024)
        for (i in 0 until count) {
            stream.read(buffer)
        }
        val tmp = surplusSkip - count * 1024
        if (tmp > 0) {
            stream.read(ByteArray(tmp.toInt()))
        }
    }

}

