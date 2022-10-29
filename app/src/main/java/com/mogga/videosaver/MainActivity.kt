package com.mogga.videosaver

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.view.View
import android.widget.MediaController
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.mogga.videosaver.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var reelsUrl:String = ""
    private var permission = 0
private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
    permission = if (it){
        1
    }else{
        0
    }
}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        requestPermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        binding.btnSearch.setOnClickListener {
            binding.progressCircular.visibility = View.VISIBLE
            getReelsData(binding.editText.text)

        }
        binding.btnDownload.setOnClickListener{
            if (permission == 1){
                if (reelsUrl.isNotEmpty()){
                    downloadReels(reelsUrl,)

                }else{
                    Toast.makeText(this,"Url is empty",Toast.LENGTH_LONG).show()
                }

            }else{
                Toast.makeText(this,"Storage permission Denied",Toast.LENGTH_LONG).show()
            }

        }
    }


    private fun getReelsData(userUrl: Editable) {
        val url = "https://instagram-media-downloader.p.rapidapi.com/rapid/post.php?url=$userUrl"
        val queue = Volley.newRequestQueue(this@MainActivity)
        val jsonObjectRequest = object : JsonObjectRequest(
            Method.GET,
            url,
            null,
            Response.Listener {
                reelsUrl = it.getString("video")
                if (reelsUrl.isNotEmpty()){
                    val uri: Uri = Uri.parse(reelsUrl)
                    binding.showVideo.setVideoURI(uri)
                    val mediaController = MediaController(this)
                    mediaController.setAnchorView(binding.showVideo)
                    mediaController.setMediaPlayer(binding.showVideo)
                    binding.showVideo.setMediaController(mediaController)
                    binding.showVideo.start()
                    binding.progressCircular.visibility = View.GONE

                }else{
                    Toast.makeText(this,"InvalidUrl",Toast.LENGTH_LONG).show()
                }


                              },
            Response.ErrorListener {
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
            }

        ){
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-type"] = "application/json"
                headers["x-rapidapi-key"] =
                    "Enter Your Key"
                headers["x-rapidapi-host"] =
                    "instagram-media-downloader.p.rapidapi.com"
                return headers
            }
        }

        queue.add(jsonObjectRequest)
    }

    private fun downloadReels(url:String,){
        try {
            var downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val videoUrl = Uri.parse(url)
            val request = DownloadManager.Request(videoUrl)
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE)
                .setMimeType("video/mp4")
                .setAllowedOverRoaming(false)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setTitle("Preet Developer")
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,File.separator + "Preet Developer" + ".mp4")
            downloadManager.enqueue(request)
            Toast.makeText(this,"Success",Toast.LENGTH_LONG).show()

        }catch (e:Exception){
            Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show()


        }



    }




}