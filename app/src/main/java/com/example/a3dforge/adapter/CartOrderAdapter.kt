package com.example.a3dforge.adapter

import OkHttpConfig
import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.InputFilter
import android.util.Log
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
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.marginStart
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.a3dforge.R
import com.example.a3dforge.entities.CartGetRequestBody
import com.example.a3dforge.entities.Item
import com.example.a3dforge.factories.CartViewModelFactory
import com.example.a3dforge.factories.DeleteCartViewModelFactory
import com.example.a3dforge.factories.ProductPictureViewModelFactory
import com.example.a3dforge.models.CartViewModel
import com.example.a3dforge.models.DeleteCartViewModel
import com.example.a3dforge.models.ProductPictureViewModel

class CartOrderAdapter(private val fragmentActivity: FragmentActivity, private val listener: CartOrderAdapterListener) :
    ListAdapter<Item, CartOrderAdapter.ViewHolder>(Comparator()) {

    private var selectedButton: AppCompatButton? = null

    interface CartOrderAdapterListener {
        fun onItemRemoved(item: Item)
    }

    val okHttpConfig = OkHttpConfig
    private var modelId: Int = 0

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImageView = itemView.findViewById<ImageView>(R.id.productOrderImageView)
        val cartModelNameTextView = itemView.findViewById<TextView>(R.id.cartOrderModelNameTextView)
        val cartOrderModelColorTextView = itemView.findViewById<TextView>(R.id.cartOrderModelColorTextView)
        val cartModelPriceTextView = itemView.findViewById<TextView>(R.id.cartOrderModelPriceTextView)
        val textViewCounter: TextView = itemView.findViewById(R.id.countOrderTextView)
        val imageViewPlus: ImageView = itemView.findViewById(R.id.countOrderIncreaseImageView)
        val imageViewMinus: ImageView = itemView.findViewById(R.id.countOrderDecreaseImageView)
        val orderParametersImageView: ImageView = itemView.findViewById(R.id.orderParametersImageView)

        var currentItem: Item? = null
        var counter: Int = 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_order_model_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartOrderAdapter.ViewHolder, position: Int) {
        val item = getItem(position)
        holder.currentItem = item

        holder.cartModelNameTextView.text = item.name
        holder.cartModelPriceTextView.text = item.minPrice.toString() + " ₴"

        val productPictureViewModel = ViewModelProvider(fragmentActivity, ProductPictureViewModelFactory(okHttpConfig))
            .get(ProductPictureViewModel::class.java)

        val imageObserver = Observer<Pair<Int, ByteArray?>?> { result ->
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

        productPictureViewModel.pictureResult.observe(fragmentActivity, imageObserver)
        productPictureViewModel.getProductPicture(item.id)

        holder.imageViewPlus.setOnClickListener {
            if (holder.counter < 10) {
                holder.counter++
                holder.textViewCounter.text = holder.counter.toString()
            } else {
            }
        }

        holder.imageViewMinus.setOnClickListener {
            if (holder.counter > 1) {
                holder.counter--
                holder.textViewCounter.text = holder.counter.toString()
            } else {
/*                showDeleteConfirmationDialog(holder, item)*/
            }
        }

        holder.orderParametersImageView.setOnClickListener {
            showPrintOptionsDialog(holder, item)
        }
    }

    fun showDeleteConfirmationDialog(holder: ViewHolder, item: Item) {
        val dialogView = LayoutInflater.from(fragmentActivity).inflate(R.layout.dialog_confirm_delete, null)
        val dialogBuilder = AlertDialog.Builder(fragmentActivity)
            .setView(dialogView)
            .setCancelable(false)

        val alertDialog = dialogBuilder.create()

        val btnConfirm = dialogView.findViewById<Button>(R.id.confirmDeleteButton)
        btnConfirm.setOnClickListener {
            alertDialog.dismiss()

            val position = holder.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val removedItem = getItem(position)
                val updatedList = currentList.toMutableList()
                updatedList.remove(removedItem)
                submitList(updatedList)
                notifyItemRemoved(position)

                listener.onItemRemoved(removedItem)

                val cartGetViewModel = ViewModelProvider(fragmentActivity, CartViewModelFactory(okHttpConfig))
                    .get(CartViewModel::class.java)

                val cartDeleteViewModel = ViewModelProvider(fragmentActivity, DeleteCartViewModelFactory(okHttpConfig))
                    .get(DeleteCartViewModel::class.java)

                val cartDeleteObserver = Observer<Boolean?> { result ->
                    result?.let {
                        if (it) {
                            Log.d("CartAdapter", "Item deleted")
                        } else {
                            Log.d("CartAdapter", "Item not deleted")
                        }
                    }
                }

                val cartGetObserver = Observer<Pair<Boolean, CartGetRequestBody?>?> { cartResult ->
                    cartResult?.second?.data?.orderedModelIDs?.forEach { orderedModelId ->
                        if (orderedModelId.catalogModelId == item.id) {
                            cartDeleteViewModel.deleteCart(orderedModelId.id)
                        }
                    }
                }

                cartGetViewModel.cartResult.observe(fragmentActivity, cartGetObserver)
                cartDeleteViewModel.cartDeleteResult.observe(fragmentActivity, cartDeleteObserver)

                cartGetViewModel.getCart()
            }
        }

        val btnCancel = dialogView.findViewById<Button>(R.id.cancelDeleteButton)
        btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun showPrintOptionsDialog(holder: ViewHolder, item: Item) {
        val inflater = LayoutInflater.from(fragmentActivity)
        val dialogView = inflater.inflate(R.layout.order_parameters_dialog, null)

        val alertDialog = AlertDialog.Builder(fragmentActivity)
            .setView(dialogView)
            .create()

        val confirmButton: AppCompatButton = dialogView.findViewById(R.id.confirmOrderParametersButton)
        val depthOrderParametersEditText: EditText = dialogView.findViewById(R.id.depthOrderParametersEditText)
        val xAxisOrderParametersEditText: EditText = dialogView.findViewById(R.id.xAxisOrderParametersEditText)
        val yAxisOrderParametersEditText: EditText = dialogView.findViewById(R.id.yAxisOrderParametersEditText)
        val zAxisOrderParametersEditText: EditText = dialogView.findViewById(R.id.zAxisOrderParametersEditText)
        val scaleOrderParametersEditText: EditText = dialogView.findViewById(R.id.scaleOrderParametersEditText)

        val percentTextView: TextView = dialogView.findViewById(R.id.percentTextView)

        val xAxisSmOrderParametersTextView: TextView = dialogView.findViewById(R.id.xAxisSmOrderParametersTextView)
        val yAxisSmOrderParametersTextView: TextView = dialogView.findViewById(R.id.yAxisSmOrderParametersTextView)
        val zAxisSmOrderParametersTextView: TextView = dialogView.findViewById(R.id.zAxisSmOrderParametersTextView)

        val closeOrderParametersImageView: ImageView = dialogView.findViewById(R.id.closeOrderParametersImageView)
        val orderParametersPrintTypeSpinner: Spinner = dialogView.findViewById(R.id.orderParametersPrintTypeSpinner)

        val orderAbsButton: AppCompatButton = dialogView.findViewById(R.id.orderAbsButton)
        val orderPlaButton: AppCompatButton = dialogView.findViewById(R.id.orderPlaButton)
        val orderAsaButton: AppCompatButton = dialogView.findViewById(R.id.orderAsaButton)

        val blackColorOrderParametersButton: AppCompatButton = dialogView.findViewById(R.id.blackColorOrderParametersButton)
        val whiteColorOrderParametersButton: AppCompatButton = dialogView.findViewById(R.id.whiteColorOrderParametersButton)
        val redColorOrderParametersButton: AppCompatButton = dialogView.findViewById(R.id.redColorOrderParametersButton)
        val blueColorOrderParametersButton: AppCompatButton = dialogView.findViewById(R.id.blueColorOrderParametersButton)


        orderAbsButton.setOnClickListener {
            orderAbsButton.setBackgroundResource(R.drawable.order_parameters_btn_pressed)
            orderPlaButton.setBackgroundResource(R.drawable.order_parameters_btn_unpressed)
            orderAsaButton.setBackgroundResource(R.drawable.order_parameters_btn_unpressed)
        }

        orderPlaButton.setOnClickListener {
            orderPlaButton.setBackgroundResource(R.drawable.order_parameters_btn_pressed)
            orderAbsButton.setBackgroundResource(R.drawable.order_parameters_btn_unpressed)
            orderAsaButton.setBackgroundResource(R.drawable.order_parameters_btn_unpressed)
        }

        orderAsaButton.setOnClickListener {
            orderAsaButton.setBackgroundResource(R.drawable.order_parameters_btn_pressed)
            orderAbsButton.setBackgroundResource(R.drawable.order_parameters_btn_unpressed)
            orderPlaButton.setBackgroundResource(R.drawable.order_parameters_btn_unpressed)
        }

        blackColorOrderParametersButton.setOnClickListener {
            blackColorOrderParametersButton.setBackgroundResource(R.drawable.order_parameters_btn_pressed)
            whiteColorOrderParametersButton.setBackgroundResource(R.drawable.order_parameters_btn_unpressed)
            redColorOrderParametersButton.setBackgroundResource(R.drawable.order_parameters_btn_unpressed)
            blueColorOrderParametersButton.setBackgroundResource(R.drawable.order_parameters_btn_unpressed)
        }

        whiteColorOrderParametersButton.setOnClickListener {
            whiteColorOrderParametersButton.setBackgroundResource(R.drawable.order_parameters_btn_pressed)
            blackColorOrderParametersButton.setBackgroundResource(R.drawable.order_parameters_btn_unpressed)
            redColorOrderParametersButton.setBackgroundResource(R.drawable.order_parameters_btn_unpressed)
            blueColorOrderParametersButton.setBackgroundResource(R.drawable.order_parameters_btn_unpressed)
        }

        redColorOrderParametersButton.setOnClickListener {
            redColorOrderParametersButton.setBackgroundResource(R.drawable.order_parameters_btn_pressed)
            blackColorOrderParametersButton.setBackgroundResource(R.drawable.order_parameters_btn_unpressed)
            whiteColorOrderParametersButton.setBackgroundResource(R.drawable.order_parameters_btn_unpressed)
            blueColorOrderParametersButton.setBackgroundResource(R.drawable.order_parameters_btn_unpressed)
        }

        blueColorOrderParametersButton.setOnClickListener {
            blueColorOrderParametersButton.setBackgroundResource(R.drawable.order_parameters_btn_pressed)
            blackColorOrderParametersButton.setBackgroundResource(R.drawable.order_parameters_btn_unpressed)
            whiteColorOrderParametersButton.setBackgroundResource(R.drawable.order_parameters_btn_unpressed)
            redColorOrderParametersButton.setBackgroundResource(R.drawable.order_parameters_btn_unpressed)
        }

        val sortItems = listOf("Лиття за виплавленим воском (Lost-Wax Casting)", "Струменеве сполучне (Binder Jetting)", "Багатоструменевий синтез (MJF)")

        val arrayAdapter = ArrayAdapter(fragmentActivity, R.layout.spinner_order_item, sortItems)
        orderParametersPrintTypeSpinner.adapter = arrayAdapter

        orderParametersPrintTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val textView = view?.findViewById<TextView>(R.id.orderSpinnerTextView)
                val itemText = sortItems[position]
                val newText = if (orderParametersPrintTypeSpinner.isActivated) {
                    itemText
                } else {
                    itemText.replace(Regex("\\(.*?\\)"), "").trim()
                }
                textView?.text = newText
                textView?.textSize = 16F
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        val depthInputFilter = InputFilter { source, start, end, dest, dstart, dend ->
            val newText = dest.toString().substring(0, dstart) + source.toString().substring(start, end) + dest.toString().substring(dend)
            if (newText.isEmpty()) {
                return@InputFilter ""
            }
            val value = newText.toIntOrNull()
            if (value != null && value in 0..100) {
                if (value in 0..9){
                    val layoutParams = percentTextView.layoutParams as? ViewGroup.MarginLayoutParams
                    layoutParams?.marginStart = dpToPx(32, percentTextView.context)
                    percentTextView.layoutParams = layoutParams
                }
                if (value in 10..99){
                    val layoutParams = percentTextView.layoutParams as? ViewGroup.MarginLayoutParams
                    layoutParams?.marginStart = dpToPx(40, percentTextView.context)
                    percentTextView.layoutParams = layoutParams
                }
                if (value == 100){
                    val layoutParams = percentTextView.layoutParams as? ViewGroup.MarginLayoutParams
                    layoutParams?.marginStart = dpToPx(44, percentTextView.context)
                    percentTextView.layoutParams = layoutParams
                }
                return@InputFilter null
            }
            return@InputFilter ""
        }

        val axisInputFilter = InputFilter { source, start, end, dest, dstart, dend ->
            val newText = dest.toString().substring(0, dstart) + source.toString().substring(start, end) + dest.toString().substring(dend)
            if (newText.isEmpty()) {
                return@InputFilter "0"
            }
            val value = newText.toIntOrNull()
            if (value != null && value in 0..100) {
                return@InputFilter null
            }
            return@InputFilter ""
        }

        val scaleInputFilter = InputFilter { source, start, end, dest, dstart, dend ->
            val newText = dest.toString().substring(0, dstart) + source.toString().substring(start, end) + dest.toString().substring(dend)
            if (newText.isEmpty()) {
                return@InputFilter "0"
            }
            val value = newText.toIntOrNull()
            if (value != null && value in 0..100) {
                return@InputFilter null
            }
            return@InputFilter ""
        }

        depthOrderParametersEditText.filters = arrayOf(depthInputFilter)

        xAxisOrderParametersEditText.filters = arrayOf(axisInputFilter)
        yAxisOrderParametersEditText.filters = arrayOf(axisInputFilter)
        zAxisOrderParametersEditText.filters = arrayOf(axisInputFilter)

        scaleOrderParametersEditText.filters = arrayOf(scaleInputFilter)

        confirmButton.setOnClickListener {
            alertDialog.dismiss()
        }

        closeOrderParametersImageView.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()

        val window = alertDialog.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun dpToPx(dp: Int, context: Context): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
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