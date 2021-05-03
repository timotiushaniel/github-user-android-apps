package com.timotiushaniel.githubuser.helper

import com.timotiushaniel.githubuser.db.DatabaseContract

object Constant {
    const val USER_DETAIL = "user_detail"
    const val ARG_USERNAME = "username"
    const val REMINDER_CHANNEL_ID = "alarm"
    const val REMINDER_CHANNEL_NAME = "daily notification"
    const val NOTIFICATION_ID = 100
    const val ALARM_TITLE = "Daily Reminder"
    const val PREFS_NAME = "SettingPref"
    const val DAILY = "daily"
    const val EXTRA_MESSAGE = "message"
    const val EXTRA_TYPE = "type"
    const val TIME_SCHEDULE = "09:00"

    const val DATABASE_NAME = "favoriteUser"
    const val DATABASE_VERSION = 1
    const val SQL_CREATE_TABLE_NOTE = "CREATE TABLE ${DatabaseContract.FavColumns.TABLE_NAME}" +
            " (${DatabaseContract.FavColumns.USERNAME} TEXT PRIMARY KEY  NOT NULL," +
            " ${DatabaseContract.FavColumns.AVATAR} TEXT NOT NULL," +
            " ${DatabaseContract.FavColumns.USER_TYPE} TEXT NOT NULL," +
            " ${DatabaseContract.FavColumns.FAVORITE} TEXT NOT NULL)"
}