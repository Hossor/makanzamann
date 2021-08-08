package ir.nahad.makanzaman

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.mapbox.mapboxsdk.geometry.LatLng
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import java.util.*


class MainActivity : AppCompatActivity() {
    var username :String = ""
    protected var locationListener: LocationListener? = null
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var getUsername = intent.extras
        getSupportActionBar()!!.hide();
        username = "1"//getUsername!!.getString("usernameLogin").toString()
        getTime()
        var status = true
        //getstatusworkshet()

            //.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        getLoctionfromApi(username)
        getWorksheet(username)

        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !==
            PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                )
            } else {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                )
            }
        }

        sabt.setOnClickListener {
            var shPref:SharedPreferences
            shPref = getSharedPreferences("latlng" , Context.MODE_PRIVATE)
            var latSaved = shPref.getString("lat" , null)
            var lngSaved = shPref.getString("lng" , null)

            var loc = getLocation()

var dist = distance(loc.latitude , loc.longitude , latSaved!!.toDouble(),lngSaved!!.toDouble())
            var dist2 = String.format("%.0f" , dist)

                checkstatus(username ,dist2)

            Log.d("Satus", status.toString()+", " +latSaved+","+lngSaved+","+loc+" "+dist)
        }

    }

    private fun getWorksheet(username: String) {
        var queue = Volley.newRequestQueue(this)
        var url ="http://makanzaman.ir/api/workSheet?username=$username"
        var requestString = StringRequest(Request.Method.GET , url , Response.Listener { response->
            Log.d("RESPONSS" , response)
          var worksheetJson = JSONArray(response)
            var worksheetsize = worksheetJson.length()-1
            val data = ArrayList<worksheetItems>()

            for (i in 0 .. worksheetsize)
            {
                var date = worksheetJson.getJSONObject(i).optString("date")
                var start = worksheetJson.getJSONObject(i).optString("start")
                var end = worksheetJson.getJSONObject(i).optString("finish")
                var karkerd = "111" //worksheetJson.getJSONObject(i).optString("")
                var worksheetitems:worksheetItems = worksheetItems(start,end ,date ,karkerd)
                recyWorksheet.layoutManager = LinearLayoutManager(this)
                data.add(worksheetitems)

            }
            val adapter = worksheetAdapter(data)
            recyWorksheet.adapter = adapter
        } , Response.ErrorListener {

        })
        queue.add(requestString)
    }

    private fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist = (Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + (Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta))))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist = dist * 60 * 1.1515
        return dist*1000
    }
    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }

    private fun checkstatus(username: String , dist2:String) {

        var queue = Volley.newRequestQueue(this)
        var url ="http://makanzaman.ir/api/WorkSheet?usernameGettime=$username"
        var requestString = StringRequest(Request.Method.GET , url , Response.Listener { response->
            var status:String =""

            status = response.toString().replace('"' , ' ')
            Log.d("Satus2" , status.toString())

            if (status == " 0 "){
                if (dist2.toInt() < 100) {

                    strat(username)
                }
            else{
                Toast.makeText(this , "شما در موقعیت ثبت شده نمیباشید",Toast.LENGTH_SHORT).show()
            }
            }
            else
            {
                finishKar(username)
            }
        } , Response.ErrorListener {

        })
        queue.add(requestString)



    }

    private fun getLoctionfromApi(username: String){
        var queue = Volley.newRequestQueue(this)
        var url = "http://makanzaman.ir/api/getLoction?username=${username}"
        var requestString = StringRequest(Request.Method.GET , url , Response.Listener {response->

            var loc = JSONArray(response)
            var lat = loc.getJSONObject(0).optString("lat")
            var lng = loc.getJSONObject(0).optString("lng")
            var shPref:SharedPreferences
            shPref = getSharedPreferences("latlng" , Context.MODE_PRIVATE)
            var sEdit :SharedPreferences.Editor = shPref.edit()
            sEdit.putString("lat" , lat)
            sEdit.putString("lng" , lng)
            sEdit.apply()

            Log.d("getLoctionfromApi" , lat + " , "+lng)
        },Response.ErrorListener {

        })
        queue.add(requestString)
    }
    private fun finishKar(username: String) {
        var queue = Volley.newRequestQueue(this)
        var url = "http://makanzaman.ir/api/WorkSheet?usernameFinish=${username}"
        var requestString = StringRequest(Request.Method.GET , url , Response.Listener {response->

            Log.d("StartApi" , response.toString())
        },Response.ErrorListener {

        })
        queue.add(requestString)
    }

    private fun strat(username:String) {
        var queue = Volley.newRequestQueue(this)
        var url = "http://makanzaman.ir/api/WorkSheet?username=${username}"
        var requestString = StringRequest(Request.Method.GET , url , Response.Listener {response->

                                                                                       Log.d("StartApi" , response.toString())
        },Response.ErrorListener {

        })
        queue.add(requestString)
    }

    private fun getTime() {
        var queue = Volley.newRequestQueue(this)
        var url = "http://makanzaman.ir/api/Time"
        var requestString = StringRequest(Request.Method.GET, url , Response.Listener { response->
            todayDate.text = response.toString().replace('"', ' ')
        } ,Response.ErrorListener { response->

        } )
        queue.add(requestString)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    if ((ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) ===
                                PackageManager.PERMISSION_GRANTED)
                    ) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }
    private val REQUEST_LOCATION = 1
    var latitude: String? = null
    var longitude:String? = null
    private fun getLocation():LatLng {
        var locationManager: LocationManager =  getSystemService(Context.LOCATION_SERVICE) as LocationManager;

        var loc :LatLng = LatLng()
        if (ActivityCompat.checkSelfPermission(
                this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION
            )
        } else {

            var locationGPS = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (locationGPS != null) {
                val lat = locationGPS.latitude
                val longi = locationGPS.longitude
                latitude = lat.toString()
                longitude = longi.toString()
                loc.latitude = lat
                loc.longitude = longi
                Log.d("UserLocation" , latitude + longitude)

            } else {
                Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show()
            }
        }
        return loc
    }



}