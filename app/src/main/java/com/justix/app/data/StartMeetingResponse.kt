package com.justix.app.data

data class StartMeetingResponse(
    val success: Boolean,
    val meetingCode: String,
    val meetingId: String
)