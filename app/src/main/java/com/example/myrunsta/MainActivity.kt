package com.example.myrunsta

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

@SuppressLint("InflateParams")
class MainActivity : FragmentActivity() {
    private lateinit var pager: ViewPager2
    private var pagerAdapter: MainAdapter? = null

    class MainAdapter(fa: FragmentActivity) :
        FragmentStateAdapter(fa) {

        override fun getItemCount(): Int {
            return 3
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> StartFragment()
                1 -> HistoryFragment()
                2 -> SettingsFragment()
                else -> throw Exception("Position out of Range")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // retrieve views
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        pager = findViewById(R.id.view_pager)
        pagerAdapter = MainAdapter(this)
        pager.adapter = pagerAdapter
        val strategy = TabLayoutMediator.TabConfigurationStrategy { tab, position ->
            val view = layoutInflater.inflate(R.layout.tab_view, null)
            when (position) {
                0       -> view.findViewById<TextView>(R.id.tab_text).text =
                    getString(R.string.start)
                1       -> view.findViewById<TextView>(R.id.tab_text).text =
                    getString(R.string.history)
                2       -> view.findViewById<TextView>(R.id.tab_text).text =
                    getString(R.string.settings)
                else    -> throw Exception("Position out of Range")
            }
            tab.customView = view
        }
        TabLayoutMediator(tabLayout, pager, strategy).attach()
    }

    override fun onBackPressed() {
        if (pager.currentItem == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed()
        } else {
            // Otherwise, select the previous step.
            pager.currentItem = pager.currentItem - 1
        }
    }

}