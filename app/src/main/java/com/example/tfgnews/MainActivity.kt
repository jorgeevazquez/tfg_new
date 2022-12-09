package com.example.tfgnews


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toFile
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tfgnews.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


enum class ProviderType{
    BASIC
}

class MainActivity : AppCompatActivity() {
    private var list = mutableListOf<NewsDataClass>()
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mAdapter: NewsAdapter
    private var uriCode: Uri? = null
    private lateinit var bitMap: Bitmap
    private val getcontent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                uriCode = uri
                /*
                lifecycleScope.launch {
                    val compress = Compressor.compress(this@MainActivity, uriCode!!.toFile(), Dispatchers.Main)
                    val compresed = BitmapFactory.decodeFile(compress.absoluteFile.toString())
                    bitMap = compresed
                }*/

                mBinding.btSelectImageFromGalery.setBackgroundResource(R.drawable.ic_check)
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


        model.listaNewsMutableLivedata.observe(this) {
            list = it
            mAdapter.updateAdapter(list)

        }

        mBinding.btSelectImageFromGalery.setOnClickListener {
            showGalleryPhone()


        }

        mBinding.btUploadImage.setOnClickListener {
            model.saveFireStorage(uriCode, mBinding)


        }

        mBinding.btnAdd.setOnClickListener {
            model.isTextEmptyDowloadImage(mBinding, list, this)
            initsetupAdapter()


        }
        mBinding.singOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }

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

