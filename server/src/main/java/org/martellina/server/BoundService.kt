package org.martellina.server

import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.martellina.aidl.base.AidlException
import org.martellina.aidl.base.AidlResult
import org.martellina.aidl.callback.AsyncCallback
import org.martellina.aidl.ReturningData
import org.martellina.aidl.model.Calc

class BoundService : Service() {

    companion object {
        private const val TAG = "BoundService"
    }

    override fun onBind(intent: Intent?): IBinder {
        return object : ReturningData.Stub() {
            override fun calcPrimitive(first: Int, second: Int, action: Int) : Double {
                return calculateResult(first, second, action)
            }
            override fun calcObject(first: Int, second: Int, action: Int, callback: AsyncCallback?) {
                try {
                    val result = Calc(calculateResult(first, second, action))
                    val aidlResult = AidlResult(result)
                    callback?.onSuccess(aidlResult)
                } catch (e: Throwable) {
                    if (e is RuntimeException) {
                        val aidlException = AidlException(e.message, AidlException.ARITHMETIC_EXCEPTION)
                        callback?.onError(aidlException)
                    }
                }
            }
        }
    }

    private fun calculateResult(first: Int, second: Int, action: Int): Double {
        val result = when (action) {
            0 -> first.toDouble() + second.toDouble()
            1 -> first.toDouble() - second.toDouble()
            2 -> first.toDouble() * second.toDouble()
            3 -> first.toDouble() / second.toDouble()
            else -> 0.00
        }
        return result
    }
}
