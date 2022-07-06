package com.heandroid.utils.extn

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.heandroid.R

fun <A : Activity> Activity.startNewActivityByClearingStack(activity: Class<A>) {
    Intent(this, activity).run {
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
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

fun <A : Activity> Activity.startNormalActivity(activity: Class<A>) {
    Intent(this, activity).run {
        startActivity(this)
    }
}

fun Activity.toolbar(title: String?) {
    val ivBack = findViewById<ImageView?>(R.id.btn_back)
    val tvHeader = findViewById<TextView?>(R.id.tv_header)
    tvHeader?.text = title
    ivBack?.setOnClickListener {
        onBackPressed()
    }
}

fun Activity.customToolbar(title: String?) {
    val ivBack = findViewById<ImageView?>(R.id.back_button)
    val tvHeader = findViewById<TextView?>(R.id.title_txt)
    tvHeader?.text = title
    ivBack?.setOnClickListener {
        onBackPressed()
    }
}

fun Activity.setRightButtonText(title: String) {
    val btnRight = findViewById<AppCompatTextView>(R.id.btn_login)
    btnRight.text = title
}