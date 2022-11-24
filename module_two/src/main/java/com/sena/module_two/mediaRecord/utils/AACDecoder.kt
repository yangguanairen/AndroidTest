package com.sena.module_two.mediaRecord.utils

import android.media.*
import android.util.Log
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer
import kotlin.experimental.and
import kotlin.system.measureTimeMillis


/**
 * FileName: AacTest
 * Author: JiaoCan
 * Date: 2022/11/17 14:52
 *
 *
 *
 * AAC格式解析
 * https://www.likecs.com/show-203543505.html
 * https://blog.csdn.net/leixiaohua1020/article/details/50535042
 * https://blog.csdn.net/cindywry/article/details/107916132
 *
 * Android AAC解码播放
 * https://blog.csdn.net/qq_34161388/article/details/73773901
 * https://jspping.blog.csdn.net/article/details/113262445
 *
 * Android AudioTrack
 * https://blog.csdn.net/duoduo_11011/article/details/105642848
 *
 * PCM格式解析
 * https://zhuanlan.zhihu.com/p/499759463
 * https://blog.csdn.net/jh1988abc/article/details/122714920
 *
 * PCM与WAV互转
 * https://www.csdn.net/tags/OtTaggxsMzYxNS1ibG9n.html
 *
 * WAV格式解析
 * https://blog.csdn.net/weixin_42600398/article/details/115092500
 *
 * 需要完成的事情
 * 1.解耦
 * 2.实时解析，现在是把数据全部读取到内存中，易造成内存溢出
 *
 * 注意事项
 * 1.AAC音频的通道数量，根据这个配置AudioTrack，否则会造成音频拖长
 * 2.
 */

object AACDecoder {

    private lateinit var mDecoder: MediaCodec
    private lateinit var mAudioTrack: AudioTrack

    // 采样率
    private var sampleRate: Int = 0
    // 声道数量
    private var channelCount: Int = 0
    // 比特率
    private var bitRate: Int = 0
    // atds头
    private var adtsHeader: ByteBuffer = ByteBuffer.wrap(byteArrayOf(0))
    // 声道类型：单声道/双声道
    private val channelConfig by lazy {
        if (channelCount == 1) AudioFormat.CHANNEL_OUT_MONO
        else AudioFormat.CHANNEL_OUT_STEREO
    }

    private var count = 0

    fun test(audioPath: String) {
        // 检查文件路劲的正确性
        try {
            File(audioPath)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("AACDecoder.test", "错误的路径或者文件不存在!!")
            return
        }

        getConfig(audioPath)
        createDecoder()
        createTrack()
        val time = measureTimeMillis {
            test2(audioPath)
        }
        println("耗时: $time")
//        // 这里把音频文件全量加载到内存里，容易造成内存溢出，后面要改写法
//        val frameList = getFrameList(audioPath)
//
//        // 要开子线程，否则主线程会卡死
//        Thread {
//            val time = measureTimeMillis {
//                for (bytes in frameList) {
//                    decode(bytes)
//                }
//            }
//            println("总过失败${count}次 总耗时: ${time / 1000}")
//        }.start()

    }

    private fun test2(audioPath: String) {
        val inputStream = BufferedInputStream(FileInputStream(audioPath))

        val headers = arrayListOf<Int>()
        val recycleCount = 0

        var readLen = 0
        var len: Int
        val buffer = ByteArray(1024)

        val list = arrayListOf<Int>()

//        inputStream.mark(readLen)
//        if (inputStream.read(buffer).also { len = it } > 0) {
//            var findHeader = false
//            for (i in 0 until len - 1) {
//                if ((buffer[i] == 0xFF.toByte()) && (buffer[i + 1] and 0xF0.toByte() == 0xF0.toByte())) {
//                    findHeader = true
//                    readLen += i
//                    inputStream.reset()
//                    val newBuffer = ByteArray(i)
//                    inputStream.read(newBuffer)
//                    inputStream.mark(readLen)
//                    list.add(readLen)
////                    decode(newBuffer)
//                }
//            }
//            if (!findHeader) readLen += len
//
//        }

        inputStream.mark(readLen)
        looper@ while (inputStream.read(buffer).also { len = it } > 0) {
            for (i in 0 until len - 1) {
                if ((buffer[i] == 0xFF.toByte()) && (buffer[i + 1] and 0xF0.toByte() == 0xF0.toByte())) {

                    var size = 0
                    size = size or ((buffer[i + 3].toInt() and 0x03) shl 11)
                    size = size or (buffer[i + 4].toInt() shl 3)
                    size = size or ((buffer[i + 5].toInt() and 0xE0) shr 5)

                    inputStream.reset()
                    val newBuffer = ByteArray(size)
                    inputStream.read(newBuffer)
                    readLen += size
                    inputStream.mark(readLen)

                    list.add(size)
                    decode(newBuffer)
                    break

                }
            }
        }

//        Log.e("", "test2: ${list.toString()} ${list.size}", )

    }


