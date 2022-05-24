package com.example.mystoryapp.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.mystoryapp.R
import com.example.mystoryapp.data.Session
import com.example.mystoryapp.databinding.ActivityStoryDetailBinding
import com.example.mystoryapp.response.ListStoryItem
import com.example.mystoryapp.settings.SettingPreferences
import com.example.mystoryapp.settings.SettingPreferencesViewModel
import com.example.mystoryapp.settings.SettingPreferencesViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

private val Context.datasore: DataStore<Preferences> by preferencesDataStore(name = "login")
class StoryDetailActivity : AppCompatActivity() {

    private var _activityStoryDetailBinding: ActivityStoryDetailBinding? = null
    private val binding get() = _activityStoryDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _activityStoryDetailBinding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val intentData = intent.getParcelableExtra<ListStoryItem>(EXTRA_STORY)

        val storyDescription = binding?.storyDescription

        binding?.apply {
            storyName.text = getString(R.string.username_tag, intentData?.name)
            storyDescription?.text = getString(R.string.description_tag, intentData?.description)
        }
        binding?.storyImage?.let {
            Glide.with(this)
                .load(intentData?.photoUrl)
                .into(it)
        }

        binding?.showMap?.setOnClickListener {
            if (intentData?.lat != 0.0 && intentData?.lon != 0.0){
                val intentMap = Intent(this@StoryDetailActivity, MapsActivity::class.java)
                intentMap.putExtra(MapsActivity.EXTRA_STORIES, intentData)
                startActivity(intentMap)
            } else{
                Toast.makeText(this, "Tidak ada data Peta", Toast.LENGTH_SHORT).show()
            }
        }

        getLoginSetting()
    }

    private fun getLoginSetting(){
        val pref = SettingPreferences.getInstance(datasore)
        val settingPreferencesViewModel = ViewModelProvider(this, SettingPreferencesViewModelFactory(pref))[SettingPreferencesViewModel::class.java]

        settingPreferencesViewModel.getLoginSettings().observe(this
        ) { data: Session ->
            if (!data.isLogin) {
                finish()
            }
        }
    }

    companion object{
        const val EXTRA_STORY = "story"
    }
}