package com.timotiushaniel.githubuser.view

import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.timotiushaniel.githubuser.R
import com.timotiushaniel.githubuser.adapter.SectionsPageAdapter
import com.timotiushaniel.githubuser.databinding.ActivityUserDetailBinding
import com.timotiushaniel.githubuser.db.DatabaseContract.FavColumns.Companion.AVATAR
import com.timotiushaniel.githubuser.db.DatabaseContract.FavColumns.Companion.CONTENT_URI
import com.timotiushaniel.githubuser.db.DatabaseContract.FavColumns.Companion.FAVORITE
import com.timotiushaniel.githubuser.db.DatabaseContract.FavColumns.Companion.USERNAME
import com.timotiushaniel.githubuser.db.DatabaseContract.FavColumns.Companion.USER_TYPE
import com.timotiushaniel.githubuser.db.FavoriteHelper
import com.timotiushaniel.githubuser.helper.Constant
import com.timotiushaniel.githubuser.helper.MappingHelper
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_user_detail.*
import kotlinx.android.synthetic.main.item_row_userlist.*
import org.json.JSONObject
import timber.log.Timber

class UserDetailActivity : AppCompatActivity(), View.OnClickListener {

    private var userName: String? = null
    private lateinit var binding: ActivityUserDetailBinding
    private lateinit var favoriteHelper: FavoriteHelper
    private var isFavorite = false
    private lateinit var imageAvatar: String
    private lateinit var uriWithId: Uri

    private val favoriteButton: Int = R.drawable.ic_thumb_up
    private val notFavoriteButton: Int = R.drawable.ic_thumb_down

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.followers,
            R.string.following
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userName = intent.getStringExtra(Constant.USER_DETAIL)
        getUserData()
        checkIsFavorite()
        sectionsPageAdapterConf()

        fab.setOnClickListener(this@UserDetailActivity)

        favoriteHelper = FavoriteHelper.getInstance(applicationContext)
        favoriteHelper.open()
    }

    private fun checkIsFavorite() {
        uriWithId = Uri.parse("$CONTENT_URI/${userName.toString()}")
        Timber.d("Uri: $uriWithId")

        val cursor = contentResolver.query(uriWithId, null, null, null, null)
        Timber.d("Cursor: $cursor")
        if (cursor != null) {
            val user = MappingHelper.mapCursorToObject(cursor)
            isFavorite = if (user != null) {
                fab.setImageResource(notFavoriteButton)
                true
            } else {
                fab.setImageResource(favoriteButton)
                false
            }
            cursor.close()
        }
    }

    private fun getUserData() {
        showProgressBar()
        val asyncHttpClient = AsyncHttpClient()
        asyncHttpClient.addHeader("User-Agent", "request")
        asyncHttpClient.addHeader("Authorization", "token d0d57a98f2282febdd9c2fb42adfff1b8001b24b")
        val url = "https://api.github.com/users/$userName"

        asyncHttpClient.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>,
                responseBody: ByteArray
            ) {
                hideProgressBar()
                fab.visibility = View.VISIBLE
                try {
                    val result = String(responseBody)
                    val users = JSONObject(result)

                    val username: String? = users.getString("login")
                    val name: String? = users.getString("name")
                    val avatarUrl: String? = users.getString("avatar_url")
                    val company: String? = users.getString("company")
                    val location: String? = users.getString("location")
                    val repository: String? = users.getString("public_repos")
                    val followers: String? = users.getString("followers")
                    val following: String? = users.getString("following")

                    Glide.with(this@UserDetailActivity)
                        .load(avatarUrl)
                        .apply(RequestOptions().override(90, 90))
                        .into(binding.detailAvatar)
                    imageAvatar = avatarUrl.toString()

                    binding.detailCompany.text = company
                    binding.detailLocation.text = location
                    binding.detailUsername.text = username
                    binding.detailName.text = name
                    detail_repository.text = repository
                    detail_followers.text = followers
                    detail_following.text = following

                } catch (e: Exception) {
                    Toast.makeText(this@UserDetailActivity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error?.message}"
                }
                Toast.makeText(this@UserDetailActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun sectionsPageAdapterConf() {
        val sectionsPagerAdapter = userName?.let { SectionsPageAdapter(this, it) }
        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
        supportActionBar?.elevation = 0f
    }

    private fun showProgressBar() {
        binding.progressBarDetail.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBarDetail.visibility = View.INVISIBLE
    }

    override fun onClick(view: View) {
        val favoriteButton: Int = R.drawable.ic_thumb_up
        val notFavoriteButton: Int = R.drawable.ic_thumb_down

        if (view.id == R.id.fab) {
            if (isFavorite) {
                favoriteHelper.deleteById(userName.toString())
                Timber.d("Delete: $userName")
                fab.setImageResource(favoriteButton)
                Toast.makeText(
                    this@UserDetailActivity,
                    "User has been removed from favorite",
                    Toast.LENGTH_SHORT
                ).show()
                isFavorite = false
            } else {
                val dataUsername = binding.detailUsername.text.toString()
                val dataImage = imageAvatar
                val dataUserType = tv_type.text.toString()
                val dataFavorite = "1"

                val values = ContentValues()
                values.put(USERNAME, dataUsername)
                values.put(AVATAR, dataImage)
                values.put(USER_TYPE, dataUserType)
                values.put(FAVORITE, dataFavorite)

                isFavorite = true
                contentResolver.insert(CONTENT_URI, values)

                Toast.makeText(
                    this@UserDetailActivity,
                    "User has been added to favorite",
                    Toast.LENGTH_SHORT
                ).show()

                fab.setImageResource(notFavoriteButton)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        favoriteHelper.close()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}