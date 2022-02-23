package com.heandroid.ui.startNow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.heandroid.ui.base.BaseFragment

class StartNowFragment : BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//            binding = DataBindingUtil.setContentView(this, R.layout.activity_start_now);
//            binding.btnStartNow.setOnClickListener {
//                var intent = Intent(this, ActivityHome::class.java)
//                startActivity(intent);
//            }
//
//            binding.idToolBarLyt.btnLogin.setOnClickListener {
//                var intent = Intent(this, LoginActivity::class.java)
//                startActivity(intent);
//            }
//
//            binding.rlAboutService.setOnClickListener {
//                //
//                var intent = Intent(this, StartNowBaseActivity::class.java)
//                var bundle = Bundle()
//                bundle.putString(Constants.SHOW_SCREEN, Constants.ABOUT_SERVICE)
//                intent.putExtra(Constants.DATA, bundle);
//                startActivity(intent)
//            }
//
//            binding.rlContactDartCharge.setOnClickListener {
//                //
//                var intent = Intent(this, StartNowBaseActivity::class.java)
//                var bundle = Bundle()
//                bundle.putString(Constants.SHOW_SCREEN, Constants.CONTACT_DART_CHARGES)
//                intent.putExtra(Constants.DATA, bundle);
//                startActivity(intent)
//            }
//
//            binding.rlCrossingServiceUpdate.setOnClickListener {
//                //
//                var intent = Intent(this, StartNowBaseActivity::class.java)
//                var bundle = Bundle()
//                bundle.putString(Constants.SHOW_SCREEN, Constants.CROSSING_SERVICE_UPDATE)
//                intent.putExtra(Constants.DATA, bundle);
//                startActivity(intent)
//            }
//
//        }
//    }
    }
}