package com.conduent.nationalhighways.ui.account.profile.profileContent.businessAccount

import android.os.Looper.getMainLooper
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.profile.ProfileDetailModel
import com.conduent.nationalhighways.ui.account.profile.ProfileViewModel
import com.conduent.nationalhighways.ui.loader.ErrorDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
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
class ViewBusinessAccountProfileFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val viewModel = mockk<ProfileViewModel>(relaxed = true)

    private val profileLiveData = MutableLiveData<Resource<ProfileDetailModel?>?>()
    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

    @Before
    fun init() {
        hiltRule.inject()
        every { viewModel.accountDetail } returns profileLiveData
    }

    @Test
    fun `test business account profile screen, navigate to next screen`() {
        launchFragmentInHiltContainer<ViewBusinessAccountProfileFragment> {
            navController.setGraph(R.navigation.navigation_profile)
            navController.setCurrentDestination(R.id.viewBusinessAccountProfileFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.rlCompanyName)).check(matches(isDisplayed()))
            onView(withId(R.id.rlCompanyRegNo)).check(matches(isDisplayed()))
            onView(withId(R.id.rlFirstName)).check(matches(isDisplayed()))
            onView(withId(R.id.rlModelNo)).check(matches(isDisplayed()))
            onView(withId(R.id.rlAddress)).check(matches(isDisplayed()))

            profileLiveData.postValue(
                Resource.Success(
                    ProfileDetailModel(
                        null, null, null,
                        null, null, "", ""
                    )
                )
            )

            onView(withId(R.id.btnEditDetail)).check(matches(isDisplayed()))
                .perform(ViewActions.click())

            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.personalInfoFragment
            )
        }
    }

    @Test
    fun `test business account profile screen, error api call`() {
        launchFragmentInHiltContainer<ViewBusinessAccountProfileFragment> {
            navController.setGraph(R.navigation.navigation_profile)
            navController.setCurrentDestination(R.id.viewBusinessAccountProfileFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.rlCompanyName)).check(matches(isDisplayed()))
            onView(withId(R.id.rlCompanyRegNo)).check(matches(isDisplayed()))
            onView(withId(R.id.rlFirstName)).check(matches(isDisplayed()))
            onView(withId(R.id.rlModelNo)).check(matches(isDisplayed()))
            onView(withId(R.id.rlAddress)).check(matches(isDisplayed()))

            profileLiveData.postValue(
                Resource.DataError(
                    "unknown error"
                )
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