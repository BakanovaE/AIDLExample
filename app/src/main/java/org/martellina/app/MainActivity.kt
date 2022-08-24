package org.martellina.app

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import org.martellina.aidl.ReturningData
import org.martellina.aidl.base.AidlException
import org.martellina.aidl.base.AidlResult
import org.martellina.aidl.callback.AsyncCallback
import org.martellina.aidl.model.Calc
import java.lang.RuntimeException

class MainActivity : AppCompatActivity() {

    companion object {
        private const val APP_SERVER_PACKAGE = "org.martellina.server"
        private const val APP_SERVER_ACTION = "org.martellina.aidl.REMOTE_CONNECTION"
    }

    private var returningData: ReturningData? = null

    private lateinit var buttonPrimitive: Button
    private lateinit var buttonObject: Button
    private lateinit var firstNumber: EditText
    private lateinit var secondNumber: EditText
    private lateinit var action: Spinner

    private val appUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.data?.schemeSpecificPart == APP_SERVER_PACKAGE) {
                unregisterReceiver(this)
                reconnect()
            }
        }
    }
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            returningData = ReturningData.Stub.asInterface(service)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            returningData = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonPrimitive = findViewById(R.id.button_primitive)
        buttonObject = findViewById(R.id.button_object)
        firstNumber = findViewById(R.id.first_number)
        secondNumber = findViewById(R.id.second_number)
        action = findViewById(R.id.action)

        buttonPrimitive.setOnClickListener {
            val calcAction = returnAction()
            returningData?.calcPrimitive(
                firstNumber.text.toString().toInt(),
                secondNumber.text.toString().toInt(),
                calcAction
            )
            Toast.makeText(
                this@MainActivity, returningData?.calcPrimitive(
                    firstNumber.text.toString().toInt(),
                    secondNumber.text.toString().toInt(),
                    calcAction
                ).toString(), Toast.LENGTH_SHORT
            )
                .show()
        }

        buttonObject.setOnClickListener {
            val calcAction = returnAction()
            returningData?.calcObject(firstNumber.text.toString().toInt(),
                secondNumber.text.toString().toInt(),
                calcAction,
                object : AsyncCallback.Stub() {
                    override fun onSuccess(aidlResult: AidlResult<*>?) {
                        val result = aidlResult?.data as? Calc
                        Toast.makeText(this@MainActivity, result.toString(), Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(aidlException: AidlException?) {
                        val exception = aidlException?.toException()
                        Toast.makeText(this@MainActivity, exception?.message, Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private fun returnAction() : Int {
        return when (action.selectedItem.toString()) {
            Actions.SUM.name -> Actions.SUM.id
            Actions.SUB.name -> Actions.SUB.id
            Actions.MUL.name -> Actions.MUL.id
            Actions.DIV.name -> Actions.DIV.id
            else -> throw RuntimeException("No such action found")
        }
    }

    override fun onStart() {
        super.onStart()
        reconnect()
    }

    override fun onStop() {
        super.onStop()
        disconnect()
    }

    private fun reconnect() {
        bindService(createExplicitIntent(), serviceConnection, Context.BIND_AUTO_CREATE)
        registerReceiver(appUpdateReceiver, IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addDataScheme("package")
        })
    }

    private fun disconnect() {
        unregisterReceiver(appUpdateReceiver)
        unbindService(serviceConnection)
    }

    private fun createExplicitIntent(): Intent {
        val intent = Intent(APP_SERVER_ACTION)
        val services = packageManager.queryIntentServices(intent, 0)
        if (services.isEmpty()) {
            throw IllegalStateException("Server was not found")
        }
        return Intent(intent).apply {
            val resolveInfo = services[0]
            val packageName = resolveInfo.serviceInfo.packageName
            val className = resolveInfo.serviceInfo.name
            component = ComponentName(packageName, className)
        }
    }
}
