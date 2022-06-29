package com.heandroid.ui.account.creation.step3

import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.LargeTest
import com.heandroid.R
import com.heandroid.ui.loader.ErrorDialog
import com.heandroid.utils.BaseActions
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.ConstantsTest
import com.heandroid.utils.data.DataFile
import com.heandroid.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
@LargeTest
class CreateAccoutPasswordFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

    private lateinit var bundle: Bundle

    @Before
    fun init() {
        hiltRule.inject()
        bundle = Bundle().apply {
            putParcelable(
                ConstantsTest.CREATE_ACCOUNT_DATA,
                DataFile.getCreateAccountRequestModel()
            )
        }
    }

    @Test
    fun `test create account password screen visibility`() {
        launchFragmentInHiltContainer<CreateAccoutPasswordFragment>(bundle) {
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.appCompatCheckedTextView2)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPersonaleInfo)).check(matches(isDisplayed()))
            onView(withId(R.id.tiePassword)).check(matches(isDisplayed()))
            onView(withId(R.id.tieConfirmPassword)).check(matches(isDisplayed()))
            onView(withId(R.id.btnAction)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun `test create account password screen, incorrect password`() {
        launchFragmentInHiltContainer<CreateAccoutPasswordFragment>(bundle) {
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            runTest {
                onView(withId(R.id.tiePassword)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("password"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tieConfirmPassword)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("password"))
                Espresso.closeSoftKeyboard()
                delay(500)
            }
            onView(withId(R.id.btnAction)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
            runTest {
                val dialogFragment =
                    requireActivity().supportFragmentManager.findFragmentByTag("") as ErrorDialog
                assert(dialogFragment.dialog?.isShowing == true)
            }
        }
    }

    @Test
    fun `test create account password screen, navigate to next screen`() {
        launchFragmentInHiltContainer<CreateAccoutPasswordFragment>(bundle) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.createAccoutPasswordFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            runTest {
                onView(withId(R.id.tiePassword)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("Password@123"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tieConfirmPassword)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("Password@123"))
                Espresso.closeSoftKeyboard()
                delay(500)
            }
            onView(withId(R.id.btnAction)).check(matches(isDisplayed()))
                .perform(ViewActions.click())

            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.createAccoutPinFragment
            )
            val bun = Bundle().apply {
                putParcelable(
                    ConstantsTest.CREATE_ACCOUNT_DATA,
                    DataFile.getCreateAccountRequestModel().apply {
                        password = "Password@123"
                    }
                )
            }
            val currentDestinationArgs = navController.backStack.last().arguments
            Assert.assertTrue(BaseActions.equalBundles(currentDestinationArgs, bun))
        }
    }

    @Test
    fun `test create account password screen, navigate to next screen, edit account type`() {
        bundle.apply {
            putInt(
                Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE,
                Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE_KEY
            )
        }
        launchFragmentInHiltContainer<CreateAccoutPasswordFragment>(bundle) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.createAccoutPasswordFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            runTest {
                onView(withId(R.id.tiePassword)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("Password@123"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tieConfirmPassword)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("Password@123"))
                Espresso.closeSoftKeyboard()
                delay(500)
            }
            onView(withId(R.id.btnAction)).check(matches(isDisplayed()))
                .perform(ViewActions.click())

            Assert.assertEquals(
                navController.currentDestination?.id,
                R.id.createAccoutPinFragment
            )
            val bun = Bundle().apply {
                putParcelable(
                    ConstantsTest.CREATE_ACCOUNT_DATA,
                    DataFile.getCreateAccountRequestModel().apply {
                        password = "Password@123"
                    }
                )
                putInt(
                    Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE,
                    Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE_KEY
                )
            }
            val currentDestinationArgs = navController.backStack.last().arguments
            Assert.assertTrue(BaseActions.equalBundles(currentDestinationArgs, bun))
        }
    }


}