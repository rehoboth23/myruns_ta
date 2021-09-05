package com.example.myrunsta

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.regex.Pattern
import kotlin.math.max

@RequiresApi(Build.VERSION_CODES.M)
fun checkEmailView(context: Context, emailView: EditText, emailLabel: TextView): Boolean {
    val s = emailView.text.toString()
    if (s.isEmpty()) return true
    val pattern = Pattern.compile("^(.+)@(.+)((\\.(.+))+)")
    val matcher = pattern.matcher(s)
    if (!matcher.find()) {
        emailView.setTextColor(context.getColor(R.color.inputErrorColor))
        emailLabel.setTextColor(context.getColor(R.color.inputErrorColor))
        Toast.makeText(context, "Email is invalid", Toast.LENGTH_SHORT).show()
        return false
    }
    emailView.setTextColor(context.getColor(R.color.textColor))
    emailLabel.setTextColor(context.getColor(R.color.textColor))
    return true
}

fun checkPermission(context: Context, perm: String): Boolean{
    return ContextCompat.checkSelfPermission(context,
        perm) == PackageManager.PERMISSION_GRANTED
}

fun checkPermissions(context: Context, permChecks:Array<String>): Boolean {
    for (perm:String in permChecks) {
        if (!checkPermission(context, perm)) {
            return false
        }
    }

    return  true
}

fun checkPermissions(activity: Activity, permChecks:Array<String>, requestOnFail:Boolean) {
    val unPermitted = mutableListOf<String>()
    for (perm:String in permChecks) {
        if (!checkPermission(activity.applicationContext, perm)) {
            unPermitted.add(perm)
        }
    }
    if (requestOnFail) {
        requestPermissions(activity, unPermitted)
    }
}

fun copyFileHelper(`in`:InputStream, `out`:OutputStream, data:ByteArray) {
    try {
        var bytesRead: Int
        while (`in`.read(data).also { bytesRead = it } > 0) {
            out.write(data.copyOfRange(0, max(0, bytesRead))) // write bytes to output stream
        }
        // close streams
        `in`.close()
        out.close()
    } catch (e:IOException) {
        e.printStackTrace()
    }
}

fun requestPermissions(activity:Activity, perms:List<String>) {
    if (perms.isNotEmpty()) {
        androidx.core.app.ActivityCompat.requestPermissions(activity, perms.toTypedArray(), 0)
    }

}

fun imageFileHelper(profileImageFile: File, profileImage: ImageView): Boolean {
    if (profileImageFile.exists()) {
        val profileImageUri = Uri.fromFile(profileImageFile) // get uri prom file
        profileImage.setImageURI(profileImageUri) // set image uri to profile image uri
        return true // indicated that the change was made
    }
    return false // indicated no change was made
}