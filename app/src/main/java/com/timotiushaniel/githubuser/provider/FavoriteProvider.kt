package com.timotiushaniel.githubuser.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.timotiushaniel.githubuser.db.DatabaseContract.AUTHORITY
import com.timotiushaniel.githubuser.db.DatabaseContract.FavColumns.Companion.CONTENT_URI
import com.timotiushaniel.githubuser.db.DatabaseContract.FavColumns.Companion.TABLE_NAME
import com.timotiushaniel.githubuser.db.FavoriteHelper
import timber.log.Timber

class FavoriteProvider : ContentProvider() {

    companion object {
        private const val FAV = 1
        private const val FAV_ID = 2
        private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        private lateinit var favoriteHelper: FavoriteHelper

        init {
            // content://com.timotiushaniel.githubuser/favorite
            sUriMatcher.addURI(AUTHORITY, TABLE_NAME, FAV)
            // content://com.timotiushaniel.githubuser/favorite/id
            sUriMatcher.addURI(AUTHORITY, "$TABLE_NAME/*", FAV_ID)
        }
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val deleted: Int = when (FAV_ID) {
            sUriMatcher.match(uri) -> favoriteHelper.deleteById(uri.lastPathSegment.toString())
            else -> 0
        }

        context?.contentResolver?.notifyChange(CONTENT_URI, null)
        return deleted
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? {
        val added: Long = when (FAV) {
            sUriMatcher.match(uri) -> favoriteHelper.insert(contentValues)
            else -> 0
        }
        context?.contentResolver?.notifyChange(CONTENT_URI, null)
        return Uri.parse("$CONTENT_URI/$added")
    }

    override fun onCreate(): Boolean {
        favoriteHelper = FavoriteHelper.getInstance(context as Context)
        favoriteHelper.open()
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        Timber.d("Fav: ${uri.lastPathSegment.toString()}")
        return when (sUriMatcher.match(uri)) {
            FAV -> favoriteHelper.queryAll() // get all data
            FAV_ID -> favoriteHelper.queryById(uri.lastPathSegment.toString()) // get data by id
            else -> null
        }
    }

    override fun update(
        uri: Uri, contentValues: ContentValues?, s: String?,
        strings: Array<String>?
    ): Int {
        val updated: Int = when (FAV_ID) {
            sUriMatcher.match(uri) -> favoriteHelper.update(
                uri.lastPathSegment.toString(),
                contentValues
            )
            else -> 0
        }
        context?.contentResolver?.notifyChange(CONTENT_URI, null)
        return updated
    }
}