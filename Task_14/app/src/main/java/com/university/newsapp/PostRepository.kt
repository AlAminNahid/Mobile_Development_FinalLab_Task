package com.university.newsapp

import com.university.newsapp.network.RetrofitClient

class PostRepository {
    suspend fun getPosts() = RetrofitClient.instance.getAllPosts()
    suspend fun getPost(id: Int) = RetrofitClient.instance.getPostById(id)
    suspend fun getComments(postId: Int) = RetrofitClient.instance.getCommentsByPost(postId)
    suspend fun getUsers() = RetrofitClient.instance.getAllUsers()
    suspend fun getUser(id: Int) = RetrofitClient.instance.getUserById(id)
    suspend fun getUsersPosts(id: Int) = RetrofitClient.instance.getPostsByUser(id)
}