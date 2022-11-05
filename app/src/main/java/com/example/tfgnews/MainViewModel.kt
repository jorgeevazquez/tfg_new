package com.example.tfgnews

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.tfgnews.databinding.ActivityMainBinding

class MainViewModel: ViewModel() {


    fun isTextEmpty( binding: ActivityMainBinding, list: MutableList<NewsDataClass>,context: Context){
        val text1 =  NewsDataClass(String(),String())
        text1.notice = binding.etCard.text.toString()
        val text = text1.notice
        if (text.isEmpty()){
            Toast.makeText(context, "Empty Text", Toast.LENGTH_SHORT).show()
        }else
            list.add(text1)
    }
}

