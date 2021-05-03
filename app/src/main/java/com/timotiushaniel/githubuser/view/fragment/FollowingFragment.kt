package com.timotiushaniel.githubuser.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.timotiushaniel.githubuser.adapter.FollowingAdapter
import com.timotiushaniel.githubuser.databinding.FragmentFollowingBinding
import com.timotiushaniel.githubuser.helper.Constant
import com.timotiushaniel.githubuser.model.UserFollow
import cz.msebera.android.httpclient.Header
import org.json.JSONArray

class FollowingFragment : Fragment() {

    private var _binding: FragmentFollowingBinding? = null
    private val binding get() = _binding!!

    private lateinit var followingAdapter: FollowingAdapter
    private var followsData: ArrayList<UserFollow> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFollowingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val username = arguments?.getString(Constant.ARG_USERNAME)

        recyclerViewAdapterConfig()

        if (username != null) {
            getFollowingData(username)
        }
    }

    private fun getFollowingData(username: String) {
        showProgressBar()
        val client = AsyncHttpClient()
        client.addHeader("User-Agent", "request")
        client.addHeader("Authorization", "token d0d57a98f2282febdd9c2fb42adfff1b8001b24b")
        val url = "https://api.github.com/users/$username/following"
        client.get(url, object : AsyncHttpResponseHandler() {
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
                        val avatar: String = jsonObject.getString("avatar_url")
                        val userType: String = jsonObject.getString("type")

                        followsData.add(UserFollow(usernameLogin, avatar, userType))
                    }
                    followingAdapter.setData(followsData)
                } catch (e: Exception) {
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
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
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun recyclerViewAdapterConfig() {
        binding.rvMainFollowing.apply {
            layoutManager = LinearLayoutManager(requireContext())
            followingAdapter = FollowingAdapter()
            adapter = followingAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showProgressBar() {
        binding.progresBarFollowing.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progresBarFollowing.visibility = View.INVISIBLE
    }

    companion object {
        fun newInstance(username: String): FollowingFragment {
            val bundle = Bundle().apply {
                putString(Constant.ARG_USERNAME, username)
            }
            return FollowingFragment().apply {
                arguments = bundle
            }
        }
    }
}