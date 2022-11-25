package com.sena.module_two.pcmToMp3

import android.media.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sena.module_two.databinding.ActivityPcmToMp3Binding

class PcmToMp3Activity : AppCompatActivity() {

    private lateinit var binding: ActivityPcmToMp3Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPcmToMp3Binding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.start.setOnClickListener {
            startConvert()
        }

//        binding.stop.setOnClickListener {
//            thread.stopRecord()
//        }
    }



    private fun startConvert() {

        val pcmPath = externalCacheDir?.absolutePath + "/双声道.wav"
        val mp3Path = externalCacheDir?.absolutePath + "/结果.mp3"

        try {
            val mediaExtractor = MediaExtractor()
            mediaExtractor.setDataSource(pcmPath)
            val mediaFormat = mediaExtractor.getTrackFormat(0)
            val sampleRate = mediaFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE)
            val channelCount = mediaFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
//            val bitRate = mediaFormat.getInteger(MediaFormat.KEY_BIT_RATE)

            val convertData = ConvertData(pcmPath, mp3Path, sampleRate, channelCount)
            val thread = PcmToMp3Thread(convertData)
            thread.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}