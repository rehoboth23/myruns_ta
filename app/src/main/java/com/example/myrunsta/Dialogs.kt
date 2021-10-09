package com.example.myrunsta

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

// unit preferences dialog fragment
class UnitDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        // get activity and assert not null
        val activity: FragmentActivity? = activity

        // Use the Builder class for convenient dialog construction
        val builder = activity?.let { AlertDialog.Builder(it) }

        // initialize layout inflater and get view
        val layoutInflater = activity?.layoutInflater
        val view: View? = layoutInflater?.inflate(R.layout.fragment_units_dialog_view, null)

        // set cancel button on click listener; use dialog dismiss
        view?.findViewById<View>(R.id.unit_dialog_cancel)?.setOnClickListener {
            val dialog = this@UnitDialog.dialog!!
            dialog.dismiss()
        }

        // get radio group
        val unitTypes = view?.findViewById<RadioGroup>(R.id.unit_types)

        // get shared preferences
        val preferences = activity?.getSharedPreferences(
            MAIN_PREFERENCES, Context.MODE_PRIVATE
        )

        // set checked unit
        preferences?.getInt(UNIT_TYPE, -1)?.let { unitTypes?.check(it) }
        unitTypes?.setOnCheckedChangeListener { group, _ -> // get activity and assert not null
            val act: FragmentActivity? = getActivity()
            val pref = act?.getSharedPreferences(
                MAIN_PREFERENCES, Context.MODE_PRIVATE
            )
            val edit = pref?.edit()
            edit?.putInt(UNIT_TYPE, group.checkedRadioButtonId)
            edit?.apply() // apply changes
            val dialog = this@UnitDialog.dialog!!
            dialog.cancel()
        }
        // set builder view
        builder?.setView(view)
        return builder?.create()!!
    }
}

// comments dialog fragment
class CommentsDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        // get activity and assert not null
        val activity: FragmentActivity? = activity

        // Use the Builder class for convenient dialog construction
        val builder = activity?.let { AlertDialog.Builder(it) }

        // initialize layout inflater and get view
        val layoutInflater = activity?.layoutInflater
        val view: View? = layoutInflater?.inflate(R.layout.fragment_text_dialog_view, null)

        // get shared preferences
        val preferences = activity?.getSharedPreferences(
            MAIN_PREFERENCES, Context.MODE_PRIVATE
        )

        // get edit text for the comment and set comment text available
        val settingsComment = view?.findViewById<EditText>(R.id.dialog_text)
        settingsComment?.setText(preferences?.getString(COMMENT_TEXT, ""))
        settingsComment?.inputType = InputType.TYPE_CLASS_TEXT

        // set cancel button on click listener
        builder?.setNegativeButton("CANCEL"
        ) { dialog, _ -> // dismiss the dialog;
            dialog.dismiss()
        }
        // set ok button on click listener
        builder?.setPositiveButton("OK"
        ) { dialog, _ -> // get activity and assert not null
            val act: FragmentActivity? = getActivity()
            val pref = act?.getSharedPreferences(
                MAIN_PREFERENCES, Context.MODE_PRIVATE
            )
            val edit = pref?.edit()
            // put text in editor
            edit?.putString(COMMENT_TEXT, settingsComment?.text.toString())
            edit?.apply() // apply changes
            dialog.dismiss()
        }

        // set builder view
        builder?.setView(view)
        return builder?.create()!!
    }

    @RequiresApi(VERSION_CODES.M)
    override fun onStart() {
        super.onStart()
        val dialog = (dialog as AlertDialog?)!!
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(requireActivity().getColor(R.color.colorAccent))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(requireActivity().getColor(R.color.colorAccent))
    }
}

