package com.example.tfgnews


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tfgnews.databinding.ActivityMainBinding



class MainActivity : AppCompatActivity() {
    private var list = mutableListOf<NewsDataClass>()
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mAdapter: NewsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        val model: MainViewModel by viewModels()


        mBinding.btnAdd.setOnClickListener {
            model.isTextEmpty(mBinding, list, this)
            initsetupAdapter()
        }
    }
        fun initsetupAdapter() {
            mAdapter = NewsAdapter(list, this)
            mBinding.recyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = mAdapter
            }
        }
    }








