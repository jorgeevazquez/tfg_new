package com.developer.tfgnews.ui.base


import android.Manifest
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.developer.tfgnews.data.NewsDataClass
import com.developer.tfgnews.databinding.ActivityMainBinding
//import com.developer.tfgnews.databinding.ActivityMainBinding

import com.developer.tfgnews.R
import com.developer.tfgnews.ui.profile.ProfileActivity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.auth.FirebaseAuth
import java.io.ByteArrayOutputStream
import java.io.File


enum class ProviderType{
    BASIC
}

class MainActivity : AppCompatActivity() {
    private var unloaledAd = true
    private var mInterstitialAd: InterstitialAd? = null
    private var list = mutableListOf<NewsDataClass>()
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mAdapter: NewsAdapter
    private var uriCode: Uri? = null
    private  var byteArray: ByteArray? = null
    private lateinit var file: File
    private val getcontent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                Glide.with(mBinding.btSelectImageFromGalery)
                    .load(uri)
                    .circleCrop()
                    .into(mBinding.btSelectImageFromGalery)
                uriCode = uri

                //Compresión funciona pero rota las fotos.
                if(Build.VERSION.SDK_INT > 28){
                val source = ImageDecoder.createSource(this.contentResolver, uriCode!!)
                val stream = ByteArrayOutputStream()
                val decorde = ImageDecoder.decodeBitmap(source)
                decorde.compress(Bitmap.CompressFormat.JPEG, 15, stream)
                byteArray = stream.toByteArray()

            }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        val model: MainViewModel by viewModels()
        model.setAuthUser()
        initSetupAdapter()
        model.getAllImagesMain(list, this)
        initAds()

        mBinding.btnAdd.isEnabled = true


        mBinding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0){
                    mBinding.btnAdd.shrink()
                }else{
                    mBinding.btnAdd.extend()
                }
            }
        })


        model.listaNewsMutableLivedata.observe(this) {
            list = it
            mAdapter.updateAdapter(list)
        }

        mBinding.btSelectImageFromGalery.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                //Permiso no aceptado
                requestReadPermission()
            }else {
                showGalleryPhone()
                getReadyAds()
            }
        }

        mBinding.btnProfileFragment.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            val option = ActivityOptions.makeCustomAnimation(this,
                R.anim.slide_anim,
                R.anim.slide_anim_exit).toBundle()
            startActivity(intent, option)
        }

        mBinding.btnAdd.setOnClickListener {
            val text = mBinding.etCard.text?.trim()
            if (text.isNullOrEmpty()) {
                Toast.makeText(this, "Empty text", Toast.LENGTH_SHORT).show()
            }else{
                model.saveFireStorage(byteArray, uriCode, mBinding, list, this)
                initSetupAdapter()
                uriCode = null
                byteArray = null
                mBinding.btnAdd.isEnabled = true
                mBinding.btSelectImageFromGalery.setImageResource(R.drawable.icons8imagen)
                showInterstitial()
                //Log.i("Item", "Item Añadido")
            }

        }

        mBinding.singOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }
    }

    private fun initSetupAdapter() {
        mAdapter = NewsAdapter(list, this)
        mBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
                .apply { stackFromEnd = true }
            adapter = mAdapter

        }
    }
    private fun showGalleryPhone() {
        getcontent.launch("image/*")
    }

   // Google Ads
    private fun initAds(){
        MobileAds.initialize(this)
    }
    private fun showInterstitial(){
        if (mInterstitialAd != null) {
            unloaledAd = true
            mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
                override fun onAdClicked() {
                    // Called when a click is recorded for an ad.
                   // Log.d(TAG, "Ad was clicked.")
                }

                override fun onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.
                    //Log.d(TAG, "Ad dismissed fullscreen content.")
                    mInterstitialAd = null
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    // Called when ad fails to show.
                    //Log.e(TAG, "Ad failed to show fullscreen content.")
                    mInterstitialAd = null
                }

                override fun onAdImpression() {
                    // Called when an impression is recorded for an ad.
                    //Log.d(TAG, "Ad recorded an impression.")
                }

                override fun onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                    //Log.d(TAG, "Ad showed fullscreen content.")
                }
            }
            mInterstitialAd?.show(this)
        } else {
            //Log.d("TAG", "The interstitial ad wasn't ready yet.")
        }
    }
    private fun getReadyAds(){
        var adRequest = AdRequest.Builder().build()
        unloaledAd = false

        //Test Code Add
        //InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest, object : InterstitialAdLoadCallback() {
        //Code Official Add
        InterstitialAd.load(this,"ca-app-pub-3930154479762716/6273170828", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
               // Log.d(TAG, adError?.toString())
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                //Log.d(TAG, 'Ad was loaded.')
                mInterstitialAd = interstitialAd
            }

        })
    }



    //PERMISOS
    private fun requestReadPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            //El usuario ya ha rechazado los permisos
            Toast.makeText(this, "Permiso no aceptado, Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        }else{
            //Pedir permisos
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 777)
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 777){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                showGalleryPhone()
            }else{
                Toast.makeText(this, "Permiso no aceptado, Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}



