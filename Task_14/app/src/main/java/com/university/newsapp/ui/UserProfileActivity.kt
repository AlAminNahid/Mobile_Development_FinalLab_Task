package com.university.newsapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.university.newsapp.R
import com.university.newsapp.repository.PostRepository
import kotlinx.coroutines.launch

class UserProfileActivity : AppCompatActivity() {
    private val repository = PostRepository()

    private lateinit var progress: ProgressBar
    private lateinit var avatar: TextView
    private lateinit var name: TextView
    private lateinit var username: TextView
    private lateinit var email: TextView
    private lateinit var phone: TextView
    private lateinit var website: TextView
    private lateinit var company: TextView
    private lateinit var catchPhrase: TextView
    private lateinit var postsContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener { finish() }

        progress = findViewById(R.id.profileProgress)
        avatar = findViewById(R.id.profileAvatar)
        name = findViewById(R.id.profileName)
        username = findViewById(R.id.profileUsername)
        email = findViewById(R.id.profileEmail)
        phone = findViewById(R.id.profilePhone)
        website = findViewById(R.id.profileWebsite)
        company = findViewById(R.id.profileCompany)
        catchPhrase = findViewById(R.id.profileCatchPhrase)
        postsContainer = findViewById(R.id.userPostsContainer)

        val userId = intent.getIntExtra("user_id", -1)
        if (userId != -1) loadProfile(userId)
    }

    private fun loadProfile(userId: Int) {
        lifecycleScope.launch {
            try {
                progress.visibility = View.VISIBLE

                val user = repository.getUser(userId)
                progress.visibility = View.GONE

                avatar.text = getInitials(user.name)
                name.text = user.name
                username.text = "@${user.username}"
                email.text = "Email: ${user.email}"
                phone.text = "Phone: ${user.phone}"
                website.text = "Website: ${user.website}"
                company.text = "Company: ${user.company.name}"
                catchPhrase.text = user.company.catchPhrase

                loadUserPosts(user.id)
            } catch (e: Exception) {
                progress.visibility = View.GONE
                name.text = "Unable to load profile"
            }
        }
    }

    private fun loadUserPosts(userId: Int) {
        lifecycleScope.launch {
            val posts = repository.getUsersPosts(userId)
            postsContainer.removeAllViews()

            posts.forEach { post ->
                val view = layoutInflater.inflate(R.layout.item_post, postsContainer, false)
                view.findViewById<TextView>(R.id.postTitle).text = post.title.replaceFirstChar { it.uppercase() }
                view.findViewById<TextView>(R.id.postBody).text = post.body
                view.findViewById<TextView>(R.id.userBadge).text = "User ${post.userId}"
                view.findViewById<TextView>(R.id.postId).text = "Post #${post.id}"

                view.setOnClickListener {
                    startActivity(Intent(this@UserProfileActivity, PostDetailActivity::class.java).putExtra("post_id", post.id))
                }

                postsContainer.addView(view)
            }
        }
    }

    private fun getInitials(name: String): String {
        return name.split(" ")
            .take(2)
            .joinToString("") { it.first().uppercase() }
    }
}
