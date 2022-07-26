package com.heandroid.ui.account.profile.profileContent.nominatedContactUser

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
import com.heandroid.R
import com.heandroid.data.model.nominatedcontacts.NominatedContactRes
import com.heandroid.data.model.profile.ProfileDetailModel
import com.heandroid.ui.account.profile.ProfileViewModel
import com.heandroid.ui.loader.ErrorDialog
import com.heandroid.utils.common.Constants
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
class ViewNominatedContactUserProfileFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val viewModel = mockk<ProfileViewModel>(relaxed = true)

    private val profileLiveData = MutableLiveData<Resource<ProfileDetailModel?>?>()
    private val nominatedContactsLiveData = MutableLiveData<Resource<NominatedContactRes?>?>()
    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

    @Before
    fun init() {
        hiltRule.inject()
        every { viewModel.accountDetail } returns profileLiveData
        every { viewModel.getNominatedContactsApiVal } returns nominatedContactsLiveData
    }

    @Test
    fun `test nominated contact profile screen, navigate to next screen for success`() {
        launchFragmentInHiltContainer<ViewNominatedContactUserProfileFragment> {
            navController.setGraph(R.navigation.navigation_profile)
            navController.setCurrentDestination(R.id.viewNominatedUserAccountProfileFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.rlFirstName)).check(matches(isDisplayed()))
            onView(withId(R.id.rlLastName)).check(matches(isDisplayed()))
            onView(withId(R.id.rlEmailId)).check(matches(isDisplayed()))
            onView(withId(R.id.rlMobileNo)).check(matches(isDisplayed()))

            profileLiveData.postValue(
                Resource.Success(
                    ProfileDetailModel(
                        null, null, null,
                        null, null, "", ""
                    )
                )
            )
            nominatedContactsLiveData.postValue(
                Resource.Success(
                    NominatedContactRes(
                        null, null, null
                    )
                )
            )

            onView(withId(R.id.btnEditDetail)).check(matches(isDisplayed()))
                .perform(ViewActions.click())

            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.nominatedPersonalInfoFragment
            )
        }
    }

    @Test
    fun `test nominated contact profile screen, navigate to profile screen for success`() {
        launchFragmentInHiltContainer<ViewNominatedContactUserProfileFragment> {
            navController.setGraph(R.navigation.navigation_profile)
            navController.setCurrentDestination(R.id.viewNominatedUserAccountProfileFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.rlFirstName)).check(matches(isDisplayed()))
            onView(withId(R.id.rlLastName)).check(matches(isDisplayed()))
            onView(withId(R.id.rlEmailId)).check(matches(isDisplayed()))
            onView(withId(R.id.rlMobileNo)).check(matches(isDisplayed()))

            profileLiveData.postValue(
                Resource.Success(
                    ProfileDetailModel(
                        null, null, null,
                        null, null, "", ""
                    )
                )
            )
            nominatedContactsLiveData.postValue(
                Resource.Success(
                    NominatedContactRes(
                        null, null, null
                    )
                )
            )

            onView(withId(R.id.rlAccountHolder)).check(matches(isDisplayed()))
                .perform(ViewActions.click())

            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.viewPrimaryAccountUserProfileFragment
            )
        }
    }

    @Test
    fun `test nominated contact profile screen, unknown error for account api`() {
        launchFragmentInHiltContainer<ViewNominatedContactUserProfileFragment> {
            navController.setGraph(R.navigation.navigation_profile)
            navController.setCurrentDestination(R.id.viewNominatedUserAccountProfileFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.rlFirstName)).check(matches(isDisplayed()))
            onView(withId(R.id.rlLastName)).check(matches(isDisplayed()))
            onView(withId(R.id.rlEmailId)).check(matches(isDisplayed()))
            onView(withId(R.id.rlMobileNo)).check(matches(isDisplayed()))

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

    @Test
    fun `test nominated contact profile screen, unknown error for nominated profile api`() {
        launchFragmentInHiltContainer<ViewNominatedContactUserProfileFragment> {
            navController.setGraph(R.navigation.navigation_profile)
            navController.setCurrentDestination(R.id.viewNominatedUserAccountProfileFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.rlFirstName)).check(matches(isDisplayed()))
            onView(withId(R.id.rlLastName)).check(matches(isDisplayed()))
            onView(withId(R.id.rlEmailId)).check(matches(isDisplayed()))
            onView(withId(R.id.rlMobileNo)).check(matches(isDisplayed()))

            profileLiveData.postValue(
                Resource.Success(
                    ProfileDetailModel(
                        null, null, null,
                        null, null, "", ""
                    )
                )
            )
            nominatedContactsLiveData.postValue(
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