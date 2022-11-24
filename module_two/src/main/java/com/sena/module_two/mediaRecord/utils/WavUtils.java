package com.sena.module_two.mediaRecord.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * FileName: WavUtils
 * Author: JiaoCan
 * Date: 2022/2/18 16:18
 */

public class WavUtils {

    /**
     * @param pcmFile
     * @param sampleRate 采样率
     * @param channels   通道数
     * @param bitNum     位深度
     * @return
     */
    public static String covertPcmToWav(File pcmFile, int sampleRate, int channels, int bitNum) {

        FileInputStream fis;
        FileOutputStream fos;
        byte[] buffer = new byte[1024];

        String pcmFilePath = pcmFile.getAbsolutePath();
        String wavePath = pcmFilePath.substring(0, pcmFilePath.lastIndexOf(".")) + "." + "wav";

        try {
            long byteRate = (long) sampleRate * channels * bitNum / 8;

            fis = new FileInputStream(pcmFile);
            fos = new FileOutputStream(wavePath);

            long totalAudioLen = fis.getChannel().size();
            long totalDataLen = totalAudioLen + 36;

            writeWaveFileHeader(fos, totalAudioLen, totalDataLen, sampleRate, channels, byteRate);

            int len;
            while ((len = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }

            fos.close();
            fis.close();

        } catch (Exception e) {
            return null;
        }
        return wavePath;
    }


    private static void writeWaveFileHeader(FileOutputStream fos, long totalAudioLen, long totalDataLen,
                                     int sampleRate, int channels, long byteRate) throws IOException {

        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);//数据大小
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';//WAVE
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        //FMT Chunk
        header[12] = 'f'; // 'fmt '
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';//过渡字节
        //数据大小
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        //编码方式 10H为PCM编码格式
        header[20] = 1; // format = 1
        header[21] = 0;
        //通道数
        header[22] = (byte) channels;
        header[23] = 0;
        //采样率，每个通道的播放速度
        header[24] = (byte) (sampleRate & 0xff);
        header[25] = (byte) ((sampleRate >> 8) & 0xff);
        header[26] = (byte) ((sampleRate >> 16) & 0xff);
        header[27] = (byte) ((sampleRate >> 24) & 0xff);
        //音频数据传送速率,采样率*通道数*采样深度/8
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        // 确定系统一次要处理多少个这样字节的数据，确定缓冲区，通道数*采样位数
        header[32] = (byte) (channels * 16 / 8);
        header[33] = 0;
        //每个样本的数据位数
        header[34] = 16;
        header[35] = 0;
        //Data chunk
        header[36] = 'd';//data
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        fos.write(header, 0, 44);

    }


}
