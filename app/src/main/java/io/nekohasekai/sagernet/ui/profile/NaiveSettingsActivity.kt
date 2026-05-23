package io.nekohasekai.sagernet.ui.profile

import android.os.Bundle
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import io.nekohasekai.sagernet.Key
import io.nekohasekai.sagernet.R
import io.nekohasekai.sagernet.database.DataStore
import io.nekohasekai.sagernet.database.preference.EditTextPreferenceModifiers
import io.nekohasekai.sagernet.fmt.naive.NaiveBean
import moe.matsuri.nb4a.ui.SimpleMenuPreference

private const val KEY_TUNNEL_TIMEOUT = "tunnelTimeout"

class NaiveSettingsActivity : ProfileSettingsActivity<NaiveBean>() {

    override fun createEntity() = NaiveBean()

    override fun NaiveBean.init() {
        DataStore.profileName = name
        DataStore.serverAddress = serverAddress
        DataStore.serverPort = serverPort
        DataStore.serverUsername = username
        DataStore.serverPassword = password
        DataStore.serverProtocol = proto
        DataStore.serverSNI = sni
        DataStore.serverCertificates = certificates
        DataStore.serverHeaders = extraHeaders
        DataStore.serverInsecureConcurrency = insecureConcurrency
        DataStore.profileCacheStore.putString(
            KEY_TUNNEL_TIMEOUT,
            (tunnelTimeout ?: 0).takeIf { it > 0 }?.toString()
        )
        DataStore.profileCacheStore.putBoolean("sUoT", sUoT)
    }

    override fun NaiveBean.serialize() {
        name = DataStore.profileName
        serverAddress = DataStore.serverAddress
        serverPort = DataStore.serverPort
        username = DataStore.serverUsername
        password = DataStore.serverPassword
        proto = DataStore.serverProtocol
        sni = DataStore.serverSNI
        certificates = DataStore.serverCertificates
        extraHeaders = DataStore.serverHeaders.replace("\r\n", "\n")
        insecureConcurrency = DataStore.serverInsecureConcurrency
        tunnelTimeout = DataStore.profileCacheStore.getString(KEY_TUNNEL_TIMEOUT)?.toIntOrNull() ?: 0
        sUoT = DataStore.profileCacheStore.getBoolean("sUoT")
    }

    override fun PreferenceFragmentCompat.createPreferences(
        savedInstanceState: Bundle?,
        rootKey: String?,
    ) {
        addPreferencesFromResource(R.xml.naive_preferences)
        findPreference<EditTextPreference>(Key.SERVER_PORT)!!.apply {
            setOnBindEditTextListener(EditTextPreferenceModifiers.Port)
        }
        findPreference<EditTextPreference>(Key.SERVER_PASSWORD)!!.apply {
            summaryProvider = PasswordSummaryProvider
        }
        findPreference<EditTextPreference>(Key.SERVER_INSECURE_CONCURRENCY)!!.apply {
            setOnBindEditTextListener(EditTextPreferenceModifiers.Number)
        }
        val tunnelTimeout = findPreference<EditTextPreference>(KEY_TUNNEL_TIMEOUT)!!.apply {
            setOnBindEditTextListener(EditTextPreferenceModifiers.Number)
        }
        val protocol = findPreference<SimpleMenuPreference>(Key.SERVER_PROTOCOL)!!

        fun updateProtocol(proto: String?) {
            tunnelTimeout.isVisible = proto == "https"
        }

        updateProtocol(protocol.value)
        protocol.setOnPreferenceChangeListener { _, newValue ->
            updateProtocol(newValue as? String)
            true
        }
    }

    override fun finish() {
        if (DataStore.profileName == "喵要打开隐藏功能") {
            DataStore.isExpert = true
        } else if (DataStore.profileName == "喵要关闭隐藏功能") {
            DataStore.isExpert = false
        }
        super.finish()
    }

}
