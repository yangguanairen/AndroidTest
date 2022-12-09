package com.sena.module_two.fft

import android.media.*
import android.media.audiofx.Visualizer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.gson.Gson
import com.sena.module_two.databinding.ActivityFftactivityBinding
import com.sena.module_two.fft.a.AndroidVisualizer
import com.sena.module_two.fft.a.Test
import java.io.File
import java.io.FileInputStream
import kotlin.math.abs
import kotlin.math.hypot


/**
 * Android 傅里叶变换
 * https://blog.csdn.net/weixin_39619478/article/details/113043189
 *
 *
 * Android Visualizer
 * https://blog.csdn.net/weixin_43880692/article/details/124626163
 * http://events.jianshu.io/p/c95bb166fb28
 */

class FFTActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFftactivityBinding

    private lateinit var mMediaPlayer: MediaPlayer
    private lateinit var mVisualizer: Visualizer

    private var bufferSize: Int = 0
    private lateinit var mAudioTrack: AudioTrack

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFftactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        initView()

       initAudioTrack()
        Thread{
            readStream()
        }.start()
    }

    private fun initAudioTrack() {
        bufferSize = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT)
        mAudioTrack = AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM)
        mAudioTrack.play()

    }

    private fun readStream() {
        val inputStream = FileInputStream(File(externalCacheDir, "双声道.wav"))
        var len: Int
        val buffer = ByteArray(1024)
        while ((inputStream.read(buffer)).also { len = it } > 0) {


            for (i in 0 until buffer.size) {
                if (buffer[i] > 1024) {
                    buffer[i] = 1024.toByte()
                }
            }

//            val model = FFT.fft(byteToDouble(buffer), 0)

//            val model = Test().test(buffer)

            val fft = AndroidVisualizer.doFft(buffer)
            val model = FloatArray(fft.size / 2 + 1)
            model[0] = abs(fft[1].toFloat())

            for (i in 2 until fft.size / 2 step 2) {
                model[i / 2] = abs(hypot(fft[i].toFloat(), fft[i + 1].toFloat()))
            }

            val convertModel = FloatArray(60)
            for (i in 0 until 60) {
                convertModel[i] = model[i]
            }

            Handler(Looper.getMainLooper()).postDelayed({
                binding.visualizeView.onWrite(convertModel)
            }, 0)

            mAudioTrack.write(buffer, 0, len)
        }
    }

    private fun byteToDouble(pcmData: ByteArray): DoubleArray {
        val array = DoubleArray(pcmData.size / 2)
        for (i in 0 until pcmData.size / 2) {
            val l = pcmData[i * 2].toInt()
            val h = pcmData[i * 2 + 1].toInt() shl 8
            val d = (l or h).toDouble()
            array[i] = d
        }
        return array
    }











    private fun initView() {

        val uri = Uri.parse(externalCacheDir?.absolutePath + "/双声道.wav")
        mMediaPlayer = MediaPlayer.create(this, uri)
        mMediaPlayer.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )
        mMediaPlayer.setOnPreparedListener {

            it.isLooping = true


        }
        mMediaPlayer.stop()
        mMediaPlayer.prepare()
        mMediaPlayer.start()
        val audioSessionId = mMediaPlayer.audioSessionId
        createVisualizer(audioSessionId)
    }

    private fun createVisualizer(id: Int) {
        mVisualizer = Visualizer(id)
        mVisualizer.captureSize = Visualizer.getCaptureSizeRange()[1]

        println("最小采集范围: ${Visualizer.getCaptureSizeRange()[0]}")

        mVisualizer.setDataCaptureListener(object : Visualizer.OnDataCaptureListener {
            override fun onWaveFormDataCapture(visualizer: Visualizer?, waveform: ByteArray?, samplingRate: Int) {
            }

            override fun onFftDataCapture(visualizer: Visualizer, fft: ByteArray, samplingRate: Int) {
                val frequencyEach = samplingRate * 2 / visualizer.captureSize

                val maxHz = samplingRate / 2
//                println("最大频率: $maxHz")

                val model = FloatArray(fft.size / 2 + 1)
                model[0] = abs(fft[1].toFloat())

                for (i in 2 until fft.size / 2 step 2) {
                    model[i / 2] = abs(hypot(fft[i].toFloat(), fft[i + 1].toFloat()))
                }


                var count = model.filter { it == 0.0f }.size
//                println("无效数据: $count")


                model.forEach {
                    if (it > 22.05) {
                        println("超出数据: $it")
                    }
                }
//                println(Gson().toJson(model))
                val convertModel = FloatArray(60)
                for (i in 0 until 60) {
                    convertModel[i] = model[i]
                }
                binding.visualizeView.onWrite(convertModel)
            }

        }, Visualizer.getMaxCaptureRate() / 2, false, true)
        mVisualizer.enabled = true
    }

    override fun onDestroy() {
        super.onDestroy()

        mVisualizer.release()
        mMediaPlayer.stop()
        mMediaPlayer.release()

    }
}