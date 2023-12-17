package com.example.a3dforge.activities

import OkHttpConfig
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.a3dforge.R
import com.example.a3dforge.adapter.CartAdapter
import com.example.a3dforge.base.CustomToast
import com.example.a3dforge.entities.Item
import com.example.a3dforge.factories.CartViewModelFactory
import com.example.a3dforge.factories.ModelByIdViewModelFactory
import com.example.a3dforge.factories.ProfileViewModelFactory
import com.example.a3dforge.models.CartViewModel
import com.example.a3dforge.models.ModelByIdViewModel
import com.example.a3dforge.models.ProfileViewModel

class CartFragment : Fragment(), CartAdapter.CartAdapterListener {

    private lateinit var backArrowImageView: ImageView
    private lateinit var whiteLineImageView: ImageView
    private lateinit var cartIconImageView: ImageView

    private lateinit var makeOrderButton: Button

    private lateinit var cartRecyclerView: RecyclerView

    private lateinit var emptyCartTextView: TextView
    private lateinit var finalPriceTextView: TextView
    private lateinit var generalPriceTextView: TextView
    private lateinit var priceForDeliveryTextView: TextView
    private lateinit var deliveryTextPriceTextView: TextView
    private lateinit var priceForModelsTextView: TextView
    private lateinit var priceTextForModelsTextView: TextView

    private lateinit var cartAdapter: CartAdapter

