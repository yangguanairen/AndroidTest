package com.sena.module_two.mediaRecord

import android.media.*
import android.util.Log
import java.io.ByteArrayInputStream
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
 * 需要完成的事情
 * 1.解耦
 * 2.实时解析，现在是把数据全部读取到内存中，易造成内存溢出
 *
 * 注意事项
 * 1.AAC音频的通道数量，根据这个配置AudioTrack，否则会造成音频拖长
 * 2.
 */

object AacTest {

    private lateinit var mDecoder: MediaCodec
    private lateinit var mAudioTrack: AudioTrack

    private var sampleRate: Int = 0
    private var channelCount: Int = 0
    private var bitRate: Int = 0
    private var atdsHeader: ByteBuffer = ByteBuffer.wrap(byteArrayOf(0))

    private val channelConfig by lazy {
        if (channelCount == 1) AudioFormat.CHANNEL_OUT_MONO
        else AudioFormat.CHANNEL_OUT_STEREO
    }



    fun test(audioPath: String) {

        getConfig(audioPath)


        createDecoder()
        createTrack()

        val bufferList = getFrame(audioPath)


//

        Thread {
            val time = measureTimeMillis {
//                for (i in 0 until bufferList.size / 2) {
//                    decode(bufferList[i * 2] + bufferList[i * 2 + 1])
//                }

                for (bytes in bufferList) {
                    decode(bytes)
                }
            }
            println("总过跳过${count}帧 耗时: ${time / 1000}")
        }.start()


//        decodedData.forEach {
//            mAudioTrack.write(it, 0, it.size)
//        }




    }

    private fun getFrame(audioPath: String): List<ByteArray> {
        val file = File(audioPath)

        var inputStream = FileInputStream(file)
        val data = inputStream.readBytes()

        val headerIndex = arrayListOf<Int>()
        val frameLength = arrayListOf<Int>()
        val profileList = arrayListOf<Int>()
        val frequencyList = arrayListOf<Int>()

        var i = 0
        while (i < data.size - 1) {
            if ((data[i] == 0xFF.toByte()) && (data[i + 1] and 0xF0.toByte() == 0xF0.toByte())) {
                if (data.size - i < 7) break
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

        for (j in 0 until frameLength.size) {
            Log.e(
                "",
                "Number: $j  Profile: ${profileList[j]}  Frequency: ${frequencyList[j]}  Size: ${frameLength[j]}"
            )
        }
        Log.d("", "getFrame: ${frameLength.size}")
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
        mediaFormat.setByteBuffer("csd-0", atdsHeader)

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
            atdsHeader = mediaFormat.getByteBuffer("csd-0")!!
            Log.e("TAG", "mimeType: $mimeType, sampleRate: $sampleRate, channelCount: $channelCount, bitRate: $bitRate, atdsHeader: ${atdsHeader.toString()}")

            val a = atdsHeader!![0].toInt()
            val b = atdsHeader!![1].toInt()
            Log.e("TAG", "getConfig: $a  $b")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var count = 0


    private fun decode(buffer: ByteArray) {

        val timeoutUs = 0L

//        val decodeInputBuffers = mDecoder.inputBuffers
//        val decodeOutputBuffers = mDecoder.outputBuffers
        try {
            val inputBufferIndex = mDecoder.dequeueInputBuffer(timeoutUs)
            if (inputBufferIndex >= 0) {
                val newBuffer = mDecoder.getInputBuffer(inputBufferIndex)!!
                newBuffer.clear()
                newBuffer.put(buffer)
                mDecoder.queueInputBuffer(inputBufferIndex, 0, buffer.size, 0, 0)
            }

            val info = MediaCodec.BufferInfo()
            var outputBufferIndex = mDecoder.dequeueOutputBuffer(info, timeoutUs)
            if (outputBufferIndex < 0) {
                count++
//                Log.e("", "解码失败", )
            }
            var outputBuffer: ByteBuffer
            while (outputBufferIndex >= 0) {
                outputBuffer = mDecoder.getOutputBuffer(outputBufferIndex)!!
                val outData = ByteArray(info.size)
                outputBuffer.get(outData)
                outputBuffer.clear()
//                decodedData.add(outData)
                mAudioTrack.write(outData, 0, info.size)
                mDecoder.releaseOutputBuffer(outputBufferIndex, false)
                outputBufferIndex = mDecoder.dequeueOutputBuffer(info, timeoutUs)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}

