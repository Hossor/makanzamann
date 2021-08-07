package ir.nahad.makanzaman

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.mapbox.mapboxsdk.geometry.LatLng
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray


class MainActivity : AppCompatActivity() {
    var username :String = ""
    protected var locationManager: LocationManager? = null
    protected var locationListener: LocationListener? = null
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var getUsername = intent.extras
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
          var worksheetJson = JSONArray(response)
            var worksheetsize = worksheetJson.length()
            for (i in 0 .. worksheetsize)
            {
                var date = worksheetJson.getJSONObject(i).optString("date")
                var start = worksheetJson.getJSONObject(i).optString("start")
                var end = worksheetJson.getJSONObject(i).optString("finish")
                var karkerd = "111" //worksheetJson.getJSONObject(i).optString("")
                var worksheetitems:worksheetItems = worksheetItems(start,end ,date ,karkerd)


            }
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
            Log.d("Satus" , status.toString())

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
    @SuppressLint("MissingPermission")
    private fun getLocation():LatLng {
        val lm = getSystemService(LOCATION_SERVICE) as LocationManager
        val location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        var longitude = location!!.longitude
        var latitude = location!!.latitude
        Log.d("Satus", "Lng: "+longitude.toString() + " lat: " + latitude)
        var loc = LatLng(latitude, longitude)
        return loc
    }

}