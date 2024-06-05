package com.conduent.nationalhighways.utils

import android.content.Context
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.text.TextUtils
import android.util.Base64
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.math.BigInteger
import java.security.*
import java.security.cert.CertificateException
import java.util.*
import javax.crypto.*
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.security.auth.x500.X500Principal

class KeystoreHelper @Throws(NoSuchPaddingException::class, NoSuchProviderException::class, NoSuchAlgorithmException::class, InvalidAlgorithmParameterException::class, KeyStoreException::class, CertificateException::class, IOException::class)

constructor(ctx: Context) {

    private var keyStore: KeyStore? = null
    private var secretKey: Key? = null

    private val aesKeyFromKS: Key
        @Throws(NoSuchProviderException::class, NoSuchAlgorithmException::class, InvalidAlgorithmParameterException::class, KeyStoreException::class, CertificateException::class, IOException::class, UnrecoverableKeyException::class)
        get() {
            keyStore = KeyStore.getInstance(AndroidKeyStore)
            keyStore!!.load(null)
            return keyStore!!.getKey(KEY_ALIAS, null) as SecretKey
        }

    init {
        this.generateEncryptKey(ctx)
        this.generateRandomIV(ctx)
        try {
            this.generateAESKey(ctx)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    @Throws(NoSuchProviderException::class, NoSuchAlgorithmException::class, InvalidAlgorithmParameterException::class, KeyStoreException::class, CertificateException::class, IOException::class)
    private fun generateEncryptKey(ctx: Context) {

        keyStore = KeyStore.getInstance(AndroidKeyStore)
        keyStore!!.load(null)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (!keyStore!!.containsAlias(KEY_ALIAS)) {
                val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, AndroidKeyStore)
                keyGenerator.init(
                        KeyGenParameterSpec.Builder(KEY_ALIAS,
                                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                                .setRandomizedEncryptionRequired(false)
                                .setDigests(KeyProperties.DIGEST_SHA256)
                                .build())
                keyGenerator.generateKey()
            }
        } else {
            if (!keyStore!!.containsAlias(KEY_ALIAS)) {
                // Generate a key pair for encryption
                val start = Calendar.getInstance()
                val end = Calendar.getInstance()
                end.add(Calendar.YEAR, 30)
                val spec = KeyPairGeneratorSpec.Builder(ctx)
                        .setAlias(KEY_ALIAS)
                        .setSubject(X500Principal("CN=$KEY_ALIAS"))
                        .setSerialNumber(BigInteger.TEN)
                        .setStartDate(start.time)
                        .setEndDate(end.time)
                        .build()
                val kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, AndroidKeyStore)
                kpg.initialize(spec)
                kpg.generateKeyPair()
            }
        }


    }

    @Throws(Exception::class)
    private fun rsaEncrypt(secret: ByteArray): ByteArray {
        val privateKeyEntry = keyStore!!.getEntry(KEY_ALIAS, null) as KeyStore.PrivateKeyEntry
        // Encrypt the text
        val inputCipher = Cipher.getInstance(RSA_MODE, "AndroidOpenSSL")
        inputCipher.init(Cipher.ENCRYPT_MODE, privateKeyEntry.certificate.publicKey)

        val outputStream = ByteArrayOutputStream()
        val cipherOutputStream = CipherOutputStream(outputStream, inputCipher)
        cipherOutputStream.write(secret)
        cipherOutputStream.close()

        return outputStream.toByteArray()
    }

    @Throws(Exception::class)
    private fun rsaDecrypt(encrypted: ByteArray): ByteArray {
        val privateKeyEntry = keyStore!!.getEntry(KEY_ALIAS, null) as KeyStore.PrivateKeyEntry
        val output = Cipher.getInstance(RSA_MODE, "AndroidOpenSSL")
        output.init(Cipher.DECRYPT_MODE, privateKeyEntry.privateKey)
        val cipherInputStream = CipherInputStream(
                ByteArrayInputStream(encrypted), output)
        val values = ArrayList<Byte>()
        var nextByte: Int
        nextByte = cipherInputStream.read()
        while (nextByte != -1) {
            values.add(nextByte.toByte())
        }

        val bytes = ByteArray(values.size)
        for (i in bytes.indices) {
            bytes[i] = values[i]
        }
        return bytes
    }

    @Throws(Exception::class)
    private fun generateAESKey(context: Context) {
        val pref = context.getSharedPreferences(SHARED_PREFENCE_NAME, Context.MODE_PRIVATE)
        var enryptedKeyB64 = pref.getString(ENCRYPTED_KEY, null)
        if (enryptedKeyB64 == null) {
            val key = ByteArray(16)
            val secureRandom = SecureRandom()
            secureRandom.nextBytes(key)
            val encryptedKey = rsaEncrypt(key)
            enryptedKeyB64 = Base64.encodeToString(encryptedKey, Base64.DEFAULT)
            val edit = pref.edit()
            edit.putString(ENCRYPTED_KEY, enryptedKeyB64)
            edit.apply()
        }
    }


    @Throws(Exception::class)
    private fun getSecretKey(context: Context): Key {
        if(secretKey==null) {
            val pref = context.getSharedPreferences(SHARED_PREFENCE_NAME, Context.MODE_PRIVATE)
            val enryptedKeyB64 = pref.getString(ENCRYPTED_KEY, null)

            val encryptedKey = Base64.decode(enryptedKeyB64, Base64.DEFAULT)
            val key = rsaDecrypt(encryptedKey)
            secretKey =  SecretKeySpec(key, "AES")
        }
        return secretKey as Key
    }

    @Throws(NoSuchAlgorithmException::class, NoSuchPaddingException::class, NoSuchProviderException::class, BadPaddingException::class, IllegalBlockSizeException::class, UnsupportedEncodingException::class)
    fun encrypt(context: Context, input: String): String {
        if (TextUtils.isEmpty(input))
            return ""
        val c: Cipher
        val iv = Base64.decode(generateRandomIV(context), Base64.DEFAULT)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            c = Cipher.getInstance(AES_MODE_M)
            try {
                val spec = GCMParameterSpec(BIT_LENGTH, iv)
                c.init(Cipher.ENCRYPT_MODE, aesKeyFromKS, spec)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else {
            c = Cipher.getInstance(AES_MODE_M)
            try {
                val spec = GCMParameterSpec(BIT_LENGTH, iv)
                c.init(Cipher.ENCRYPT_MODE, getSecretKey(context), spec)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        //encryptionIV = c.iv
        val s = Base64.encodeToString(iv, Base64.DEFAULT)
        val encodedBytes = c.doFinal(input.toByteArray(charset("UTF-8")))
        return s + "|" + Base64.encodeToString(encodedBytes, Base64.DEFAULT)
    }


    @Throws(NoSuchAlgorithmException::class, NoSuchPaddingException::class, NoSuchProviderException::class, BadPaddingException::class, IllegalBlockSizeException::class, UnsupportedEncodingException::class)
    fun decrypt(context: Context, encrypted: String): String {
        val c: Cipher
        val split = encrypted.split("|")
        if (split.size < 2) return ""

        val iv = Base64.decode(split.get(0), Base64.DEFAULT)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            c = Cipher.getInstance(AES_MODE_M)
            try {
                val spec = GCMParameterSpec(BIT_LENGTH, iv)
                c.init(Cipher.DECRYPT_MODE, aesKeyFromKS, spec)

            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else {
            c = Cipher.getInstance(AES_MODE_M)
            try {
                val spec = GCMParameterSpec(BIT_LENGTH, iv)
                c.init(Cipher.DECRYPT_MODE, getSecretKey(context), spec)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        val decodedValue = Base64.decode(split[1].toByteArray(charset("UTF-8")), Base64.DEFAULT)
        val decryptedVal = c.doFinal(decodedValue)
        return String(decryptedVal)
    }

    fun generateRandomIV(ctx: Context): String {
        val random = SecureRandom()
        val generated = random.generateSeed(12)
        val generatedIVstr = Base64.encodeToString(generated, Base64.DEFAULT)
        return generatedIVstr
    }


    companion object {


        private val RSA_MODE = "RSA/ECB/PKCS1Padding"
        private val AES_MODE_M = "AES/GCM/NoPadding"

        private val KEY_ALIAS = "KEY"
        private val AndroidKeyStore = "AndroidKeyStore"
        val SHARED_PREFENCE_NAME = "SAVED_TO_SHARED"
        val ENCRYPTED_KEY = "ENCRYPTED_KEY"
        val PUBLIC_IV = "PUBLIC_IV"
        val PUBLIC_IV_PERSONAL = "PUBLIC_IV_PERSONAL"
        private var keystoreHelper: KeystoreHelper? = null

        fun getInstance(ctx: Context): KeystoreHelper? {
            if (keystoreHelper == null) {
                try {
                    keystoreHelper = KeystoreHelper(ctx)
                } catch (e: NoSuchPaddingException) {
                    e.printStackTrace()
                } catch (e: NoSuchProviderException) {
                    e.printStackTrace()
                } catch (e: NoSuchAlgorithmException) {
                    e.printStackTrace()
                } catch (e: InvalidAlgorithmParameterException) {
                    e.printStackTrace()
                } catch (e: KeyStoreException) {
                    e.printStackTrace()
                } catch (e: CertificateException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            return keystoreHelper
        }
    }
}
private const val  BIT_LENGTH = 128
