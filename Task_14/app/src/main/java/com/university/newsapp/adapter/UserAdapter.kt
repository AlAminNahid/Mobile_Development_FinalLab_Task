package com.university.newsapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.university.newsapp.R
import com.university.newsapp.model.User

class UserAdapter(
    private val onClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private val users = mutableListOf<User>()

    fun submitList(newUsers: List<User>) {
        users.clear()
        users.addAll(newUsers)
        notifyDataSetChanged()
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val avatar: TextView = itemView.findViewById(R.id.avatarText)
        private val name: TextView = itemView.findViewById(R.id.userName)
        private val username: TextView = itemView.findViewById(R.id.userUsername)
        private val email: TextView = itemView.findViewById(R.id.userEmail)

        fun bind(user: User) {
            avatar.text = getInitials(user.name)
            name.text = user.name
            username.text = "@${user.username}"
            email.text = user.email
            itemView.setOnClickListener { onClick(user) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount() = users.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    private fun getInitials(name: String): String {
        return name.split(" ")
            .take(2)
            .joinToString("") { it.first().uppercase() }
    }
}
