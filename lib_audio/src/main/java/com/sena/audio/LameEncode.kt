package com.sena.audio

object LameEncode {

    /**
     * A native method that is implemented by the 'audio' native library,
     * which is packaged with this application.
     */

        // Used to load the 'audio' library on application startup.
        init {
            System.loadLibrary("lame-lib")
        }

    /**
     * 初始化lame,cpp中初始化采用lame默认参数配置
     *
     * @param sampleRate     ：采样率 -- 录音默认44100
     * @param channelCount   ：通道数 -- 录音默认双通道2
     * @param audioFormatBit ：位宽 -- 录音默认ENCODING_PCM_16BIT 16bit
     * @param quality        ：MP3音频质量 0~9 其中0是最好，非常慢，9是最差  2=high(高)  5 = medium(中)  7=low(低)
     */
    external fun init(sampleRate: Int, channelCount: Int, audioFormatBit: Int, quality: Int)

    /**
     * 启用lame编码
     *
     * @param pcmBuffer  ：音频数据源
     * @param mp3_buffer ：写入MP3数据buffer
     * @param sample_num ：采样个数
     */
    external fun encoder(pcmBuffer: ShortArray, mp3_buffer: ByteArray, sample_num: Int): Int

    /**
     * 刷新缓冲器
     *
     * @param mp3_buffer ：MP3编码buffer
     * @return int 返回剩余编码器字节数据,需要写入文件
     */
    external fun flush(mp3_buffer: ByteArray): Int

    external fun writeTag(path: String)

    /**
     * 释放编码器
     */
    external fun close(): String

}