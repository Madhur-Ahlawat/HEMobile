package com.conduent.nationalhighways.hiltTesting

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.conduent.nationalhighways.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.core.internal.deps.guava.base.Preconditions

@ExperimentalCoroutinesApi
inline fun <reified T : Fragment> launchFragmentInHiltContainer(
    fragmentArgs: Bundle? = null,
    themeResId: Int = R.style.FragmentScenarioEmptyFragmentActivityTheme,
    fragmentFactory: FragmentFactory? = null,
    crossinline action: T.() -> Unit = {}
) {
   val landingActivityIntent = Intent.makeMainActivity(ComponentName(
       ApplicationProvider.getApplicationContext(),
       HiltTestActivity::class.java
     )
   ).putExtra("androidx.fragment.app.testing.FragmentScenario.EmptyFragmentActivity.THEME_EXTRAS_BUNDLE_KEY",themeResId)


    ActivityScenario.launch<HiltTestActivity>(landingActivityIntent).onActivity{
        activity->
        fragmentFactory?.let {
            activity.supportFragmentManager.fragmentFactory = it
          }
         val fragment = activity.supportFragmentManager.fragmentFactory.instantiate(
             Preconditions.checkNotNull(T::class.java.classLoader) as ClassLoader,
             T::class.java.name
         )
          fragment.arguments = fragmentArgs
        activity.supportFragmentManager.beginTransaction()
            .add(android.R.id.content , fragment, "")
            .commitNow()
        (fragment as T).action()
        }
    }