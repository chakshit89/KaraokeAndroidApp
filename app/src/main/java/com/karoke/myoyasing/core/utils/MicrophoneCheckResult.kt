
package com.karoke.myoyasing.core.utils

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build

data class MicrophoneCheckResult(
    val isExternalMicrophoneConnected: Boolean,
    val deviceName: String?,
    val deviceType: Int? = null
)

object MicrophoneDetector {

    fun checkConnectedMicrophone(
        context: Context
    ): MicrophoneCheckResult {

        val audioManager = context.getSystemService(
            Context.AUDIO_SERVICE
        ) as AudioManager

        val inputDevices = audioManager.getDevices(
            AudioManager.GET_DEVICES_INPUTS
        )

        val externalMicrophone = inputDevices.firstOrNull { device ->

            if (!device.isSource) {
                return@firstOrNull false
            }

            when (device.type) {

                AudioDeviceInfo.TYPE_WIRED_HEADSET,
                AudioDeviceInfo.TYPE_USB_DEVICE,
                AudioDeviceInfo.TYPE_USB_HEADSET,
                AudioDeviceInfo.TYPE_BLUETOOTH_SCO,
                AudioDeviceInfo.TYPE_LINE_ANALOG,
                AudioDeviceInfo.TYPE_LINE_DIGITAL -> true

                else -> {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                            device.type == AudioDeviceInfo.TYPE_BLE_HEADSET
                }
            }
        }

        return MicrophoneCheckResult(
            isExternalMicrophoneConnected =
                externalMicrophone != null,

            deviceName = externalMicrophone
                ?.productName
                ?.toString(),

            deviceType = externalMicrophone?.type
        )
    }
}
