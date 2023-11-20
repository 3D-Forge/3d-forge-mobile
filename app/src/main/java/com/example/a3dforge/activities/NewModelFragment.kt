package com.example.a3dforge.activities

import OkHttpConfig
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.a3dforge.R
import com.example.a3dforge.adapter.CategoriesAdapter
import com.example.a3dforge.adapter.TagsAdapter
import com.example.a3dforge.entities.modelUploadBody
import com.example.a3dforge.factories.CategoriesViewModelFactory
import com.example.a3dforge.factories.UploadModelViewModelFactory
import com.example.a3dforge.models.CategoriesViewModel
import com.example.a3dforge.models.UploadModelViewModel
import java.io.File


class NewModelFragment : Fragment() {
    private lateinit var filePicker: ActivityResultLauncher<Intent>
    private lateinit var imagePicker: ActivityResultLauncher<Intent>

    private lateinit var modelLoadButton1: Button
    private lateinit var modelLoadButton2: Button
    private lateinit var photoLoadButton: Button
    private lateinit var lastClickedButton: Button
    private lateinit var uploadNewModelButton: Button

    private lateinit var modelNameEditText: EditText
    private lateinit var modelDescEditText: EditText
    private lateinit var depthEditText: EditText

    private lateinit var undoAddingTextView: TextView

    private lateinit var categoriesRcView: RecyclerView
    private lateinit var tagsRcView: RecyclerView
    private lateinit var categoriesImageView: ImageView
    private lateinit var closeAddingImageView: ImageView
    private lateinit var categoriesAdapter: CategoriesAdapter
    private lateinit var tagsAdapter: TagsAdapter

    private val selectedCategories = ArrayList<String>()
    private val selectedCategoriesIndexes = ArrayList<Int>()
    private val selectedTags = ArrayList<String>()

    val modelUploadBody = modelUploadBody(
        name = "ModelName",
        description = "Model description",
        depth = 1,
        keywords = null,
        categoryes = mutableListOf(),
        files = mutableListOf()
    )

