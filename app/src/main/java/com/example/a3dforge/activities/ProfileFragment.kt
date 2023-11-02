package com.example.a3dforge.activities

import OkHttpConfig
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.a3dforge.R
import com.example.a3dforge.factories.AvatarViewModelFactory
import com.example.a3dforge.factories.ProfileViewModelFactory
import com.example.a3dforge.models.AvatarViewModel
import com.example.a3dforge.models.ProfileViewModel

class ProfileFragment : Fragment() {

    private lateinit var userNameView: TextView
    private lateinit var userLogin: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        userNameView = view.findViewById(R.id.userNameView)
        userLogin = arguments?.getString("userLogin") ?: ""

        val okHttpConfig = OkHttpConfig

        val profileViewModel = ViewModelProvider(this, ProfileViewModelFactory(okHttpConfig, userLogin)).get(ProfileViewModel::class.java)

        profileViewModel.profileResult.observe(viewLifecycleOwner) { profile ->
            if (profile != null) {
                val userData = profile.second
                if (userData != null) {
                    userNameView.text = userData.data.login
                } else {
                    userNameView.text = "Failed to load profile"
                }
            } else {
                userNameView.text = "Failed to load profile"
            }
        }

        val avatarViewModel = ViewModelProvider(this, AvatarViewModelFactory(okHttpConfig, userLogin)).get(AvatarViewModel::class.java)

        avatarViewModel.avatarResult.observe(viewLifecycleOwner) { avatar ->
            println(avatar.second)
        }

        profileViewModel.getProfile()

        avatarViewModel.getAvatar()

        return view
    }
}
