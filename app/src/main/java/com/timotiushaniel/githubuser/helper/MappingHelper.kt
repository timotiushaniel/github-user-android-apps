package com.timotiushaniel.githubuser.helper

import android.database.Cursor
import com.timotiushaniel.githubuser.db.DatabaseContract
import com.timotiushaniel.githubuser.model.User

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

    fun mapCursorToObject(favoriteCursor: Cursor?): User? {
        var user: User? = null
        favoriteCursor?.apply {
            if (!moveToFirst()) {
                moveToFirst()
            }
            if (count > 0) {
                val username =
                    getString(getColumnIndexOrThrow(DatabaseContract.FavColumns.USERNAME))
                val avatar = getString(getColumnIndexOrThrow(DatabaseContract.FavColumns.AVATAR))
                val userType =
                    getString(getColumnIndexOrThrow(DatabaseContract.FavColumns.USER_TYPE))
                if (!username.isNullOrEmpty() || !avatar.isNullOrEmpty() || !userType.isNullOrEmpty()) {
                    user = User(username, avatar, userType)
                }
            }
        }
        return user
    }
}