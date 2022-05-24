package com.example.mystoryapp.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mystoryapp.R
import com.example.mystoryapp.adapter.ListStoryAdapter
import com.example.mystoryapp.data.Session
import com.example.mystoryapp.databinding.ActivityStoryBinding
import com.example.mystoryapp.response.ListStoryItem
import com.example.mystoryapp.settings.SettingPreferences
import com.example.mystoryapp.settings.SettingPreferencesViewModel
import com.example.mystoryapp.settings.SettingPreferencesViewModelFactory
import com.example.mystoryapp.viewmodel.StoryViewModel
import com.example.mystoryapp.viewmodel.ViewModelFactory

private val Context.datasore: DataStore<Preferences> by preferencesDataStore(name = "login")

class StoryActivity : AppCompatActivity(), ListStoryAdapter.OnItemClickCallback {

    private var _activityStoryBinding: ActivityStoryBinding? = null
    private val binding get() = _activityStoryBinding

    private val adapter = ListStoryAdapter(this)

    private val storyViewModel: StoryViewModel by viewModels{
        ViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _activityStoryBinding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.rvStory?.adapter = adapter
        binding?.rvStory?.layoutManager = LinearLayoutManager(this)

        getLoginSetting()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.logout -> {
                login(false)
                true
            }
            R.id.map -> {
                showMap()
                true
            }
            else -> true
        }
    }

    private fun showRecyclerList(token: String){
        showLoading(false)
        storyViewModel.story(token).observe(this){
            adapter.submitData(lifecycle, it)
            Toast.makeText(this, "Paging Berhasil", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getLoginSetting(){
        val pref = SettingPreferences.getInstance(datasore)
        val settingPreferencesViewModel = ViewModelProvider(this, SettingPreferencesViewModelFactory(pref))[SettingPreferencesViewModel::class.java]

        settingPreferencesViewModel.getLoginSettings().observe(this
        ) { data: Session ->
            if (!data.isLogin) {
                val intent = Intent(this@StoryActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                showLoading(true)
                showRecyclerList(data.token)

                binding?.fabAddStory?.setOnClickListener {
                    val intent = Intent(this@StoryActivity, MainCameraActivity::class.java)
                    intent.putExtra(MainCameraActivity.EXTRA_POST_STORY, data.token)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean){
        if (isLoading){
            binding?.progressBar?.visibility  = View.VISIBLE
        } else {
            binding?.progressBar?.visibility  = View.GONE
        }
    }

    override fun onItemClicked(data: ListStoryItem) {
        val intentToDetail = Intent(this@StoryActivity, StoryDetailActivity::class.java)
        intentToDetail.putExtra(StoryDetailActivity.EXTRA_STORY, data)
        startActivity(intentToDetail, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle())
    }

    private fun login(isLogin: Boolean){
        val pref = SettingPreferences.getInstance(datasore)
        val settingPreferencesViewModel = ViewModelProvider(this, SettingPreferencesViewModelFactory(pref))[SettingPreferencesViewModel::class.java]

        settingPreferencesViewModel.saveLoginSetting(isLogin, "")
    }

    private fun showMap(){
        val intentMap = Intent(this, MapsActivity::class.java)
        startActivity(intentMap)
    }

    companion object{
        const val TAG = "StoryActivity"
        const val DATA_SESSION = "data session"
    }
}