    val okHttpConfig = OkHttpConfig
    private var modelId: Int = 0
    val modelList = mutableListOf<Item>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)

        val existingFragment = requireActivity().supportFragmentManager.findFragmentByTag("ConfirmOrderFragment")

        emptyCartTextView = view.findViewById(R.id.emptyCartTextView)
        priceForModelsTextView = view.findViewById(R.id.priceForModelsTextView)
        generalPriceTextView = view.findViewById(R.id.generalPriceTextView)
        priceForDeliveryTextView = view.findViewById(R.id.priceForDeliveryTextView)
        deliveryTextPriceTextView = view.findViewById(R.id.deliveryTextPriceTextView)
        priceTextForModelsTextView = view.findViewById(R.id.priceTextForModelsTextView)
        finalPriceTextView = view.findViewById(R.id.finalPriceTextView)

        backArrowImageView = view.findViewById(R.id.backArrowImageView)
        whiteLineImageView = view.findViewById(R.id.whiteLineImageView)
        cartIconImageView = view.findViewById(R.id.cartIconImageView)

        cartRecyclerView = view.findViewById(R.id.cartRecyclerView)

        makeOrderButton = view.findViewById(R.id.makeOrderButton)

        backArrowImageView.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment())
                .commit()
        }

        cartAdapter = CartAdapter(requireActivity(), this)

        cartRecyclerView.adapter = cartAdapter
        cartRecyclerView.layoutManager = GridLayoutManager(this.requireContext(), 1)

        val cartViewModel = ViewModelProvider(this, CartViewModelFactory(okHttpConfig))
            .get(CartViewModel::class.java)

        val modelByIdViewModel = ViewModelProvider(this, ModelByIdViewModelFactory(okHttpConfig))
            .get(ModelByIdViewModel::class.java)

        cartViewModel.cartResult.observe(viewLifecycleOwner) { cartResult ->
            if (cartResult.second?.data?.orderedModelIDs?.size != 0) {
                emptyCartTextView.visibility = View.GONE
                cartRecyclerView.visibility = View.VISIBLE
                priceForModelsTextView.visibility = View.VISIBLE
                finalPriceTextView.visibility = View.VISIBLE
                priceForDeliveryTextView.visibility = View.VISIBLE
                deliveryTextPriceTextView.visibility = View.VISIBLE
                priceTextForModelsTextView.visibility = View.VISIBLE
                generalPriceTextView.visibility = View.VISIBLE
                makeOrderButton.visibility = View.VISIBLE
                whiteLineImageView.visibility = View.VISIBLE
                cartIconImageView.visibility = View.VISIBLE

                cartResult.second?.data?.orderedModelIDs?.forEach { orderedModelId ->
                    val catalogModelId = orderedModelId.catalogModelId
                    modelByIdViewModel.getModelById(catalogModelId)
                }
            } else {
                emptyCartTextView.visibility = View.VISIBLE
                cartRecyclerView.visibility = View.GONE
                priceForModelsTextView.visibility = View.GONE
                finalPriceTextView.visibility = View.GONE
                priceForDeliveryTextView.visibility = View.GONE
                deliveryTextPriceTextView.visibility = View.GONE
                priceTextForModelsTextView.visibility = View.GONE
                generalPriceTextView.visibility = View.GONE
                makeOrderButton.visibility = View.GONE
                whiteLineImageView.visibility = View.GONE
                cartIconImageView.visibility = View.GONE
            }
        }

        modelByIdViewModel.modelResult.observe(viewLifecycleOwner) { modelByIdResult ->
            if (existingFragment == null){
                modelList.add(modelByIdResult.second!!.data)
                cartAdapter.submitList(modelList)
                updateTotalPrice(modelList)
            }
            else {
                modelList.clear()
                modelList.add(modelByIdResult.second!!.data)
                cartAdapter.submitList(modelList)
                updateTotalPrice(modelList)
            }
        }

        cartViewModel.getCart()

        val profileViewModel = ViewModelProvider(this, ProfileViewModelFactory(okHttpConfig, "self")).get(
            ProfileViewModel::class.java)

        profileViewModel.profileResult.observe(viewLifecycleOwner) { profile ->
            if (profile != null) {
                val userData = profile.second
                if (userData != null) {
                    val missingDataList = mutableListOf<String>()

                    if (userData.data.firstName.isEmpty()) {
                        missingDataList.add("ім'я")
                    }

                    if (userData.data.midName.isEmpty()) {
                        missingDataList.add("по батькові")
                    }

                    if (userData.data.phoneNumber.isEmpty()) {
                        missingDataList.add("номер телефону")
                    }

                    if (missingDataList.isNotEmpty()) {
                        val missingDataString = missingDataList.joinToString(", ")
                        val errorMessage = "Для оформлення замовлення необхідно заповнити дані про $missingDataString"
                        CustomToast.showError(requireContext(), errorMessage)
                        makeOrderButton.isClickable = false
                    } else {
                        val confirmOrderFragment = ConfirmOrderFragment()

                        val transaction = requireActivity().supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.fragment_container, confirmOrderFragment)
                        transaction.addToBackStack(null)
                        transaction.commit()

                        (requireActivity() as MainActivity).hideBottomNavigationView()
                    }
                }
            }
        }

        makeOrderButton.setOnClickListener {
            profileViewModel.getProfile()
        }

        return view
    }

    private fun updateTotalPrice(models: List<Item>) {
        val totalPrice = models.sumOf { it.minPrice.toDouble() }
        priceForModelsTextView.text = getString(R.string.price_format, totalPrice)
        val deliveryPriceString = priceForDeliveryTextView.text.toString().trim()
        val deliveryPrice = try {
            deliveryPriceString.substringBefore('₴').trim().toInt()
        } catch (e: NumberFormatException) {
            0
        }

        finalPriceTextView.text = getString(R.string.price_format, totalPrice + deliveryPrice)
    }

    override fun onItemRemoved(item: Item) {
        val updatedModelList = cartAdapter.currentList.toMutableList()
        updatedModelList.remove(item)
        cartAdapter.submitList(updatedModelList)
        updateTotalPrice(updatedModelList)

        if (updatedModelList.isEmpty()) {
            emptyCartTextView.visibility = View.VISIBLE
            cartRecyclerView.visibility = View.GONE
            priceForModelsTextView.visibility = View.GONE
            finalPriceTextView.visibility = View.GONE
            priceForDeliveryTextView.visibility = View.GONE
            deliveryTextPriceTextView.visibility = View.GONE
            priceTextForModelsTextView.visibility = View.GONE
            generalPriceTextView.visibility = View.GONE
            makeOrderButton.visibility = View.GONE
            whiteLineImageView.visibility = View.GONE
            cartIconImageView.visibility = View.GONE
        }
    }

}