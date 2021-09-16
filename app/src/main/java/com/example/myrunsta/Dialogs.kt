package com.example.myrunsta

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.RadioGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity

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
        unitTypes?.setOnCheckedChangeListener { group, checkedId -> // get activity and assert not null
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
        ) { dialog, which -> // dismiss the dialog;
            dialog.dismiss()
        }
        // set ok button on click listener
        builder?.setPositiveButton("OK"
        ) { dialog, which -> // get activity and assert not null
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

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStart() {
        super.onStart()
        val dialog = (dialog as AlertDialog?)!!
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(requireActivity().getColor(R.color.colorAccent))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(requireActivity().getColor(R.color.colorAccent))
    }
}