package com.example.multiwebapplications

import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.multiwebapplications.databinding.ActivityMainBinding
import com.example.multiwebapplications.fragaments.Fragment1
import com.example.multiwebapplications.fragaments.Fragment2
import com.example.multiwebapplications.fragaments.Fragment3
import com.example.multiwebapplications.fragaments.Fragment4
import nl.joery.animatedbottombar.AnimatedBottomBar

class MainActivity : AppCompatActivity() {
    private val binding:ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        switchFragment(Fragment1())


        binding.bottomBar.setOnTabSelectListener(object : AnimatedBottomBar.OnTabSelectListener {
            override fun onTabSelected(
                lastIndex: Int,
                lastTab: AnimatedBottomBar.Tab?,
                newIndex: Int,
                newTab: AnimatedBottomBar.Tab
            ) {
                when (newIndex) {
                    0 -> {
                        // Handle actions for the first tab
                        switchFragment(Fragment1())
                        true
                    }
                    1 -> {
                        switchFragment(Fragment2())
                        true
                    }
                    2 -> {
                        switchFragment(Fragment3())
                        true
                    }
                    3 -> {
                        switchFragment(Fragment4())
                        true
                    }
                    else ->
                        false
                    // Add more cases for additional tabs as needed
                }


                Log.d("bottom_bar", "Selected index: $newIndex, title: ${newTab.title}")
            }

            // An optional method that will be fired whenever an already selected tab has been selected again.
            override fun onTabReselected(index: Int, tab: AnimatedBottomBar.Tab) {
                Log.d("bottom_bar", "Reselected index: $index, title: ${tab.title}")
            }
        })




    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit()
    }
}