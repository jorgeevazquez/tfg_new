package com.example.tfgnews

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.tfgnews.databinding.ActivityMainBinding
import com.example.tfgnews.databinding.NoticeCardBinding


class MainActivity : AppCompatActivity() {
    private var list = mutableListOf<NewsDataClass>()
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var recycler: NewsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        val model: MainViewModel by viewModels()

        mBinding.btnAdd.setOnClickListener {
            isTextEmpty()
            setupAdapter()
        }
    }

    fun setupAdapter() {
        recycler = NewsAdapter(list, this)
        mBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recycler
        }

    }
    fun isTextEmpty(){
        val text1 =  NewsDataClass(String())
        text1.notice = mBinding.etCard.text.toString()
        val text = text1.notice
        if (text.isEmpty()){
            Toast.makeText(this, "Empty Text", Toast.LENGTH_SHORT).show()
        }else
            list.add(text1)
    }
}








