package com.example.myrunsta

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.lang.Exception
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
class ManualInputActivity : AppCompatActivity() {
    private var preferences: SharedPreferences? = null
    private var label: String? = null
    class ManualInputAdapter(context: Context, resource: Int, md: () -> Unit, l: (String) -> Unit): ArrayAdapter<String>(context, resource) {
        private val rec = resource
        private var labels = arrayOf("Date", "Time", "Duration", "Distance", "Calories", "Heart Rate", "Comment")
        private val setLabel = l
        private val makeDialog = md

        override fun getCount(): Int {
            return labels.size
        }

        override fun getItem(position: Int): String {
            return labels[position]
        }

        fun setUp(activity: Activity, cancelBtn: View, saveBtn: View, preferences: SharedPreferences) {
            cancelBtn.setOnClickListener {
                activity.finish()
                Toast.makeText(context, "Entry discarded", Toast.LENGTH_SHORT).show()
            }
            saveBtn.setOnClickListener {
                activity.finish()
                (activity as ManualInputActivity).onSave()
                Toast.makeText(context, "Entry Saved", Toast.LENGTH_SHORT).show()
            }
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val inflater = LayoutInflater.from(context)
            @SuppressLint("ViewHolder")
            val row: View = inflater.inflate(rec, parent, false)
            val buttonText = row.findViewById<TextView>(R.id.button_text)
            buttonText.text = labels[position]
            row.setOnClickListener { v -> viewClick(v) }

            // return the row view

            // return the row view
            return row
        }

        private fun viewClick(v: View) {
            val textView = v.findViewById<TextView>(R.id.button_text)
            setLabel(textView.text.toString())
            makeDialog()
        }
    }

    private fun makeDialog() {
        when (label?.lowercase(Locale.getDefault())) {
            "date" -> {
                MyDatePickerDialog().show(supportFragmentManager, "WorkingFragment")
            }
            "time" -> {
                MyTimePickerDialog().show(supportFragmentManager, "WorkingFragment")
            }
            "duration", "distance", "calories", "heart rate", "comment" -> {
                val edit = preferences?.edit()
                edit?.putString("label", label)
                edit?.apply()
                GeneralDialog().show(supportFragmentManager, "WorkingFragment")
            }
            else -> Log.d("unknown", "Method received unknown input. Potential Threat!")
        }
    }

    private fun onSave() {
        val entryType = "Manual Entry"
        val activityType = intent.getStringExtra(Activity_Type)!!
        val comment = preferences!!.getString("comment", "")
        val distance = Objects.requireNonNull(
            preferences!!.getString("distance", "0")!!).toFloat()
        val duration = Objects.requireNonNull(preferences!!.getString("duration", "0")!!).toFloat()

        val c = Calendar.getInstance()
        val d = LocalDate.now()
        val t = LocalTime.now()
        c[Calendar.YEAR] = preferences!!.getInt("year", d.year)
        c[Calendar.MONTH] = preferences!!.getInt("month", d.monthValue)
        c[Calendar.DAY_OF_MONTH] = preferences!!.getInt("dayOfMonth", d.dayOfMonth)
        c[Calendar.HOUR_OF_DAY] = preferences!!.getInt("hour", t.hour)
        c[Calendar.MINUTE] = preferences!!.getInt("minute", t.minute)
        c[Calendar.SECOND] = 0

        val calories = Objects.requireNonNull(
            preferences!!.getString("calories", "0")!!).toInt()
        val heartRate = Objects.requireNonNull(preferences!!.getString("heart rate", "0")!!).toInt()

        val entry = Entry(entryType, activityType, comment!!,
            distance, duration, c, calories, heartRate)

        val db = DataBaseUtil(this)

        // insert cv into the table

        // insert cv into the table
        val thread = Thread {
            try {
                db.addManualEntry(entry)

                // indicate that the data source has been updated
                val edit = preferences!!.edit()
                edit.putBoolean("DB_UPDATED", true)
                edit.apply()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        thread.start()
        clearPreferences()
        removeLabel(this)
        finish()
    }

    private fun clearPreferences() {

        // array keys used in the shared preferences
        val keys = arrayOf("year", "month", "dayOfMonth", "hour", "minute", "duration",
            "distance", "calories", "heart rate", "comment")
        // editor
        val edit = preferences!!.edit();
        // loop through and remove all keys
        for(key in keys) {
            if(preferences!!.contains(key)) edit.remove(key);
        }
        // apply removals
        edit.apply();
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_entry)
        val lv = findViewById<ListView>(R.id.entry_list_view)
        val entryAdapter = ManualInputAdapter(this, R.layout.manual_entry_list_item, ::makeDialog) { l: String ->
            label = l
        }
        lv.adapter = entryAdapter
        preferences = getSharedPreferences(MANUAL_INPUT_PREFS, MODE_PRIVATE)
        entryAdapter.setUp(this, findViewById(R.id.cancel_button), findViewById(R.id.save_button), preferences!!)
        label = savedInstanceState?.getString("label", null)
        if (label != null) {
            makeDialog()
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        if(label != null) {
            outState.putString("label", label)
        }
        super.onSaveInstanceState(outState, outPersistentState)
    }
}