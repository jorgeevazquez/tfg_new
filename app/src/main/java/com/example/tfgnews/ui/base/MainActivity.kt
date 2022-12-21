package com.example.tfgnews


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.tfgnews.data.NewsDataClass
import com.example.tfgnews.databinding.ActivityMainBinding
import com.example.tfgnews.ui.base.MainViewModel
import com.example.tfgnews.ui.base.NewsAdapter
import com.example.tfgnews.ui.profile.ProfileActivity
import com.google.firebase.auth.FirebaseAuth
import java.io.ByteArrayOutputStream


enum class ProviderType{
    BASIC
}

class MainActivity : AppCompatActivity() {
    private var list = mutableListOf<NewsDataClass>()
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mAdapter: NewsAdapter
    private var uriCode: Uri? = null
    //private lateinit var byteArray: ByteArray
    private val getcontent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                uriCode = uri
                Glide.with(mBinding.btSelectImageFromGalery)
                    .load(uri)
                    .circleCrop()
                    .into(mBinding.btSelectImageFromGalery)

                //Compresión funciona pero rota las fotos.
                /*val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uriCode)
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream)
                byteArray = stream.toByteArray()*/
                //mBinding.btSelectImageFromGalery.setBackgroundResource(R.drawable.ic_check)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val model: MainViewModel by viewModels()
        initsetupAdapter()
        model.getAllImagesMain(list)
        mBinding.btnAdd.isEnabled = true


        model.listaNewsMutableLivedata.observe(this) {
            list = it
            mAdapter.updateAdapter(list)
        }


        mBinding.btSelectImageFromGalery.setOnClickListener {
            showGalleryPhone()
        }

        /*mBinding.btUploadImage.setOnClickListener {
           // model.saveFireStorage(uriCode, mBinding)
        }*/

        mBinding.btnProfileFragment.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        mBinding.btnAdd.setOnClickListener {
            val text = mBinding.etCard.text?.trim()
            if (text.isNullOrEmpty()) {
                Toast.makeText(this, "Empty text", Toast.LENGTH_SHORT).show()
            }else{
                model.saveFireStorage(uriCode, mBinding, list, this)
                initsetupAdapter()
                uriCode = null
                mBinding.btnAdd.isEnabled = true

            }
            mBinding.btSelectImageFromGalery.setImageResource(R.drawable.ic_image_search)
            mBinding.btSelectImageFromGalery.setBackgroundColor(resources.getColor(R.color.white))
        }

        mBinding.singOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }
        println(list)
    }

    private fun initsetupAdapter() {
        mAdapter = NewsAdapter(list, this)
        mBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }
    }

    private fun showGalleryPhone() {
        getcontent.launch("image/*")
    }

    private fun setAccessUpdateButton() = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            /* val text = mBinding.etCard.text?.trim()
            if (text != null) {
                mBinding.btnAdd.isEnabled = text.isNotEmpty()
            }*/
        }

        override fun afterTextChanged(p0: Editable?) {
        }
    }

}

