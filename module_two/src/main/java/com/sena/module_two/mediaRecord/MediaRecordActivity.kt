package com.sena.module_two.mediaRecord

import android.Manifest
import android.media.MediaRecorder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.sena.module_two.databinding.ActivityMediaRecordBinding
import com.sena.module_two.mediaRecord.aacDecode.AacDecoder
import com.sena.module_two.mediaRecord.utils.AACDecoder
import com.sena.module_two.mediaRecord.utils.Mp3Information
import java.io.File

class MediaRecordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaRecordBinding

    private var recorder: MediaRecorder? = null

    private lateinit var getPermission: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initActivityResult()
    }

    private fun initView() {
        binding.startRecord.setOnClickListener {
            startRecordAudio()
        }
        binding.stopRecord.setOnClickListener {
            stopRecordAudio()
        }

    }

    private fun initActivityResult() {
        getPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            Toast.makeText(this, "申请${if (it) "成功" else "失败"}!!", Toast.LENGTH_SHORT).show()
        }
        getPermission.launch(Manifest.permission.RECORD_AUDIO)
    }

    private fun startRecordAudio() {
        if (recorder == null) {
            recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(this)
            } else {
                MediaRecorder()
            }
        }

        recorder?.also {
            it.setAudioSource(MediaRecorder.AudioSource.MIC)
            it.setOutputFormat(MediaRecorder.OutputFormat.MPEG_2_TS)
            it.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB)
            it.setOutputFile(File(externalCacheDir, "Test.aac"))

            try {
                it.prepare()
                it.start()
                Toast.makeText(this, "开始录音!!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun stopRecordAudio() {

        val audioPath = externalCacheDir?.absolutePath + "/Record03.aac"
//        AacTest.test(externalCacheDir?.absolutePath + "/Test.aac")
//        AACDecoder.test(externalCacheDir?.absolutePath + "/Record03.aac")
//        AacDecoder(audioPath).start()
        Mp3Information(externalCacheDir?.absolutePath + "/emoshinobi - summer nite【sold】.mp3")

//        val track = ReadAACFileThread(externalCacheDir?.absolutePath + "/Test.aac")

//
//        recorder?.also {
//            try {
//                it.stop()
//                it.reset()
//                it.release()
//                Toast.makeText(this, "停止录音!!", Toast.LENGTH_SHORT).show()
////
////                val time = measureTimeMillis {
////                    AndroidAudioConverter.with(this)
////                        .setFile(File(externalCacheDir, "Test.aac"))
////                        .setFormat(AudioFormat.WAV)
////                        .convert()
////                }
////                Log.e("TAG", "stopRecordAudio: $time", )
//
//
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }

//        }
    }
}