    private val files = ArrayList<File>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new_model, container, false)

        val okHttpConfig = OkHttpConfig

        modelLoadButton1 = view.findViewById(R.id.modelLoadButton1)
        modelLoadButton2 = view.findViewById(R.id.modelLoadButton2)
        photoLoadButton = view.findViewById(R.id.photoLoadButton)
        uploadNewModelButton = view.findViewById(R.id.uploadNewModelButton)

        modelNameEditText = view.findViewById(R.id.modelNameEditText)
        modelDescEditText = view.findViewById(R.id.modelDescEditText)
        depthEditText = view.findViewById(R.id.depthEditText)

        undoAddingTextView = view.findViewById(R.id.undoAddingTextView)

        categoriesRcView = view.findViewById(R.id.categoriesRcView)
        tagsRcView = view.findViewById(R.id.tagsRcView)
        categoriesImageView = view.findViewById(R.id.categoriesImageView)
        closeAddingImageView = view.findViewById(R.id.closeAddingImageView)

        categoriesRcView.layoutManager = GridLayoutManager(requireContext(), 2)

        modelLoadButton1.paintFlags = modelLoadButton1.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        modelLoadButton2.paintFlags = modelLoadButton2.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        photoLoadButton.paintFlags = photoLoadButton.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        undoAddingTextView.paintFlags = undoAddingTextView.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        modelLoadButton1.setOnClickListener{
            if (modelLoadButton1.text == "Обрати файл для завантаження") {
                lastClickedButton = modelLoadButton1

                openFilePicker()
            }
            else{
                if (modelLoadButton2.text != "Файл було завантажено!"){
                    modelUploadBody.files.removeAt(0)
                    toggleButtonState(modelLoadButton1)
                }
                else{
                    Toast.makeText(requireContext(), "Приберіть спочатку 2 файл!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        modelLoadButton2.setOnClickListener {
            if (modelLoadButton1.text == "Файл було завантажено!"){
                if (modelLoadButton2.text == "Обрати файл для завантаження") {
                    lastClickedButton = modelLoadButton2

                    openFilePicker()
                }
                else{
                    toggleButtonState(modelLoadButton2)
                    modelUploadBody.files.removeAt(1)
                }
            }
            else{
                Toast.makeText(requireContext(), "Оберіть спочатку 1 файл!", Toast.LENGTH_SHORT).show()
            }
        }

        photoLoadButton.setOnClickListener{
            if (photoLoadButton.text == "Обрати файл для завантаження" && modelLoadButton1.text == "Файл було завантажено!" && modelLoadButton2.text == "Файл було завантажено!") {
                lastClickedButton = photoLoadButton
                openImagePicker()
            }
            else{
                toggleButtonState(photoLoadButton)
            }
        }

        filePicker = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let { selectedFileUri ->
                    val mimeType = getFileMimeType(selectedFileUri)
                    if (mimeType?.endsWith(".stl") == true || mimeType?.endsWith(".obj") == true) {
                        val filePath = getFilePathFromUri(selectedFileUri, requireContext())
                        filePath?.let { path ->
                            val file = File(path)
                            println(filePath)
                            println(filePath)
                            val newFilePath = path + ".stl"
                            val newFile = File(newFilePath)
                            file.renameTo(newFile)
                            modelUploadBody.files.add(newFile)
                            files.add(newFile)
                            toggleButtonState(lastClickedButton)
                        }
                    } else {
                        Toast.makeText(this.requireContext(), "Тільки STL і OBJ формати!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        imagePicker = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let { selectedFileUri ->
                    val mimeType = getImageMimeType(selectedFileUri)
                    if (mimeType == "image/png"){
                        val filePath = getImagePathFromUri(selectedFileUri)
                        filePath?.let { path ->
                            val file = File(path)
                            modelUploadBody.files.add(file)
                            files.add(file)
                            for (i in files){
                                println(i)
                            }
                            toggleButtonState(lastClickedButton)
                        }
                    }
                    else {
                        Toast.makeText(this.requireContext(), "Тільки PNG-формат!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        categoriesRcView.layoutManager = GridLayoutManager(this.requireContext(), 2)

        categoriesRcView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                outRect.left = 0
                outRect.right = 0
                outRect.top = 0
                outRect.bottom = 0
            }
        })

        categoriesAdapter = CategoriesAdapter(selectedCategories) { position ->
            selectedCategories.removeAt(position)
            categoriesAdapter.notifyDataSetChanged()
        }

        categoriesRcView.adapter = categoriesAdapter

        val categoriesViewModel = ViewModelProvider(this, CategoriesViewModelFactory(okHttpConfig))
            .get(CategoriesViewModel::class.java)

        categoriesViewModel.categoriesResult.observe(viewLifecycleOwner, Observer { result ->
            val categoriesList = result?.data?.map { it.name } ?: emptyList()
            showCategoriesDialog(categoriesList)
        })

        categoriesImageView.setOnClickListener{
            categoriesViewModel.getCategory()
        }

        val tagsRcView = view.findViewById<RecyclerView>(R.id.tagsRcView)
        tagsRcView.layoutManager = GridLayoutManager(this.requireContext(), 3)

        tagsRcView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                outRect.left = 0
                outRect.right = 0
                outRect.top = 0
                outRect.bottom = 0
            }
        })

        tagsAdapter = TagsAdapter(selectedTags) { position ->
            selectedTags.removeAt(position)
            tagsAdapter.notifyDataSetChanged()
        }

        tagsRcView.adapter = tagsAdapter

        val tagsEditText = view.findViewById<EditText>(R.id.tagsAddEditText)
        tagsEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val tag = tagsEditText.text.toString().trim()
                if (tag.isNotEmpty()) {
                    selectedTags.add(tag)
                    tagsAdapter.notifyDataSetChanged()
                    tagsEditText.text.clear()
                }
                true
            } else {
                false
            }
        }

        closeAddingImageView.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CatalogFragment())
                .commit()
        }

        undoAddingTextView.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CatalogFragment())
                .commit()
        }

        val uploadModelViewModel = ViewModelProvider(this, UploadModelViewModelFactory(okHttpConfig)).get(
            UploadModelViewModel::class.java)

        uploadModelViewModel.uploadModelResult.observe(viewLifecycleOwner, Observer { result ->
            if (result) {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, CatalogFragment())
                    .commit()
                Toast.makeText(requireContext(), "Модель успішно завантажена!", Toast.LENGTH_SHORT).show()
            } else {
                Log.e("AuthRegisterActivity", "Registration failed")
                Toast.makeText(requireContext(), "Помилка завантаження", Toast.LENGTH_SHORT).show()
            }
        })

        uploadNewModelButton.setOnClickListener{
            uploadModelViewModel.uploadModel(
                modelNameEditText.text.toString(),
                modelDescEditText.text.toString(),
                depthEditText.text.toString(),
                selectedTags.toTypedArray(),
                selectedCategoriesIndexes.toTypedArray(),
                files.toTypedArray()
            )
        }

        return view
    }

    private fun showCategoriesDialog(categoriesList: List<String>) {
        val checkedItems = BooleanArray(categoriesList.size)

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Оберіть категорії")
        builder.setMultiChoiceItems(categoriesList.toTypedArray(), checkedItems) { _, position, isChecked ->
            checkedItems[position] = isChecked
        }

        builder.setPositiveButton("OK") { dialog: DialogInterface, _: Int ->
            selectedCategories.clear()
            for (i in categoriesList.indices) {
                if (checkedItems[i]) {
                    selectedCategories.add(categoriesList[i])
                    selectedCategoriesIndexes.add(i + 1)
                }
            }
            if (selectedCategories.size == 0){
                Toast.makeText(requireContext(), "Оберіть що найменше 1 категорію!", Toast.LENGTH_SHORT).show()
            }
            else{
                categoriesAdapter.notifyDataSetChanged()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Відміна") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }


    private fun openFilePicker() {
        val intent = Intent()
        intent.type = "application/*"
        intent.action = Intent.ACTION_GET_CONTENT
        filePicker.launch(Intent.createChooser(intent, "Оберіть файл для завантаження"))
    }

    private fun openImagePicker() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        imagePicker.launch(Intent.createChooser(intent, "Оберіть фото для завантаження"))
    }

    private fun getFilePathFromUri(uri: Uri, context: Context): String? {
        return try {
            val contentResolver = context.contentResolver
            val cursor = contentResolver.query(uri, null, null, null, null)

            cursor?.use {
                it.moveToFirst()
                val columnIndex = it.getColumnIndex(MediaStore.Images.Media.DATA)
                if (columnIndex != -1) {
                    val filePath = it.getString(columnIndex)
                    if (!filePath.isNullOrBlank()) {
                        filePath
                    } else {
                        copyFileToCache(uri, context)
                    }
                } else {
                    copyFileToCache(uri, context)
                }
            } ?: run {
                copyFileToCache(uri, context)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun copyFileToCache(uri: Uri, context: Context): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val fileName = System.currentTimeMillis().toString()
            val cachedFile = File(context.cacheDir, fileName)

            inputStream?.use { input ->
                cachedFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            cachedFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    private fun getFileMimeType(uri: Uri): String? {
        return try {
            val contentResolver = context?.contentResolver
            val type = contentResolver?.getType(uri)

            if (type.isNullOrBlank()) {
                val fileExtension = getFileExtension(uri.toString())
                if (!fileExtension.isNullOrBlank()) {
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension)
                } else {
                    null
                }
            } else {
                type
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getFileExtension(url: String): String? {
        val lastDot = url.lastIndexOf(".")
        return if (lastDot != -1) {
            url.substring(lastDot + 1)
        } else {
            null
        }
    }

    private fun getImagePathFromUri(uri: Uri): String? {
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

    private fun getImageMimeType(uri: Uri): String? {
        return context?.contentResolver?.getType(uri)
    }

    private fun toggleButtonState(button: Button) {
        val background = button.background as LayerDrawable
        val stripe = background.getDrawable(0) as GradientDrawable

        val greenColor = ContextCompat.getColor(requireContext(), R.color.green)
        val defaultStripColor = ContextCompat.getColor(requireContext(), R.color.strip_button_gray)
        val purpleColor = ContextCompat.getColor(requireContext(), R.color.purple)

        button.text = if (button.text == "Обрати файл для завантаження") {
            "Файл було завантажено!"
        }
        else {
            "Обрати файл для завантаження"
        }

        button.setTextColor(if (button.text == "Обрати файл для завантаження") purpleColor else greenColor)

        stripe.setColor(if (button.text == "Обрати файл для завантаження") defaultStripColor else greenColor)
    }

}