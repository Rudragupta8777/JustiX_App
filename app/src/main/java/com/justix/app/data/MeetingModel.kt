package com.justix.app.data

data class MeetingModel(
    val _id: String,
    val meeting_code: String,
    val status: String,
    val score: Int?,
    val meeting_number: Int?,
    val summary: String?,  // <--- ADD THIS
    val feedback: String?,
    val transcript: List<TranscriptItem>?
)