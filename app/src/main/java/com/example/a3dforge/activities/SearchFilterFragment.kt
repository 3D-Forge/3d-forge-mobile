package com.example.a3dforge.activities

import OkHttpConfig
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.a3dforge.R
import com.example.a3dforge.factories.CategoriesViewModelFactory
import com.example.a3dforge.factories.ProductPictureViewModelFactory
import com.example.a3dforge.models.CategoriesViewModel
import com.example.a3dforge.models.ProductPictureViewModel
import com.example.a3dforge.models.SharedViewModel
import com.google.android.material.slider.RangeSlider

class SearchFilterFragment : Fragment() {

    private lateinit var closeImageView: ImageView
    private lateinit var minPriceEditText: EditText
    private lateinit var maxPriceEditText: EditText
    private lateinit var sliderRange: RangeSlider
    private lateinit var categoriesButt: Button

    private lateinit var checkedItems: MutableList<Boolean>

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val productPictureViewModel: ProductPictureViewModel by viewModels {
        ProductPictureViewModelFactory(OkHttpConfig)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var categoriesList = ArrayList<String>()

        checkedItems = savedInstanceState?.getBooleanArray(CHECKED_ITEMS_KEY)?.toMutableList()
            ?: MutableList(categoriesList.size) { false }

        val view = inflater.inflate(R.layout.fragment_search_filter, container, false)

        sliderRange = view.findViewById(R.id.sliderRange)

        closeImageView = view.findViewById(R.id.closeImageView)

        minPriceEditText = view.findViewById(R.id.minPriceEditText)
        maxPriceEditText = view.findViewById(R.id.maxPriceEditText)

        categoriesButt = view.findViewById(R.id.categoriesButt)

        closeImageView.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CatalogFragment())
                .commit()
        }

        minPriceEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (minPriceEditText.text.toString() != "" && minPriceEditText.text.toString().toFloat() > 100 && minPriceEditText.text.toString().toFloat() < 20000 && minPriceEditText.text.toString().toFloat() < maxPriceEditText.text.toString().toFloat()) {
                    updateMinProgressBar()
                }
                return@setOnEditorActionListener true
            }
            false
        }

        maxPriceEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (maxPriceEditText.text.toString() != "" && maxPriceEditText.text.toString().toFloat() > 100 && maxPriceEditText.text.toString().toFloat() < 20000 && minPriceEditText.text.toString().toFloat() < maxPriceEditText.text.toString().toFloat()) {
                    updateMaxProgressBar()
                }
                return@setOnEditorActionListener true
            }
            false
        }

        minPriceEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {

            }

            override fun afterTextChanged(editable: Editable?) {

            }
        })

        sliderRange.addOnChangeListener { _, _, _ ->
            updateEditTextValues()
        }

        maxPriceEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {

            }

            override fun afterTextChanged(editable: Editable?) {

            }
        })

        val okHttpConfig = OkHttpConfig

        val categoriesViewModel = ViewModelProvider(this, CategoriesViewModelFactory(okHttpConfig))
            .get(CategoriesViewModel::class.java)

        categoriesViewModel.categoriesResult.observe(viewLifecycleOwner, Observer { result ->
            categoriesList = (result?.data?.map { it.name } ?: emptyList()) as ArrayList<String>
        })

        categoriesViewModel.getCategory()

        categoriesButt.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Select Categories")

            val checkedItems = BooleanArray(categoriesList.size)
            builder.setMultiChoiceItems(categoriesList.toTypedArray(), checkedItems) { _, _, _ -> }

            builder.setPositiveButton("OK") { _, _ ->
                val selectedIndices = checkedItems.indices.filter { index -> checkedItems[index] }
                sharedViewModel.filterParameters.categories = selectedIndices.map { it.toString() }.toTypedArray()
            }

            builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }

            val dialog = builder.create()
            dialog.show()
        }

        return view
    }

    private fun updateEditTextValues() {
        val currentValues = sliderRange.values
        minPriceEditText.setText(currentValues[0].toInt().toString())
        maxPriceEditText.setText(currentValues[1].toInt().toString())
    }

    private fun updateMinProgressBar() {
        try {
            val minValue = minPriceEditText.text.toString().toFloat()
            val currentValues = sliderRange.values

            sliderRange.values = listOf(minValue, currentValues[1])
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
    }

    private fun updateMaxProgressBar() {
        try {
            val maxValue = maxPriceEditText.text.toString().toFloat()
            val currentValues = sliderRange.values

            sliderRange.values = listOf(currentValues[0], maxValue)
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBooleanArray(CHECKED_ITEMS_KEY, checkedItems.toBooleanArray())
    }

    companion object {
        private const val CHECKED_ITEMS_KEY = "checkedItems"
    }

}