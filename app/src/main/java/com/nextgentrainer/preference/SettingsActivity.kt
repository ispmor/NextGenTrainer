package com.nextgentrainer.preference

import android.os.Bundle
import android.preference.PreferenceFragment
import androidx.appcompat.app.AppCompatActivity
import com.nextgentrainer.R
import com.nextgentrainer.preference.CameraXLivePreviewPreferenceFragment
import com.nextgentrainer.preference.SettingsActivity.LaunchSource

/**
 * Hosts the preference fragment to configure settings for a demo activity that specified by the
 * [LaunchSource].
 */
class SettingsActivity : AppCompatActivity() {
    /**
     * Specifies where this activity is launched from.
     */
    // CameraX is only available on API 21+
    enum class LaunchSource(val titleResId: Int, val prefFragmentClass: Class<out PreferenceFragment>) {
        CAMERAX_LIVE_PREVIEW(
                R.string.pref_screen_title_camerax_live_preview,
                CameraXLivePreviewPreferenceFragment::class.java);
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val launchSource = intent.getSerializableExtra(EXTRA_LAUNCH_SOURCE) as LaunchSource?
        val actionBar = supportActionBar
        actionBar?.setTitle(launchSource!!.titleResId)
            fragmentManager
                    .beginTransaction()
                    .replace(
                            R.id.settings_container,
                            launchSource!!.prefFragmentClass.getDeclaredConstructor().newInstance())
                    .commit()

    }

    companion object {
        const val EXTRA_LAUNCH_SOURCE = "extra_launch_source"
    }
}
