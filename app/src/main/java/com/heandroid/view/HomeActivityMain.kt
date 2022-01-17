package com.heandroid.view

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.heandroid.R
import com.heandroid.customviews.BottomNavigationView
import com.heandroid.databinding.ActivityHomeMainBinding
import com.heandroid.listener.OnNavigationItemChangeListener

class HomeActivityMain : AppCompatActivity() {

    private lateinit var dataBinding: ActivityHomeMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_home_main)
        setView()
    }

    private fun setView(){
        dataBinding.idToolBarLyt.btnLogin.visibility= View.GONE

        dataBinding.bottomNavigationView.setActiveNavigationIndex(0)
        dataBinding.bottomNavigationView.setOnNavigationItemChangedListener(object :
            OnNavigationItemChangeListener {
            override fun onNavigationItemChanged(navigationItem: BottomNavigationView.NavigationItem) {

                when(navigationItem.position){

                    0->{
                        dataBinding.fragmentContainerView.findNavController().navigate(R.id.dashBoardFragment)

                    }
                    1->{
                        dataBinding.fragmentContainerView.findNavController().navigate(R.id.vehicleFragment)
                    }
                    2->{
                        dataBinding.fragmentContainerView.findNavController().navigate(R.id.notificationFragment)
                    }
                    3->{
                        dataBinding.fragmentContainerView.findNavController().navigate(R.id.accountFragment)

                    }

                }
            }
        })


    }
}