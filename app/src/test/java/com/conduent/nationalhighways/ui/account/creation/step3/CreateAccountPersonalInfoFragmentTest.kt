package com.conduent.nationalhighways.ui.account.creation.step3

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
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.utils.BaseActions
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ConstantsTest
import com.conduent.nationalhighways.utils.data.DataFile
import com.conduent.nationalhighways.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.assertEquals
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
class CreateAccountPersonalInfoFragmentTest {

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
                DataFile.getCreateAccountRequestModel().apply {
                    accountType = Constants.PERSONAL_ACCOUNT
                    planType = Constants.PAYG
                }
            )
        }
    }

    @Test
    fun `test create account personal info screen visibility for personal account for pay-g`() {
        launchFragmentInHiltContainer<CreateAccountPersonalInfoFragment>(bundle) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.personalDetailsEntryFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.appCompatCheckedTextView2)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPersonaleInfo)).check(matches(isDisplayed()))
            onView(withId(R.id.personalAccountParent)).check(matches(isDisplayed()))
            runTest {
                onView(withId(R.id.tieFullName)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("First name"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tieLastName)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("Last name"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.btnAction)).check(matches(isDisplayed()))
                    .perform(BaseActions.forceClick())
            }
            val bun = Bundle().apply {
                putParcelable(
                    ConstantsTest.CREATE_ACCOUNT_DATA,
                    DataFile.getCreateAccountRequestModel().apply {
                        accountType = Constants.PERSONAL_ACCOUNT
                        planType = Constants.PAYG
                        firstName = "First name"
                        lastName = "Last name"
                        enable = true
                    }
                )
            }

            assertEquals(
                navController.currentDestination?.id,
                R.id.postcodeFragment
            )
            val currentDestinationArgs = navController.backStack.last().arguments
            Assert.assertTrue(BaseActions.equalBundles(currentDestinationArgs, bun))
        }
    }

    @Test
    fun `test create account personal info screen visibility for personal account for pre pay`() {
        val bund = Bundle().apply {
            putParcelable(
                ConstantsTest.CREATE_ACCOUNT_DATA,
                DataFile.getCreateAccountRequestModel().apply {
                    accountType = Constants.PERSONAL_ACCOUNT
                    planType = null
                }
            )
        }
        launchFragmentInHiltContainer<CreateAccountPersonalInfoFragment>(bund) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.personalDetailsEntryFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.appCompatCheckedTextView2)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPersonaleInfo)).check(matches(isDisplayed()))
            onView(withId(R.id.personalAccountParent)).check(matches(isDisplayed()))
            runTest {
                onView(withId(R.id.tieFullName)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("First name"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tieLastName)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("Last name"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tieMobileNo)).check(matches(isDisplayed()))
                    .perform(BaseActions.forceTypeText("1234567890"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.btnAction)).check(matches(isDisplayed()))
                    .perform(BaseActions.forceClick())
            }
            val bun = Bundle().apply {
                putParcelable(
                    ConstantsTest.CREATE_ACCOUNT_DATA,
                    DataFile.getCreateAccountRequestModel().apply {
                        accountType = Constants.PERSONAL_ACCOUNT
                        planType = null
                        firstName = "First name"
                        lastName = "Last name"
                        cellPhone = "1234567890"
                        enable = true
                    }
                )
            }

            assertEquals(
                navController.currentDestination?.id,
                R.id.postcodeFragment
            )
            val currentDestinationArgs = navController.backStack.last().arguments
            Assert.assertTrue(BaseActions.equalBundles(currentDestinationArgs, bun))
        }
    }

    //@Test
    fun `test create account personal info screen visibility for business account`() {
        val bund = Bundle().apply {
            putParcelable(
                ConstantsTest.CREATE_ACCOUNT_DATA,
                DataFile.getCreateAccountRequestModel().apply {
                    accountType = Constants.BUSINESS_ACCOUNT
                }
            )
        }
        launchFragmentInHiltContainer<CreateAccountPersonalInfoFragment>(bund) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.personalDetailsEntryFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.appCompatCheckedTextView2)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPersonaleInfo)).check(matches(isDisplayed()))
            runTest {
                onView(withId(R.id.companyName)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("company name"))
                Espresso.closeSoftKeyboard()
                delay(500)
//                onView(withId(R.id.companyRegNumber)).check(matches(isDisplayed()))
//                    .perform(ViewActions.clearText(), ViewActions.typeText("First name"))
//                Espresso.closeSoftKeyboard()
//                delay(500)

                onView(withId(R.id.tieFullName)).perform(BaseActions.betterScrollTo())
                    .perform(BaseActions.forceTypeText("First name"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tieLastName)).perform(BaseActions.betterScrollTo())
                    .perform(BaseActions.forceTypeText("Last name"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.businessMobNo)).perform(BaseActions.betterScrollTo())
                    .perform(BaseActions.forceTypeText("1234567890"))
                Espresso.closeSoftKeyboard()
                delay(500)

                onView(withId(R.id.btnAction)).check(matches(isDisplayed()))
                    .perform(BaseActions.forceClick())
            }
            val bun = Bundle().apply {
                putParcelable(
                    ConstantsTest.CREATE_ACCOUNT_DATA,
                    DataFile.getCreateAccountRequestModel().apply {
                        accountType = Constants.BUSINESS_ACCOUNT
                        companyName = "company name"
                        firstName = "First name"
                        lastName = "Last name"
                        cellPhone = "1234567890"
                        fein = ""
                        enable = true
                    }
                )
            }

            assertEquals(
                navController.currentDestination?.id,
                R.id.postcodeFragment
            )
            val currentDestinationArgs = navController.backStack.last().arguments
            Assert.assertTrue(BaseActions.equalBundles(currentDestinationArgs, bun))
        }
    }

    @Test
    fun `test create account personal info screen visibility for personal account for pay-g, edit account type`() {
        bundle.apply {
            putInt(
                Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE,
                Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE_KEY
            )
        }
        launchFragmentInHiltContainer<CreateAccountPersonalInfoFragment>(bundle) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.personalDetailsEntryFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.appCompatCheckedTextView2)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPersonaleInfo)).check(matches(isDisplayed()))
            onView(withId(R.id.personalAccountParent)).check(matches(isDisplayed()))
            runTest {
                onView(withId(R.id.tieFullName)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("First name"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tieLastName)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("Last name"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.btnAction)).check(matches(isDisplayed()))
                    .perform(BaseActions.forceClick())
            }
            val bun = Bundle().apply {
                putParcelable(
                    ConstantsTest.CREATE_ACCOUNT_DATA,
                    DataFile.getCreateAccountRequestModel().apply {
                        accountType = Constants.PERSONAL_ACCOUNT
                        planType = Constants.PAYG
                        firstName = "First name"
                        lastName = "Last name"
                        enable = true
                    }
                )
                putInt(
                    Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE,
                    Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE_KEY
                )
            }

            assertEquals(
                navController.currentDestination?.id,
                R.id.postcodeFragment
            )
            val currentDestinationArgs = navController.backStack.last().arguments
            Assert.assertTrue(BaseActions.equalBundles(currentDestinationArgs, bun))
        }
    }

    @Test
    fun `test create account personal info screen visibility for personal account for pre pay, edit account type`() {
        val bund = Bundle().apply {
            putParcelable(
                ConstantsTest.CREATE_ACCOUNT_DATA,
                DataFile.getCreateAccountRequestModel().apply {
                    accountType = Constants.PERSONAL_ACCOUNT
                    planType = null
                }
            )
            putInt(
                Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE,
                Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE_KEY
            )
        }
        launchFragmentInHiltContainer<CreateAccountPersonalInfoFragment>(bund) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.personalDetailsEntryFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.appCompatCheckedTextView2)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPersonaleInfo)).check(matches(isDisplayed()))
            onView(withId(R.id.personalAccountParent)).check(matches(isDisplayed()))
            runTest {
                onView(withId(R.id.tieFullName)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("First name"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tieLastName)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("Last name"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tieMobileNo)).check(matches(isDisplayed()))
                    .perform(BaseActions.forceTypeText("1234567890"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.btnAction)).check(matches(isDisplayed()))
                    .perform(BaseActions.forceClick())
            }
            val bun = Bundle().apply {
                putParcelable(
                    ConstantsTest.CREATE_ACCOUNT_DATA,
                    DataFile.getCreateAccountRequestModel().apply {
                        accountType = Constants.PERSONAL_ACCOUNT
                        planType = null
                        firstName = "First name"
                        lastName = "Last name"
                        cellPhone = "1234567890"
                        enable = true
                    }
                )
                putInt(
                    Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE,
                    Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE_KEY
                )
            }

            assertEquals(
                navController.currentDestination?.id,
                R.id.postcodeFragment
            )
            val currentDestinationArgs = navController.backStack.last().arguments
            Assert.assertTrue(BaseActions.equalBundles(currentDestinationArgs, bun))
        }
    }

    //@Test
    fun `test create account personal info screen visibility for business account, edit account type`() {
        val bund = Bundle().apply {
            putParcelable(
                ConstantsTest.CREATE_ACCOUNT_DATA,
                DataFile.getCreateAccountRequestModel().apply {
                    accountType = Constants.BUSINESS_ACCOUNT
                }
            )
            putInt(
                Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE,
                Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE_KEY
            )
        }
        launchFragmentInHiltContainer<CreateAccountPersonalInfoFragment>(bund) {
            navController.setGraph(R.navigation.nav_graph_account_creation)
            navController.setCurrentDestination(R.id.personalDetailsEntryFragment)
            Navigation.setViewNavController(requireView(), navController)
            onView(withId(R.id.tvLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.tvStep)).check(matches(isDisplayed()))
            onView(withId(R.id.appCompatCheckedTextView2)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPersonaleInfo)).check(matches(isDisplayed()))
            runTest {
                onView(withId(R.id.companyName)).check(matches(isDisplayed()))
                    .perform(ViewActions.clearText(), ViewActions.typeText("company name"))
                Espresso.closeSoftKeyboard()
                delay(500)
//                onView(withId(R.id.companyRegNumber)).check(matches(isDisplayed()))
//                    .perform(ViewActions.clearText(), ViewActions.typeText("First name"))
//                Espresso.closeSoftKeyboard()
//                delay(500)
                onView(withId(R.id.tieFullName)).perform(ViewActions.scrollTo())
                    .perform(ViewActions.clearText(), ViewActions.typeText("First name"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.tieLastName)).perform(ViewActions.scrollTo())
                    .perform(ViewActions.clearText(), ViewActions.typeText("Last name"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.businessMobNo)).perform(ViewActions.scrollTo())
                    .perform(BaseActions.forceTypeText("1234567890"))
                Espresso.closeSoftKeyboard()
                delay(500)
                onView(withId(R.id.btnAction)).check(matches(isDisplayed()))
                    .perform(BaseActions.forceClick())
            }
            val bun = Bundle().apply {
                putParcelable(
                    ConstantsTest.CREATE_ACCOUNT_DATA,
                    DataFile.getCreateAccountRequestModel().apply {
                        accountType = Constants.BUSINESS_ACCOUNT
                        companyName = "company name"
                        firstName = "First name"
                        lastName = "Last name"
                        cellPhone = "1234567890"
                        fein = ""
                        enable = true
                    }
                )
                putInt(
                    Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE,
                    Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE_KEY
                )
            }

            assertEquals(
                navController.currentDestination?.id,
                R.id.postcodeFragment
            )
            val currentDestinationArgs = navController.backStack.last().arguments
            Assert.assertTrue(BaseActions.equalBundles(currentDestinationArgs, bun))
        }
    }


}