package com.timotiushaniel.githubuser.db

import android.net.Uri
import android.provider.BaseColumns

object DatabaseContract {

    const val AUTHORITY = "com.timotiushaniel.githubuser"
    const val SCHEME = "content"

    internal class FavColumns : BaseColumns {
        companion object {
            const val TABLE_NAME = "favorite"
            const val USERNAME = "username"
            const val AVATAR = "avatar"
            const val USER_TYPE = "user_type"
            const val FAVORITE = "isFav"

            // untuk membuat URI content://com.timotiushaniel.githubuser/favorite
            val CONTENT_URI: Uri = Uri.Builder().scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build()
        }
    }
}