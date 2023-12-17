package com.example.a3dforge.activities

import OkHttpConfig
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.a3dforge.R
import com.example.a3dforge.entities.Item
import com.example.a3dforge.factories.ProfileViewModelFactory
import com.example.a3dforge.models.ProfileViewModel

class ConfirmOrderFragment : Fragment() {

    private lateinit var backToCartArrowImageView: ImageView

    private lateinit var confirmOrderUserInfoTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_confirm_order, container, false)

        backToCartArrowImageView = view.findViewById(R.id.backToCartArrowImageView)

        confirmOrderUserInfoTextView = view.findViewById(R.id.confirmOrderUserInfoTextView)

        backToCartArrowImageView.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CartFragment())
                .commit()
        }

        val okHttpConfig = OkHttpConfig

        val profileViewModel = ViewModelProvider(this, ProfileViewModelFactory(okHttpConfig, "self")).get(
            ProfileViewModel::class.java)

        profileViewModel.profileResult.observe(viewLifecycleOwner) { profile ->
            val userData = profile.second?.data

            val userInfo = buildString {
                append("${userData?.firstName} ${userData?.midName} ${userData?.lastName}\n")
                append("${userData?.phoneNumber}\n")

                val address = userData?.let {
                    listOf(
                        if (it.street == "null") null else it.street,
                        if (it.house == "null") null else it.house,
                        if (it.apartment == "null") null else it.apartment,
                        if (it.cityRegion == "null") null else it.cityRegion,
                        if (it.country == "null") null else it.country,
                        if (it.city == "null") null else it.city
                    )
                }

                if (address != null) {
                    val filteredAddress = address.filterNotNull().filter { it.isNotBlank() }
                    if (filteredAddress.isNotEmpty()) {
                        val formattedAddress = filteredAddress.joinToString(", ")
                        append("$formattedAddress\n")
                    }
                }
            }

            confirmOrderUserInfoTextView.text = userInfo
        }


        profileViewModel.getProfile()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()

        (requireActivity() as MainActivity).showBottomNavigationView()
    }

}