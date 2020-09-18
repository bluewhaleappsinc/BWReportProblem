package com.ultimuslab.bwreportproblem

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ultimuslab.bwreportlibrary.IssueTracker
import com.ultimuslab.imageeditengine.ImageEditor
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            IssueTracker.Builder(this)
                .withProjectName("test-project")
                .withSystemInfo()
                .build()
                .start()
        }
    }



}