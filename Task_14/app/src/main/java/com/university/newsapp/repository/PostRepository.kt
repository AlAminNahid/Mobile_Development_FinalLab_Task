package com.university.newsapp.repository

import com.university.newsapp.network.RetrofitClient

class PostRepository {
    private val api = RetrofitClient.instance

    suspend fun getPosts() = api.getAllPosts()
    suspend fun getPost(id: Int) = api.getPostById(id)
    suspend fun getComments(postId: Int) = api.getCommentsByPost(postId)
    suspend fun getUsers() = api.getAllUsers()
    suspend fun getUser(id: Int) = api.getUserById(id)
    suspend fun getUsersPosts(id: Int) = api.getPostsByUser(id)
}