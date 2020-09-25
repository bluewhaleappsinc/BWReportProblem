package com.ultimuslab.bwreportlibrary

import android.content.Context
import android.content.Intent

class IssueTracker(builder: Builder) {
    private val context: Context
    private val projectName: String?
    private val fixedVersionId: String?
    private var userName: String? = null
    private var buildVesrion: String? = null
    private var enviroment: String? = null
    private val withSystemInfo: Boolean

    class Builder(val context: Context, val projectName: String, val fixed_version_id: String) {
        var userName: String? = null
        var buildNumber: String? = null
        var deviceInfo: String? = null
        var enviroment: String? = null
        var withSystemInfo = false

        fun withUserName(uName: String): Builder {
            userName = uName
            return this
        }

        fun withEnviroment(e: String): Builder {
            enviroment = e
            return this
        }

        fun withBuildVesion(version: String): Builder {
            buildNumber = version
            return this
        }

//        fun withSystemInfo(): Builder {
//            withSystemInfo = true
//            return this
//        }

        fun build(): IssueTracker {
            return IssueTracker(this)
        }
    }

    fun start() {
        val intent = Intent(context, FeedbackActivity::class.java)
        intent.putExtra(FeedbackActivity.KEY_PROJECT_NAME, projectName)
        intent.putExtra(FeedbackActivity.KEY_WITH_INFO, withSystemInfo)
        intent.putExtra(FeedbackActivity.KEY_FIXED_VER_ID, fixedVersionId)
        intent.putExtra(FeedbackActivity.KEY_USER_NAME, userName)
        intent.putExtra(FeedbackActivity.KEY_BUILD_VERSION, buildVesrion)
        intent.putExtra(FeedbackActivity.KEY_ENVIROMENT, enviroment)
        context.startActivity(intent)
    }

    init {
        context = builder.context
        projectName = builder.projectName
        fixedVersionId = builder.fixed_version_id
        userName = builder.userName
        enviroment = builder.enviroment
        buildVesrion = builder.buildNumber
        withSystemInfo = builder.withSystemInfo
    }
}