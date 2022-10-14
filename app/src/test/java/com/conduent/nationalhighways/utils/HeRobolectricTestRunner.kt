package com.conduent.nationalhighways.utils

import org.junit.runners.model.FrameworkMethod
import org.robolectric.RobolectricTestRunner
import org.robolectric.internal.bytecode.InstrumentationConfiguration
import org.robolectric.util.inject.Injector

class HeRobolectricTestRunner : RobolectricTestRunner {

    constructor(testClass: Class<*>) : super(testClass)

    constructor(testClass: Class<*>, injector: Injector) : super(testClass, injector)

    override fun createClassLoaderConfig(method: FrameworkMethod?): InstrumentationConfiguration {
        val parentClassLoaderConfig = super.createClassLoaderConfig(method)
        val builder = InstrumentationConfiguration.Builder(parentClassLoaderConfig)
        builder.doNotInstrumentPackage("androidx.fragment")
        builder.doNotInstrumentPackage("androidx.datastore")
        return builder.build()
    }
}