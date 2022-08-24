package org.martellina.aidl.base

import android.os.Parcelable
import java.lang.RuntimeException
import kotlinx.parcelize.Parcelize
import java.lang.NullPointerException
import java.lang.NumberFormatException

@Parcelize
class AidlException(
    private val errorMessage: String?,
    private val errorCode: Int = RUNTIME_EXCEPTION
) : Parcelable {

    companion object {
        const val RUNTIME_EXCEPTION = 1000
        const val ARITHMETIC_EXCEPTION = 1001
        const val NULL_POINTER_EXCEPTION = 1002
        const val NUMBER_FORMAT_EXCEPTION = 1003
    }

    fun toException(): Exception {
        return when (errorCode) {
            RUNTIME_EXCEPTION -> RuntimeException(errorMessage)
            ARITHMETIC_EXCEPTION -> ArithmeticException(errorMessage)
            NULL_POINTER_EXCEPTION -> NullPointerException(errorMessage)
            NUMBER_FORMAT_EXCEPTION -> NumberFormatException(errorMessage)
            else -> RuntimeException(errorMessage)
        }
    }
}
