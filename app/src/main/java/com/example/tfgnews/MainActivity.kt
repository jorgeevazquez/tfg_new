package com.example.tfgnews


import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tfgnews.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

enum class ProviderType{
    BASIC
}



class MainActivity : AppCompatActivity() {
    private var list = mutableListOf<NewsDataClass>()
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mAdapter: NewsAdapter
    private var uriCode: Uri? = null
    private val getcontent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
             uriCode = uri

        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        //setup

        val model: MainViewModel by viewModels()

        model.listaNewsMutableLivedata.observe(this){
            list = it
            mAdapter.updateAdapter(list)
        }

        mBinding.btGetImage.setOnClickListener {
            showGalleryPhone()
        }

        mBinding.btnAdd.setOnClickListener {
            model.saveFireStorage(list,uriCode,mBinding)
           // model.isTextEmpty(mBinding, list, this)
           // model.saveFireBase(list)
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
        mBinding.tvImage.text = uriCode.toString()

    }

    }

