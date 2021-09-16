package com.example.myrunsta

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
class ManualInputActivity : AppCompatActivity() {
    private var preferences: SharedPreferences? = null
    private var datePickerDialog: DatePickerDialog? = null
    private var timePickerDialog: AlertDialog? = null
    private var textDialog: AlertDialog? = null
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
                val date = LocalDate.now()
                val preYear = preferences?.getInt("year", date.year)!!
                val preMonth = preferences?.getInt("month", date.monthValue - 1)!!
                val preDayOfMonth = preferences?.getInt("dayOfMonth", date.dayOfMonth)!!

                // instantiate date picker
                datePickerDialog =
                    DatePickerDialog(this, null, preYear, preMonth, preDayOfMonth)

                // apply changes on ok click
                datePickerDialog?.setButton(DialogInterface.BUTTON_POSITIVE, "OK") { dialog, _ ->

                    // apply changes on ok click
                    removeLabel()
                    dialog.dismiss() // dismiss dialog
                }
                // cancel changes on cancel click
                datePickerDialog?.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL"
                ) { dialog, which ->

                    // cancel changes on cancel click
                    if (which == DialogInterface.BUTTON_NEGATIVE) { // get the shared preferences and make an editor
                        val edit: SharedPreferences.Editor? = preferences?.edit()

                        // put in current date in case no changes are met
                        edit?.putInt("year", preYear)
                        edit?.putInt("month", preMonth)
                        edit?.putInt("dayOfMonth", preDayOfMonth)
                        edit?.apply()
                    }
                    removeLabel()
                    dialog.dismiss() // dismiss dialog
                }
                val dp: DatePicker? = datePickerDialog?.datePicker
                dp?.setOnDateChangedListener { _, year, monthOfYear, dayOfMonth -> // get the shared preferences and make an editor
                    val edit: SharedPreferences.Editor? = preferences?.edit()
                    edit?.putInt("year", year)
                    edit?.putInt("month", monthOfYear)
                    edit?.putInt("dayOfMonth", dayOfMonth)
                    edit?.apply()
                }
                datePickerDialog!!.show()
                datePickerDialog!!.getButton(DialogInterface.BUTTON_POSITIVE)
                    .setTextColor(getColor(R.color.colorAccent))
                datePickerDialog!!.getButton(DialogInterface.BUTTON_NEGATIVE)
                    .setTextColor(getColor(R.color.colorAccent))
            }
            "time" -> {
                val localTime = LocalTime.now()
                val timePicker = TimePicker(this)
                timePicker.setIs24HourView(LocalTime.MAX.hour == 23)

                // set initial hour and minute
                timePicker.hour = preferences?.getInt("hour", localTime.hour)!!
                timePicker.minute = preferences?.getInt("minute", localTime.minute)!!

                // make alert dialog with time picker widget
                val timePickerBuilder = AlertDialog.Builder(this)
                timePickerBuilder.setPositiveButton("OK"
                ) { dialog, _ ->
                    removeLabel()
                    dialog.dismiss() // dismiss dialog
                }
                timePickerBuilder.setNegativeButton("CANCEL"
                ) { dialog, _ ->
                    // get the shared preferences and make an editor
                    val edit: SharedPreferences.Editor? = preferences?.edit()

                    //put in current date in case no changes are met
                    edit?.remove("hour")
                    edit?.remove("minute")
                    edit?.apply()
                    removeLabel()
                    dialog.dismiss() // dismiss dialog
                }
                timePickerBuilder.setView(timePicker)
                timePicker.setOnTimeChangedListener { _, hourOfDay, minute ->
                    val edit: SharedPreferences.Editor? = preferences?.edit()
                    edit?.putInt("hour", hourOfDay)
                    edit?.putInt("minute", minute)
                    edit?.apply()
                }
                timePickerDialog = timePickerBuilder.create()
                timePickerDialog?.show()
                timePickerDialog?.getButton(AlertDialog.BUTTON_NEGATIVE)
                    ?.setTextColor(getColor(R.color.colorAccent))
                timePickerDialog?.getButton(AlertDialog.BUTTON_POSITIVE)
                    ?.setTextColor(getColor(R.color.colorAccent))
            }
            "duration", "distance", "calories", "heart rate", "comment" -> {
                // Use the Builder class for convenient dialog construction
                val builder = AlertDialog.Builder(this)

                // inflate view using view inflate
                val view = View.inflate(this, R.layout.fragment_text_dialog_view, null)

                // set label of view
                val viewLabel = view.findViewById<TextView>(R.id.text_dialog_label)
                viewLabel.text = label

                // get edit text  in view and set the value to what is in preferences @label
                val editText = view.findViewById<EditText>(R.id.dialog_text)
                editText.setText(preferences?.getString(label?.lowercase(Locale.getDefault()), ""))

                // set input type (also set input hint in case of comments is clicked
                if (label?.lowercase(Locale.getDefault()) == "comment") {
                    editText.inputType = InputType.TYPE_CLASS_TEXT
                    editText.hint = "How did it go? Notes here."
                } else if (label?.lowercase(Locale.getDefault()) == "calories" || label?.lowercase(
                        Locale.getDefault()) == "heart rate") {
                    editText.inputType = InputType.TYPE_CLASS_NUMBER
                }

                // set cancel button on click listener; use dialog dismiss
                builder.setNegativeButton("CANCEL"
                ) { dialog, _ -> // dismiss dialog
                    removeLabel()
                    dialog.dismiss()
                }
                // set ok button on click listener; use dialog dismiss
                builder.setPositiveButton("OK"
                ) { dialog, _ -> // get the shared preferences and make an editor
                    val edit: SharedPreferences.Editor? = preferences?.edit()
                    edit?.putString(label?.lowercase(Locale.getDefault()), editText.text.toString())
                    // apply change
                    edit?.apply()

                    // dismiss dialog
                    removeLabel()
                    dialog.dismiss()
                }

                // set builder view to view and get dialog
                builder.setView(view)
                textDialog = builder.create()
                textDialog?.show()
                textDialog?.getButton(DialogInterface.BUTTON_NEGATIVE)
                    ?.setTextColor(getColor(R.color.colorAccent))
                textDialog?.getButton(DialogInterface.BUTTON_POSITIVE)
                    ?.setTextColor(getColor(R.color.colorAccent))
            }
            else -> Log.d("unknown", "Method received unknown input. Potential Threat!")
        }
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

    private fun removeLabel() {
        label = null
        val edit = preferences!!.edit()
        edit.remove("label")
        edit.apply()
    }
}