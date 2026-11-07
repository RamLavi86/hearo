package com.hearo.app

import android.content.Context
import android.media.AudioDeviceCallback
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build

enum class HeadphoneKind { NONE, WIRED, USB, BLUETOOTH }

/** Detects whether headphones are the active audio output. Bluetooth is flagged: lower confidence. */
class HeadphoneDetector(context: Context) {
    private val audioManager = context.getSystemService(AudioManager::class.java)

    fun current(): HeadphoneKind {
        val kinds = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS).map { kindOf(it.type) }
        // Prefer the most trustworthy connection if several are present.
        return listOf(HeadphoneKind.WIRED, HeadphoneKind.USB, HeadphoneKind.BLUETOOTH)
            .firstOrNull { it in kinds } ?: HeadphoneKind.NONE
    }

    private fun kindOf(type: Int): HeadphoneKind = when (type) {
        AudioDeviceInfo.TYPE_WIRED_HEADPHONES, AudioDeviceInfo.TYPE_WIRED_HEADSET -> HeadphoneKind.WIRED
        AudioDeviceInfo.TYPE_USB_HEADSET -> HeadphoneKind.USB
        AudioDeviceInfo.TYPE_BLUETOOTH_A2DP, AudioDeviceInfo.TYPE_BLUETOOTH_SCO -> HeadphoneKind.BLUETOOTH
        else -> if (Build.VERSION.SDK_INT >= 31 && type == AudioDeviceInfo.TYPE_BLE_HEADSET) {
            HeadphoneKind.BLUETOOTH
        } else HeadphoneKind.NONE
    }

    fun register(onChange: () -> Unit): AudioDeviceCallback {
        val cb = object : AudioDeviceCallback() {
            override fun onAudioDevicesAdded(added: Array<out AudioDeviceInfo>?) = onChange()
            override fun onAudioDevicesRemoved(removed: Array<out AudioDeviceInfo>?) = onChange()
        }
        audioManager.registerAudioDeviceCallback(cb, null)
        return cb
    }

    fun unregister(cb: AudioDeviceCallback) = audioManager.unregisterAudioDeviceCallback(cb)
}
