package org.martellina.aidl.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Calc(val calc: Double) : Parcelable
