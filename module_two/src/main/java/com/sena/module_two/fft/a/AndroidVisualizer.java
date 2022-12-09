package com.sena.module_two.fft.a;

/**
 * FileName: AndroidVisualizer
 * Author: JiaoCan
 * Date: 2022/12/9 16:59
 */

public class AndroidVisualizer {

    private static int mCaptureSize = 1024;


    public static byte[] doFft(byte[] waveform) {
        byte[] fft = new byte[mCaptureSize];

        int[] workspace = new int[mCaptureSize >> 1];
        int nonzero = 0;

        for (int i = 0; i < mCaptureSize; i += 2) {
            workspace[i >> 1] =
                    ((waveform[i] ^ 0x80) << 24) | ((waveform[i + 1] ^ 0x80) << 8);
            nonzero |= workspace[i >> 1];
        }

        if (nonzero != 0) {
            workspace = AndroidFftJava.fixed_fft_real(mCaptureSize >> 1, workspace);
        }

        for (int i = 0; i < mCaptureSize; i += 2) {
            short tmp = (short) (workspace[i >> 1] >> 21);
            while (tmp > 127 || tmp < -128) tmp >>= 1;
            fft[i] = (byte) tmp;
            tmp = (short) workspace[i >> 1];
            tmp >>= 5;
            while (tmp > 127 || tmp < -128) tmp >>= 1;
            fft[i + 1] = (byte) tmp;
        }

        return fft;
    }

}
