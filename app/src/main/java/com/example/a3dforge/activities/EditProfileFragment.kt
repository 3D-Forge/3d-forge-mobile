package com.example.a3dforge.activities

import OkHttpConfig
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.a3dforge.R
import com.example.a3dforge.base.AvatarProcessor
import com.example.a3dforge.base.PreferenceHelper
import com.example.a3dforge.entities.ProfileGetRequestBody
import com.example.a3dforge.entities.ProfilePutRequestBody
import com.example.a3dforge.factories.AvatarViewModelFactory
import com.example.a3dforge.factories.ChangeProfileViewModelFactory
import com.example.a3dforge.factories.ProfileViewModelFactory
import com.example.a3dforge.factories.UploadAvatarViewModelFactory
import com.example.a3dforge.models.AvatarViewModel
import com.example.a3dforge.models.ChangeProfileViewModel
import com.example.a3dforge.models.ProfileViewModel
import com.example.a3dforge.models.UploadAvatarViewModel
import org.json.JSONObject
import java.io.File


class EditProfileFragment : Fragment() {

    private lateinit var backArrowImageView: ImageView
    private lateinit var userLoginEditTextView: TextView
    private lateinit var systemThemeTextView: TextView
    private lateinit var securityTextView: TextView
    private lateinit var nicknameEditText: EditText
    private lateinit var fullNameEditProfileEditText: EditText
    private lateinit var phoneNumberEditText: EditText
    private lateinit var cityEditText: EditText
    private lateinit var streetEditText: EditText
    private lateinit var cityRegionEditText: EditText
    private lateinit var houseEditText: EditText
    private lateinit var genInfoTextView: TextView
    private lateinit var addressEditProfileTextView: TextView
    private lateinit var notificationTextView: TextView
    private lateinit var avatarEditImageView: ImageView
    private lateinit var systemThemeSpinner: Spinner
    private lateinit var saveChangesButton: Button
    private lateinit var choosePhotoButton: Button
    private lateinit var avatarImageView: ImageView

    private lateinit var filePicker: ActivityResultLauncher<Intent>

    private lateinit var preferenceHelper: PreferenceHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View? {

        val userLogin = arguments?.getString("userLogin")

        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        backArrowImageView = view.findViewById(R.id.backArrowImageView)
        userLoginEditTextView = view.findViewById(R.id.userLoginEditTextView)
        systemThemeTextView = view.findViewById(R.id.systemThemeTextView)
        nicknameEditText = view.findViewById(R.id.nicknameEditText)
        phoneNumberEditText = view.findViewById(R.id.phoneNumberEditText)
        fullNameEditProfileEditText = view.findViewById(R.id.fullNameEditProfileEditText)
        cityEditText = view.findViewById(R.id.cityEditText)
        streetEditText = view.findViewById(R.id.streetEditText)
        cityRegionEditText = view.findViewById(R.id.cityRegionEditText)
        houseEditText = view.findViewById(R.id.houseEditText)
        genInfoTextView = view.findViewById(R.id.genInfoTextView)
        addressEditProfileTextView = view.findViewById(R.id.addressEditProfileTextView)
        notificationTextView = view.findViewById(R.id.notificationTextView)
        securityTextView = view.findViewById(R.id.securityTextView)
        systemThemeSpinner = view.findViewById(R.id.systemThemeSpinner)
        saveChangesButton = view.findViewById(R.id.saveChangesButton)
        choosePhotoButton = view.findViewById(R.id.choosePhotoButton)
        avatarImageView = view.findViewById(R.id.avatarEditImageView)

        val data = listOf("Світла тема", "Темна тема")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, data)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        systemThemeSpinner.adapter = adapter

        preferenceHelper = PreferenceHelper(requireContext())

        val selectedTheme = preferenceHelper.getSelectedTheme()

        val spinnerPosition = when (selectedTheme) {
            AppCompatDelegate.MODE_NIGHT_NO -> 0
            AppCompatDelegate.MODE_NIGHT_YES -> 1
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> {
                val currentNightMode = AppCompatDelegate.getDefaultNightMode()
                if (currentNightMode == AppCompatDelegate.MODE_NIGHT_NO) 0 else 1
            }
            else -> 0 // default to light theme
        }

