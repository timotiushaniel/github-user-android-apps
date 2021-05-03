package com.timotiushaniel.consumerapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val username: String,
    val avatar: String,
    val userType: String
) : Parcelable