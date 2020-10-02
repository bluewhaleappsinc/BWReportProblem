package com.ultimuslab.bwreportlibrary

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.ultimuslab.*
import com.ultimuslab.bwreportlibrary.components.DeviceInfo
import com.ultimuslab.bwreportlibrary.components.SystemLog
import com.ultimuslab.bwreportlibrary.components.Utils
import com.ultimuslab.imageeditengine.ImageEditor
import com.ultimuslab.imageeditengine.ImageEditor.EDITOR_FILTERS
import com.ultimuslab.imageeditengine.ImageEditor.EDITOR_STICKER
import kotlinx.android.synthetic.main.feedback_layout.*
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import java.io.*
import java.util.*

class FeedbackActivity : AppCompatActivity(), View.OnClickListener {
    var LOG_TO_STRING = SystemLog.extractLogToString()
    var appLabel: String? = null
    var fixedVersionName: String? = null
    var userName: String? = null
    var buildVesrion: String? = null
    var enviroment: String? = null
    private val REQUEST_APP_SETTINGS = 321
    private val REQUEST_PERMISSIONS = 123
    private val EDITED_IMAGE_CODE = 152
    private var deviceInfo: String? = null
    private var withInfo = false
    private val PICK_MEDIA_REQUEST = 125
    private var realPath: String? = null
    private lateinit var api: RetrofitApiMutipart

    companion object {
        const val KEY_WITH_INFO = "with_info"
        const val KEY_PROJECT_NAME = "project_name"
        const val KEY_FIXED_VER_ID = "fixed_version_id"
        const val KEY_USER_NAME = "user_name"
        const val KEY_BUILD_VERSION = "build_version"
        const val KEY_ENVIROMENT = "enviroment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.feedback_layout)
        if (supportActionBar != null) supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        init()
    }

