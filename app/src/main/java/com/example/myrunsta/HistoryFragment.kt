package com.example.myrunsta

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.lang.Exception
import java.util.*

class HistoryFragment: Fragment() {
    private lateinit var mView: View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        mView = inflater.inflate(R.layout.fragment_history, container, false)
        // Inflate the layout for this fragment
        return mView
    }

    override fun onResume() {
        super.onResume()
        setListAdapter()
        Log.d("hello", "this is a useless message")
    }


    private fun setListAdapter() {
        try {
            val t = Thread {
                val dataBaseUtil = DataBaseUtil(requireContext())
                val entries: List<Entry> = dataBaseUtil.entries
                requireActivity().runOnUiThread { // make adapter
                    val lv = mView.findViewById<ListView>(R.id.list_view)
                    lv.adapter = HistoryListAdapter(requireContext(), R.layout.layout_history_item,
                        entries)
                    // get view, list view in view and set adapter to list view
                }
            }
            t.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

class HistoryListAdapter(private var mContext: Context, private var resource: Int,
                         private var entries: List<Entry>) :
    ArrayAdapter<Entry?>(mContext, resource, entries) {
    private var preferences: SharedPreferences

    init {
        val activity = mContext as Activity
        preferences = activity.getSharedPreferences(
            MAIN_PREFERENCES, Context.MODE_PRIVATE
        )
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(mContext)
        val view = inflater.inflate(resource, null, false)
        val entry = entries[position]
        Log.d("entry type", entry.entryType)
        val unit = preferences.getInt(UNIT_TYPE, 0)
        return when (entry.entryType) {
            "Manual Entry" -> startManualEntry(view, entry, unit)
//            startMapEntry(view, entry, unit)
            else -> View(mContext)
        }
    }

//    private fun startMapEntry(convertView: View, entry: Entry, unit: Int): View {
//        val entryType: String = entry.entryType
//        val activityType: String = entry.activityType
//        val c: Calendar = entry.timeStamp
//        // retrieve and format time stamp
//        var hour = c[Calendar.HOUR_OF_DAY].toString() + ""
//        var minute = c[Calendar.MINUTE].toString() + ""
//        if (hour.length == 1) hour = "0$hour"
//        if (minute.length == 1) minute = "0$minute"
//        val timestamp = hour + ":" + minute + ":00 " +
//                getMonthString(c[Calendar.MONTH]) + " " +
//                c[Calendar.DAY_OF_MONTH] + " " + c[Calendar.YEAR]
//
//        // merge to form main sentence
//        val entryTypeTime = "$entryType: $activityType, $timestamp"
//
//        // retrieve and format distance
//        val distance: String
//        distance = if (unit == 0 || unit == R.id.imperial_option) {
//            val distanceValue: Float = entry.imperialDistance
//            "$distanceValue Miles"
//        } else {
//            val distanceValue: Float = entry.metricDistance
//            "$distanceValue Kilometers"
//        }
//
//        // retrieve and format time lapse
//        val duration: Float = entry.durationInMinutes
//        val minutes = Math.floor(duration.toDouble()).toInt()
//        val seconds = Math.floor(((duration - minutes) * 60).toDouble()).toInt()
//        var timeLapse = ""
//        if (minutes > 0) timeLapse += minutes.toString() + "mins "
//        timeLapse += seconds.toString() + "secs"
//
//        // merge distance and time lapse
//        val entryDetails = "$distance $timeLapse"
//
//        // set view text
//        val v1 = convertView.findViewById<TextView>(R.id.entry_type_time)
//        v1.text = entryTypeTime
//        val v2 = convertView.findViewById<TextView>(R.id.entry_details)
//        v2.text = entryDetails
//        convertView.setOnClickListener {
//            val entryIntent = Intent(mContext, MapEntryActivity::class.java)
//            val bundle = Bundle()
//            bundle.putString("activityType", activityType)
//            bundle.putString("calories", entry.getCalories().toString() + "")
//            bundle.putInt("unit", unit)
//            val locs = ArrayList<String>()
//            for (l in entry.getLocations()) {
//                locs.add(l[0].toString() + "," + l[1] + "," + l[2] + "," + l[3])
//            }
//            bundle.putStringArrayList("locations", locs)
//            bundle.putInt("id", entry.getId())
//            entryIntent.putExtras(bundle)
//            val activity = mContext as Activity
//            activity.startActivity(entryIntent)
//        }
//        return convertView
//    }

    fun startManualEntry(convertView: View, entry: Entry, unit: Int): View {
        val entryType: String = entry.entryType
        val activityType: String = entry.activityType
        val c: Calendar = entry.timeStamp

        // retrieve and format time stamp
        var hour = c[Calendar.HOUR_OF_DAY].toString() + ""
        var minute = c[Calendar.MINUTE].toString() + ""
        if (hour.length == 1) hour = "0$hour"
        if (minute.length == 1) minute = "0$minute"
        val timestamp = hour + ":" + minute + ":00 " +
                getMonthString(c[Calendar.MONTH]) + " " +
                c[Calendar.DAY_OF_MONTH] + " " + c[Calendar.YEAR]

        // merge to form main sentence
        val entryTypeTime = "$entryType: $activityType, $timestamp"

        // retrieve and format distance
        val distance: String
        distance = if (unit == 0 || unit == R.id.imperial_option) {
            val distanceValue: Float = entry.imperialDistance
            "$distanceValue Miles"
        } else {
            val distanceValue: Float = entry.metricDistance
            "$distanceValue Kilometers"
        }

        // retrieve and format time lapse
        val duration: Float = entry.durationInMinutes
        val minutes = Math.floor(duration.toDouble()).toInt()
        val seconds = Math.floor(((duration - minutes) * 60).toDouble()).toInt()
        var timeLapse = ""
        if (minutes > 0) timeLapse += minutes.toString() + "mins "
        timeLapse += seconds.toString() + "secs"

        // merge distance and time lapse
        val entryDetails = "$distance $timeLapse"

        // set view text
        val v1 = convertView.findViewById<TextView>(R.id.entry_type_time)
        v1.text = entryTypeTime
        val v2 = convertView.findViewById<TextView>(R.id.entry_details)
        v2.text = entryDetails

        // set on click listener
        val finalTimeLapse = timeLapse
        convertView.setOnClickListener {
            val entryIntent = Intent(mContext, ManualInputHistoryActivity::class.java)
            val bundle = Bundle()
            bundle.putString("entryType", entryType)
            bundle.putString("activityType", activityType)
            bundle.putString("timestamp", timestamp)
            bundle.putString("distance", distance)
            bundle.putString("duration", finalTimeLapse)
            bundle.putString("calories", entry.calories.toString() + "")
            bundle.putString("heartRate", entry.heartRate.toString() + "")
            bundle.putInt("id", entry.id)
            entryIntent.putExtras(bundle)
            val activity = mContext as Activity
            activity.startActivity(entryIntent)
        }
        return convertView
    }
}
