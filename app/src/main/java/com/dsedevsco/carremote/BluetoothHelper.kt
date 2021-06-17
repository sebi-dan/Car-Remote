package com.dsedevsco.carremote

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.internal.operators.single.SingleCreate

class BluetoothHelper {

    fun connect() : Single<BluetoothSocket> =
        SingleCreate {
            try {
                val btAdapter = BluetoothAdapter.getDefaultAdapter()
                if (btAdapter != null
                    && btAdapter.isEnabled){
                    val btDevice = btAdapter.getRemoteDevice("98:D3:11:FC:61:0B")
                    if (btDevice != null) {
                        val socket = btDevice.javaClass.getMethod(
                            "createRfcommSocket", *arrayOf<Class<*>?>(
                                Int::class.javaPrimitiveType
                            )
                        ).invoke(btDevice, 1) as BluetoothSocket
                        socket.connect()
                        btAdapter.cancelDiscovery()

                        it.onSuccess(socket)
                    }else{
                        it.onError(Exception("Device not connected!"))
                    }
                }else{
                    it.onError(Exception("Bluetooth adapter not enabled!"))
                }
            }catch (e: Exception){
                it.onError(e)
            }
    }
}