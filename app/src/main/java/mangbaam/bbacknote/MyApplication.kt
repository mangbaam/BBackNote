package mangbaam.bbacknote

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class MyApplication: Application() {
    companion object {
        lateinit var encryptedPrefs: EncryptedPrefsManger
    }

    override fun onCreate() {
        super.onCreate()
        encryptedPrefs = EncryptedPrefsManger(applicationContext)
    }

}

class EncryptedPrefsManger(context: Context) {
    var masterKeyAlias: String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    var sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        "encryptedData",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    fun getPassword() = getString(SECRET_KEY)

    fun setPassword(password: String) = setString(SECRET_KEY, password)

    private fun setString(key: String, value: String) =
        sharedPreferences.edit().putString(key, value).apply()

    private fun getString(key: String) = sharedPreferences.getString(key, "")


    companion object {
        private const val SECRET_KEY = "secret_note_password"
    }

}