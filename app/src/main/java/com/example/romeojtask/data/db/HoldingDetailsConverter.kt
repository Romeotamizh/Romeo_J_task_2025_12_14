package com.example.romeojtask.data.db

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import com.example.romeojtask.security.CryptographyManager
import com.example.romeojtask.security.EncryptedData
import com.squareup.moshi.Moshi

data class HoldingDetails(
    val quantity: Int,
    val ltp: Double,
    val avgPrice: Double,
    val close: Double
)

@RequiresApi(Build.VERSION_CODES.M)
class HoldingDetailsConverter {

    private val moshi = Moshi.Builder().build()
    private val adapter = moshi.adapter(HoldingDetails::class.java)
    private val encryptedDataAdapter = moshi.adapter(EncryptedData::class.java)

    @TypeConverter
    fun fromHoldingDetails(details: HoldingDetails): String {
        val json = adapter.toJson(details)
        val encryptedData = CryptographyManager.encrypt(json)
        return encryptedDataAdapter.toJson(encryptedData)
    }

    @TypeConverter
    fun toHoldingDetails(encryptedJson: String): HoldingDetails {
        val encryptedData = encryptedDataAdapter.fromJson(encryptedJson)!!
        val decryptedJson = CryptographyManager.decrypt(encryptedData)
        return adapter.fromJson(decryptedJson)!!
    }
}
