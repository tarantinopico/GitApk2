package com.fluidgit.app.data.api

import com.squareup.moshi.JsonClass
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

@JsonClass(generateAdapter = true)
data class AccessTokenResponse(
    val access_token: String,
    val scope: String?,
    val token_type: String?
)

interface GithubAuthApi {
    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("login/oauth/access_token")
    suspend fun getAccessToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String?,
        @Field("code") code: String
    ): AccessTokenResponse
}
