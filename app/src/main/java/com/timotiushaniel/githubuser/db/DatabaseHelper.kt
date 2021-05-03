package com.timotiushaniel.githubuser.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.timotiushaniel.githubuser.db.DatabaseContract.FavColumns.Companion.TABLE_NAME
import com.timotiushaniel.githubuser.helper.Constant

internal class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, Constant.DATABASE_NAME, null, Constant.DATABASE_VERSION) {

    // create the SQLite database table
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(Constant.SQL_CREATE_TABLE_NOTE)
    }

    // if the table of database is exist, this function will delete it
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
}