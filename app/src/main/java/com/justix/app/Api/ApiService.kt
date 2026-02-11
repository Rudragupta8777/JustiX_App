package com.justix.app.Api

import com.justix.app.data.CaseModel
import com.justix.app.data.MeetingModel
import com.justix.app.data.StartMeetingRequest
import com.justix.app.data.StartMeetingResponse
import com.justix.app.data.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @Multipart
    @POST("api/cases/upload")
    suspend fun uploadCase(
        @Header("Authorization") token: String,
        @Part pdf: MultipartBody.Part,
        @Part("title") title: RequestBody
    ): Response<UploadResponse>

    // New Endpoint: Get All Cases
    @GET("api/cases/my-cases")
    suspend fun getMyCases(@Header("Authorization") token: String): Response<List<CaseModel>>

    @GET("api/cases/{caseId}/history")
    suspend fun getCaseHistory(
        @Header("Authorization") token: String,
        @Path("caseId") caseId: String
    ): Response<List<MeetingModel>>

    @POST("api/cases/meeting/start")
    suspend fun startMeeting(
        @Header("Authorization") token: String,
        @Body request: StartMeetingRequest
    ): Response<StartMeetingResponse>
}