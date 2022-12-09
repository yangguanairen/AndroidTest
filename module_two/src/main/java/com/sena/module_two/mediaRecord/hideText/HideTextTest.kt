package com.sena.module_two.mediaRecord.hideText

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.charset.Charset


/**
 * FileName: HideTextTest
 * Author: JiaoCan
 * Date: 2022/12/2 16:16
 */

class HideTextTest(
    private val context: Context,
    private val aacPath: String,
    text: String,
    private val isEncrypt: Boolean = true
) {

    private var inputStream: BufferedInputStream
    private val outputStream: FileOutputStream
    private val textByteArray: ByteArray

    private var readLen = 0
    private val frameSizeList = arrayListOf<Int>()

    init {
        inputStream= BufferedInputStream(FileInputStream(aacPath))
        outputStream = FileOutputStream(File(context.externalCacheDir, "hideText.aac"))
        textByteArray = text.toByteArray(Charset.defaultCharset())
    }

    private fun getFrame() {
        inputStream.mark(readLen)
        var len: Int
        val buffer = ByteArray(1024)
        while (inputStream.read(buffer).also { len = it } > 0) {

            for (i in 0 until len) {
                if (buffer[0].toInt() == 0xFF && (buffer[1].toInt() and 0xF0) == 0xF0) {
                    // 找到帧头, 计算帧长
                    var size = 0
                    size = size or ((buffer[i + 3].toInt() and 0x03) shl 11)
                    size = size or (buffer[i + 4].toInt() shl 3)
                    size = size or ((buffer[i + 5].toInt() and 0xE0) shr 5)

                    inputStream.reset()
                    frameSizeList.add(size)
                    readLen += size
                    inputStream.mark(readLen)

                    break
                }
            }
        }

        inputStream.close()
    }

    private fun encrypt() {
        if (frameSizeList.size < textByteArray.size) {
            throw Exception("加密文本过长或音频过短!!")
        }

        inputStream = BufferedInputStream(FileInputStream(aacPath))
        val lenByte = textByteArray.size


        frameSizeList.forEachIndexed { i, size ->
            val buffer = ByteArray(size)
            if (i < textByteArray.size) {
                val b = textByteArray[i]

            }
        }

    }

    private fun decrypt() {


        val tmp = ByteArray(frameSizeList.size)
        val buffer = ByteArray(1)
        for (i in 0 until frameSizeList.size) {
            val offset = if (i == 0) 7 else frameSizeList[i - 1] - 1
            inputStream.read(buffer, offset, 1)
            tmp[i] = buffer[0]
        }

        inputStream.close()

        val encryptText = String(tmp)

        Handler(context.mainLooper).post {
            Toast.makeText(context, "解密: $encryptText", Toast.LENGTH_SHORT).show()
            Log.e("HideText", "encrypt: $encryptText")
        }

    }

    private fun skip(len: Int) {
        val skipedSize = inputStream.skip(len.toLong()).toInt()
        var surplusSize = len - skipedSize

        if (surplusSize < 1024) {
            inputStream.read(ByteArray(surplusSize))
            return
        }

        val buffer = ByteArray(1024)
        while (surplusSize > 1024) {
            inputStream.read(buffer)
            surplusSize -= 1024
        }
        inputStream.read(ByteArray(surplusSize))

    }
}

