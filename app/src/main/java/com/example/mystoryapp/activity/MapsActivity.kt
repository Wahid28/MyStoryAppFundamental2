package com.example.mystoryapp.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.mystoryapp.R
import com.example.mystoryapp.data.Session
import com.example.mystoryapp.databinding.ActivityMapsBinding
import com.example.mystoryapp.response.GetStoriesResponse
import com.example.mystoryapp.response.ListStoryItem
import com.example.mystoryapp.retrofit.ApiConfig
import com.example.mystoryapp.settings.SettingPreferences
import com.example.mystoryapp.settings.SettingPreferencesViewModel
import com.example.mystoryapp.settings.SettingPreferencesViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.datasore: DataStore<Preferences> by preferencesDataStore(name = "login")

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

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
                Toast.makeText(this, "Sudah berada di halaman Map", Toast.LENGTH_SHORT).show()
                true
            }
            else -> true
        }
    }

    private fun getLoginSetting(){
        val pref = SettingPreferences.getInstance(datasore)
        val settingPreferencesViewModel = ViewModelProvider(this, SettingPreferencesViewModelFactory(pref))[SettingPreferencesViewModel::class.java]

        settingPreferencesViewModel.getLoginSettings().observe(this
        ) { data: Session ->
            if (!data.isLogin) {
                finish()
            } else {
                val intentData = intent.getParcelableExtra<ListStoryItem>(EXTRA_STORIES)
                if (intentData != null){
                    val latitude = intentData.lat
                    val longitude = intentData.lon

                    val position = LatLng(latitude, longitude)
                    mMap.addMarker(MarkerOptions().position(position).title("Upload Location $latitude, $longitude"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(position))
                } else{
                    showAllStoriesWithLocation(data.token)
                }
            }
        }
    }

    private fun showAllStoriesWithLocation(token: String?){
        val bearerToken = "Bearer $token"
        Log.d(StoryActivity.TAG, bearerToken)

        val listStoryItem = arrayListOf <List<ListStoryItem>>()
        val storyItem = arrayListOf<ListStoryItem>()

        val client = ApiConfig.getApiService().getStoriesWithLocation(bearerToken)
        client.enqueue(object : Callback<GetStoriesResponse> {
            override fun onResponse(
                call: Call<GetStoriesResponse>,
                response: Response<GetStoriesResponse>
            ) {
                val stories = response.body()
                if (stories != null){
                    listStoryItem.add(stories.listStory)
                    Log.d("listStoryItem: ", listStoryItem.toString())
                    for (story in listStoryItem){
                        story.forEach {
                            Log.d("listStoryMap:", it.toString())
                            storyItem.add(it)

                            val latitude = it.lat
                            val longitude = it.lon
                            val position = LatLng(latitude, longitude)
                            mMap.addMarker(MarkerOptions().position(position).title("Upload Location $latitude, $longitude"))
                        }
                    }
                }
            }

            override fun onFailure(call: Call<GetStoriesResponse>, t: Throwable) {
                t.message?.let { debugLog(it) }
            }
        })
    }

    private fun login(isLogin: Boolean){
        val pref = SettingPreferences.getInstance(datasore)
        val settingPreferencesViewModel = ViewModelProvider(this, SettingPreferencesViewModelFactory(pref))[SettingPreferencesViewModel::class.java]

        settingPreferencesViewModel.saveLoginSetting(isLogin, "")
    }

    private fun debugLog(message: String){
        Log.d(StoryActivity.TAG, message)
    }

    companion object{
        const val EXTRA_STORIES = "extra_stories"
    }
}