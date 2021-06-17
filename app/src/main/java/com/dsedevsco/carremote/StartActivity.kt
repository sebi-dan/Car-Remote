package com.dsedevsco.carremote

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class StartActivity : AppCompatActivity() {

    private lateinit var loadingSpinner : ProgressBar
    private lateinit var retryConnectionBtn : Button;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        loadingSpinner = findViewById(R.id.loadingSpinner)
        retryConnectionBtn = findViewById(R.id.retryConnectionBtn)
        retryConnectionBtn.setOnClickListener {
            prepareDeviceConnection()
        }

        prepareDeviceConnection()
    }

    private fun prepareDeviceConnection(){
        loadingSpinner.visibility = View.VISIBLE
        retryConnectionBtn.visibility = View.INVISIBLE
        BluetoothHelper()
            .connect()
            .observeOn(AndroidSchedulers.mainThread())
            .delaySubscription(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .subscribe(
                { socket ->
                    run {
                        SessionParams.bluetoothSocket = socket
                        navigateToMainScreen()
                    }
                },
                { exception ->
                    run {
                        Toast.makeText(
                            this,
                            exception.message!!,
                            Toast.LENGTH_SHORT
                        ).show()
                        loadingSpinner.visibility = View.INVISIBLE
                        retryConnectionBtn.visibility = View.VISIBLE
                    }
                }
            );
    }

    private fun navigateToMainScreen(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}