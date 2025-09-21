package com.conduent.nationalhighways.utils.extn

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.conduent.nationalhighways.R

fun <A : Activity> Activity.startNewActivityByClearingStack(
    activity: Class<A>,
    extras: Bundle.() -> Unit = {}
) {

    Intent(this, activity).run {
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        putExtras(Bundle().apply(extras))
        startActivity(this)
    }

}

fun <A : Activity> Activity.openActivityWithData(it: Class<A>, extras: Bundle.() -> Unit = {}) {
    Intent(this, it).run {
        putExtras(Bundle().apply(extras))
        startActivity(this)
        finish()
    }
}

fun <A : Activity> Activity.openActivityWithDataBack(it: Class<A>, extras: Bundle.() -> Unit = {}) {
    Intent(this, it).run {
        putExtras(Bundle().apply(extras))
        startActivity(this)
    }
}

fun <A : Activity> Activity.openActivityWithData(it: Class<A>, extras: Bundle) {
    Intent(this, it).run {
        putExtras(extras)
        startActivity(this)
    }
}

fun <A : Activity> Activity.startNormalActivity(activity: Class<A>) {
    Intent(this, activity).run {
        startActivity(this)
    }
}

fun <A : Activity> Activity.startNormalActivityWithFinish(activity: Class<A>) {
    Intent(this, activity).run {
        startActivity(this)
        finish()
    }
}

fun Activity.toolbar(title: String?, isBackWithText: Boolean = false) {
    val imgBack: ImageView? = findViewById(R.id.btn_back)
    val txtBack: AppCompatTextView? = findViewById(R.id.btn_backText)
    if (isBackWithText) {
        txtBack?.visibility = View.VISIBLE
        imgBack?.visibility = View.GONE
        txtBack?.setOnClickListener {
            onBackPressed()
        }
    } else {
        txtBack?.visibility = View.GONE
        imgBack?.visibility = View.VISIBLE
        imgBack?.setOnClickListener {
            onBackPressed()
        }
    }
    val tvHeader = findViewById<TextView?>(R.id.tv_header)
    tvHeader?.text = title

}

fun Activity.customToolbar(title: String?, isBackWithText: Boolean = false) {

    val imgBack: ImageView? = findViewById(R.id.back_button)
    val txtBack: AppCompatTextView? = findViewById(R.id.btn_backText)
    if (isBackWithText) {
        txtBack?.visibility = View.VISIBLE
        imgBack?.visibility = View.GONE
        txtBack?.setOnClickListener {
            onBackPressed()
        }
    } else {
        txtBack?.visibility = View.GONE
        imgBack?.visibility = View.VISIBLE
        imgBack?.setOnClickListener {
            onBackPressed()
        }
    }
    val tvHeader = findViewById<TextView?>(R.id.title_txt)
    tvHeader?.text = title
}

fun Activity.setRightButtonText(title: String) {
    val btnRight = findViewById<AppCompatTextView>(R.id.btn_login)
    btnRight.text = title
}