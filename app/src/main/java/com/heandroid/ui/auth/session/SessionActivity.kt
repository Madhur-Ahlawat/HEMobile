package com.heandroid.ui.auth.session

import androidx.navigation.findNavController
import com.heandroid.R
import com.heandroid.databinding.ActivitySessionBinding
import com.heandroid.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SessionActivity : BaseActivity<Any>() {

    private lateinit var binding: ActivitySessionBinding

    override fun initViewBinding() {
        binding= ActivitySessionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadFragment()
        initCtrl()
    }

    private fun loadFragment() {
       val navController= findNavController(R.id.fragment)
       val oldGraph= navController.graph
        when(intent?.getStringExtra("screen")){
            "expire" ->{ oldGraph.startDestination=R.id.sessionExpireFragment }
            "signout" ->{ oldGraph.startDestination=R.id.signOutFragment }
        }
        navController.graph=oldGraph
    }


    private fun initCtrl(){
        binding.toolbar.tvContactUs.setOnClickListener {
        }
    }

    override fun observeViewModel() {
    }

}