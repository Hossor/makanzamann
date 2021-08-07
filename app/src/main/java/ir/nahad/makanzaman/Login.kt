package ir.nahad.makanzaman

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_login.*
import kotlin.math.log

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        getSupportActionBar()?.hide()


        login_btn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                Loginfun()
                login_btn.startLoading()


            }
        })

    }

    private fun Loginfun() {
        var queue = Volley.newRequestQueue(this@Login)
        var username = username.text.toString()
        var password = password.text.toString()
        val namePut = "usernameLogin"

        var url = "http://makanzaman.ir/api/Values?username=${username}&password=${password}"
        var stringRequest = StringRequest(Request.Method.GET, url, Response.Listener<String> {
            Log.d("LoginRes", it.toString())
            val res = it.toString()
            if (res.contains("faild")) {
                login_btn.loadingFailed()
                // login_btn.resetAfterFailed
            } else if (res.contains("successfully")) {
Log.d("checkRes" , res.replace('"' , ' ') )
                if (res.replace('"' , ' ').contains("active")) {
                    var mainIntent:Intent = Intent(this , MainActivity::class.java)
                    mainIntent.putExtra(namePut , username)
                    startActivity(mainIntent)

                } else {
                    login_btn.loadingSuccessful()
                    var intentMap: Intent = Intent(this, Map::class.java)
                    intentMap.putExtra(namePut , username)

                    startActivity(intentMap)
                }
            }
        },
            Response.ErrorListener { Log.e("APILogin", it.message.toString()) })
        queue.add(stringRequest)

    }
}