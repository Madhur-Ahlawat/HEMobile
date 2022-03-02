package com.heandroid.utils.extn

import android.app.Activity
import android.content.Intent
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.heandroid.R

fun <A : Activity> Activity.startNewActivity(activity: Class<A>){
    Intent(this, activity).run {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(this)
    }
}


fun <A : Activity> Activity.startNormalActivity(activity: Class<A>){
    Intent(this, activity).run {
        startActivity(this)
    }
}

fun Activity.toolbar(title: String?){
    val ivBack=findViewById<ImageView>(R.id.btn_back)
    val tvHeader=findViewById<TextView>(R.id.tv_header)
    tvHeader.text=title
    ivBack.setOnClickListener{
        onBackPressed()
    }
}

fun Activity.setRightButtonText(title:String)
{
    val btnRight = findViewById<AppCompatTextView>(R.id.btn_login)
    btnRight.text= title

}