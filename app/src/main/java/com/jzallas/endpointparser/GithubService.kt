package com.jzallas.endpointparser

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface GithubService {
  @GET("users/{user}/repos")
  fun listRepos(@Path("user") user: String): Call<List<Repo>>
}

data class Repo(val data: String?)