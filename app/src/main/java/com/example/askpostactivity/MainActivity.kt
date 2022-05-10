package com.example.askpostactivity

import android.Manifest
import android.Manifest.permission.CAMERA
import android.Manifest.permission_group.CAMERA
import android.content.Context
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater

import android.text.*

import android.webkit.*
import android.widget.*
import android.util.DisplayMetrics
import android.R.string.no
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import androidx.annotation.RequiresApi
import android.webkit.WebView
import androidx.core.widget.addTextChangedListener
import android.webkit.WebViewClient
import android.webkit.WebChromeClient
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import android.webkit.ValueCallback
import android.widget.Toast

import android.content.ActivityNotFoundException
import android.database.Cursor
import android.hardware.SensorPrivacyManager.Sensors.CAMERA
import android.media.MediaRecorder.VideoSource.CAMERA
import android.os.Environment.getExternalStorageDirectory
import android.webkit.WebChromeClient.FileChooserParams
import androidx.activity.result.contract.ActivityResultContracts

import androidx.core.app.ActivityCompat.startActivityForResult
import java.lang.Exception
import java.security.Permissions
import java.text.SimpleDateFormat
import java.util.*
import android.webkit.JavascriptInterface
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.net.toUri
import java.io.*
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import androidx.core.content.ContentProviderCompat.requireContext
import kotlin.collections.ArrayList


//코드 블록 사진 생길때, 없애고 webview 할때, edittext 할때, 처음 코드 블록이 생성됐을 때 index를 받아서 거따가 insert, delete 하기!



class MainActivity : AppCompatActivity() {

