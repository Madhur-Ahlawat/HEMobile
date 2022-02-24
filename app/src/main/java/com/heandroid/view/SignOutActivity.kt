package com.heandroid.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.heandroid.R
import com.heandroid.databinding.ActivitySignOutBinding

class SignOutActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding : ActivitySignOutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        initCtrl()
    }

    private fun init() {
        binding.toolbar.tvContactUs.setOnClickListener(this)
        binding.btnSignin.setOnClickListener(this)
        binding.btnStart.setOnClickListener(this)
    }

    private fun initCtrl(){ }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.tvContactUs -> { /*startActivity(Intent(this,Contact))*/ }
            R.id.btnSignin ->{ startActivity(Intent(this,LoginActivity::class.java)) }
            R.id.btnStart ->{ startActivity(Intent(this,StartNowActivity::class.java)) }
        }
    }
}