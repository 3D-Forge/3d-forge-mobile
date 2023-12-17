package com.example.a3dforge.base

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.a3dforge.R

class CustomToast {

    companion object {
        fun showSuccess(context: Context, message: String) {
            showToast(context, message, R.drawable.ic_check_circle)
        }

        fun showError(context: Context, message: String) {
            showToast(context, message, R.drawable.ic_error)
        }

        private fun showToast(context: Context, message: String, iconResId: Int) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val layout = inflater.inflate(R.layout.custom_toast_layout, null) as ConstraintLayout

            val textView = layout.findViewById<TextView>(R.id.toast_text)
            val imageView = layout.findViewById<ImageView>(R.id.toast_icon)

            textView.text = message
            imageView.setImageResource(iconResId)

            val toast = Toast(context)
            toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 100)
            toast.duration = Toast.LENGTH_SHORT
            toast.view = layout
            toast.show()
        }
    }
}

