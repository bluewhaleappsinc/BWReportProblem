package com.ultimuslab.bwreportproblem

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ultimuslab.bwreportlibrary.IssueTracker
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            IssueTracker.Builder(this, "test-project", "158")
                .withUserName("Mayur")
                .withEnviroment("staging")
                .withBuildVesion("${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})")
//                .withSystemInfo()
                .build()
                .start()

        }

    }


}