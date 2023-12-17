package com.example.a3dforge.activities

import OkHttpConfig
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.a3dforge.R
import com.example.a3dforge.adapter.CategoriesModelAdapter
import com.example.a3dforge.adapter.TagsModelAdapter
import com.example.a3dforge.base.CustomToast
import com.example.a3dforge.entities.CartPutRequestBody
import com.example.a3dforge.factories.CartPutViewModelFactory
import com.example.a3dforge.factories.CartViewModelFactory
import com.example.a3dforge.factories.ModelByIdViewModelFactory
import com.example.a3dforge.factories.ProductPictureViewModelFactory
import com.example.a3dforge.models.CartPutViewModel
import com.example.a3dforge.models.CartViewModel
import com.example.a3dforge.models.ModelByIdViewModel
import com.example.a3dforge.models.ProductPictureViewModel
import com.google.android.material.snackbar.Snackbar

class ModelFragment : Fragment() {

    private lateinit var specificModelNameTextView: TextView
    private lateinit var modelPriceTextView: TextView
    private lateinit var specificModelDescTextView: TextView
    private lateinit var modelImageView: ImageView
    private lateinit var backToCatalogArrowImageView: ImageView
    private lateinit var categoriesModelRcView: RecyclerView
    private lateinit var tagsModelRcView: RecyclerView
    private lateinit var categoriesAdapter: CategoriesModelAdapter
    private lateinit var tagsAdapter: TagsModelAdapter
    private lateinit var addToCartButton: Button

    private var selectedCategories: MutableList<String> = mutableListOf()
    private var selectedTags: MutableList<String> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_model, container, false)

        specificModelNameTextView = view.findViewById(R.id.specificModelNameTextView)
        modelPriceTextView = view.findViewById(R.id.modelPriceTextView)
        specificModelDescTextView = view.findViewById(R.id.specificModelDescTextView)
        modelImageView = view.findViewById(R.id.modelImageView)
        backToCatalogArrowImageView = view.findViewById(R.id.backToCatalogArrowImageView)
        categoriesModelRcView = view.findViewById(R.id.categoriesModelRcView)
        tagsModelRcView = view.findViewById(R.id.tagsModelRcView)
        addToCartButton = view.findViewById(R.id.addToCartButton)

        val args = arguments
        val modelId = args?.getInt("modelId")

        val okHttpConfig = OkHttpConfig

        val modelByIdViewModel = ViewModelProvider(this, ModelByIdViewModelFactory(okHttpConfig))
            .get(ModelByIdViewModel::class.java)

        categoriesModelRcView.layoutManager = GridLayoutManager(this.requireContext(), 3)

        categoriesModelRcView.addItemDecoration(object : RecyclerView.ItemDecoration() {
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

        tagsModelRcView.layoutManager = GridLayoutManager(this.requireContext(), 3)

        tagsModelRcView.addItemDecoration(object : RecyclerView.ItemDecoration() {
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

        categoriesAdapter = CategoriesModelAdapter(selectedCategories) { position ->
            selectedCategories.removeAt(position)
            categoriesAdapter.notifyDataSetChanged()
        }

        tagsAdapter = TagsModelAdapter(selectedTags) { position ->
            selectedTags.removeAt(position)
            tagsAdapter.notifyDataSetChanged()
        }

        categoriesModelRcView.adapter = categoriesAdapter

        tagsModelRcView.adapter = tagsAdapter

        modelByIdViewModel.modelResult.observe(viewLifecycleOwner, Observer { result  ->
            result.second?.let {
                val list = result.second!!
                specificModelNameTextView.text = list.data.name
                modelPriceTextView.text = list.data.minPrice.toString() + " ₴"
                list.data.categories.let { categories ->
                    for (i in categories) {
                        selectedCategories.add(i.name)
                    }
                }
                for (i in list.data.keywords) {
                    selectedTags.add(i.toString())
                }
                categoriesAdapter.notifyDataSetChanged()
                tagsAdapter.notifyDataSetChanged()
                specificModelDescTextView.text = list.data.description
            }
        })

        val productPictureViewModel = ViewModelProvider(this, ProductPictureViewModelFactory(okHttpConfig))
            .get(ProductPictureViewModel::class.java)

        productPictureViewModel.pictureResult.observe(viewLifecycleOwner, Observer { result ->
            result.second?.let {
                val imageBytes: ByteArray? = result.second
                val imageBitmap: Bitmap =
                    BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes!!.size)
                modelImageView.setImageBitmap(imageBitmap)
            }
        })

        modelByIdViewModel.getModelById(modelId!!)
        productPictureViewModel.getProductPicture(modelId)

        backToCatalogArrowImageView.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CatalogFragment())
                .commit()
        }

        addToCartButton.setOnClickListener{
            val cartGetViewModel = ViewModelProvider(this, CartViewModelFactory(okHttpConfig))
                .get(CartViewModel::class.java)

            val cartPutViewModel =
                ViewModelProvider(this, CartPutViewModelFactory(okHttpConfig))
                    .get(CartPutViewModel::class.java)

            cartPutViewModel.cartPutResult.observe(
                viewLifecycleOwner,
                Observer { result ->
                    if (result) {
                        CustomToast.showSuccess(
                            requireContext(),
                            "Модель успішно додана до кошику"
                        )
                    } else {
                        CustomToast.showError(
                            requireContext(),
                            "Помилка при додаванні до кошику"
                        )
                    }
                })

            var putData: CartPutRequestBody? = null

            putData = CartPutRequestBody(
                modelId,
                1,
                10,
                2,
                1,
                "SLA",
                "ABS",
                null
            )

            cartGetViewModel.cartResult.observe(viewLifecycleOwner, Observer { cartResult ->
                cartResult?.second?.data?.orderedModelIDs?.let { orderedModelIDs ->
                    val isModelInCart = orderedModelIDs.any { it.catalogModelId == modelId }
                    if (isModelInCart) {
                        CustomToast.showError(requireContext(), "Така модель вже є в корзині")
                    } else {
                        cartPutViewModel.putCart(putData)
                    }
                }
            })

            cartGetViewModel.getCart()

        }

        return view
    }

}