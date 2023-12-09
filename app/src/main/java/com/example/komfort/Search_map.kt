package com.example.komfort

import android.annotation.SuppressLint
import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.model.LatLng
import com.example.komfort.databinding.ActivitySearchMapBinding
import com.google.maps.android.SphericalUtil
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class Search_map : Activity() {
private lateinit var editText: EditText
private lateinit var Image_map:ImageView
private lateinit var info_sity:TextView
    private lateinit var binding: ActivitySearchMapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Image_map = findViewById(R.id.imgKarta)
        editText = findViewById(R.id.findSity)
        info_sity=findViewById(R.id.text_info)
        val sharedPreferences = getSharedPreferences("loc", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        editor.putString("a", "60.597474")
        editor.putString("b", "56.838011")
        editor.apply()

    }
    fun SearhButton(view: View){
        zapros(editText.text.toString())
    }
    private fun zapros(sity: String){
        Log.d("MyLog", "zapros() method called")

        // Проверка наличия разрешения на использование интернета


        val url = "https://geocode-maps.yandex.ru/1.x/?apikey=3ee7e538-e94a-42c4-ac69-00f6160dfd34&geocode=$sity&format=json"

        val queue = Volley.newRequestQueue(this)

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                // Обработка успешного ответа
                try {
                    val obj = JSONObject(response)
                    val featureMember = obj.getJSONObject("response")
                        .getJSONObject("GeoObjectCollection")
                        .getJSONArray("featureMember")

                    if (featureMember.length() > 0) {
                        val firstObject = featureMember.getJSONObject(0)
                        val point = firstObject.getJSONObject("GeoObject")
                            .getJSONObject("Point")
                            .getString("pos")
                        val tochki = point.toString().split(" ")
                        //txt.text = "${tochki[0]}UU${tochki[1]} "
                        searchKarta(tochki,sity)

                        Log.d("MyLog", "Coordinates: $point")
                    } else {
                        Log.d("MyLog", "No features found in the response")
                    }
                } catch (e: JSONException) {
                    Log.d("MyLog", "JSON parsing error: ${e.message}")
                }
            },
            { error ->
                // Обработка ошибки
                val statusCode = error.networkResponse.statusCode
                Log.d("MyLog", "Volley error status code: $statusCode")


            })

        // Добавление запроса в очередь
        queue.add(stringRequest)
    }
    private fun zapros_info(sity: String,callback: (String) -> Unit){
        Log.d("MyLog", "zapros_info() method called")

        val url =
            "https://search-maps.yandex.ru/v1/?text=Достопримечательности%20$sity&type=biz&lang=ru_RU&apikey=5ff8c278-65fd-4803-b43e-ff99ff9b21d1"

        val requestQueue = Volley.newRequestQueue(this)

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    val features: JSONArray = response.getJSONArray("features")
                    val stringBuilder = StringBuilder()

                    for (i in 0 until features.length()) {
                        val feature: JSONObject = features.getJSONObject(i)
                        val properties: JSONObject = feature.getJSONObject("properties")
                        val name: String? = properties.optString("name", "")
                        val info: String = "$sity, $name"
                        stringBuilder.append(info).append("\n")


                        Log.d("MyLog", "Город $sity Достопримечательность $name")

                        val db = MainDb.getDb(this)
                        val item = Item(null, sity.toString(), name.toString())
                        Thread{
                            db.getDao().insertItem(item)
                        }.start()
                    }

                    callback(stringBuilder.toString())
                } catch (e: JSONException) {
                    callback("")
                    e.printStackTrace()
                }
            },
            { error ->
                callback("")
                error.printStackTrace()
            })
        requestQueue.add(request)
    }
    fun searchKarta(array: List<String>,sity: String){
        val imageUrl = "https://static-maps.yandex.ru/v1?ll=${array[0]},${array[1]}&size=450,450&z=13&pt=${array[0]},${array[1]},pmwtm1~${array[0]},${array[1]},pmwtm99&apikey=f9ce7b23-8786-44b7-8308-864c74bf640a"

        Picasso.get().load(imageUrl).into(Image_map)
        val stringBuilder = StringBuilder()
        val name = mutableListOf<String>()
        zapros_info(sity) { info ->
            stringBuilder.append(info).append("\n") // Append the received info
            info_sity.append("$info\n") // Add each value to the EditText
        }

        val info = stringBuilder.toString()
        info_sity.text = info // Set the combined info to the info_sity TextView


    }
}