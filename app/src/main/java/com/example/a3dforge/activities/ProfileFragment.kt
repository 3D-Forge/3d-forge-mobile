package com.example.a3dforge.activities

import OkHttpConfig
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.a3dforge.R
import com.example.a3dforge.base.AvatarProcessor
import com.example.a3dforge.factories.AvatarViewModelFactory
import com.example.a3dforge.factories.ProfileViewModelFactory
import com.example.a3dforge.models.AvatarViewModel
import com.example.a3dforge.models.ProfileViewModel

class ProfileFragment : Fragment() {

    private lateinit var userNameView: TextView
    private lateinit var addressInfoTextView: TextView
    private lateinit var basicInfoTextView: TextView
    private lateinit var fullNameEditText: EditText
    private lateinit var emailInfoEditText: EditText
    private lateinit var phoneInfoEditText: EditText
    private lateinit var cityInfoEditText: EditText
    private lateinit var streetInfoEditText: EditText
    private lateinit var logoutImageView: ImageView
    private lateinit var editProfileImageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        userNameView = view.findViewById(R.id.userNameView)
        addressInfoTextView = view.findViewById(R.id.addressInfoTextView)
        basicInfoTextView = view.findViewById(R.id.basicInfoTextView)
        logoutImageView = view.findViewById(R.id.logoutImageView)
        editProfileImageView = view.findViewById(R.id.editProfileImageView)
        fullNameEditText = view.findViewById(R.id.fullNameEditText)
        emailInfoEditText = view.findViewById(R.id.emailInfoEditText)
        phoneInfoEditText = view.findViewById(R.id.phoneInfoEditText)
        cityInfoEditText = view.findViewById(R.id.cityInfoEditText)
        streetInfoEditText = view.findViewById(R.id.streetInfoEditText)

        basicInfoTextView.paintFlags = basicInfoTextView.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        addressInfoTextView.paintFlags = addressInfoTextView.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        val userLogin = (requireActivity() as MainActivity).getUserLoginValue()

        val okHttpConfig = OkHttpConfig

        val profileViewModel = ViewModelProvider(this, ProfileViewModelFactory(okHttpConfig, userLogin!!)).get(ProfileViewModel::class.java)

        profileViewModel.profileResult.observe(viewLifecycleOwner) { profile ->
            if (profile != null) {
                val userData = profile.second
                if (userData != null) {
                    userNameView.text = userData.data.login
                    val email = userData.data.email
                    val emailEditable = Editable.Factory.getInstance().newEditable(email)
                    emailInfoEditText.text = emailEditable
                    if (userData.data.firstName != "null") {
                        val fullName = userData.data.middleName + " " + userData.data.firstName + " " + userData.data.lastName
                        val fullNameEditable = Editable.Factory.getInstance().newEditable(fullName)
                        fullNameEditText.text = fullNameEditable
                    }
                    if (userData.data.phone != "null"){
                        val phone = userData.data.phone
                        val phoneEditable = Editable.Factory.getInstance().newEditable(phone)
                        phoneInfoEditText.text = phoneEditable
                    }
                    if (userData.data.city != "null"){
                        val city = userData.data.city
                        val cityEditable = Editable.Factory.getInstance().newEditable(city)
                        cityInfoEditText.text = cityEditable
                    }
                    if (userData.data.street != "null"){
                        val street = userData.data.street + ", " + userData.data.house
                        val streetEditable = Editable.Factory.getInstance().newEditable(street)
                        streetInfoEditText.text = streetEditable
                    }
                } else {
                    userNameView.text = "Failed to load profile"
                }
            } else {
                userNameView.text = "Failed to load profile"
            }
        }

        val avatarViewModel = ViewModelProvider(this, AvatarViewModelFactory(okHttpConfig, userLogin)).get(AvatarViewModel::class.java)

        avatarViewModel.avatarResult.observe(viewLifecycleOwner) { avatar ->
            val imageBytes: ByteArray? = avatar.second

            val imageBitmap: Bitmap =
                imageBytes?.let { BitmapFactory.decodeByteArray(imageBytes, 0, it.size) }!!

/*            val imageProcessor = AvatarProcessor()
            val avatarWithBorder = imageProcessor.addBorderWithScale(imageBitmap, 30, Color.WHITE, 0.8f)*/
            val imageView: ImageView = view.findViewById(R.id.avatarImageView)
            imageView.setImageBitmap(imageBitmap)
        }

        profileViewModel.getProfile()

        avatarViewModel.getAvatar()

        logoutImageView.setOnClickListener{
            val intent = Intent(requireActivity(), AuthRegisterActivity::class.java)
            startActivity(intent)
        }

        editProfileImageView.setOnClickListener{
            val editProfileFragment = EditProfileFragment()

            val args = Bundle()
            args.putString("userLogin", userLogin)

            editProfileFragment.arguments = args

            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, editProfileFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        return view
    }
}