    var isCodeBlockClicked = 0;
    var firstClicked = 0;
    var cameraPath = ""
    var mWebViewImageUpload: ValueCallback<Array<Uri>>? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.askpostfragmentlayout)

        var body_webview = findViewById<WebView>(R.id.body_webview)
        body_webview.settings.javaScriptEnabled = true
        body_webview.settings.domStorageEnabled = true
        body_webview.settings.allowContentAccess = true
        body_webview.settings.allowContentAccess = true


        var weburl = "file:///android_asset/auto_highlight.html"

        class WebBrideg(private val mContext: Context) {
            @JavascriptInterface
            fun getwidth(): Float {
                var width: Float = 0.0f
                var linear = findViewById<LinearLayout>(R.id.body_box)
                width = linear.width.toFloat()
                return width
            }

            @JavascriptInterface
            fun showToast(code: String) {
                Toast.makeText(mContext, code, Toast.LENGTH_SHORT).show()
            }

            @JavascriptInterface
            fun getwidth(px: Float): Float {
                var resources = resources
                val metrics: DisplayMetrics = resources.getDisplayMetrics()
                val dp = px * (DisplayMetrics.DENSITY_DEFAULT / metrics.densityDpi.toFloat())
                return dp
            }

            @JavascriptInterface
            fun getText(innerHTML: String, changeText: String) {
                var myString = innerHTML
                var myText = changeText
                Log.d("qwe", myString)
                Log.d("asdf", myText)

            }

            @JavascriptInterface
            fun getHtml(html: String) {
                //위 자바스크립트가 호출되면 여기로 html이 반환됨
                var source = html
                Log.e("html: ", source)

            }

            @JavascriptInterface
            fun gettest() {
                Toast.makeText(mContext, "Asdfasdf", Toast.LENGTH_SHORT).show()
            }


        }





        body_webview.addJavascriptInterface(WebBrideg(this), "Android")
        body_webview.setWebViewClient(WebViewClient())





        body_webview?.apply {
            webViewClient = object : WebViewClient() {

                override fun onPageStarted(
                    view: WebView?,
                    url: String?,
                    favicon: Bitmap?
                ) {
                    super.onPageStarted(view, url, favicon)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)

                    runOnUiThread(Runnable {
                        Log.d("asdfasdfads", "Asdfasfsfewf")
                        body_webview.loadUrl("javascript:myupdate()")
                    })


                }
            }
        }


        fun createImageFile(): File? {
            val timeStap = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val imageFileName = "img_" + timeStap + "_"
            val storageDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            return File.createTempFile(imageFileName, ".jpg", storageDir)
        }

        var launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val intent = result.data

                    if (intent == null) {
                        val results = arrayOf(Uri.parse(cameraPath))

                        mWebViewImageUpload!!.onReceiveValue(results!!)
                    } else {
                        val results = intent!!.data!!
                        Log.d("씨발",results.toString())
                        mWebViewImageUpload!!.onReceiveValue(arrayOf(results!!))
                    }
                } else {
                    mWebViewImageUpload!!.onReceiveValue(null)
                    mWebViewImageUpload = null
                }
                Log.d("asdfasdf",result.data.toString())
                Log.d("리죨트2", result.toString())



            }

        body_webview.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {

                try {
                    mWebViewImageUpload = filePathCallback!!
                    var takePictureIntent: Intent?
                    takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    if (takePictureIntent.resolveActivity(packageManager) != null) {
                        var photoFile: File?
                        photoFile = createImageFile()
                        takePictureIntent.putExtra("PhotoPath", photoFile.toString())

                        if (photoFile != null) {
                            cameraPath = "file:${photoFile.absolutePath}"
                            takePictureIntent.putExtra(
                                MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile)
                            )
                        } else {
                            takePictureIntent = null
                        }

                    }
                    val contentSelectionIntent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    contentSelectionIntent.type = "image/*"
                    var intentArray: Array<Intent?>

                    if (takePictureIntent != null) {
                        intentArray = arrayOf(takePictureIntent)
                        Log.d("take",intentArray.toString())
                    } else {
                        intentArray = takePictureIntent?.get(0)!!
                    }

                    val chooserIntent = Intent(Intent.ACTION_CHOOSER)
                    chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
                    chooserIntent.putExtra(Intent.EXTRA_TITLE, "조수민진짜개멋있다")
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
                    launcher.launch(chooserIntent)
                } catch (e: Exception) {

                }
                return true
            }

        }


        body_webview.loadUrl(weburl)

        var test = findViewById<TextView>(R.id.test)
        test.setOnClickListener() {
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("wtwewt",data?.data.toString())

        val bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data?.data)
        Log.d("eifjskfsefs",bitmap.toString())
        saveImage(bitmap)
        if (requestCode == Activity.RESULT_OK ) {
            val pickedImage = data?.data
            //set the selected image to ImageView
            Log.d("eifjskfsefs",pickedImage.toString())
        }
    }



    fun uploadImageOnpage(resultCode: Int, intent : Intent?){
        if (resultCode == Activity.RESULT_OK){
            if(intent !=null){
                mWebViewImageUpload?.onReceiveValue(
                    WebChromeClient.FileChooserParams.parseResult(Activity.RESULT_OK,intent)
                )
                mWebViewImageUpload = null
            }
        }
        else{
            mWebViewImageUpload?.onReceiveValue(null)
            mWebViewImageUpload = null
        }
    }

    fun saveImage(bitmap: Bitmap){
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,bytes)

        val photoDirectory = File(getExternalStorageDirectory().absolutePath+"/cameraphoto/")
        if (!photoDirectory.exists()){
            photoDirectory.mkdirs()
        }
        val imgFile = File(photoDirectory, "${System.currentTimeMillis()}.jpg")
        Log.d("asfsfssef",imgFile.toString())
        val fo:FileOutputStream
//        try{
////            imgFile.createNewFile()
////            fo = FileOutputStream(imgFile)
////            fo.write(bytes.toByteArray())
////            fo.close()
//        }
//        catch (e: FileNotFoundException){
//            e.printStackTrace()
//        }
//        catch (e: IOException){
//            e.printStackTrace()
//        }

        uploadImageOnpage(Activity.RESULT_OK,Intent().apply {
            data = imgFile.toUri()
        })
    }

    fun captureImageResult(data : Uri?){

        if(data == null){
            mWebViewImageUpload?.onReceiveValue(null)
            mWebViewImageUpload = null

        }
        else {

            val bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data)
            saveImage(bitmap)

        }
    }

}













