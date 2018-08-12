package com.jzallas.endpointparser

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import retrofit2.Retrofit

class ExampleActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_example)

    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .build()

    val service = retrofit.create(GithubService::class.java)
  }
}
