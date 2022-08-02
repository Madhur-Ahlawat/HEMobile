package com.heandroid.ui.checkpaidcrossings

import android.os.Looper.getMainLooper
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.heandroid.R
import com.heandroid.data.model.checkpaidcrossings.UsedTollTransactionResponse
import com.heandroid.ui.loader.ErrorDialog
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.Resource
import com.heandroid.utils.data.DataFile
import com.heandroid.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
class UsedChargesFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val viewModel = mockk<CheckPaidCrossingViewModel>(relaxed = true)

    private val usedTollTranLiveData =
        MutableLiveData<Resource<List<UsedTollTransactionResponse?>?>?>()

    @Before
    fun init() {
        hiltRule.inject()
        every { viewModel.usedTollTransactions } returns usedTollTranLiveData
    }

    @Test
    fun `test used charges screen for success api call`() {
        launchFragmentInHiltContainer<UsedChargesFragment> {
            onView(withId(R.id.tv_title)).check(matches(isDisplayed()))
            val tran1 = DataFile.getUsedTollTransaction("112233")
            val tran2 = DataFile.getUsedTollTransaction("3344455")
            val list = listOf(tran1, tran2)
            shadowOf(getMainLooper()).idle()
            usedTollTranLiveData.postValue(
                Resource.Success(list)
            )
            shadowOf(getMainLooper()).idle()
            onView(withId(R.id.rvHistory)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun `test used charges screen for error api call`() {
        launchFragmentInHiltContainer<UsedChargesFragment> {
            onView(withId(R.id.tv_title)).check(matches(isDisplayed()))
            shadowOf(getMainLooper()).idle()
            usedTollTranLiveData.postValue(
                Resource.DataError("unknown error")
            )
            shadowOf(getMainLooper()).idle()
            runTest {
                val dialogFragment =
                    requireActivity().supportFragmentManager.findFragmentByTag(Constants.ERROR_DIALOG) as ErrorDialog
                assert(dialogFragment.dialog?.isShowing == true)
            }
        }
    }

}