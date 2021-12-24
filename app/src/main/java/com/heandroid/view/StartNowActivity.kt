package com.heandroid.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.heandroid.R
import com.heandroid.databinding.ActivityStartNowBinding
import com.heandroid.fragments.ContactDartChargeFragment
import com.heandroid.fragments.CrossingServiceUpdateFragment
import com.heandroid.utils.Constants
import kotlinx.android.synthetic.main.toolbar_with_logo.view.*

class StartNowActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartNowBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_start_now);
        binding.btnStartNow.setOnClickListener {
            var intent = Intent(this, ActivityHome::class.java)
            startActivity(intent);
        }

        binding.idToolBarLyt.btnLogin.setOnClickListener {
            var intent = Intent(this, LoginActivity::class.java)
            startActivity(intent);
        }


        binding.rlAboutService.setOnClickListener {
            //
            var intent = Intent(this, StartNowBaseActivity::class.java)
            var bundle = Bundle()
            bundle.putString(Constants.SHOW_SCREEN, Constants.ABOUT_SERVICE)
            intent.putExtra(Constants.DATA, bundle);
            startActivity(intent)
        }

        binding.rlContactDartCharge.setOnClickListener {
            //
            var intent = Intent(this, StartNowBaseActivity::class.java)
            var bundle = Bundle()
            bundle.putString(Constants.SHOW_SCREEN, Constants.CONTACT_DART_CHARGES)
            intent.putExtra(Constants.DATA, bundle);
            startActivity(intent)
        }

        binding.rlCrossingServiceUpdate.setOnClickListener {
            //
            var intent = Intent(this, StartNowBaseActivity::class.java)
            var bundle = Bundle()
            bundle.putString(Constants.SHOW_SCREEN, Constants.CROSSING_SERVICE_UPDATE)
            intent.putExtra(Constants.DATA, bundle);
            startActivity(intent)
        }

    }
}