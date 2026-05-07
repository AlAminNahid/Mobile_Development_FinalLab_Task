package com.university.newsapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import com.university.newsapp.R
import com.university.newsapp.model.Post
import com.university.newsapp.repository.PostRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val repository = PostRepository()
    private lateinit var postAdapter: PostAdapter
    private lateinit var userAdapter: UserAdapter

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorLayout: LinearLayout
    private lateinit var errorText: TextView
    private lateinit var retryButton: MaterialButton
    private lateinit var searchView: SearchView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var tabLayout: TabLayout

    private var allPosts = listOf<Post>()
    private var currentTab = "posts"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        errorLayout = findViewById(R.id.errorLayout)
        errorText = findViewById(R.id.errorText)
        retryButton = findViewById(R.id.retryButton)
        searchView = findViewById(R.id.searchView)
        swipeRefresh = findViewById(R.id.swipeRefresh)
        tabLayout = findViewById(R.id.tabLayout)

        recyclerView.layoutManager = LinearLayoutManager(this)

        postAdapter = PostAdapter { post ->
            startActivity(Intent(this, PostDetailActivity::class.java).putExtra("post_id", post.id))
        }

        userAdapter = UserAdapter { user ->
            startActivity(Intent(this, UserProfileActivity::class.java).putExtra("user_id", user.id))
        }

        recyclerView.adapter = postAdapter

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        currentTab = "posts"
                        searchView.visibility = View.VISIBLE
                        recyclerView.adapter = postAdapter
                        postAdapter.submitList(allPosts)
                    }
                    1 -> {
                        currentTab = "users"
                        searchView.visibility = View.GONE
                        recyclerView.adapter = userAdapter
                        loadUsers()
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        retryButton.setOnClickListener {
            if (currentTab == "posts") loadPosts() else loadUsers()
        }

        swipeRefresh.setOnRefreshListener {
            if (currentTab == "posts") loadPosts() else loadUsers()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = true

            override fun onQueryTextChange(newText: String?): Boolean {
                val filtered = allPosts.filter {
                    it.title.contains(newText.orEmpty(), ignoreCase = true)
                }
                postAdapter.submitList(filtered)
                return true
            }
        })

        loadPosts()
    }

    private fun loadPosts() {
        showLoading()
        lifecycleScope.launch {
            try {
                allPosts = repository.getPosts()
                showSuccess()
                postAdapter.submitList(allPosts)
            } catch (e: HttpException) {
                showError("Server error: ${e.code()}")
            } catch (e: IOException) {
                showError("Network error. Check your connection.")
            } catch (e: Exception) {
                showError("Something went wrong: ${e.message}")
            }
        }
    }

    private fun loadUsers() {
        showLoading()
        lifecycleScope.launch {
            try {
                val users = repository.getUsers()
                showSuccess()
                userAdapter.submitList(users)
            } catch (e: Exception) {
                showError("Unable to load users: ${e.message}")
            }
        }
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        errorLayout.visibility = View.GONE
    }

    private fun showSuccess() {
        swipeRefresh.isRefreshing = false
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        errorLayout.visibility = View.GONE
    }

    private fun showError(message: String) {
        swipeRefresh.isRefreshing = false
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        errorLayout.visibility = View.VISIBLE
        errorText.text = message
    }
}
