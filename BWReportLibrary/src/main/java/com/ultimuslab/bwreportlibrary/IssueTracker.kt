package com.ultimuslab.bwreportlibrary

import android.content.Context
import android.content.Intent
import com.ultimuslab.bwreportlibrary.FeedbackActivity

class IssueTracker(builder: Builder) {
    private val context: Context
    private val projectName: String?
    private val withSystemInfo: Boolean

    class Builder(val context: Context) {
        var projectName: String? = null
        var withSystemInfo = false
        fun withProjectName(name: String?): Builder {
            projectName = name
            return this
        }

        fun withSystemInfo(): Builder {
            withSystemInfo = true
            return this
        }

        fun build(): IssueTracker {
            return IssueTracker(this)
        }
    }

    fun start() {
        val intent = Intent(context, FeedbackActivity::class.java)
        intent.putExtra("project_name", projectName)
        intent.putExtra("with_info", withSystemInfo)
        context.startActivity(intent)
    }

    init {
        projectName = builder.projectName
        context = builder.context
        withSystemInfo = builder.withSystemInfo
    }
}