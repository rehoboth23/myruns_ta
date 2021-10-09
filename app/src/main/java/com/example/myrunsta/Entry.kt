package com.example.myrunsta

import android.annotation.SuppressLint
import java.util.*

class Entry {
    var id = -1
    var entryType: String
    var activityType: String
    var comment: String
    var distanceInMiles: Float
    var durationInMinutes: Float
    var calories: Int
    var heartRate: Int
    var timeStamp: Calendar
    var locations: ArrayList<DoubleArray>? = null

    constructor(
        entryType: String, activityType: String, comment: String,
        distance: Float, duration: Float, c: Calendar, calories: Int, heartRate: Int,
    ) {
        this.comment = comment
        this.entryType = entryType
        this.activityType = activityType
        distanceInMiles = distance
        durationInMinutes = duration
        this.calories = calories
        this.heartRate = heartRate
        timeStamp = c
    }

    constructor(
        entryType: String,
        activityType: String,
        locs: ArrayList<DoubleArray>?,
        distance: Float,
        duration: Float,
        c: Calendar,
        calories: Int,
    ) {
        comment = ""
        this.entryType = entryType
        this.activityType = activityType
        locations = locs
        distanceInMiles = distance
        durationInMinutes = duration
        this.calories = calories
        heartRate = 0
        timeStamp = c
    }

    val imperialDistance: Float
        get() {
            @SuppressLint("DefaultLocale") val distance = String.format("%.2f", distanceInMiles)
            return distance.toFloat()
        }
    val metricDistance: Float
        get() {
            @SuppressLint("DefaultLocale") val distance =
                String.format("%.2f", distanceInMiles * 1.60934f)
            return distance.toFloat()
        }
}
