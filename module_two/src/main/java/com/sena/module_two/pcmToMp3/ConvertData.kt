package com.sena.module_two.pcmToMp3

import android.media.AudioFormat


/**
 * FileName: ConvertData
 * Author: JiaoCan
 * Date: 2022/11/25 17:39
 */

data class ConvertData(
    val pcmPath: String,
    val mp3Path: String,
    val sampleRate: Int = 44100,
    val channelCount: Int = 2,
    val bitRat: Int = 16,
    val audioFormat: Int = AudioFormat.ENCODING_PCM_16BIT
) {
    val channelConfig = if (channelCount == 2) AudioFormat.CHANNEL_IN_STEREO else AudioFormat.CHANNEL_IN_MONO
}