    private fun init() {
        api = RetrofitApiMutipart.createMultipart()

        val info = findViewById<View>(R.id.info_legal) as TextView
        submitSuggestion.setOnClickListener(this)
        lnr_add_image.setOnClickListener(this)
        lnr_edit_image.setOnClickListener(this)
        appLabel = intent.getStringExtra(KEY_PROJECT_NAME)
        fixedVersionName = intent.getStringExtra(KEY_FIXED_VER_ID)
        userName = intent.getStringExtra(KEY_USER_NAME)
        buildVesrion = intent.getStringExtra(KEY_BUILD_VERSION)
        enviroment = intent.getStringExtra(KEY_ENVIROMENT)
        withInfo = intent.getBooleanExtra(KEY_WITH_INFO, false)
        deviceInfo = DeviceInfo.getAllDeviceInfo(
            this,
            userName.orEmpty(),
            buildVesrion ?: "0",
            enviroment.orEmpty()
        )
        if (withInfo) {
            val infoFeedbackStart: CharSequence =
                resources.getString(R.string.info_fedback_legal_start)
            val deviceInfo =
                SpannableString(resources.getString(R.string.info_fedback_legal_system_info))
            deviceInfo.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    AlertDialog.Builder(this@FeedbackActivity)
                        .setTitle(R.string.info_fedback_legal_system_info)
                        .setMessage(this@FeedbackActivity.deviceInfo)
                        .setPositiveButton(R.string.Ok) { dialog, which -> dialog.dismiss() }
                        .show()
                }
            }, 0, deviceInfo.length, 0)
            val infoFeedbackAnd: CharSequence = resources.getString(R.string.info_fedback_legal_and)
            val systemLog =
                SpannableString(resources.getString(R.string.info_fedback_legal_log_data))
            systemLog.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    AlertDialog.Builder(this@FeedbackActivity)
                        .setTitle(R.string.info_fedback_legal_log_data)
                        .setMessage(LOG_TO_STRING)
                        .setPositiveButton(R.string.Ok) { dialog, which -> dialog.dismiss() }
                        .show()
                }
            }, 0, systemLog.length, 0)
            val infoFeedbackEnd: CharSequence =
                resources.getString(R.string.info_fedback_legal_will_be_sent, appLabel)
            val finalLegal = TextUtils.concat(
                infoFeedbackStart,
                deviceInfo,
                infoFeedbackAnd,
                systemLog,
                infoFeedbackEnd
            ) as Spanned
            info.text = finalLegal
            info.movementMethod = LinkMovementMethod.getInstance()
        } else info.visibility = View.GONE
    }


    fun editImage(realPath: String) {

        val intent = Intent(this, PhotoEditorActivity::class.java)
        intent.putExtra("selectedImagePath", realPath)
        startActivityForResult(intent, EDITED_IMAGE_CODE)

//        ImageEditor.Builder(this, realPath)
////            .setStickerAssets("stickers")
//            .disable(EDITOR_FILTERS)
//            .disable(EDITOR_STICKER)
//            .open()
    }

    fun selectImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val hasWriteContactsPermission =
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_PERMISSIONS
                )
                return
            } else  //already granted
                selectPicture()
        } else {
            //normal process
            selectPicture()
        }
    }

    private fun selectPicture() {
        realPath = null
        val intent = Intent()
        intent.type = "image/* video/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(
                intent,
                getString(R.string.select_media_title)
            ), PICK_MEDIA_REQUEST
        )
    }

    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton(R.string.Ok, okListener)
            .setNegativeButton(R.string.cancel, null)
            .create()
            .show()
    }

    private fun showProgressDialog(message: String) {
        val progress = ProgressDialog(this)
        progress.setMessage(message)
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progress.isIndeterminate = true
        progress.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_PERMISSIONS -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                selectPicture()
            } else {
                // Permission Denied
                showMessageOKCancel("You need to allow access to SD card to select images.",
                    DialogInterface.OnClickListener { dialog, which -> goToSettings() })
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun goToSettings() {
        val myAppSettings = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:$packageName")
        )
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT)
        myAppSettings.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivityForResult(myAppSettings, REQUEST_APP_SETTINGS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_APP_SETTINGS -> {
                if (hasPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                ) {

                    //Toast.makeText(this, "All permissions granted!", Toast.LENGTH_SHORT).show();
                    selectPicture()
                } else {
                    showMessageOKCancel("You need to allow access to Storage permission to select images.",
                        DialogInterface.OnClickListener { dialog, which -> goToSettings() })
                }
            }

            PICK_MEDIA_REQUEST -> {
                if (resultCode == RESULT_OK && data != null && data.data != null) {
                    data.data?.let {
                        realPath = Utils.getPath(this, it)

                        if (realPath?.endsWith(".mp4", true) == true) {
                            lnr_edit_image.visibility = View.GONE
                            img_play_button.visibility = View.VISIBLE
                            Glide.with(this).load(realPath).into(selectedImageView)
                        } else {
                            lnr_edit_image.visibility = View.VISIBLE
                            img_play_button.visibility = View.GONE

                            selectedImageView.setImageBitmap(
                                Utils.decodeSampledBitmap(
                                    realPath,
                                    selectedImageView.width, selectedImageView.height
                                )
                            )
                        }

                    }

                }
            }

//            ImageEditor.RC_IMAGE_EDITOR -> {
//                if (resultCode == Activity.RESULT_OK && data != null) {
//                    realPath = data.getStringExtra(ImageEditor.EXTRA_EDITED_PATH)
//                    if (realPath.isNullOrEmpty()) {
//                        Toast.makeText(this, getString(R.string.fail_to_get), Toast.LENGTH_SHORT)
//                            .show()
//
//                    } else {
//                        selectedImageView.setImageBitmap(
//                            BitmapFactory.decodeFile(realPath)
//                        )
//                    }
//
//                }
//            }

            EDITED_IMAGE_CODE -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    realPath = data.getStringExtra("imagePath")
                    if (realPath.isNullOrEmpty()) {
                        Toast.makeText(this, getString(R.string.fail_to_get), Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        selectedImageView.setImageBitmap(BitmapFactory.decodeFile(realPath))
//                        Glide.with(this).load(realPath).into(selectedImageView)
                    }

                }

            }

        }


    }

    fun hasPermissions(vararg permissions: String): Boolean {
        for (permission in permissions) if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(
                this,
                permission
            )
        ) return false
        return true
    }

    fun sendEmail(body: String?) {
        val emailIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
        emailIntent.type = "text/plain"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(appLabel))
        emailIntent.putExtra(
            Intent.EXTRA_SUBJECT,
            getString(R.string.feedback_mail_subject, appLabel)
        )
        emailIntent.putExtra(Intent.EXTRA_TEXT, body)
        val uris = ArrayList<Uri>()
        if (withInfo) {
            val deviceInfoUri =
                createFileFromString(deviceInfo, getString(R.string.file_name_device_info))
            uris.add(deviceInfoUri)
            val logUri =
                createFileFromString(LOG_TO_STRING, getString(R.string.file_name_device_log))
            uris.add(logUri)
        }
        if (realPath != null) {
            val uri = FileProvider.getUriForFile(
                this,
                applicationContext
                    .packageName + ".provider", File(realPath)
            )
            //Uri uri = Uri.parse("file://" + realPath);
            uris.add(uri)
        }
        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(
            Utils.createEmailOnlyChooserIntent(
                this,
                emailIntent,
                getString(R.string.send_feedback_two)
            )
        )
    }

    fun sendReport() {

        showProgressDialog("Process...")

        if (realPath.isNullOrEmpty()) {
            createIssueAPiCall("")

        } else {
            realPath?.let {
//            var file = Uri.parse(realPath).toFile()

                val strem = FileInputStream(File(realPath))
                val buf: ByteArray
                buf = ByteArray(strem.available())
                while (strem.read(buf) !== -1);
                val requestBody = RequestBody
                    .create(MediaType.parse("application/octet-stream"), buf)

//            val requestBody: RequestBody = RequestBody
//                .create(MediaType.parse("application/octet-stream"), realPath)
//            val requestFile: RequestBody =
//                RequestBody.create(MediaType.parse("multipart/form-data"), file)
//            val body = MultipartBody.Part.createFormData(
//                "image",
//                file.name, requestFile
//            )

                api.uploadMediaToRedmine(requestBody)
                    ?.enqueue(object : Callback<UploadRes> {
                        override fun onFailure(call: Call<UploadRes>, t: Throwable) {

                            Log.e("==>", "===>>>" + t)
                        }

                        override fun onResponse(
                            call: Call<UploadRes>,
                            response: retrofit2.Response<UploadRes>
                        ) {
                            if (response.isSuccessful) {
                                Log.e("==>", "===>>>" + response.body()?.upload?.token)
                                val token = response.body()?.upload?.token.orEmpty()

                                createIssueAPiCall(token)

                            } else {
                                Toast.makeText(
                                    this@FeedbackActivity,
                                    "Fail to upload",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                        }
                    })


            }
        }

    }

    fun createIssueAPiCall(tokenForImage: String) {
        var file_name = "attachment.jpg"
        var content_type = "image/jpg"
        if (realPath?.endsWith(".mp4", true) == true) {
            file_name = "attachment.mp4"
            content_type = "video/mp4"
        }

        var issueCreator = RedmineIssueCreator(
            Issue(
                appLabel.orEmpty(),
                editHeader.text.toString(),
                "${editText.text.toString()} $deviceInfo",
                fixedVersionName.orEmpty(),
                arrayListOf(MediaRedmine(tokenForImage, file_name, content_type))
            )
        )

        api.createRedmineIssue(issueCreator)
            ?.enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                    Log.e("==>", "===>>>" + t)
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: retrofit2.Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {

                        Toast.makeText(
                            this@FeedbackActivity,
                            "Successfully Submitted",
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()
                    } else {
                        Toast.makeText(
                            this@FeedbackActivity,
                            "Fail to submit",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }
            })


    }

    private fun createFileFromString(text: String?, name: String): Uri {
        val file = File(externalCacheDir, name)
        //create the file if it didn't exist
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        try {
            //BufferedWriter for performance, false to overrite to file flag
            val buf = BufferedWriter(FileWriter(file, false))
            buf.write(text)
            buf.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return FileProvider.getUriForFile(
            this,
            applicationContext.packageName + ".provider", file
        )
    }

    override fun onClick(view: View) {

        when (view.id) {
            R.id.submitSuggestion -> {
                val suggestion = editHeader.text.toString()
                if (suggestion.trim { it <= ' ' }.isNotEmpty()) {
//                    sendEmail(suggestion)
                    sendReport()
//                    finish()
                } else editHeader.error = getString(R.string.please_add_header)
            }
            R.id.lnr_edit_image -> {
                if (realPath.isNullOrEmpty()) {
                    Toast.makeText(
                        this,
                        getString(R.string.select_media_title),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    editImage(realPath!!)
                }
            }

            R.id.lnr_add_image -> {
                selectImage()
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

}