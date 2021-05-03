package com.timotiushaniel.githubuser.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.timotiushaniel.githubuser.databinding.ItemRowFollowsBinding
import com.timotiushaniel.githubuser.model.UserFollow

class FollowingAdapter : RecyclerView.Adapter<FollowingAdapter.ListViewHolder>() {

    private var mData = ArrayList<UserFollow>()

    fun setData(items: ArrayList<UserFollow>) {
        mData.clear()
        mData.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): FollowingAdapter.ListViewHolder {
        val binding =
            ItemRowFollowsBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(listViewHolder: ListViewHolder, position: Int) {
        listViewHolder.bind(mData[position])
    }

    override fun getItemCount(): Int = mData.size

    inner class ListViewHolder(binding: ItemRowFollowsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val binding = ItemRowFollowsBinding.bind(itemView)
        fun bind(followingData: UserFollow) {
            with(itemView) {
                Glide.with(context)
                    .load(followingData.avatar)
                    .apply(RequestOptions().override(90, 90))
                    .into(binding.avatarFollows)

                binding.tvUsername.text = followingData.username
                binding.tvType.text = followingData.userType
            }
        }
    }
}