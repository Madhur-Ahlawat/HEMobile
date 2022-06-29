package com.heandroid.ui.landing

import android.os.Looper.getMainLooper
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import com.heandroid.R
import com.heandroid.data.model.webstatus.WebSiteStatus
import com.heandroid.ui.loader.ErrorDialog
import com.heandroid.ui.websiteservice.WebSiteServiceViewModel
import com.heandroid.utils.common.Resource
import com.heandroid.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@ExperimentalCoroutinesApi
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
@MediumTest
class StartNowFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val viewModel = mockk<WebSiteServiceViewModel>(relaxed = true)

    private val webService = MutableLiveData<Resource<WebSiteStatus?>>()

    private lateinit var navController: NavController

    @Before
    fun init() {
        hiltRule.inject()
        navController = Mockito.mock(NavController::class.java)
    }

    @Test
    fun `test start now screen visibility for no maintenance`() {
        every { viewModel.webServiceLiveData } returns webService
        webService.postValue(Resource.Success(WebSiteStatus("NOT LIVE", null, "", "", "", "")))
        launchFragmentInHiltContainer<StartNowFragment> {
            onView(withId(R.id.pay_dart_txt)).check(matches(isDisplayed()))
            onView(withId(R.id.id_title_lyt)).check(matches(isDisplayed()))
            onView(withId(R.id.rl_about_service)).check(matches(isDisplayed()))
            onView(withId(R.id.rl_crossing_service_update)).check(matches(isDisplayed()))
            onView(withId(R.id.rl_contact_dart_charge)).check(matches(isDisplayed()))
            onView(withId(R.id.rl_understand_charges_fines_lyt)).check(matches(isDisplayed()))
            onView(withId(R.id.rl_dart_charge_guidance_and_documents)).check(matches(isDisplayed()))
            onView(withId(R.id.btn_start_now)).check(matches(isDisplayed()))
            onView(withId(R.id.maintainance_lyt)).check(matches(not(isDisplayed())))
        }
    }

    @Test
    fun `test start now screen visibility for maintenance`() {
        every { viewModel.webServiceLiveData } returns webService

        launchFragmentInHiltContainer<StartNowFragment> {
            webService.postValue(
                Resource.Success(
                    WebSiteStatus(
                        "LIVE",
                        "This is title for maintenance", "This is description for maintenance",
                        "", "", ""
                    )
                )
            )
            onView(withId(R.id.pay_dart_txt)).check(matches(isDisplayed()))
            onView(withId(R.id.id_title_lyt)).check(matches(isDisplayed()))
            onView(withId(R.id.rl_about_service)).check(matches(isDisplayed()))
            onView(withId(R.id.rl_crossing_service_update)).check(matches(isDisplayed()))
            onView(withId(R.id.rl_contact_dart_charge)).check(matches(isDisplayed()))
            onView(withId(R.id.rl_understand_charges_fines_lyt)).check(matches(isDisplayed()))
            onView(withId(R.id.rl_dart_charge_guidance_and_documents)).check(matches(isDisplayed()))
            onView(withId(R.id.btn_start_now)).check(matches(isDisplayed()))
            onView(withId(R.id.maintainance_lyt)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun `test start now screen visibility for unknown error`() {
        every { viewModel.webServiceLiveData } returns webService
        webService.postValue(
            Resource.DataError(
                "unknown error"
            )
        )
        launchFragmentInHiltContainer<StartNowFragment> {
            shadowOf(getMainLooper()).idle()
            runTest {
                val dialogFragment =
                    requireActivity().supportFragmentManager.findFragmentByTag("") as ErrorDialog
                assert(dialogFragment.dialog?.isShowing == true)
            }
        }
    }

}