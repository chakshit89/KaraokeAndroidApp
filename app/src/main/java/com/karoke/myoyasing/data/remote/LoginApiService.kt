package com.karoke.myoyasing.data.remote

import com.karoke.myoyasing.data.dto.CommentsDTO.AddCommentRequestDto
import com.karoke.myoyasing.data.dto.CommentsDTO.PerformanceCommentDto
import com.karoke.myoyasing.data.dto.LoginDTO.LoginRequestDto
import com.karoke.myoyasing.data.dto.LoginDTO.LoginResponseDto
import com.karoke.myoyasing.data.dto.OtpverificationDTO.OtpVerificationRequestDto
import com.karoke.myoyasing.data.dto.OtpverificationDTO.OtpVerificationResponseDto
import com.karoke.myoyasing.data.dto.CreatePerformanceDTO.CreatePerformanceRequestDTO
import com.karoke.myoyasing.data.dto.CreatePerformanceDTO.CreatePerformanceResponse
import com.karoke.myoyasing.data.dto.FeedDTO.FeedResponseDto
import com.karoke.myoyasing.data.dto.ShareDTO.SharePerformanceResponseDto
import com.karoke.myoyasing.data.dto.SongsDto.SongDto
import com.karoke.myoyasing.data.dto.calculateScoreDTO.MelodyComparisonResponseDto
import com.karoke.myoyasing.data.dto.hashtags.HashtagDto
import com.karoke.myoyasing.data.dto.publishPerformanceDTO.PublishPerformanceRequestDto
import com.karoke.myoyasing.data.dto.uploadsongsDTO.UploadMediaResponseDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface LoginApiService {

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): Response<LoginResponseDto>


    @POST("auth/verify-otp")
    suspend fun verifyOtp(
        @Body request: OtpVerificationRequestDto
    ): Response<OtpVerificationResponseDto>

    @GET("songs")
    suspend fun getSongs(): Response<List<SongDto>>

    @GET("songs/{songId}")
    suspend fun getSongById(
        @Path("songId") songId: String
    ): Response<SongDto>

    @Multipart
    @POST("media/upload")
    suspend fun uploadMedia(
        @Part audio: MultipartBody.Part,
        @Part video: MultipartBody.Part,
        @Part thumbnail: MultipartBody.Part
    ): Response<UploadMediaResponseDto>

    @POST("performances")
    suspend fun createPerformance(@Body request: CreatePerformanceRequestDTO): Response<CreatePerformanceResponse>

    @Multipart
    @POST("melody/compare")
    suspend fun compareMelody(

        @Part("songId")
        songId: RequestBody,

        @Part
        userMelody: MultipartBody.Part

    ): Response<MelodyComparisonResponseDto>

    @GET("hashtags")
    suspend fun searchHashtags(
        @Query("q") query: String
    ): List<HashtagDto>

    @POST("performances/{performanceId}/publish")
    suspend fun publishPerformance(
        @Path("performanceId")
        performanceId: Int,

        @Body
        request: PublishPerformanceRequestDto
    ): Response<ResponseBody>


    @GET("feed")
    suspend fun getFeed(): Response<FeedResponseDto>


    @POST("performances/{performanceId}/share")
    suspend fun sharePerformance(
        @Path("performanceId") performanceId: Long
    ): Response<SharePerformanceResponseDto>

    @POST("performances/{performanceId}/like")
    suspend fun likePerformance(
        @Path("performanceId") performanceId: Long
    ): Response<Unit>

    @DELETE("performances/{performanceId}/like")
    suspend fun unlikePerformance(
        @Path("performanceId") performanceId: Long
    ): Response<Unit>

    @GET("performances/{performanceId}/comments")
    suspend fun getPerformanceComments(
        @Path("performanceId") performanceId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: List<String>? = null
    ): Response<List<PerformanceCommentDto>>


    @POST("performances/{performanceId}/comments")
    suspend fun addPerformanceComment(
        @Path("performanceId") performanceId: Long,
        @Body request: AddCommentRequestDto
    ): Response<PerformanceCommentDto>


    @DELETE("performances/{performanceId}/comments/{commentId}")
    suspend fun deletePerformanceComment(
        @Path("performanceId") performanceId: Long,
        @Path("commentId") commentId: Long
    ): Response<Unit>

    @POST("users/{userId}/follow")
    suspend fun followUser(
        @Path("userId") userId: Int
    ): Response<Unit>

    @DELETE("users/{userId}/follow")
    suspend fun unfollowUser(
        @Path("userId") userId: Int
    ): Response<Unit>

}