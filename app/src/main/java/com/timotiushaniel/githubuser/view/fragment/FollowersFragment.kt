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
import com.timotiushaniel.githubuser.adapter.FollowersAdapter
import com.timotiushaniel.githubuser.databinding.FragmentFollowersBinding
import com.timotiushaniel.githubuser.helper.Constant
import com.timotiushaniel.githubuser.model.UserFollow
import cz.msebera.android.httpclient.Header
import org.json.JSONArray

class FollowersFragment : Fragment() {

    private var _binding: FragmentFollowersBinding? = null
    private val binding get() = _binding!!

    private lateinit var followersAdapter: FollowersAdapter
    private var followsData: ArrayList<UserFollow> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFollowersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val username = arguments?.getString(Constant.ARG_USERNAME)

        recyclerViewAdapterConfig()

        if (username != null) {
            getFollowersData(username)
        }
    }

    private fun getFollowersData(username: String) {
        showProgressBar()
        val client = AsyncHttpClient()
        client.addHeader("User-Agent", "request")
        client.addHeader("Authorization", "token d0d57a98f2282febdd9c2fb42adfff1b8001b24b")
        val url = "https://api.github.com/users/$username/followers"
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
                        followsData.add(
                            UserFollow(
                                username = jsonObject.getString("login"),
                                avatar = jsonObject.getString("avatar_url"),
                                userType = jsonObject.getString("type")
                            )
                        )
                    }
                    followersAdapter.setData(followsData)
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
        binding.rvMainFollowers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            followersAdapter = FollowersAdapter()
            adapter = followersAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showProgressBar() {
        binding.progressBarFollowers.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBarFollowers.visibility = View.INVISIBLE
    }

    companion object {
        fun newInstance(username: String): FollowersFragment {
            val bundle = Bundle().apply {
                putString(Constant.ARG_USERNAME, username)
            }
            return FollowersFragment().apply {
                arguments = bundle
            }
        }
    }
}