class MyTimePickerDialog : DialogFragment() {
    @RequiresApi(VERSION_CODES.O)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val preferences = requireContext().getSharedPreferences(MANUAL_INPUT_PREFS,
            AppCompatActivity.MODE_PRIVATE)
        val localTime = LocalTime.now()
        val timePicker = TimePicker(requireContext())
        timePicker.setIs24HourView(LocalTime.MAX.hour == 23)
        // set initial hour and minute
        timePicker.hour = preferences.getInt("hour", localTime.hour)
        timePicker.minute = preferences.getInt("minute", localTime.minute)
        // make alert dialog with time picker widget
        val timePickerBuilder = AlertDialog.Builder(requireActivity())
        timePickerBuilder.setPositiveButton("OK"
        ) { dialog, _ ->
                removeLabel(requireContext())
            dialog.dismiss() // dismiss dialog
        }
        timePickerBuilder.setNegativeButton("CANCEL"
        ) { dialog, _ ->
            removeLabel(requireContext())
            // get the shared preferences and make an editor
            val edit: SharedPreferences.Editor? = preferences.edit()

            //put in current date in case no changes are met
            edit?.remove("hour")
            edit?.remove("minute")
            edit?.apply()
            dialog.dismiss() // dismiss dialog
        }
        timePickerBuilder.setView(timePicker)
        timePicker.setOnTimeChangedListener { _, hourOfDay, minute ->
            val edit: SharedPreferences.Editor? = preferences.edit()
            edit?.putInt("hour", hourOfDay)
            edit?.putInt("minute", minute)
            edit?.apply()
        }
        val timePickerDialog = timePickerBuilder.create()
        timePickerDialog.show()
        timePickerDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            ?.setTextColor(requireActivity().getColor(R.color.colorAccent))
        timePickerDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            ?.setTextColor(requireActivity().getColor(R.color.colorAccent))
        return timePickerDialog
    }
}

class MyDatePickerDialog : DialogFragment() {
    @RequiresApi(VERSION_CODES.O)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val preferences = requireContext().getSharedPreferences(MANUAL_INPUT_PREFS,
            AppCompatActivity.MODE_PRIVATE)
        val date = LocalDate.now()
        val preYear = preferences.getInt("year", date.year)
        val preMonth = preferences.getInt("month", date.monthValue - 1)
        val preDayOfMonth = preferences.getInt("dayOfMonth", date.dayOfMonth)

        // instantiate date picker
        val datePickerDialog =
            DatePickerDialog(requireContext(), null, preYear, preMonth, preDayOfMonth)

        // apply changes on ok click
        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK") { dialog, _ ->
            // apply changes on ok click
            removeLabel(requireContext())
            dialog.dismiss() // dismiss dialog
        }
        // cancel changes on cancel click
        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL"
        ) { dialog, which ->
            removeLabel(requireContext())
            // cancel changes on cancel click
            if (which == DialogInterface.BUTTON_NEGATIVE) { // get the shared preferences and make an editor
                val edit: SharedPreferences.Editor? = preferences.edit()

                // put in current date in case no changes are met
                edit?.putInt("year", preYear)
                edit?.putInt("month", preMonth)
                edit?.putInt("dayOfMonth", preDayOfMonth)
                edit?.apply()
            }
            dialog.dismiss() // dismiss dialog
        }
        val dp: DatePicker = datePickerDialog.datePicker
        dp.setOnDateChangedListener { _, year, monthOfYear, dayOfMonth -> // get the shared preferences and make an editor
            val edit: SharedPreferences.Editor? = preferences.edit()
            edit?.putInt("year", year)
            edit?.putInt("month", monthOfYear)
            edit?.putInt("dayOfMonth", dayOfMonth)
            edit?.apply()
        }
        datePickerDialog.show()
        datePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE)
            .setTextColor(requireActivity().getColor(R.color.colorAccent))
        datePickerDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
            .setTextColor(requireActivity().getColor(R.color.colorAccent))

        return datePickerDialog
    }
}

class GeneralDialog: DialogFragment() {
    @RequiresApi(VERSION_CODES.O)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val preferences = requireContext().getSharedPreferences(MANUAL_INPUT_PREFS,
            AppCompatActivity.MODE_PRIVATE)
        val builder = AlertDialog.Builder(requireContext())

        // inflate view using view inflate
        val view = View.inflate(requireContext(),
            R.layout.fragment_text_dialog_view, null)

        // set label of view
        val viewLabel = view.findViewById<TextView>(R.id.text_dialog_label)
        val label = preferences.getString("label", "")
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
            removeLabel(requireContext())
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
            removeLabel(requireContext())
            dialog.dismiss()
        }

        // set builder view to view and get dialog
        builder.setView(view)
        val textDialog = builder.create()
        textDialog.show()
        textDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
            ?.setTextColor(requireActivity().getColor(R.color.colorAccent))
        textDialog.getButton(DialogInterface.BUTTON_POSITIVE)
            ?.setTextColor(requireActivity().getColor(R.color.colorAccent))
        return textDialog
    }
}