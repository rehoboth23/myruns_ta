package com.example.myrunsta

const val ProfileEmail="profile_email"
const val ProfileName="profile_name"
const val ProfilePhone="profile_phone"
const val ProfileGender="profile_gender"
const val ProfileClass="profile_class"
const val ProfileMajor="profile_major"
const val ProfileImageUri = "profile_image_uri"
const val PROFILE_IMAGE_FILE_NAME = "profile_image.jpeg"
const val PROFILE_TEMP_IMAGE_NAME = "profile_image_temp.jpeg"
val PROFILE_PERMS = arrayOf (
    android.Manifest.permission.CAMERA,
    android.Manifest.permission.READ_EXTERNAL_STORAGE,
    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
)