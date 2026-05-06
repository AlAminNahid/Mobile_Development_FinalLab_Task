package com.university.newsapp.ui

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.university.newsapp.R
import com.university.newsapp.model.Post

class PostViewHolder(
    itemView: View,
    private val onClick: (Post) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private val title: TextView = itemView.findViewById(R.id.postTitle)
    private val body: TextView = itemView.findViewById(R.id.postBody)
    private val userBadge: TextView = itemView.findViewById(R.id.userBadge)
    private val postId: TextView = itemView.findViewById(R.id.postId)

    fun bind(post: Post) {
        title.text = post.title.replaceFirstChar { it.uppercase() }
        body.text = post.body
        userBadge.text = "User ${post.userId}"
        postId.text = "Post #${post.id}"
        itemView.setOnClickListener { onClick(post) }
    }
}

