package com.oz.playground

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

interface SomeRetrofitService {

    @GET("/posts")
    fun posts() : Call<ResponseBody>

}