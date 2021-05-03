package com.timotiushaniel.githubuser.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.timotiushaniel.githubuser.R
import com.timotiushaniel.githubuser.adapter.UsersAdapter
import com.timotiushaniel.githubuser.databinding.ActivityMainBinding
import com.timotiushaniel.githubuser.helper.Constant
import com.timotiushaniel.githubuser.model.User
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var usersAdapter: UsersAdapter
    private var listUser: ArrayList<User> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerViewAdapterConfig()
        getUserList()
        clickUser()
        initSearchView()
    }

    private fun initSearchView() {
        binding.search.apply {
            isIconified = false
            isFocusable = false
            isFocusableInTouchMode = true
            clearFocus()
            setOnQueryTextListener(object :
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (!query.isNullOrEmpty()) {
                        searchUser(query)
                        listUser.clear()
                        hideKeyboard(currentFocus ?: View(this@MainActivity))
                        Toast.makeText(this@MainActivity, query, Toast.LENGTH_SHORT).show()
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }
            })
        }
    }

    private fun searchUser(query: String) {
        showProgressBar()
        val asyncHttpClient = AsyncHttpClient()
        asyncHttpClient.addHeader("User-Agent", "request")
        asyncHttpClient.addHeader("Authorization", "token d0d57a98f2282febdd9c2fb42adfff1b8001b24b")
        val url = "https://api.github.com/search/users?q=$query"

        asyncHttpClient.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>,
                responseBody: ByteArray
            ) {
                hideProgressBar()
                try {
                    val result = String(responseBody)
                    val jsonObject = JSONObject(result)
                    val dataArray = jsonObject.getJSONArray("items")
                    for (i in 0 until dataArray.length()) {
                        val dataObject = dataArray.getJSONObject(i)
                        val username: String = dataObject.getString("login")
                        val avatarUrl: String = dataObject.getString("avatar_url")
                        val userType: String = dataObject.getString("type")

                        listUser.add(User(username, avatarUrl, userType))
                    }
                    usersAdapter.setData(listUser)
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getUserList() {
        showProgressBar()
        val asyncHttpClient = AsyncHttpClient()
        asyncHttpClient.addHeader("User-Agent", "request")
        asyncHttpClient.addHeader("Authorization", "token d0d57a98f2282febdd9c2fb42adfff1b8001b24b")
        val url = "https://api.github.com/users"

        asyncHttpClient.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>,
                responseBody: ByteArray
            ) {
                hideProgressBar()
                val result = String(responseBody)
                try {
                    val jsonArray = JSONArray(result)
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val usernameLogin: String = jsonObject.getString("login")
                        val avatarUrl: String = jsonObject.getString("avatar_url")
                        val userType: String = jsonObject.getString("type")
                        listUser.add(User(usernameLogin, avatarUrl, userType))
                    }
                    usersAdapter.setData(listUser)
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.favoriteActivity -> {
                val mIntent = Intent(this, FavoriteActivity::class.java)
                startActivity(mIntent)
            }
            R.id.action_change_settings -> {
                val mIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(mIntent)
            }
            R.id.alarmActivity -> {
                val mIntent = Intent(this, NotificationActivity::class.java)
                startActivity(mIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun recyclerViewAdapterConfig() {
        usersAdapter = UsersAdapter()
        binding.rvMain.layoutManager = LinearLayoutManager(this)
        binding.rvMain.adapter = usersAdapter
    }

    private fun clickUser() {
        usersAdapter.setOnItemClickCallback(object : UsersAdapter.OnItemClickCallback {
            override fun onItemClicked(user: User) {
                Timber.d("User: ${user.username}")
                Intent(this@MainActivity, UserDetailActivity::class.java).apply {
                    putExtra(Constant.USER_DETAIL, user.username)
                }.run {
                    startActivity(this)
                }
            }
        })
    }

    private fun hideKeyboard(view: View) {
        view.apply {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.INVISIBLE
    }
}