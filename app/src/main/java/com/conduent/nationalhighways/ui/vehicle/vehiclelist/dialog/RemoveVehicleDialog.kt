package com.conduent.nationalhighways.ui.vehicle.vehiclelist.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse
import com.conduent.nationalhighways.databinding.DialogRemoveVehicleBinding
import com.conduent.nationalhighways.ui.base.BaseDialog
import com.conduent.nationalhighways.ui.vehicle.vehiclelist.adapter.RemoveVehicleDialogAdapter
import kotlinx.coroutines.launch

class RemoveVehicleDialog : BaseDialog<DialogRemoveVehicleBinding>() {

    private lateinit var removeAdapter: RemoveVehicleDialogAdapter
    private var selectedVehicleList = mutableListOf<String?>()

    override fun getDialogBinding(inflater: LayoutInflater, container: ViewGroup?) =
        DialogRemoveVehicleBinding.inflate(inflater, container, false)

    override fun init() {
        setBtnActivated()
        removeAdapter = RemoveVehicleDialogAdapter(this)
        binding.removeRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = removeAdapter
        }
        removeAdapter.setList(vehicleList)
    }

    override fun initCtrl() {
        binding.btnRemove.setOnClickListener {
            dismiss()
            mListener?.onRemoveClick(selectedVehicleList)
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.ivClose.setOnClickListener {
            dismiss()
        }
    }

    fun addRemoveVehicleData(id: String?) {
        if (selectedVehicleList.contains(id)) {
            selectedVehicleList.remove(id)
        } else {
            selectedVehicleList.add(id)
        }
        setBtnActivated()
    }

    override fun observer() {
        lifecycleScope.launch {
//            observe(viewModel.logout,::handleLogout)
        }
    }

    companion object {
        var vehicleList: ArrayList<VehicleResponse?> = ArrayList()
        var mListener: RemoveVehicleListener? = null

        fun newInstance(
            list: ArrayList<VehicleResponse?>,
            listener: RemoveVehicleListener
        ): RemoveVehicleDialog {
            mListener = listener
            vehicleList = list
            return RemoveVehicleDialog()
        }
    }

    private fun setBtnActivated() {
        binding.model = selectedVehicleList.size >= 1
    }


    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.dialog_background
            )
        )
    }
}