package com.example.a3dforge.activities

import android.R
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var bottomAppBar: BottomAppBar

    private lateinit var mainLogoImageView: ImageView
    private lateinit var houseMainImageView: ImageView
    private lateinit var printMainImageView: ImageView
    private lateinit var deliveryMainImageView: ImageView
    private lateinit var receivingMainImageView: ImageView

    private lateinit var mainLogoTextView: TextView
    private lateinit var limitationTextView: TextView
    private lateinit var fantasyTextView: TextView
    private lateinit var mainTextView: TextView


    var userLogin: String? = null
    var check = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.a3dforge.R.layout.activity_main)

        mainLogoImageView = findViewById(com.example.a3dforge.R.id.mainLogoImageView)
        houseMainImageView = findViewById(com.example.a3dforge.R.id.houseMainImageView)
        printMainImageView = findViewById(com.example.a3dforge.R.id.printMainImageView)
        deliveryMainImageView = findViewById(com.example.a3dforge.R.id.deliveryMainImageView)
        receivingMainImageView = findViewById(com.example.a3dforge.R.id.receivingMainImageView)

        mainLogoTextView = findViewById(com.example.a3dforge.R.id.mainLogoTextView)
        limitationTextView = findViewById(com.example.a3dforge.R.id.limitationTextView)
        fantasyTextView = findViewById(com.example.a3dforge.R.id.fantasyTextView)
        mainTextView = findViewById(com.example.a3dforge.R.id.mainTextView)

        val fullText = "твоя фантазія!"

        val spannableString = SpannableString(fullText)

        val startIndex = fullText.indexOf("фантазія")
        val endIndex = startIndex + "фантазія".length

        spannableString.setSpan(
            StyleSpan(Typeface.ITALIC),
            startIndex,
            endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        fantasyTextView.text = spannableString

        bottomNavigationView = findViewById(com.example.a3dforge.R.id.bottomNavigationView)
        bottomAppBar = findViewById(com.example.a3dforge.R.id.bottomAppBar)

        userLogin = intent.getStringExtra("user_login")

        bottomNavigationView.setOnItemSelectedListener { item ->
            var selectedFragment: Fragment? = null

            when (item.itemId) {
                com.example.a3dforge.R.id.home ->  {
                    selectedFragment = null

                    mainLogoImageView.visibility = View.VISIBLE
                    houseMainImageView.visibility = View.VISIBLE
                    printMainImageView.visibility = View.VISIBLE
                    deliveryMainImageView.visibility = View.VISIBLE
                    receivingMainImageView.visibility = View.VISIBLE

                    mainLogoTextView.visibility = View.VISIBLE
                    limitationTextView.visibility = View.VISIBLE
                    fantasyTextView.visibility = View.VISIBLE
                    mainTextView.visibility = View.VISIBLE
                }
                com.example.a3dforge.R.id.person -> {
                    selectedFragment = ProfileFragment()
                    val args = Bundle()
                    args.putString("userLogin", userLogin)
                    selectedFragment.arguments = args

                    mainLogoImageView.visibility = View.GONE
                    houseMainImageView.visibility = View.GONE
                    printMainImageView.visibility = View.GONE
                    deliveryMainImageView.visibility = View.GONE
                    receivingMainImageView.visibility = View.GONE

                    mainLogoTextView.visibility = View.GONE
                    limitationTextView.visibility = View.GONE
                    fantasyTextView.visibility = View.GONE
                    mainTextView.visibility = View.GONE
                }
                com.example.a3dforge.R.id.book -> {
                    selectedFragment = CatalogFragment()

                    mainLogoImageView.visibility = View.GONE
                    houseMainImageView.visibility = View.GONE
                    printMainImageView.visibility = View.GONE
                    deliveryMainImageView.visibility = View.GONE
                    receivingMainImageView.visibility = View.GONE

                    mainLogoTextView.visibility = View.GONE
                    limitationTextView.visibility = View.GONE
                    fantasyTextView.visibility = View.GONE
                    mainTextView.visibility = View.GONE
                }
            }

            if (selectedFragment != null) {
                supportFragmentManager.beginTransaction()
                    .replace(com.example.a3dforge.R.id.fragment_container, selectedFragment)
                    .commit()
            } else {
                supportFragmentManager.beginTransaction()
                    .replace(com.example.a3dforge.R.id.fragment_container, Fragment())
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

    fun hideBottomNavigationView() {
        bottomAppBar.visibility = View.GONE
        bottomNavigationView.visibility = View.GONE
    }

    fun showBottomNavigationView() {
        bottomAppBar.visibility = View.VISIBLE
        bottomNavigationView.visibility = View.VISIBLE
    }

}
