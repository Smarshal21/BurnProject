package com.example.burnproject

import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import android.content.Intent as Intent1


class MainActivity : AppCompatActivity() {

    lateinit var select_image_button: Button

    // lateinit var img_view1 : ImageView
    lateinit var img_view2: ImageView
    lateinit var camerabtn: Button
    lateinit var predictbtn: Button
    lateinit var predictbtn2: Button
    var uri: String = ""
    var bitmap: Bitmap? = null
    var encodedImage: String?=null
    lateinit var resultee: TextView

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent1?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 250) {
            img_view2.setImageURI(data?.data)

            var uuri: Uri? = data?.data
            uri = uuri.toString()
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uuri)
            val imageBytes = runBlocking {
                val baos = ByteArrayOutputStream()
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                baos.toByteArray()
            }
            encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT)


        } else if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
            bitmap = data?.extras?.get("data") as Bitmap
            img_view2.setImageBitmap(bitmap)
        }
        fun ARGBBitmap(img: Bitmap): Bitmap {
            return img.copy(Bitmap.Config.ARGB_8888, true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        select_image_button = findViewById(R.id.selectbtn)
        //     img_view1 = findViewById(R.id.imageview1)
        img_view2 = findViewById(R.id.imageview2)
        camerabtn = findViewById<Button>(R.id.capturebtn)
        predictbtn = findViewById<Button>(R.id.predictbtn)
        resultee = findViewById(R.id.result)
        predictbtn2 = findViewById<Button>(R.id.getbtn)
        checkandGetpermissions()



        select_image_button.setOnClickListener(View.OnClickListener {
            Log.d("mssg", "button pressed")
            var intent: Intent1 = Intent1(Intent1.ACTION_GET_CONTENT)
            intent.type = "image/*"

            startActivityForResult(intent, 250)
        })

        camerabtn.setOnClickListener(View.OnClickListener {
            var camera: Intent1 = Intent1(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(camera, 200)
        })
        predictbtn.setOnClickListener {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://2b22-2401-4900-5ac4-fd3e-8cf4-1df1-7b73-800a.ngrok-free.app/predict/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(
                    OkHttpClient.Builder()
                        .addInterceptor(HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BODY
                        })
                        .build()
                )
                .build()
            val myApi = retrofit.create(MyApi::class.java)

            val imageRequest = encodedImage?.let { it1 -> ImageRequest(it1) }
            if (imageRequest != null) {
                myApi.uploadImage(imageRequest).enqueue(object : Callback<ApiResponse> {
                    override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
//                        val result = response.body()
//                        resultee.text = result.toString()

                        Toast.makeText(this@MainActivity, "Image posted", Toast.LENGTH_SHORT).show()

                    }

                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                        Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_SHORT).show()
                    }
                })


            }
        }
        predictbtn2.setOnClickListener {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://2b22-2401-4900-5ac4-fd3e-8cf4-1df1-7b73-800a.ngrok-free.app/predict/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(
                    OkHttpClient.Builder()
                        .addInterceptor(HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BODY
                        })
                        .build()
                )
                .build()
            val myApi = retrofit.create(MyApi::class.java)
            myApi.getFloat().enqueue(object : Callback<Float> {

                override fun onResponse(call: Call<Float>, response: Response<Float>) {
                    var result = response.body()
                    resultee.text = result.toString()
                }
                override fun onFailure(call: Call<Float>, t: Throwable) {
                    Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_SHORT).show()
                }
            })
        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    public fun checkandGetpermissions() {
        if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 100)
        } else {
            Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show()
        }
    }

}