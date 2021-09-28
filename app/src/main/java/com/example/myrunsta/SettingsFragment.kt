package com.example.myrunsta

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

class SettingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        val activity: FragmentActivity? = activity
        val preferences = activity?.getSharedPreferences(
            MAIN_PREFERENCES, Context.MODE_PRIVATE
        )
        val checkBox = view.findViewById<CheckBox>(R.id.privacy_setting_checkbox)
        checkBox.isChecked = preferences?.getBoolean(PRIVACY, false)!!
        view.findViewById<View>(R.id.user_profile_clickable).setOnClickListener {
            val profileIntent = Intent(context, ProfileActivity::class.java)
            startActivity(profileIntent)
        }
        val listener =  {
            val act: FragmentActivity? = getActivity()
            val pref = act?.getSharedPreferences(
                MAIN_PREFERENCES, Context.MODE_PRIVATE
            )
            val edit = pref?.edit()
            edit?.putBoolean(PRIVACY, checkBox.isChecked)
            edit?.apply() // apply changes
        }
        view.findViewById<View>(R.id.anonymous_setting_clickable).setOnClickListener {
            checkBox.isChecked = !checkBox.isChecked
            listener()
        }
        view.findViewById<View>(R.id.privacy_setting_checkbox).setOnClickListener{
            listener()
        }
        view.findViewById<View>(R.id.unit_preferences_clickable)
            .setOnClickListener { openUnitDialog() }
        view.findViewById<View>(R.id.comments_clickable).setOnClickListener { openCommentsDialog() }
        view.findViewById<View>(R.id.webpage_clickable).setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW,
                Uri.parse(getString(R.string.webpage_link))
            )
            startActivity(browserIntent)
        }
        return view
    }

    private fun openUnitDialog() {
        // make new dialog using unit dialog fragment from main activity
        val dialog = UnitDialog()
        // get fragment manager and assert not null
        val fragmentManager = childFragmentManager
        // show dialog
        dialog.show(fragmentManager, UNIT_DIALOG_TAG)
    }

    private fun openCommentsDialog() {
        // make new dialog using comments dialog fragment from main activity
        val dialog = CommentsDialog()
        // get fragment manager and assert not null
        val fragmentManager = childFragmentManager
        // show dialog
        dialog.show(fragmentManager, COMMENTS_DIALOG_TAG)
    }
}