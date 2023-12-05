package com.example.a3dforge.adapter

import OkHttpConfig
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.a3dforge.R
import com.example.a3dforge.entities.CartGetRequestBody
import com.example.a3dforge.entities.Item
import com.example.a3dforge.entities.ModelByIdGetRequestBody
import com.example.a3dforge.factories.CartViewModelFactory
import com.example.a3dforge.factories.ModelByIdViewModelFactory
import com.example.a3dforge.factories.ProductPictureViewModelFactory
import com.example.a3dforge.models.CartViewModel
import com.example.a3dforge.models.ModelByIdViewModel
import com.example.a3dforge.models.ProductPictureViewModel

class CartAdapter(private val fragmentActivity: FragmentActivity) :
    ListAdapter<Item, CartAdapter.ViewHolder>(Comparator()) {

    val okHttpConfig = OkHttpConfig
    private var modelId: Int = 0

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImageView = itemView.findViewById<ImageView>(R.id.productImageView)
        val cartModelNameTextView = itemView.findViewById<TextView>(R.id.cartModelNameTextView)
        val cartModelDescriptionTextView = itemView.findViewById<TextView>(R.id.cartModelDescriptionTextView)
        val cartModelPriceTextView = itemView.findViewById<TextView>(R.id.cartModelPriceTextView)
        val textViewCounter: TextView = itemView.findViewById(R.id.countTextView)
        val imageViewPlus: ImageView = itemView.findViewById(R.id.countIncreaseImageView)
        val imageViewMinus: ImageView = itemView.findViewById(R.id.countDecreaseImageView)

        var currentItem: Item? = null
        var counter: Int = 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_model_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.currentItem = item

        holder.cartModelNameTextView.text = item.name
        holder.cartModelDescriptionTextView.text = item.description
        holder.cartModelPriceTextView.text = item.minPrice.toString() + " ₴"

        val productPictureViewModel = ViewModelProvider(fragmentActivity, ProductPictureViewModelFactory(okHttpConfig))
            .get(ProductPictureViewModel::class.java)

        val imageObserver = Observer<Pair<Int, ByteArray?>?> { result ->
            result?.let {
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

        productPictureViewModel.pictureResult.observe(fragmentActivity, imageObserver)
        productPictureViewModel.getProductPicture(item.id)


        holder.imageViewPlus.setOnClickListener {
            if (holder.counter < 10) {
                holder.counter++
                holder.textViewCounter.text = holder.counter.toString()
            } else {
                // Максимальное значение достигнуто
                // Вы можете добавить здесь код для обработки максимального значения
            }
        }

        holder.imageViewMinus.setOnClickListener {
            if (holder.counter > 1) {
                holder.counter--
                holder.textViewCounter.text = holder.counter.toString()
            } else {
                // Удаление элемента при достижении значения 1
            }
        }
    }

    class Comparator : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }
    }
}
