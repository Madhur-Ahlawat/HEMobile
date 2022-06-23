//package com.heandroid.ui.base
//
//import android.content.pm.ActivityInfo
//import android.os.Bundle
//import android.text.SpannableString
//import android.text.style.ClickableSpan
//import android.view.View
//import android.widget.TextView
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.FragmentFactory
//import androidx.fragment.app.testing.FragmentScenario
//import androidx.navigation.Navigation
//import androidx.navigation.testing.TestNavHostController
//import androidx.recyclerview.widget.RecyclerView
//import androidx.test.core.app.ApplicationProvider
//import androidx.test.espresso.NoMatchingViewException
//import androidx.test.espresso.Root
//import androidx.test.espresso.UiController
//import androidx.test.espresso.ViewAction
//import androidx.test.espresso.action.ViewActions
//import androidx.test.espresso.matcher.BoundedMatcher
//import androidx.test.espresso.matcher.RootMatchers
//import androidx.test.espresso.matcher.ViewMatchers
//import androidx.test.platform.app.InstrumentationRegistry
//import com.heandroid.R
//import org.hamcrest.CoreMatchers
//import org.hamcrest.Description
//import org.hamcrest.Matcher
//import org.hamcrest.Matchers
//import kotlin.reflect.KClass
//
//
//abstract class BaseFragmentTest<TFragment : Fragment> {
//
//
//    protected abstract val fragmentClass: KClass<TFragment>
//    protected lateinit var fragment: TFragment
//    protected var fragmentArgs: Bundle? = Bundle()
//
//
//    protected abstract fun onFragmentInstantiated(fragment: TFragment)
//
//     private var mockNavController = TestNavHostController(ApplicationProvider.getApplicationContext())
//
//    protected open fun launch(onInstantiated: (TFragment) -> Unit = {})
//            : FragmentScenario<TFragment> {
//        return FragmentScenario.launchInContainer(
//            fragmentClass.java,
//            fragmentArgs,
//            R.style.Theme_MaterialComponents,
//            object : FragmentFactory() {
//                @Suppress("UNCHECKED_CAST")
//                override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
//                    val fragment = super.instantiate(classLoader, className) as TFragment
//                    this@BaseFragmentTest.fragment = fragment
//                    this@BaseFragmentTest.fragmentArgs = fragment.arguments
//                    onFragmentInstantiated(fragment)
//                    onInstantiated(fragment)
//                    return fragment
//                }
//            }
//        ).onFragment { fragment ->
//            Navigation.setViewNavController(fragment.requireView(), mockNavController)
//            fragment.activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//
//        }.apply {
//            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
//        }
//    }
//
//    protected fun forceClick(): ViewAction {
//        return object : ViewAction {
//            override fun getConstraints(): Matcher<View> {
//                return CoreMatchers.anyOf(
//                    ViewMatchers.isDisplayed(),
//                    ViewMatchers.isClickable(),
//                    ViewMatchers.isEnabled()
//                )
//            }
//
//            override fun getDescription(): String {
//                return "force click"
//            }
//
//            override fun perform(uiController: UiController, view: View) {
//                view.performClick()
//                uiController.loopMainThreadUntilIdle()
//            }
//        }
//    }
//    protected fun clickClickableSpan(textToClick: CharSequence): ViewAction {
//        return object : ViewAction {
//
//            override fun getConstraints(): Matcher<View> {
//                return Matchers.instanceOf(TextView::class.java)
//            }
//
//            override fun getDescription(): String {
//                return "clicking on a ClickableSpan"
//            }
//
//            override fun perform(uiController: UiController, view: View) {
//                val textView = view as TextView
//                val spannableString = textView.text as SpannableString
//
//                if (spannableString.isEmpty()) {
//                    throw NoMatchingViewException.Builder()
//                        .includeViewHierarchy(true)
//                        .withRootView(textView)
//                        .build()
//                }
//                val spans =
//                    spannableString.getSpans(0, spannableString.length, ClickableSpan::class.java)
//                if (spans.isNotEmpty()) {
//                    var spanCandidate: ClickableSpan
//                    for (span: ClickableSpan in spans) {
//                        spanCandidate = span
//                        val start = spannableString.getSpanStart(spanCandidate)
//                        val end = spannableString.getSpanEnd(spanCandidate)
//                        val sequence = spannableString.subSequence(start, end)
//                        if (textToClick.toString() == (sequence.toString())) {
//                            span.onClick(textView)
//                            return
//                        }
//                    }
//                }
//
//                throw NoMatchingViewException.Builder()
//                    .includeViewHierarchy(true)
//                    .withRootView(textView)
//                    .build()
//
//            }
//        }
//    }
//
//    protected fun withCustomConstraints(action: ViewAction, constraints: Matcher<View>): ViewAction {
//        return object : ViewAction {
//            override fun getConstraints(): Matcher<View> {
//                return constraints
//            }
//
//            override fun getDescription(): String {
//                return action.description
//            }
//
//            override fun perform(uiController: UiController, view: View) {
//                action.perform(uiController, view)
//            }
//        }
//    }
//
//    protected fun isPopupWindow(): Matcher<Root?>? {
//        return RootMatchers.isPlatformPopup()
//    }
//
//    protected fun clickOnViewChild(viewId: Int) = object : ViewAction {
//        override fun getConstraints(): Matcher<View> {
//            return CoreMatchers.allOf(ViewMatchers.isClickable(), ViewMatchers.isDisplayed())
//        }
//
//        override fun getDescription() = "Click on a child view with specified id."
//        override fun perform(uiController: UiController, view: View) =
//            ViewActions.click().perform(uiController, view.findViewById(viewId))
//    }
//    protected fun waitFor(millis: Long): ViewAction {
//        return object : ViewAction {
//            override fun getConstraints(): Matcher<View> {
//                return ViewMatchers.isRoot()
//            }
//
//            override fun getDescription(): String {
//                return "Wait for $millis milliseconds."
//            }
//
//            override fun perform(uiController: UiController, view: View?) {
//                uiController.loopMainThreadForAtLeast(millis)
//            }
//        }
//    }
//    protected fun atPosition(position: Int, itemMatcher: Matcher<View?>): Matcher<View?> {
//        return object : BoundedMatcher<View?, RecyclerView>(RecyclerView::class.java) {
//            override fun describeTo(description: Description) {
//                description.appendText("has item at position $position: ")
//                itemMatcher.describeTo(description)
//            }
//
//            override fun matchesSafely(view: RecyclerView): Boolean {
//                val viewHolder = view.findViewHolderForAdapterPosition(position)
//                    ?:
//                    return false
//                return itemMatcher.matches(viewHolder.itemView)
//            }
//        }
//    }
//}
