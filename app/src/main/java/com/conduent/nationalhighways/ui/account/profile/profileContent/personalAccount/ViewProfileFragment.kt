package  com.conduent.nationalhighways.ui.account.profile.profileContent.personalAccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.profile.ProfileDetailModel
import com.conduent.nationalhighways.databinding.FragmentViewProfileBinding
import com.conduent.nationalhighways.ui.account.profile.ProfileActivity
import com.conduent.nationalhighways.ui.account.profile.ProfileViewModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil.showError
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.observe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewProfileFragment : BaseFragment<FragmentViewProfileBinding>(), View.OnClickListener {

    private val viewModel: ProfileViewModel by viewModels()
    private var loader: LoaderDialog? = null

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentViewProfileBinding = FragmentViewProfileBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
//        (requireActivity() as ProfileActivity).showLoader()
        viewModel.accountDetail()
    }

    override fun initCtrl() {
        binding.btnEditDetail.setOnClickListener(this)
    }

    override fun observer() {
        observe(viewModel.accountDetail, ::handleAccountDetail)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnEditDetail -> {
                val bundle = Bundle()
                bundle.putParcelable(Constants.DATA, binding.model)
                findNavController().navigate(
                    R.id.action_viewProfile_to_personalInfoFragment,
                    bundle
                )
            }
        }
    }

    private fun handleAccountDetail(status: Resource<ProfileDetailModel?>?) {
//        (requireActivity() as ProfileActivity).hideLoader()
        loader?.dismiss()
        when (status) {
            is Resource.Success -> {
                status.data?.run {
                    if (status.equals("500")) showError(binding.root, message)
                    else binding.model = this
                }

            }
            is Resource.DataError -> {
                showError(binding.root, status.errorMsg)
            }
            else -> {
            }
        }
    }

}