package com.university.newsapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.university.newsapp.R
import com.university.newsapp.model.User
import com.university.newsapp.repository.PostRepository
import kotlinx.coroutines.launch

class PostDetailActivity : AppCompatActivity() {

    private val repository = PostRepository()
    private var authorUser: User? = null

    private lateinit var postProgress: ProgressBar
    private lateinit var authorProgress: ProgressBar
    private lateinit var commentsProgress: ProgressBar
    private lateinit var title: TextView
    private lateinit var body: TextView
    private lateinit var authorCard: LinearLayout
    private lateinit var authorName: TextView
    private lateinit var authorEmail: TextView
    private lateinit var authorCompany: TextView
    private lateinit var commentsContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_detail)

        postProgress = findViewById(R.id.postProgress)
        authorProgress = findViewById(R.id.authorProgress)
        commentsProgress = findViewById(R.id.commentsProgress)
        title = findViewById(R.id.detailTitle)
        body = findViewById(R.id.detailBody)
        authorCard = findViewById(R.id.authorCard)
        authorName = findViewById(R.id.authorName)
        authorEmail = findViewById(R.id.authorEmail)
        authorCompany = findViewById(R.id.authorCompany)
        commentsContainer = findViewById(R.id.commentsContainer)

        val postId = intent.getIntExtra("post_id", -1)
        if (postId != -1) loadPost(postId)

        authorCard.setOnClickListener {
            authorUser?.let {
                startActivity(Intent(this, UserProfileActivity::class.java).putExtra("user_id", it.id))
            }
        }
    }

    private fun loadPost(postId: Int) {
        lifecycleScope.launch {
            try {
                postProgress.visibility = View.VISIBLE
                val post = repository.getPost(postId)
                postProgress.visibility = View.GONE
                title.text = post.title.replaceFirstChar { it.uppercase() }
                body.text = post.body
                loadAuthor(post.userId)
                loadComments(post.id)
            } catch (e: Exception) {
                postProgress.visibility = View.GONE
                title.text = "Unable to load post"
                body.text = e.message
            }
        }
    }

    private fun loadAuthor(userId: Int) {
        lifecycleScope.launch {
            try {
                authorProgress.visibility = View.VISIBLE
                val user = repository.getUser(userId)
                authorUser = user
                authorProgress.visibility = View.GONE
                authorName.text = user.name
                authorEmail.text = user.email
                authorCompany.text = user.company.name
            } catch (e: Exception) {
                authorProgress.visibility = View.GONE
                authorName.text = "Author unavailable"
            }
        }
    }

    private fun loadComments(postId: Int) {
        lifecycleScope.launch {
            try {
                commentsProgress.visibility = View.VISIBLE
                val comments = repository.getComments(postId)
                commentsProgress.visibility = View.GONE
                commentsContainer.removeAllViews()

                comments.forEach { comment ->
                    val view = layoutInflater.inflate(R.layout.item_comment, commentsContainer, false)
                    view.findViewById<TextView>(R.id.commentName).text = comment.name
                    view.findViewById<TextView>(R.id.commentEmail).text = comment.email
                    view.findViewById<TextView>(R.id.commentBody).text = comment.body
                    commentsContainer.addView(view)
                }
            } catch (e: Exception) {
                commentsProgress.visibility = View.GONE
            }
        }
    }
}