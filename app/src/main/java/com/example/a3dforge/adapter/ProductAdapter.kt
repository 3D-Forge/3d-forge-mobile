package com.example.a3dforge.adapter

import OkHttpConfig
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.a3dforge.R
import com.example.a3dforge.entities.Item
import com.example.a3dforge.factories.AvatarViewModelFactory
import com.example.a3dforge.factories.ProductPictureViewModelFactory
import com.example.a3dforge.models.AvatarViewModel
import com.example.a3dforge.models.ProductPictureViewModel

class ProductAdapter(private val fragmentActivity: FragmentActivity) :
    ListAdapter<Item, ProductAdapter.Holder>(Comparator()) {

    val okHttpConfig = OkHttpConfig

    val productPictureViewModel = ViewModelProvider(fragmentActivity, ProductPictureViewModelFactory(okHttpConfig))
        .get(ProductPictureViewModel::class.java)

    class Comparator : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {

        val productNameTextView: TextView = view.findViewById(R.id.productNameTextView)
        val productPriceTextView: TextView = view.findViewById(R.id.productPriceTextView)
        val productRating: RatingBar = view.findViewById(R.id.productRating)
        val productImageView: ImageView = view.findViewById(R.id.productImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return Holder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = getItem(position)

        holder.productNameTextView.text = item.name
        holder.productPriceTextView.text = item.minPrice.toString() + " ₴"
        holder.productRating.rating = item.rating

        val observer = Observer<Pair<Int, ByteArray?>?> { result ->
            result?.let {
                if (it.first == item.id) {
                    it.second?.let { imageBytes ->
                        try {
                            val imageBitmap: Bitmap =
                                BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                            holder.productImageView.setImageBitmap(imageBitmap)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }

        productPictureViewModel.pictureResult.observe(fragmentActivity, observer)

        productPictureViewModel.getProductPicture(item.id)
    }

}

