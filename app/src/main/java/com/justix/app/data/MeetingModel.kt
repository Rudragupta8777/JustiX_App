package com.justix.app.data

data class MeetingModel(
    val _id: String,
    val meeting_code: String,
    val status: String,
    val score: Int?,
    val feedback: String?,
    val transcript: List<TranscriptItem>?
)
