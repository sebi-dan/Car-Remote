package com.dsedevsco.carremote

import android.R.attr.button
import android.os.Bundle
import android.os.Debug
import android.text.Editable
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.internal.operators.observable.ObservableTimeInterval
import io.reactivex.rxjava3.internal.operators.observable.ObservableTimer
import io.reactivex.rxjava3.kotlin.addTo
import java.io.IOException
import java.lang.Exception
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    lateinit var hornButton : Button
    lateinit var leftSignalButton : ImageButton
    lateinit var rightSignalButton : ImageButton
    lateinit var hazardButton : Button

    var disposables = CompositeDisposable()

    companion object Constants{
        const val LEFT_SIGNAL = "signalLeft%"
        const val RIGHT_SIGNAL = "signalRight%"
        const val HAZARD = "activateHazardLights%"
        const val HORN = "horn%"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hornButton = findViewById(R.id.hornButton)
        leftSignalButton = findViewById(R.id.leftSignalButton)
        rightSignalButton = findViewById(R.id.rightSignalButton)
        hazardButton = findViewById(R.id.hazardButton)

        hornButton.setOnTouchListener{ _, event -> resolveButtonTouch(event, HORN) }
        leftSignalButton.setOnTouchListener{ _, event -> resolveButtonTouch(event, LEFT_SIGNAL) }
        rightSignalButton.setOnTouchListener{ _, event -> resolveButtonTouch(event, RIGHT_SIGNAL) }
        hazardButton.setOnTouchListener{ _, event -> resolveButtonTouch(event, HAZARD) }
    }

    private fun resolveButtonTouch(
        event : MotionEvent,
        commandText: String) : Boolean {
        when(event.action){
            MotionEvent.ACTION_DOWN -> startSendingCommand(commandText)
            MotionEvent.ACTION_UP -> stopSendingCommand()
        }
        return true
    }

    private fun startSendingCommand(commandText: String){
        disposables = CompositeDisposable()
        ObservableTimeInterval.interval(200, TimeUnit.MILLISECONDS)
                .subscribe { _ ->
                    run {
                        sendCommand(commandText)
                    }
                }
                .addTo(disposables)
    }

    private fun stopSendingCommand(){
        if (!disposables.isDisposed){
            disposables.dispose()
        }
    }

    private fun sendCommand(commandText: String){
        try {
            SessionParams.bluetoothSocket
                .outputStream
                .write(commandText.toByteArray());
        } catch (e: IOException) {
            Log.d("SendCommand", e.message!!)
        }
    }
}