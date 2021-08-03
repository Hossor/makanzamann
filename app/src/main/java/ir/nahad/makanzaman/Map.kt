package ir.nahad.makanzaman

import android.app.VoiceInteractor
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.awesomedialog.*
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.geojson.Polygon
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.mapboxsdk.style.layers.FillLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import ir.map.sdk_map.MapirStyle
import kotlinx.android.synthetic.main.activity_map.*


class Map : AppCompatActivity() {
    lateinit var map: MapboxMap
    lateinit var mapStyle: Style
    var username: String = ""
    val samplePoint = LatLng(32.661343, 51.680374)
    var counter = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        var getUsername = intent.extras
        username = getUsername!!.getString("usernameLogin").toString()
        Log.d("Username" , username)
        AwesomeDialog.build(this)
            .title("ثبت موقعیت")
            .body("لطفا موقعیت مورد نظر را انتخاب نمایید")
            .onPositive("تایید")
//            .icon(R.drawable.ic_congrts)


        map_view.onCreate(savedInstanceState)
        map_view!!.getMapAsync(OnMapReadyCallback { mapboxMap ->
            map = mapboxMap
            map!!.setStyle(Style.Builder().fromUri(MapirStyle.MAIN_MOBILE_VECTOR_STYLE)) { style ->
                mapStyle = style
                zoomToSpecificLocation(samplePoint.latitude, samplePoint.longitude, 11.0)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(samplePoint, 10.0))
               // addSymbolSourceAndLayerToMap(samplePoint.latitude, samplePoint.longitude)
            }

            var Polygon: ArrayList<LatLng> = ArrayList()



            map!!.addOnMapClickListener { point ->
                Log.d("mapclick", "Click\npoint:${point.latitude}+${point.longitude}")
                zoomToSpecificLocation(point.latitude, point.longitude, 13.0)
                var latlog = LatLng(point.latitude, point.longitude)
                AwesomeDialog.build(this)
                    .title("ثبت موقعیت")
                    .body("موقعیت مکانی انتخاب شده ثبت شود؟")
                    .icon(R.drawable.ic_congrts)
                    .onPositive("تایید") {
                        var queue = Volley.newRequestQueue(this@Map)

                        // volley
                        val url = "http://makanzaman.ir/api/Values?username=$username&lat=${latlog.latitude}&lng=${latlog.longitude}"
                        var stringRequest= StringRequest(Request.Method.GET , url , Response.Listener {response->
                            Log.d("Response" , response.toString())
                            if(response.contains( "Insert OK!"))
                            {
                                var intent: Intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                            }
                            else{
                                Toast.makeText(this , "عملیات ناموفق" , Toast.LENGTH_LONG).show()
                            }



                        } ,
                        Response.ErrorListener {

                        }
                            )
                        queue.add(stringRequest)

                    }
                    .onNegative("لغو") {
                        Log.d("TAG", "negative ")
                    }

                false

            }
        })


    }

//
//    private fun addSymbolSourceAndLayerToMap(latitude: Double, longitude: Double) {
//        var point = LatLng(latitude, longitude)
//        var options: SymbolOptions = SymbolOptions()
//// Add source to map
//        val samplePointsFeatures: MutableList<Feature> = ArrayList()
//        val sampleFeature = Feature.fromGeometry(Point.fromLngLat(point.longitude, point.latitude))
//        samplePointsFeatures.add(sampleFeature)
//        val featureCollection = FeatureCollection.fromFeatures(samplePointsFeatures)
//        val geoJsonSource = GeoJsonSource("sample_source_id", featureCollection)
//        mapStyle!!.addSource(geoJsonSource)
//// Add image to map
//        val icon = BitmapFactory.decodeResource(resources, R.drawable.mapbox_marker_icon_default)
//        mapStyle!!.addImage("sample_image_id", icon)
//// Add layer to map
//        val symbolLayer = SymbolLayer("sample_layer_id", "sample_source_id")
//        symbolLayer.setProperties(
//            PropertyFactory.iconImage("sample_image_id"),
//            PropertyFactory.iconSize(1.5f),
//            PropertyFactory.iconOpacity(.8f),
//            PropertyFactory.textColor("#ff5252")
//        )
//        options.withDraggable(true)
//        mapStyle!!.addLayer(symbolLayer)
//    }

    private fun zoomToSpecificLocation(latitude: Double, longitude: Double, zoom: Double) {
        val sampleZoom = zoom
        var point = LatLng(latitude, longitude)

        map.animateCamera(CameraUpdateFactory.newLatLng(point))
    }


}