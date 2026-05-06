package com.university.newsapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.university.newsapp.databinding.ItemPostBinding
import com.university.newsapp.model.Post

class PostAdapter(
    private var list: List<Post>
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(
        val binding: ItemPostBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) : PostViewHolder {

        val binding = ItemPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return PostViewHolder(binding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(
        holder: PostViewHolder,
        position: Int
    ) {

        val post = list[position]

        holder.binding.tvTitle.text = post.title
        holder.binding.tvBody.text = post.body
        holder.binding.tvUserId.text = "User ${post.userId}"
        holder.binding.tvPostId.text = "Post #${post.id}"

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, PostDetailActivity::class.java)

            intent.putExtra("postId", post.id)
            intent.putExtra("userId", post.userId)

            holder.itemView.context.startActivity(intent)
        }
    }

    fun updateData(newList: List<Post>) {
        list = newList
        notifyDataSetChanged()
    }
}