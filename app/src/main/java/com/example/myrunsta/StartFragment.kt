package com.example.myrunsta

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import java.util.*

class StartFragment : Fragment() {
    private var ctx: Context? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_start, container, false)
        ctx = context

        // get spinners and assign to variables
        val inputTypeSpinner = view.findViewById<Spinner>(R.id.input_type_spinner)
        val activityTypeSpinner = view.findViewById<Spinner>(R.id.activity_type_spinner)

        // make array adapters for the spinners
        val inputTypeAdapter = ArrayAdapter.createFromResource(view.context,
            R.array.input_type_options, android.R.layout.simple_spinner_dropdown_item)
        val activityTypeAdapter = ArrayAdapter.createFromResource(view.context,
            R.array.activity_type_options, android.R.layout.simple_spinner_dropdown_item)

        // specify layout to use when list of options appears
        inputTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        activityTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // link adapters to spinners
        inputTypeSpinner.adapter = inputTypeAdapter
        activityTypeSpinner.adapter = activityTypeAdapter

        // set on click listener for start
        view.findViewById<View>(R.id.start_button).setOnClickListener {
            // get spinner and get spinner value
            val inputType = inputTypeSpinner.selectedItem.toString().lowercase(Locale.getDefault())
            if (inputType == "manual entry") {
                val manualInputIntent = Intent(activity, ManualInputActivity::class.java)
                // add activity type to intent
                manualInputIntent.putExtra("activity_type",
                    activityTypeSpinner.selectedItem.toString())
                startActivity(manualInputIntent)
            } else {
                val gpsInputIntent = Intent(activity, GpsInputActivity::class.java)
                if (inputType == "automatic") {
                    // add activity type to intent
                    gpsInputIntent.putExtra("entry_type", "Automatic")

                    // add activity type to intent
                    gpsInputIntent.putExtra("activity_type",
                        "Unknown")
                } else {
                    // add activity type to intent
                    gpsInputIntent.putExtra("activity_type",
                        activityTypeSpinner.selectedItem.toString())

                    // add activity type to intent
                    gpsInputIntent.putExtra("entry_type", "GPS")
                }

                // discard any lingering prefs
                val prefs = ctx!!.getSharedPreferences(GPS_PREFS,
                    Context.MODE_PRIVATE)
                val edit = prefs.edit()
                edit.remove(PREV_LOC)
                edit.apply()
                startActivity(gpsInputIntent)
            }
        }
        return view
    }
}