    private fun getFrameList(audioPath: String): List<ByteArray> {
        val file = File(audioPath)

        var inputStream = FileInputStream(file)
        val data = inputStream.readBytes()

        val headerIndex = arrayListOf<Int>()    // header起始位置，标准为【0xFF, 0xF?】，注意？不得为0，占据12个字节
        val frameLength = arrayListOf<Int>()    // 帧长度，占据13个字节
        val profileList = arrayListOf<Int>()    // 音频类型，2个字节
        val frequencyList = arrayListOf<Int>()  //





        var i = 0
        while (i < data.size - 1) {
            if ((data[i] == 0xFF.toByte()) && (data[i + 1] and 0xF0.toByte() == 0xF0.toByte())) {
                if (data.size - i < 7) break
                // 计算一帧的长度
                var size = 0
                size = size or ((data[i + 3].toInt() and 0x03) shl 11)
                size = size or (data[i + 4].toInt() shl 3)
                size = size or ((data[i + 5].toInt() and 0xE0) shr 5)
                headerIndex.add(i)
                frameLength.add(size)

                profileList.add((data[i + 2].toInt() and 0xC0) shr 6)
                frequencyList.add((data[i + 2].toInt() and 0x3C) shr 2)

                i += size
            } else {
                i++
            }
        }

//        for (j in 0 until frameLength.size) {
//            Log.e(
//                "",
//                "Number: $j  Profile: ${profileList[j]}  Frequency: ${frequencyList[j]}  Size: ${frameLength[j]}"
//            )
//        }
//        Log.d("", "getFrame: ${frameLength.size}")
//        Log.d("", "getFrame: ${headerIndex.toString()}")

        val bufferList = arrayListOf<ByteArray>()
        inputStream = FileInputStream(audioPath)
        frameLength.forEach {
            val buffer = ByteArray(it)
            inputStream.read(buffer)
            bufferList.add(buffer)
        }

        inputStream.close()

        return bufferList
    }


    private fun createTrack() {
        val bufferSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, AudioFormat.ENCODING_PCM_16BIT)
        mAudioTrack = AudioTrack(
            AudioManager.STREAM_MUSIC, sampleRate, channelConfig, AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM
        )
        mAudioTrack.play()
    }

    private fun createDecoder() {
        val mimeType = "audio/mp4a-latm"
        val mediaFormat = MediaFormat()
        mediaFormat.setString(MediaFormat.KEY_MIME, mimeType)
        mediaFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, channelCount)
        mediaFormat.setInteger(MediaFormat.KEY_SAMPLE_RATE, sampleRate)
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitRate)
        mediaFormat.setInteger(MediaFormat.KEY_IS_ADTS, 1)
        mediaFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC)
        mediaFormat.setByteBuffer("csd-0", adtsHeader)

        mDecoder = MediaCodec.createDecoderByType(mimeType)
        mDecoder.configure(mediaFormat, null, null, 0)
        mDecoder.start()
    }

    private fun getConfig(audioPath: String) {
        try {
            val mediaExtractor = MediaExtractor()
            mediaExtractor.setDataSource(audioPath)
            val mediaFormat = mediaExtractor.getTrackFormat(0)
            val mimeType = mediaFormat.getString(MediaFormat.KEY_MIME)
            sampleRate = mediaFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE)
            channelCount = mediaFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
            bitRate = mediaFormat.getInteger(MediaFormat.KEY_BIT_RATE)
            adtsHeader = mediaFormat.getByteBuffer("csd-0")!!
            Log.e("AACDecoder.getConfig: ",
                "mimeType: $mimeType, \n" +
                    "sampleRate: $sampleRate, \n" +
                    "channelCount: $channelCount, \n" +
                    "bitRate: $bitRate, \n" +
                    "atdsHeader: [${adtsHeader[0].toInt()}, ${adtsHeader[1].toInt()}]}]")
        } catch (e: Exception) {
            e.printStackTrace()
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
            val inputBufferIndex = mDecoder.dequeueInputBuffer(timeoutUs)
            if (inputBufferIndex >= 0) {
                val newBuffer = mDecoder.getInputBuffer(inputBufferIndex)!!
                newBuffer.clear()
                newBuffer.put(buffer)
                mDecoder.queueInputBuffer(inputBufferIndex, 0, buffer.size, 0, 0)
            }

            // 播放
            val info = MediaCodec.BufferInfo()
            var outputBufferIndex = mDecoder.dequeueOutputBuffer(info, timeoutUs)
            if (outputBufferIndex < 0) {
                count++
//                Log.e("", "无数据")
            }
            var outputBuffer: ByteBuffer
            while (outputBufferIndex >= 0) {
                outputBuffer = mDecoder.getOutputBuffer(outputBufferIndex)!!
                val outData = ByteArray(info.size)
                outputBuffer.get(outData)
                outputBuffer.clear()
                // 可以把aac转换为pcm，但这里的pcm只是原始数据，其他软件识别不了
                // 需要转换为wav，WavUtils.convertPcmToWav(pcmFile, sampleRate, channelCount, 16)
                // pcmStream.write(outData)
                // pcmStream.flush()
                mAudioTrack.write(outData, 0, info.size)
                mDecoder.releaseOutputBuffer(outputBufferIndex, false)
                outputBufferIndex = mDecoder.dequeueOutputBuffer(info, timeoutUs)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stop() {
        mDecoder.stop()
        mDecoder.release()
        mAudioTrack.release()
        count = 0
    }

}

