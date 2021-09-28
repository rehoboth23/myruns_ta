package com.example.myrunsta

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.soundcloud.android.crop.Crop
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

@SuppressLint("InflateParams")
class ProfileActivity : AppCompatActivity() {
    private var tempImageUri: Uri? = null
    private var preferences: SharedPreferences? = null
    private var profileImage: ImageView? = null
    private var name: EditText? = null
    private var email:EditText? = null
    private var phone:EditText? = null
    private var myClass:EditText? = null
    private var major:EditText? = null
    private var gender: RadioGroup? = null
    private var cImage = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
       if (savedInstanceState != null) {
           cImage = savedInstanceState.getBoolean(C_IMAGE_KEY, false)
       }

        // check and get permissions for profile
        checkPermissions(this, PROFILE_PERMS, true)

        // get views
        profileImage = findViewById(R.id.profile_image)
        name = findViewById(R.id.name) // name edit text
        email = findViewById(R.id.email) // email edit text
        phone = findViewById(R.id.phone) // phone edit text
        myClass = findViewById(R.id.my_class) // class edit text
        major = findViewById(R.id.major) // major edit text
        gender = findViewById(R.id.gender)

        // retrieve shared preferences
        preferences = getSharedPreferences("PROFILE_PREFS", MODE_PRIVATE)
        loadProfile()
    }

    override fun onDestroy() {
        if (!cImage) deleteTempFile()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater:MenuInflater = menuInflater
        inflater.inflate(R.menu.profile_menu, menu)
        return true
    }

    // changeImage : starts the action to change profile image
    fun changeImage(v: View) {
        print(v.id.toString())
        if (tempImageUri == null) {
            // make a temporary file and uri for the captured image
            val tempImageFile = File(getExternalFilesDir(null), PROFILE_TEMP_IMAGE_NAME)
            tempImageFile.createNewFile()
            tempImageUri = FileProvider.getUriForFile(this@ProfileActivity,
                "com.example.myrunsta", tempImageFile)
        }

        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.change_image_dialog_fragment, null)
        builder.setView(view)
        val dialog = builder.create()
        view.findViewById<View>(R.id.capture_image).setOnClickListener {
            dialog.dismiss()
            // make intent for camera activity
            val imageCaptureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            // put uri as output target location
            imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempImageUri)
            checkPermissions(this@ProfileActivity, PROFILE_PERMS, true)
            if (checkPermissions(this@ProfileActivity, PROFILE_PERMS)) {
                // ensure resource is available: prevents application clashes
                imageCapture.launch(tempImageUri)
            }
        }

        view.findViewById<View>(R.id.select_image).setOnClickListener {
            dialog.dismiss()
            // make intent for image pick activity
            val imagePickIntent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imagePick.launch(imagePickIntent)
        }

        view.findViewById<View>(R.id.change_image_cancel).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    // activity result handler for image capture
    private val imageCapture = registerForActivityResult(
        ActivityResultContracts.TakePicture()) {
            result ->
            run {
                if (result) {
                    val cropIntent: Intent = Crop.of(tempImageUri, tempImageUri)
                        .asSquare().getIntent(this)
                    cropImage.launch(cropIntent)
                }
            }
        }

    // activity result contract for image pick
    private val imagePick = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        result ->
            run {
                if (result.resultCode == RESULT_OK) {
                    if (tempImageUri == null) {
                        val tempImageFile = File(getExternalFilesDir(null),
                            PROFILE_TEMP_IMAGE_NAME)
                        tempImageUri = FileProvider.getUriForFile(this@ProfileActivity,
                            "com.example.myrunsta", tempImageFile)
                    }
                    val data = result.data!!.data!!
                    val `in` = contentResolver.openInputStream(data)
                    val `out` = contentResolver.openOutputStream(tempImageUri!!)
                    val imageData = ByteArray(2048)
                    copyFileHelper(`in`!!, out!!, imageData)
                    val cropIntent: Intent = Crop.of(tempImageUri, tempImageUri)
                        .asSquare().getIntent(this)
                    cropImage.launch(cropIntent)
                }
            }
    }

    // activity result handler for image crop
    private val cropImage = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
            result ->
            run {
                if (result.resultCode == RESULT_OK) {
                    profileImage?.setImageURI(null)
                    profileImage?.setImageURI(tempImageUri)
                    cImage = true
                } else if (result.resultCode == RESULT_CANCELED) {
                    deleteTempFile()
                }
            }
        }

    // profileSave : saves changes to the profile
    @Suppress("unused")
    @RequiresApi(Build.VERSION_CODES.M)
    fun View.profileSave() {
        if (!checkEmailView(this@ProfileActivity, email!!,
                this@ProfileActivity.findViewById(R.id.emailId))) {
            Toast.makeText(this@ProfileActivity, "Please Fill in Email correctly",
                Toast.LENGTH_SHORT).show()
        }

        if (cImage) {
            val imageData = ByteArray(2048)
            val profileImageFile = File(getExternalFilesDir(null), PROFILE_IMAGE_FILE_NAME)
            profileImageFile.createNewFile()
            val inStream: InputStream? = contentResolver.openInputStream(tempImageUri!!)
            val outStream: OutputStream = FileOutputStream(profileImageFile)
            copyFileHelper(inStream!!, outStream, imageData)
            imageCleanUp()
        }

        val editor: SharedPreferences.Editor? = preferences?.edit()
        if (editor != null) {
            editor.putString(ProfileEmail, email?.text.toString())
            editor.putString(ProfileName, name?.text.toString())
            editor.putString(ProfilePhone, phone?.text.toString())
            editor.putString(ProfileClass, myClass?.text.toString())
            editor.putString(ProfileMajor, major?.text.toString())
            gender?.checkedRadioButtonId?.let { editor.putInt(ProfileGender, it) }
            editor.apply()
            Toast.makeText(this@ProfileActivity, "Profile Saved", Toast.LENGTH_SHORT)
                .show()
        }
        finish()
    }

    // profileCancel : cancels changes to the profile
    @Suppress("unused")
    fun View.profileCancel() {
        if (cImage) {
            // check if profile image file exists; set the profile image
            val profileImageFile = File(getExternalFilesDir(null), PROFILE_IMAGE_FILE_NAME)
            val imageSet: Boolean = imageFileHelper(profileImageFile, profileImage!!)
            if (!imageSet) {
                // set image source to default image resource
                profileImage!!.setImageResource(R.mipmap.ic_default_profile_image)
            }
            imageCleanUp()
        }
        finish()
    }

    // loadProfile : loads profile from preferences
    private fun loadProfile() {
        email?.setText(preferences?.getString(ProfileEmail, ""))
        name?.setText(preferences?.getString(ProfileName, ""))
        phone?.setText(preferences?.getString(ProfilePhone, ""))
        myClass?.setText(preferences?.getString(ProfileClass, ""))
        major?.setText(preferences?.getString(ProfileMajor, ""))
        preferences?.getInt(ProfileGender, 0)?.let { gender?.check(it) }

        if (!cImage) {
            // check if profile image file uri is in preferences
            val profileImageFile = File(getExternalFilesDir(null),
                PROFILE_IMAGE_FILE_NAME)
            imageFileHelper(profileImageFile, profileImage!!)
        } else {
            val tempImageFile = File(getExternalFilesDir(null),
                PROFILE_TEMP_IMAGE_NAME)
            tempImageUri = FileProvider.getUriForFile(this@ProfileActivity,
                "com.example.myrunsta", tempImageFile)
            profileImage?.setImageURI(null)
            profileImage?.setImageURI(tempImageUri)
        }
    }

    // helper to remove uri from preferences
    private fun removeImageUriFromPreferences() {
        val edit: SharedPreferences.Editor? = preferences?.edit()
        edit?.remove("imageUri")
        edit?.apply()
    }

    // helper to delete temporary image file
    private fun deleteTempFile() {
        // delete file with fileName
        val tempImageFile = File(getExternalFilesDir(null), PROFILE_TEMP_IMAGE_NAME)
        if (tempImageFile.exists()) {
            if (tempImageFile.delete()) Log.d("del", "Deleted $PROFILE_TEMP_IMAGE_NAME")
        }
    }

    // helper to cleanup temp image and other stuff
    private fun imageCleanUp() {
        // CLEAN UP
        removeImageUriFromPreferences()
        cImage = false // image is set to original
        tempImageUri = null // null temp image uri
        deleteTempFile() // delete temporary file name
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(C_IMAGE_KEY, cImage)
        super.onSaveInstanceState(outState)
    }
}