package com.timotiushaniel.consumerapp.helper

import android.database.Cursor
import com.timotiushaniel.consumerapp.db.DatabaseContract
import com.timotiushaniel.consumerapp.model.User

object MappingHelper {

    fun mapCursorToArrayList(notesCursor: Cursor?): ArrayList<User> {
        val listUser = ArrayList<User>()

        notesCursor?.apply {
            while (moveToNext()) {
                val username =
                    getString(getColumnIndexOrThrow(DatabaseContract.FavColumns.USERNAME))
                val avatar = getString(getColumnIndexOrThrow(DatabaseContract.FavColumns.AVATAR))
                val userType =
                    getString(getColumnIndexOrThrow(DatabaseContract.FavColumns.USER_TYPE))
                listUser.add(User(username, avatar, userType))
            }
        }
        return listUser
    }
}