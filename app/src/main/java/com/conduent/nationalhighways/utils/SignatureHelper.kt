package com.conduent.nationalhighways.utils

import android.content.Context
import android.util.Base64
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.net.URLEncoder
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import java.security.Security
import java.security.Signature
import java.security.SignatureException
import java.security.spec.PKCS8EncodedKeySpec

object SignatureHelper {
    fun generateKeyPair(context: Context) {
        val keyHelper = KeystoreHelper.getInstance(context)

        try {
            Security.addProvider(BouncyCastleProvider())
            val kpg = KeyPairGenerator.getInstance("RSA")
            kpg.initialize(4096)
            val kp = kpg.generateKeyPair()


            val publicKey = Base64.encodeToString(kp.public.encoded, Base64.DEFAULT)
            val privateKey = Base64.encodeToString(kp.private.encoded, Base64.DEFAULT)


            val encryptedPublicKey = publicKey.let { keyHelper?.encrypt(context, it) }
            val encryptedPrivateKey = privateKey.let { keyHelper?.encrypt(context, it) }

            /*  encryptedPublicKey?.let {
                  sessionManager.savePublicKey(it)
              }
              encryptedPrivateKey?.let {
                  sessionManager.savePrivateKey(it)
              }*/

        } catch (e: NoSuchAlgorithmException) {

            println("Exception thrown : $e")
        } catch (e: SignatureException) {
            e.printStackTrace()
        } catch (e: NoSuchProviderException) {
            e.printStackTrace()
        }

    }

    fun getPublicKey(context: Context): String {
        val keyHelper = KeystoreHelper.getInstance(context)
        var publicKey: String? = null

        publicKey = "-----BEGIN PUBLIC KEY-----\n$publicKey\n-----END PUBLIC KEY-----"
        return URLEncoder.encode(publicKey, "UTF-8").replace("+", "%20")
    }

    fun getSignature(context: Context, dataToBeSigned: String?): String? {

        val keyHelper = KeystoreHelper.getInstance(context)
        val privateKey = ""

        val pkcs8EncodedBytes = Base64.decode(privateKey, Base64.DEFAULT)
        val keySpec = PKCS8EncodedKeySpec(pkcs8EncodedBytes)
        val kf = KeyFactory.getInstance("RSA")
        val privKey = kf.generatePrivate(keySpec)

        val sr = Signature.getInstance("SHA256WithRSA")
        sr.initSign(privKey)
        sr.update(dataToBeSigned?.toByteArray())
        val bytes = sr.sign()
        val signature = Base64.encodeToString(bytes, Base64.DEFAULT)
        return URLEncoder.encode(signature, "UTF-8")

    }

}