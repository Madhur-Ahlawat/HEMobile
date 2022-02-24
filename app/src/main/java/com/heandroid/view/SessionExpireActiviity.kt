package com.heandroid.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.heandroid.R
import com.heandroid.databinding.ActivitySessionExpireBinding
import com.heandroid.gone

class SessionExpireActiviity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding : ActivitySessionExpireBinding
    private var type : String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySessionExpireBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        initCtrl()
    }

    private fun init() {
        type=intent?.getStringExtra("type")
        binding.toolbar.tvContactUs.gone()

        when(type){
            "LOGIN"  -> { 
                binding.tvLabel.text=getString(R.string.select_the_sign_in_button_to_log_in_to_your_account)
                binding.btn.text=getString(R.string.txt_sign_in)
            }
            "SIGN IN" -> {
                binding.tvLabel.text=getString(R.string.select_the_start_now_button_to_restart_your_session)
                binding.btn.text=getString(R.string.start_again)
            }
        }
    }
    private fun initCtrl(){
        binding.btn.setOnClickListener(this)
    }
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn ->{
                finish()
                when(type){
                    "LOGIN" ->{ startActivity(Intent(this,LoginActivity::class.java)) }
                    "SIGN IN" ->{ startActivity(Intent(this,ActivityCreateAccount::class.java)) }
                }
            }
        }
    }
}