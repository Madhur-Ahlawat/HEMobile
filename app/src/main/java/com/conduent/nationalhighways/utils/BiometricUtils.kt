package com.conduent.nationalhighways.utils

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

object BiometricUtils {

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