package com.timotiushaniel.githubuser.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserFollow(
        var username: String,
        var avatar: String,
        var userType: String
) : Parcelable