        systemThemeSpinner.setSelection(spinnerPosition)

        systemThemeSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                val selectedTheme = if (position == 0) {
                    AppCompatDelegate.MODE_NIGHT_NO
                } else {
                    AppCompatDelegate.MODE_NIGHT_YES
                }

                AppCompatDelegate.setDefaultNightMode(selectedTheme)
                preferenceHelper.saveSelectedTheme(selectedTheme)
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        })

        genInfoTextView.paintFlags = genInfoTextView.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        addressEditProfileTextView.paintFlags = addressEditProfileTextView.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        systemThemeTextView.paintFlags = systemThemeTextView.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        notificationTextView.paintFlags = notificationTextView.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        securityTextView.paintFlags = securityTextView.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        val okHttpConfig = OkHttpConfig

        backArrowImageView.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment())
                .commit()
        }

        filePicker = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let { selectedFileUri ->
                    val mimeType = getMimeType(selectedFileUri)
                    if (mimeType == "image/png"){
                        val filePath = getFilePathFromUri(selectedFileUri)
                        filePath?.let { path ->
                            val file = File(path)
                            val uploadAvatarViewModel = ViewModelProvider(this, UploadAvatarViewModelFactory(okHttpConfig, file, userLogin!!)).get(UploadAvatarViewModel::class.java)

                            val contentResolver = requireContext().contentResolver
                            val inputStream = contentResolver.openInputStream(selectedFileUri)
                            val imageBitmap = BitmapFactory.decodeStream(inputStream)

                            uploadAvatarViewModel.avatarUploadResult.observe(viewLifecycleOwner) { avatarUpload ->

                            }

                            uploadAvatarViewModel.avatarUpload()

                            val imageProcessor = AvatarProcessor()
                            val avatarWithBorder = imageProcessor.addBorderWithScale(imageBitmap, 30, Color.WHITE, 0.8f)
                            avatarImageView.setImageBitmap(avatarWithBorder)
                        }
                    }
                    else {
                        Toast.makeText(this.requireContext(), "Тільки PNG-формат!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        choosePhotoButton.setOnClickListener{
            openFilePicker()
        }

        val profileViewModel = ViewModelProvider(this, ProfileViewModelFactory(okHttpConfig, userLogin!!)).get(ProfileViewModel::class.java)

        profileViewModel.profileResult.observe(viewLifecycleOwner) { profile ->
            if (profile != null) {
                val userData = profile.second
                if (userData != null) {
                    userLoginEditTextView.text = userData.data.login
                    nicknameEditText.text = Editable.Factory.getInstance().newEditable(userData.data.login)
                    if (userData.data.firstName != "null") {
                        val fullName = userData.data.midName + " " + userData.data.firstName + " " + userData.data.lastName
                        val fullNameEditable = Editable.Factory.getInstance().newEditable(fullName)
                        fullNameEditProfileEditText.text = fullNameEditable
                    }
                    if (userData.data.phoneNumber != "null"){
                        val phone = userData.data.phoneNumber
                        val phoneEditable = Editable.Factory.getInstance().newEditable(phone)
                        phoneNumberEditText.text = phoneEditable
                    }
                    if (userData.data.city != "null"){
                        val city = userData.data.city
                        val cityEditable = Editable.Factory.getInstance().newEditable(city)
                        cityEditText.text = cityEditable
                    }
                    if (userData.data.street != "null"){
                        val street = userData.data.street
                        val streetEditable = Editable.Factory.getInstance().newEditable(street)
                        streetEditText.text = streetEditable
                    }
                    if (userData.data.cityRegion != "null"){
                        val cityRegion = userData.data.cityRegion
                        val cityRegionEditable = Editable.Factory.getInstance().newEditable(cityRegion)
                        cityRegionEditText.text = cityRegionEditable
                    }
                    if (userData.data.house != "null"){
                        val house = userData.data.house
                        val houseEditable = Editable.Factory.getInstance().newEditable(house)
                        houseEditText.text = houseEditable
                    }
                } else {
                }
            } else {
            }
        }

        val avatarViewModel = ViewModelProvider(this, AvatarViewModelFactory(okHttpConfig, userLogin)).get(AvatarViewModel::class.java)

        avatarViewModel.avatarResult.observe(viewLifecycleOwner) { avatar ->
            val imageBytes: ByteArray? = avatar.second

            val imageBitmap: Bitmap =
                imageBytes?.let { BitmapFactory.decodeByteArray(imageBytes, 0, it.size) }!!

/*            val imageProcessor = AvatarProcessor()
            val avatar0WithBorder = imageProcessor.addBorderWithScale(imageBitmap, 30, Color.WHITE, 0.8f)*/
            avatarImageView.setImageBitmap(imageBitmap)
        }

        profileViewModel.getProfile()

        avatarViewModel.getAvatar()

        saveChangesButton.setOnClickListener {
            userLoginEditTextView.text = nicknameEditText.text.toString()

            var userData: ProfilePutRequestBody.UserPutData? = null

            val words = fullNameEditProfileEditText.text.split(" ")
            if (words.size != 3 || fullNameEditProfileEditText.text.toString() == "") {
                Toast.makeText(this.requireContext(), "Введіть повне ім'я!", Toast.LENGTH_SHORT).show()
            } else {
                userData = ProfilePutRequestBody.UserPutData(
                    login = nicknameEditText.text.toString(),
                    phoneNumber = phoneNumberEditText.text.toString(),
                    firstName = (if (words[0].isNotEmpty()) words[0] else null),
                    midName = if (words[1].isNotEmpty()) words[1] else null,
                    lastName = if (words[2].isNotEmpty()) words[2] else null,
                    region = null,
                    cityRegion = if (cityRegionEditText.text.isNotEmpty()) cityRegionEditText.text.toString() else null,
                    city = if (cityEditText.text.isNotEmpty()) cityEditText.text.toString() else null,
                    street = if (streetEditText.text.isNotEmpty()) streetEditText.text.toString() else null,
                    house = if (houseEditText.text.isNotEmpty()) houseEditText.text.toString() else null,
                    apartment = null,
                    departmentNumber = null,
                    deliveryType = null,
                    orderStateChangedNotification = null,
                    getForumResponseNotification = null,
                    modelRatedNotification = null
                )
            }

            if (userData != null) {
                val json = JSONObject()

                fun putNonNull(key: String, value: Any?) {
                    if (value != null) {
                        json.put(key, value)
                    }
                }

                putNonNull("login", userData.login)
                putNonNull("phoneNumber", userData.phoneNumber)
                putNonNull("firstName", userData.firstName)
                putNonNull("midName", userData.midName)
                putNonNull("lastname", userData.lastName)
                putNonNull("region", userData.region)
                putNonNull("cityRegion", userData.cityRegion)
                putNonNull("city", userData.city)
                putNonNull("street", userData.street)
                putNonNull("house", userData.house)
                putNonNull("apartment", userData.apartment)
                putNonNull("departmentNumber", userData.departmentNumber)
                putNonNull("deliveryType", userData.deliveryType)
                putNonNull("orderStateChangedNotification", userData.orderStateChangedNotification)
                putNonNull("getForumResponseNotification", userData.getForumResponseNotification)
                putNonNull("modelRatedNotification", userData.modelRatedNotification)

                val changeProfileViewModel = ViewModelProvider(this, ChangeProfileViewModelFactory(okHttpConfig, json, userLogin)).get(ChangeProfileViewModel::class.java)

                changeProfileViewModel.profileChangeResult.observe(viewLifecycleOwner) { changeProfile ->
                    println(changeProfile.toString())
                }

                changeProfileViewModel.changeProfile()

                if (nicknameEditText.text.toString() != userLogin) {
                    val newLogin = nicknameEditText.text.toString()
                    (requireActivity() as MainActivity).updateLogin(newLogin)
                }
            } else {

            }
        }

        return view
    }

    private fun openFilePicker() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        filePicker.launch(Intent.createChooser(intent, "Выберите изображение"))
    }

    private fun getFilePathFromUri(uri: Uri): String? {
        val cursor = context?.contentResolver?.query(uri, null, null, null, null)
        cursor?.use {
            it.moveToFirst()
            val columnIndex = it.getColumnIndex("_data")
            if (columnIndex != -1) {
                return it.getString(columnIndex)
            }
        }
        return null
    }

    private fun getMimeType(uri: Uri): String? {
        return context?.contentResolver?.getType(uri)
    }

}