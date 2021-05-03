package com.timotiushaniel.githubuser.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.timotiushaniel.githubuser.R
import com.timotiushaniel.githubuser.databinding.ItemRowUserlistBinding
import com.timotiushaniel.githubuser.model.User

class UsersAdapter : RecyclerView.Adapter<UsersAdapter.ListViewHolder>() {

    private val mData = ArrayList<User>()

    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    fun setData(items: ArrayList<User>) {
        mData.clear()
        mData.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ListViewHolder {
        val mView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_row_userlist, viewGroup, false)
        return ListViewHolder(mView)
    }

    override fun onBindViewHolder(listViewHolder: ListViewHolder, position: Int) {
        listViewHolder.bind(mData[position])
    }

    override fun getItemCount(): Int = mData.size

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemRowUserlistBinding.bind(itemView)
        fun bind(user: User) {
            with(itemView) {
                Glide.with(context)
                    .load(user.avatar)
                    .apply(RequestOptions().override(90, 90))
                    .into(binding.avatar)

                binding.tvUsername.text = user.username
                binding.tvType.text = user.userType

                itemView.setOnClickListener { onItemClickCallback?.onItemClicked(user) }
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(user: User)
    }
}