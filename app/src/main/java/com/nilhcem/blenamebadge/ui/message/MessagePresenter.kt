package com.nilhcem.blenamebadge.ui.message

import android.content.Context
import com.nilhcem.blenamebadge.core.android.log.Timber
import com.nilhcem.blenamebadge.core.utils.ByteArrayUtils
import com.nilhcem.blenamebadge.device.DataToByteArrayConverter
import com.nilhcem.blenamebadge.device.bluetooth.GattClient
import com.nilhcem.blenamebadge.device.bluetooth.ScanHelper
import com.nilhcem.blenamebadge.device.model.DataToSend

class MessagePresenter {

    private val scanHelper = ScanHelper()
    private val gattClient = GattClient()

    fun sendMessage(context: Context, dataToSend: DataToSend) {
        Timber.i { "About to send data: $dataToSend" }
        val byteData = DataToByteArrayConverter.convert(dataToSend)
        Timber.i { "ByteData: ${byteData.map { ByteArrayUtils.byteArrayToHexString(it) }}" }

        scanHelper.startLeScan { device ->
            if (device == null) {
                Timber.e { "Scan could not find any device" }
            } else {
                Timber.e { "Device found: $device" }

                gattClient.startClient(context, device.address) { onConnected ->
                    if (onConnected) {
                        gattClient.writeDataStart(byteData) {
                            Timber.i { "Data sent" }
                            gattClient.stopClient()
                        }
                    }
                }
            }
        }
    }

    fun onPause() {
        scanHelper.stopLeScan()
        gattClient.stopClient()
    }
}
