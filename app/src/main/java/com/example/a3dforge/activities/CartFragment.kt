package com.example.a3dforge.activities

import OkHttpConfig
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.a3dforge.R
import com.example.a3dforge.adapter.CartAdapter
import com.example.a3dforge.entities.Item
import com.example.a3dforge.entities.ModelByIdGetRequestBody
import com.example.a3dforge.factories.CartViewModelFactory
import com.example.a3dforge.factories.ModelByIdViewModelFactory
import com.example.a3dforge.models.CartViewModel
import com.example.a3dforge.models.ModelByIdViewModel

class CartFragment : Fragment() {
    private lateinit var backArrowImageView: ImageView
    private lateinit var cartRecyclerView: RecyclerView

    private lateinit var cartAdapter: CartAdapter

    val okHttpConfig = OkHttpConfig
    private var modelId: Int = 0
    val modelList = mutableListOf<Item>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)

        backArrowImageView = view.findViewById(R.id.backArrowImageView)
        cartRecyclerView = view.findViewById(R.id.cartRecyclerView)

        backArrowImageView.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment())
                .commit()
        }

        cartAdapter = CartAdapter(requireActivity())

        cartRecyclerView.adapter = cartAdapter
        cartRecyclerView.layoutManager = GridLayoutManager(this.requireContext(), 1)

        val cartViewModel = ViewModelProvider(this, CartViewModelFactory(okHttpConfig))
            .get(CartViewModel::class.java)

        val modelByIdViewModel = ViewModelProvider(this, ModelByIdViewModelFactory(okHttpConfig))
            .get(ModelByIdViewModel::class.java)

        cartViewModel.cartResult.observe(viewLifecycleOwner) { cartResult ->
            cartResult.second?.data?.orderedModelIDs?.forEach { orderedModelId ->
                val catalogModelId = orderedModelId.catalogModelId

                modelByIdViewModel.getModelById(catalogModelId)
            }
        }

        modelByIdViewModel.modelResult.observe(viewLifecycleOwner) { modelByIdResult ->
            modelList.add(modelByIdResult.second!!.data)
            cartAdapter.submitList(modelList)
        }

        cartViewModel.getCart()

        return view
    }

}