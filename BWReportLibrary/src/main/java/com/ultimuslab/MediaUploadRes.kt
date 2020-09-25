package com.ultimuslab

class RedmineIssueCreator(
    val issue: Issue
)

class Issue(
    val project_id: String,
    val subject: String,
    val description: String,
    val fixed_version_id: String,
    val uploads: ArrayList<MediaRedmine>

)

class MediaRedmine(

    val token: String,
    val filename: String,
    val content_type: String

)

class UploadRes(
    val upload: UploadData
)

class UploadData(
    val id: Int,
    val token: String
)