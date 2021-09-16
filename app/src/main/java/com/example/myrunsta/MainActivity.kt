package com.example.myrunsta

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {
    private var pager: ViewPager? = null
    private var pagerAdapter: MainAdapter? = null

    class MainAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        private val frags: ArrayList<Fragment> = ArrayList()
        private val tls: ArrayList<String> = ArrayList()

        override fun getCount(): Int {
            return frags.size
        }

        override fun getItem(position: Int): Fragment {
            return frags[position]
        }

        override fun getPageTitle(position: Int): CharSequence {
            return tls[position]
        }

        fun addFragment(frag: Fragment, title: String) {
            frags.add(frag)
            tls.add(title)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // retrieve views
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        pager = findViewById<ViewPager>(R.id.view_pager)
        prepareViewPager() // prepare view pager
        tabLayout.setupWithViewPager(pager)
    }

    private class PageChangeListener: ViewPager.OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int,
        ) {
            TODO("Not yet implemented")
        }

        override fun onPageSelected(position: Int) {
            TODO("Not yet implemented")
        }

        override fun onPageScrollStateChanged(state: Int) {
            TODO("Not yet implemented")
        }
    }

    private fun prepareViewPager() {
        // initialize PagerAdapter
        pagerAdapter = MainAdapter(supportFragmentManager)
        pagerAdapter?.addFragment(StartFragment(), "START")
        pagerAdapter?.addFragment(HistoryFragment(), "HISTORY")
        pagerAdapter?.addFragment(SettingsFragment(), "SETTINGS")
        pager?.setAdapter(pagerAdapter)
    }
}