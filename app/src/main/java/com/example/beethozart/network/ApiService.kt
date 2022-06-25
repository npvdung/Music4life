package com.example.beethozart.network

import com.example.beethozart.entities.Song
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

private const val BASE_URL = "http://192.168.2.107:5000/"



val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface ApiService {
    @POST("auth/sign-up")
    fun signUpUser(@Body user: UserSignUpProperty): Call<UserFromServer>

    @POST("auth/sign-in")
    fun signInUser(@Body user: UserSignInProperty): Call<UserFromServer>

    @POST("song/get_song")
    fun getSearchSong(@Body song: SearchSongProperty): Call<List<Song>>

    @POST("history/push")
    fun pushHistory(@Body history : HistoryProperty) : Call<String>

    @POST("history/get")
    fun getHistory(@Body username : UserFromServer) : Call<List<Song>>
}

object Api {
    val retrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}