package com.example.a3dforge.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.a3dforge.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    var userLogin: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        userLogin = intent.getStringExtra("user_login")

        bottomNavigationView.setOnItemSelectedListener { item ->
            var selectedFragment: Fragment? = null

            when (item.itemId) {
                R.id.home -> selectedFragment = null
                R.id.person -> {
                    selectedFragment = ProfileFragment()
                    val args = Bundle()
                    args.putString("userLogin", userLogin)
                    selectedFragment.arguments = args
                }
                R.id.book -> {
                    selectedFragment = CatalogFragment()
                }
            }

            if (selectedFragment != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit()
            } else {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, Fragment())
                    .commit()
            }

            true
        }
    }

    fun updateLogin(newLogin: String) {
        userLogin = newLogin
    }

    fun getUserLoginValue(): String {
        return userLogin!!
    }
}
