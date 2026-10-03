package com.aruack.music.core.source.remote.saavn

import java.security.spec.KeySpec
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec

object SaavnUrlDecryptor {
    private const val SECRET_KEY = "38346591"

    fun decrypt(encryptedUrl: String): String {
        if (encryptedUrl.isBlank()) return ""
        return try {
            val keySpec: KeySpec = DESKeySpec(SECRET_KEY.toByteArray(Charsets.UTF_8))
            val keyFactory = SecretKeyFactory.getInstance("DES")
            val key = keyFactory.generateSecret(keySpec)
            val cipher = Cipher.getInstance("DES/ECB/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, key)
            val decodedBytes = Base64.getDecoder().decode(encryptedUrl.trim())
            val decryptedBytes = cipher.doFinal(decodedBytes)
            val decryptedUrl = String(decryptedBytes, Charsets.UTF_8)
            decryptedUrl
                .replace("_96.mp4", "_320.mp4")
                .replace("_96_p.mp4", "_320.mp4")
                .replace("_160.mp4", "_320.mp4")
                .replace("_160_p.mp4", "_320.mp4")
        } catch (e: Exception) {
            ""
        }
    }

    fun cleanHtml(text: String): String {
        return text
            .replace("&quot;", "\"")
            .replace("&amp;", "&")
            .replace("&#039;", "'")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&nbsp;", " ")
            .trim()
    }

    fun upgradeArtworkUrl(url: String): String {
        return url
            .replace("150x150.jpg", "500x500.jpg")
            .replace("50x50.jpg", "500x500.jpg")
    }
}
