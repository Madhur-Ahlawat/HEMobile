package com.heandroid.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RadioGroup
import androidx.core.view.get
import androidx.fragment.app.DialogFragment
import com.heandroid.R
import com.heandroid.databinding.DialogCrossingHistoryFilterBinding

class CrossingHistoryFilterDialog : DialogFragment(), View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private lateinit var binding : DialogCrossingHistoryFilterBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding= DialogCrossingHistoryFilterBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initCtrl()
    }

    private fun initCtrl(){
        binding.apply {
            ivClose.setOnClickListener(this@CrossingHistoryFilterDialog)
            rgFilterOption.setOnCheckedChangeListener(this@CrossingHistoryFilterDialog)
            edFrom.setOnClickListener(this@CrossingHistoryFilterDialog)
            edTo.setOnClickListener(this@CrossingHistoryFilterDialog)
            btnApply.setOnClickListener(this@CrossingHistoryFilterDialog)
            btnClear.setOnClickListener(this@CrossingHistoryFilterDialog)
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.ivClose ->{ dismiss() }
            R.id.edFrom -> { DatePicker(binding.edFrom).show(requireActivity().supportFragmentManager,"") }
            R.id.edTo -> { DatePicker(binding.edTo).show(requireActivity().supportFragmentManager,"") }
            R.id.btnApply -> {dismiss()}
            R.id.btnClear -> { clearSelection() }
        }
    }

    private fun clearSelection() {
        binding.rgFilterOption.clearCheck()
        binding.edFrom.text?.clear()
        binding.edTo.text?.clear()
    }


    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when(group?.checkedRadioButtonId){

            R.id.rbCustom ->{
                binding.llCustom.visibility=View.VISIBLE
                binding.vCustom.visibility=View.GONE
            }
            else -> {
                binding.llCustom.visibility=View.GONE
                binding.vCustom.visibility=View.VISIBLE
                binding.edFrom.text?.clear()
                binding.edTo.text?.clear()
            }

        }
    }

}