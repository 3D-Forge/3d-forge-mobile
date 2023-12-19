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
import com.example.a3dforge.adapter.CartOrderAdapter
import com.example.a3dforge.base.CustomToast
import com.example.a3dforge.entities.Item
import com.example.a3dforge.entities.OrderPutRequestBody
import com.example.a3dforge.factories.CartViewModelFactory
import com.example.a3dforge.factories.ModelByIdViewModelFactory
import com.example.a3dforge.factories.OrderPutViewModelFactory
import com.example.a3dforge.factories.ProfileViewModelFactory
import com.example.a3dforge.models.CartViewModel
import com.example.a3dforge.models.ModelByIdViewModel
import com.example.a3dforge.models.OrderPutViewModel
import com.example.a3dforge.models.ProfileViewModel

class ConfirmOrderFragment : Fragment(), CartOrderAdapter.CartOrderAdapterListener {

    private lateinit var backToCartArrowImageView: ImageView

    private lateinit var confirmOrderUserInfoTextView: TextView
    private lateinit var confirmOrderButton: Button

    private lateinit var orderRecyclerView: RecyclerView

    val modelList = mutableListOf<Item>()
    private lateinit var cartOrderAdapter: CartOrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_confirm_order, container, false)

        backToCartArrowImageView = view.findViewById(R.id.backToCartArrowImageView)
        confirmOrderButton = view.findViewById(R.id.confirmOrderButton)

        confirmOrderUserInfoTextView = view.findViewById(R.id.confirmOrderUserInfoTextView)

        orderRecyclerView = view.findViewById(R.id.orderRecyclerView)

        cartOrderAdapter = CartOrderAdapter(requireActivity(), this)

        orderRecyclerView.adapter = cartOrderAdapter
        orderRecyclerView.layoutManager = GridLayoutManager(this.requireContext(), 1)

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

        confirmOrderButton.setOnClickListener {
            val orderPutViewModel = ViewModelProvider(this, OrderPutViewModelFactory(okHttpConfig))
                .get(OrderPutViewModel::class.java)

            orderPutViewModel.orderPutResult.observe(viewLifecycleOwner) { orderPutResult ->

            }

            orderPutViewModel.putOrder(
                querry = OrderPutRequestBody(
                    cartId = 1,
                    firstname = "string",
                    midname = "string",
                    lastname = "string",
                    country = "string",
                    region = "string",
                    city = "string",
                    cityRegion = "string",
                    street = "string",
                    house = "string",
                    apartment = "string"
                )
            )

            CustomToast.showSuccess(this.requireContext(), "Замовлення успішно зроблене!")

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment())
                .commit()
        }

        profileViewModel.getProfile()

        val cartViewModel = ViewModelProvider(this, CartViewModelFactory(okHttpConfig))
            .get(CartViewModel::class.java)

        val modelByIdViewModel = ViewModelProvider(this, ModelByIdViewModelFactory(okHttpConfig))
            .get(ModelByIdViewModel::class.java)

        cartViewModel.cartResult.observe(viewLifecycleOwner) { cartResult ->
            if (cartResult.second?.data?.orderedModelIDs?.size != 0) {
                cartResult.second?.data?.orderedModelIDs?.forEach { orderedModelId ->
                    val catalogModelId = orderedModelId.catalogModelId
                    modelByIdViewModel.getModelById(catalogModelId)
                }
            }
        }

        modelByIdViewModel.modelResult.observe(viewLifecycleOwner) { modelByIdResult ->
            modelList.add(modelByIdResult.second!!.data)
            cartOrderAdapter.submitList(modelList)
/*            updateTotalPrice(modelList)*/
        }

        cartViewModel.getCart()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()

        (requireActivity() as MainActivity).showBottomNavigationView()
    }

    override fun onItemRemoved(item: Item) {
        val updatedModelList = cartOrderAdapter.currentList.toMutableList()
        updatedModelList.remove(item)
        cartOrderAdapter.submitList(updatedModelList)
/*        updateTotalPrice(updatedModelList)*/
    }

}