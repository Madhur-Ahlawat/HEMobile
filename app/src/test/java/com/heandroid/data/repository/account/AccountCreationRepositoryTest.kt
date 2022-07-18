package com.heandroid.data.repository.account

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.heandroid.data.model.address.DataAddress
import com.heandroid.data.remote.ApiService
import com.heandroid.utils.data.DataFile
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Response

@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
@ExperimentalCoroutinesApi
class AccountCreationRepositoryTest {

    private var accountCreationRepository: AccountCreationRepository? = null

    @Mock
    private lateinit var apiService: ApiService

    val response: Response<List<DataAddress?>?>? =
        Response.success(listOf(DataFile.getDataAddress()))

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
        hiltRule.inject()
        accountCreationRepository = AccountCreationRepository(apiService)
    }

    @Test
    fun `test address list for postal code for null`() {
        runTest {
            Mockito.`when`(
                apiService.getAddressListBasedOnPostalCode("", "")
            ).thenReturn(null)
            accountCreationRepository?.let {
                assertEquals(
                    it.getAddressListForPostalCode(""), null
                )
            }
        }
    }

}