package com.conduent.nationalhighways.ui.bottomnav.account.payments.history

import android.os.Looper.getMainLooper
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.accountpayment.AccountPaymentHistoryResponse
import com.conduent.nationalhighways.data.model.accountpayment.TransactionList
import com.conduent.nationalhighways.data.model.vehicle.PlateInfoResponse
import com.conduent.nationalhighways.data.model.vehicle.VehicleInfoResponse
import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse
import com.conduent.nationalhighways.ui.loader.ErrorDialog
import com.conduent.nationalhighways.utils.common.ConstantsTest
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.data.DataFile
import com.conduent.nationalhighways.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.not
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
@MediumTest
class AccountPaymentHistoryFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val viewModel = mockk<AccountPaymentHistoryViewModel>(relaxed = true)

    private val vehicleListLiveData = MutableLiveData<Resource<List<VehicleResponse?>?>?>()

    private val paymentHistoryLive = MutableLiveData<Resource<AccountPaymentHistoryResponse?>?>()

    private val navController: NavController = Mockito.mock(NavController::class.java)

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun `test payment history screen visibility`() {
        every { viewModel.paymentHistoryLiveData } returns paymentHistoryLive
        launchFragmentInHiltContainer<AccountPaymentHistoryFragment> {
            onView(withId(R.id.yourPayment)).check(matches(isDisplayed()))
            onView(withId(R.id.tvDownload)).check(matches(isDisplayed()))
            onView(withId(R.id.tvFilter)).check(matches(isDisplayed()))

            val t1 = DataFile.getPaymentHistoryTransactionData()
            val t2 = DataFile.getPaymentHistoryTransactionData()
            val list = TransactionList(mutableListOf(t1, t2), "2")
            val resp = AccountPaymentHistoryResponse(list, "200", "message")
            paymentHistoryLive.postValue(Resource.Success(resp))
            onView(withId(R.id.paymentRecycleView)).check(matches(isDisplayed()))
            assertEquals(
                requireActivity().findViewById<RecyclerView>(R.id.paymentRecycleView).adapter?.itemCount,
                2
            )
            onView(withId(R.id.paginationLayout)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun `test payment history screen visibility, for no payment history`() {
        every { viewModel.paymentHistoryLiveData } returns paymentHistoryLive
        launchFragmentInHiltContainer<AccountPaymentHistoryFragment> {
            onView(withId(R.id.yourPayment)).check(matches(isDisplayed()))
            onView(withId(R.id.tvDownload)).check(matches(isDisplayed()))
            onView(withId(R.id.tvFilter)).check(matches(isDisplayed()))

            val list = TransactionList(mutableListOf(), "2")
            val resp = AccountPaymentHistoryResponse(list, "200", "message")
            paymentHistoryLive.postValue(Resource.Success(resp))
            assertEquals(
                requireActivity().findViewById<RecyclerView>(R.id.paymentRecycleView).adapter?.itemCount,
                0
            )
            onView(withId(R.id.paginationLayout)).check(matches(not(isDisplayed())))
        }
    }

    @Test
    fun `test payment history screen visibility, for unknown error`() {
        every { viewModel.paymentHistoryLiveData } returns paymentHistoryLive
        paymentHistoryLive.postValue(Resource.DataError("unknown error"))

        launchFragmentInHiltContainer<AccountPaymentHistoryFragment> {
            shadowOf(getMainLooper()).idle()
            runTest {
                val dialogFragment =
                    requireActivity().supportFragmentManager.findFragmentByTag(ConstantsTest.ERROR_DIALOG) as ErrorDialog
                assert(dialogFragment.dialog?.isShowing == true)
            }
        }
    }

    @Test
    fun `test payment history screen, filter view visibility`() {
        every { viewModel.paymentHistoryLiveData } returns paymentHistoryLive
        every { viewModel.vehicleListVal } returns vehicleListLiveData
        launchFragmentInHiltContainer<AccountPaymentHistoryFragment> {
            onView(withId(R.id.yourPayment)).check(matches(isDisplayed()))
            onView(withId(R.id.tvDownload)).check(matches(isDisplayed()))
            onView(withId(R.id.tvFilter)).check(matches(isDisplayed()))

            val t1 = DataFile.getPaymentHistoryTransactionData()
            val t2 = DataFile.getPaymentHistoryTransactionData()
            val list = TransactionList(mutableListOf(t1, t2), "2")
            val resp = AccountPaymentHistoryResponse(list, "200", "message")
            paymentHistoryLive.postValue(Resource.Success(resp))
            onView(withId(R.id.paymentRecycleView)).check(matches(isDisplayed()))
            assertEquals(
                requireActivity().findViewById<RecyclerView>(R.id.paymentRecycleView).adapter?.itemCount,
                2
            )
            onView(withId(R.id.paginationLayout)).check(matches(isDisplayed()))
            onView(withId(R.id.tvFilter)).check(matches(isDisplayed()))
                .perform(click())

            val v1 = VehicleResponse(
                PlateInfoResponse(),
                PlateInfoResponse("1234"),
                VehicleInfoResponse(),
                false
            )
            val v2 = VehicleResponse(
                PlateInfoResponse(),
                PlateInfoResponse("ABCD"),
                VehicleInfoResponse(),
                false
            )
            val vehicleList = listOf(v1, v2)
            shadowOf(getMainLooper()).idle()
            vehicleListLiveData.postValue(Resource.Success(vehicleList))
        }
    }

}