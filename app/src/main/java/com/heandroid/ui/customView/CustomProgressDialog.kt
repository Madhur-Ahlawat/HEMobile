package com.heandroid.ui.customView

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.content.res.ResourcesCompat
import com.heandroid.R
import javax.inject.Inject

class CustomProgressDialog @Inject constructor( context:Context) : Dialog(context) {
    lateinit var dialog: CustomProgressDialog
    init {
        // Set Semi-Transparent Color for Dialog Background
        window?.decorView?.rootView?.setBackgroundResource(R.color.dialogBackground)
        window?.decorView?.setOnApplyWindowInsetsListener { _, insets ->
            insets.consumeSystemWindowInsets()
        }
    }



    fun show(context: Context): Dialog {
        return show(context, null)
    }

    fun show(context: Context, title: CharSequence?): Dialog {
        val inflater = (context as Activity).layoutInflater
        val view = inflater.inflate(R.layout.progress_dialog, null)
//        if (title != null) {
//            view.cp_title.text = title
//        }
//
//        // Card Color
//        view.cp_cardview.setCardBackgroundColor(Color.parseColor("#70000000"))
//
//        // Progress Bar Color
//        setColorFilter(view.cp_pbar.indeterminateDrawable, ResourcesCompat.getColor(context.resources, R.color.colorPrimary, null))
//
//        // Text Color
//        view.cp_title.setTextColor(Color.WHITE)

        dialog = CustomProgressDialog(context)
        dialog.setContentView(view)
        dialog.show()
        return dialog
    }

    private fun setColorFilter(drawable: Drawable, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            drawable.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
        } else {
            @Suppress("DEPRECATION")
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
    }

}