package com.example.romeojtask.security

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CryptographyManagerTest {

    @Test
    fun testEncryptAndDecrypt() {
            val originalText = "This is a secret message for testing!"

            val encryptedData = CryptographyManager.encrypt(originalText)

            assertThat(encryptedData).isNotNull()
            assertThat(String(encryptedData.data)).isNotEqualTo(originalText)

            val decryptedText = CryptographyManager.decrypt(encryptedData)

            assertThat(decryptedText).isEqualTo(originalText)
    }
}
