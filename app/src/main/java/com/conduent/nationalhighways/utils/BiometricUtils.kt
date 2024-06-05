package com.conduent.nationalhighways.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

object BiometricUtils {

    fun checkBioMetricAvailability(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                Log.d("MY_APP_TAG", "App can authenticate using biometrics.")
                return true
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Toast.makeText(
                    context,
                    "No biometric features available on this device.", Toast.LENGTH_SHORT
                ).show()

                Log.e("MY_APP_TAG", "No biometric features available on this device.")
                return false
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Toast.makeText(
                    context,
                    "Biometric features are currently unavailable.", Toast.LENGTH_SHORT
                ).show()
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.")
                return false
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Toast.makeText(
                    context,
                    "Please add biometrics", Toast.LENGTH_SHORT
                ).show()
                return false

                // Prompts the user to create credentials that your app accepts.
//                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
//                    putExtra(
//                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
//                        BIOMETRIC_STRONG or
//                                BiometricManager.Authenticators.DEVICE_CREDENTIAL
//                    )
//                }
//                startActivityForResult(enrollIntent, 100)
            }
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                Toast.makeText(
                    context,
                    "update biometric", Toast.LENGTH_SHORT
                ).show()
                return false
            }
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                Toast.makeText(
                    context,
                    "Unsupported biometric", Toast.LENGTH_SHORT
                ).show()
                return false
            }
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                Toast.makeText(
                    context,
                    "un known biometric", Toast.LENGTH_SHORT
                ).show()
                return false
            }
        }
        return false
    }

    fun showBiometricForStrongAuthWithCrypto(
        context: Fragment,
        biometricTitle: String,
        biometricSubtitle: String,
        options: String,
        cryptoObject: BiometricPrompt.CryptoObject?,
        callback: BiometricPrompt.AuthenticationCallback
    ): BiometricPrompt {
        val biometricPromptInfo = PromptInfo.Builder().apply {
            setTitle(biometricTitle)
            setSubtitle(biometricSubtitle)
            setConfirmationRequired(true)
            setAllowedAuthenticators(BIOMETRIC_STRONG)
            setNegativeButtonText(options)
        }.build()
        val prompt = BiometricPrompt(
            context, ContextCompat.getMainExecutor(context.requireContext()), callback
        )
        cryptoObject?.let {
            prompt.authenticate(biometricPromptInfo, it)
        } ?: run {
            prompt.authenticate(biometricPromptInfo)
        }
        return prompt
    }

    fun showBiometricForStrongAuthWithOptions(
        context: Fragment,
        biometricTitle: String,
        biometricSubtitle: String,
        biometricDes: String,
        options: String,
        callback: BiometricPrompt.AuthenticationCallback
    ): BiometricPrompt {
        val biometricPrompt = BiometricPrompt(
            context, ContextCompat.getMainExecutor(context.requireContext()), callback
        )
        val biometricPromptInfo = PromptInfo.Builder()
            .setTitle(biometricTitle)
            .setSubtitle(biometricSubtitle)
            .setDescription(biometricDes)
            .setAllowedAuthenticators(BIOMETRIC_STRONG)
            .setNegativeButtonText(options)
            .build()
        biometricPrompt.authenticate(biometricPromptInfo)
        return biometricPrompt
    }

    fun biometricPromptForAllAuth(
        biometricTitle: String,
        biometricSubtitle: String,
    ): PromptInfo {
        return PromptInfo.Builder().apply {
            setTitle(biometricTitle)
            setSubtitle(biometricSubtitle)
            setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_WEAK
                        or BiometricManager.Authenticators.DEVICE_CREDENTIAL
                        or BIOMETRIC_STRONG
            )
        }.build()
    }

    fun biometricPromptForStrongAuth(
        biometricTitle: String,
        biometricSubtitle: String, negative: String
    ): PromptInfo {
        return PromptInfo.Builder().apply {
            setAllowedAuthenticators(BIOMETRIC_STRONG)
            setNegativeButtonText(negative)
            setTitle(biometricTitle)
            setSubtitle(biometricSubtitle)
        }.build()
    }

    fun showBiometricForAllAuthWithOptions(
        context: Fragment,
        biometricTitle: String,
        biometricSubtitle: String,
        callback: BiometricPrompt.AuthenticationCallback
    ): BiometricPrompt {
        val biometricPromptInfo = PromptInfo.Builder().apply {
            setTitle(biometricTitle)
            setSubtitle(biometricSubtitle)
            setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_WEAK or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
                        or BIOMETRIC_STRONG
            )
        }.build()
        val prompt = BiometricPrompt(
            context,
            ContextCompat.getMainExecutor(context.requireContext()),
            callback
        )
        prompt.authenticate(biometricPromptInfo)
        return prompt
    }

}