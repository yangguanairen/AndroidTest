package com.sena.module_two.mediaRecord.aacDecode

import android.media.*
import android.util.Log
import com.sena.module_two.mediaRecord.utils.AACDecoder
import java.io.File
import java.nio.ByteBuffer


/**
 * FileName: AacDeocder
 * Author: JiaoCan
 * Date: 2022/11/21 17:43
 */

class AacDecoder(audioPath: String) {

    private val mAudioPath: String

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

    init {
        try {
            File(audioPath)
        } catch (e: Exception) {
            throw Exception("无效路径或者文件不存在!!")
        }
        mAudioPath = audioPath
    }

    fun start() {
        getConfig()
        createDecoder()
        createTrack()

        DecodeThread(mAudioPath, mDecoder, mAudioTrack).start()
    }


    private fun getConfig() {
        try {
            val mediaExtractor = MediaExtractor()
            mediaExtractor.setDataSource(mAudioPath)
            val mediaFormat = mediaExtractor.getTrackFormat(0)
            val mimeType = mediaFormat.getString(MediaFormat.KEY_MIME)
            sampleRate = mediaFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE)
            channelCount = mediaFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
            bitRate = mediaFormat.getInteger(MediaFormat.KEY_BIT_RATE)
            adtsHeader = mediaFormat.getByteBuffer("csd-0")!!
            Log.e("AACDecoder.getConfig: ",
                "mimeType: $mimeType, \n" +
                        "sampleRate: ${sampleRate}, \n" +
                        "channelCount: ${channelCount}, \n" +
                        "bitRate: ${bitRate}, \n" +
                        "atdsHeader: [${adtsHeader[0].toInt()}, ${adtsHeader[1].toInt()}]")
        } catch (e: Exception) {
            e.printStackTrace()
        }
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

    private fun createTrack() {
        val bufferSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, AudioFormat.ENCODING_PCM_16BIT)
        mAudioTrack = AudioTrack(
            AudioManager.STREAM_MUSIC, sampleRate, channelConfig, AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM
        )
        mAudioTrack.play()
    }

    private fun stop() {
        try {
            mDecoder.stop()
            mDecoder.release()
            mAudioTrack.stop()
            mAudioTrack.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

