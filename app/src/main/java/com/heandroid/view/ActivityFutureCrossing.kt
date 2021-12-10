package com.heandroid.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.heandroid.R
import com.heandroid.databinding.ActivityFutureCrossingsEnterBinding
import com.heandroid.model.VehicleDetailsModel
import com.heandroid.utils.Logg
import kotlinx.android.synthetic.main.tool_bar_with_title_back.view.*

class ActivityFutureCrossing : AppCompatActivity() {


    private lateinit var dataBinding: ActivityFutureCrossingsEnterBinding

    private lateinit var mVehicleDetails: VehicleDetailsModel

    private var TAG = "ActivityFutureCrossing"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_future_crossings_enter)
        setUpView()
    }


    private fun setUpView() {

        mVehicleDetails =
            intent?.getParcelableExtra<VehicleDetailsModel>("list") as VehicleDetailsModel
        Logg.logging(TAG, " mVehicleDetails  $mVehicleDetails ")
        setBtnActivated()
        dataBinding.vrmTitle.text = "${mVehicleDetails.vrmNo}"
        dataBinding.futureCrossingCount.text = "$mCount"

        dataBinding.addFutureCrossings.setOnClickListener {

            mCount = mCount.plus(1)
            dataBinding.futureCrossingCount.text = "$mCount"

            val mTotalAmount = (mCount * 5 + (1 * 5))
            dataBinding.totalAmountTxt.text = "£ $mTotalAmount 00"

        }

        dataBinding.removeFutureCrossings.setOnClickListener {

            if (mCount > 0) {
                mCount = mCount.minus(1)
                val mTotalAmount = (mCount * 5 + (1 * 5))
                dataBinding.totalAmountTxt.text = "£ $mTotalAmount .00"

            }

            dataBinding.futureCrossingCount.text = "$mCount"

        }
        dataBinding.idToolBarLyt.back_button.setOnClickListener {
            onBackPressed()
        }

        dataBinding.conformBtn.setOnClickListener {
            var intent = Intent(this, PaymentReceiptSelectionActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private var mCount = 0
    private fun setBtnActivated() {
        dataBinding.conformBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.btn_color))

        dataBinding.conformBtn.setTextColor(ContextCompat.getColor(this, R.color.white))
    }

}