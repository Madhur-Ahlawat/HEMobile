//package com.heandroid.ui.vehicle
//
//import android.content.Intent
//import android.os.Build
//import androidx.navigation.NavController
//import androidx.test.core.app.ActivityScenario
//import androidx.test.core.app.ApplicationProvider
//import androidx.test.espresso.Espresso
//import androidx.test.espresso.assertion.ViewAssertions
//import androidx.test.espresso.matcher.ViewMatchers
//import com.heandroid.R
//import com.heandroid.utils.common.ConstantsTest
//import dagger.hilt.android.testing.BindValue
//import dagger.hilt.android.testing.HiltAndroidRule
//import dagger.hilt.android.testing.HiltAndroidTest
//import io.mockk.mockk
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import org.junit.After
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.mockito.Mockito
//import org.robolectric.RobolectricTestRunner
//import org.robolectric.util.ReflectionHelpers
//
//@ExperimentalCoroutinesApi
//@HiltAndroidTest
//@RunWith(RobolectricTestRunner::class)
//class VehicleMgmtActivityTest {
//
//    @get:Rule(order = 0)
//    var hiltRule = HiltAndroidRule(this)
//
//    @BindValue
//    @JvmField
//    val viewModel = mockk<VehicleMgmtViewModel>(relaxed = true)
//
//
//    lateinit var activityScenario: ActivityScenario<VehicleMgmtActivity>
//
//    private val navController: NavController = Mockito.mock(NavController::class.java)
//
//    private val intent = Intent(ApplicationProvider.getApplicationContext(), VehicleMgmtActivity::class.java).apply {
//        putExtra(ConstantsTest.VEHICLE_SCREEN_KEY, ConstantsTest.VEHICLE_SCREEN_TYPE_LIST)
//    }
//
//    @Before
//    fun init() {
//        hiltRule.inject()
//    }
//
//    @After
//    fun tearDown() {
//        activityScenario.close()
//    }
//
//    @Test
//    fun `test vehicle activity visibility`() {
//        activityScenario = ActivityScenario.launch(intent)
//
//        activityScenario.onActivity {
//            ReflectionHelpers.setStaticField(Build.VERSION::class.java, "SDK_INT", 23)
//            Espresso.onView(ViewMatchers.withId(R.id.id_tool_bar_lyt))
//                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
//        }
//    }
//
//}