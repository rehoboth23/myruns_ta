package com.example.myrunsta

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ManualInputHistoryActivity: AppCompatActivity() {
    var id = 0
    var entryType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)
        val bundle: Bundle = getIntent().getExtras()!!
        entryType = bundle.getString("entryType")
        id = bundle.getInt("id")
        val info = arrayOf(entryType!!,
            bundle.getString("activityType", ""),
            bundle.getString("timestamp", ""),
            bundle.getString("duration", ""),
            bundle.getString("distance", ""),
            bundle.getString("calories", ""),
            bundle.getString("heartRate", ""))
        val lv: ListView = findViewById(R.id.entry_list_view)
        val entryAdapter = EntryAdapter(applicationContext, R.layout.layout_entry_item, info)
        lv.divider = null
        lv.adapter = entryAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = getMenuInflater()
        inflater.inflate(R.menu.entry_menu, menu) // inflate custom menu
        val item = menu.findItem(R.id.delete)
        item.setOnMenuItemClickListener {
            if (id != -1) {
                val util = DataBaseUtil(getApplicationContext())
                util.deleteEntry(id, entryType!!)
                finish()
            }
            val preferences: SharedPreferences =
                getSharedPreferences(MANUAL_INPUT_PREFS, Context.MODE_PRIVATE)
            val edit = preferences.edit()
            edit.putBoolean("DB_UPDATED", true)
            edit.apply()
            id != -1
        }
        return true
    }
}

private class EntryAdapter(private var mContext: Context, private var resource: Int,
                           private var objects: Array<String>) :
    ArrayAdapter<String?>(mContext, resource, objects) {
    private val labels = arrayOf("Input Type", "Activity Type", "Date and Time", "Duration",
        "Distance", "Calories", "HeartRate")

    @SuppressLint("ViewHolder", "InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(mContext)
        val view = inflater.inflate(resource, null, true)
        val t = view.findViewById<TextView>(R.id.field_label)
        val e = view.findViewById<EditText>(R.id.field_value)

        // set label
        t.text = labels[position]
        e.setText(objects[position])
        e.keyListener = null
        e.isFocusable = true
        e.isCursorVisible = false
        return view
    }
}