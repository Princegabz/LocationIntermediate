package com.example.locationbasics
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.Locale

class MainActivity : AppCompatActivity(), LocationListener {

    private lateinit var locationManager: LocationManager
    private lateinit var tvOutput: TextView
    private lateinit var listView: ListView
    private val locationHistory = mutableListOf<Locations>()

    private val locationList = mutableListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>
    private val locationPermissionCode = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val button: Button = findViewById(R.id.btnLocations)
        button.setBackgroundColor(Color.parseColor("#3FC0C8"))
        tvOutput = findViewById(R.id.lblOutput)
        listView = findViewById(R.id.listView)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, locationList)
        listView.adapter = adapter

        button.setOnClickListener {
            getLocation()
        }
    }

    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionCode
            )
        }
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            5000,
            5f,
            this
        )
    }

    override fun onLocationChanged(location: Location) {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

        if (addresses != null && addresses.isNotEmpty()) {
            val address = addresses[0]
            val addressText = address.getAddressLine(0)

                var i:Int=0
                for(loc in locationHistory){
                    var addres = loc.address
                    if( addressText.equals(addres)){
                        i+=1
                        break
                    }

                }
                if(i==0){
                    locationHistory.add( Locations(addressText))
                    val latitude = location.latitude
                    val longitude = location.longitude
                    val timestamp = System.currentTimeMillis()
                    val locationInfo =
                        "Latitude: $latitude\nLongitude: $longitude\nAddress: $addressText\nTimestamp: $timestamp"
                    locationList.add(locationInfo)
                    adapter.notifyDataSetChanged()
                }



        } else {
            tvOutput.text = "No address found"
        }
    }

    override fun onProviderEnabled(provider: String) {
        // Implementation for onProviderEnabled if needed
    }

    override fun onProviderDisabled(provider: String) {
        // Implementation for onProviderDisabled if needed
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        // Implementation for onStatusChanged
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
