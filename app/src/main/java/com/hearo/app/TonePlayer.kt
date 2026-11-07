package com.hearo.app

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.hearo.core.Ear
import com.hearo.core.ToneGenerator

/** Plays a tone to one ear only (float stereo, the other channel silent). */
class TonePlayer(private val sampleRate: Int = 48_000) {

    fun play(ear: Ear, freqHz: Int, levelDbFs: Double, durationMs: Int = 1000) {
        val mono = ToneGenerator.generate(freqHz, durationMs, levelDbFs, sampleRate)
        val stereo = FloatArray(mono.size * 2)
        val channel = if (ear == Ear.LEFT) 0 else 1
        for (i in mono.indices) stereo[2 * i + channel] = mono[i]

        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_FLOAT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                    .build()
            )
            .setBufferSizeInBytes(stereo.size * Float.SIZE_BYTES)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()
        track.write(stereo, 0, stereo.size, AudioTrack.WRITE_BLOCKING)
        track.play()
        Thread {
            Thread.sleep(durationMs + 150L)
            track.release()
        }.start()
    }
}
