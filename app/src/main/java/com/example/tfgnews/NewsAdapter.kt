package com.example.tfgnews

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.example.tfgnews.databinding.NoticeCardBinding
// La clase NewsAdapter extiende de RecyclerView.adapter
class NewsAdapter(private var news: MutableList<NewsDataClass>, private val context: Context)
    :RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var mBinding: NoticeCardBinding = NoticeCardBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Creamos una vista, que define la interfaz con los items
        val view = LayoutInflater.from(parent.context).inflate(R.layout.notice_card,parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //este metodo asocia la vista con los datos, debe contener un holder y la position
        val textNew = news.get(position)
        holder.mBinding.tvCard.text = textNew.notice
       // holder.mBinding.imgCard.setImageURI(textNew.image) Glide
       Glide.with(holder.mBinding.imgCard).load(textNew.image).into(holder.mBinding.imgCard)
       //GlideApp.with(holder.mBinding.imgCard).load(textNew.image).into(holder.mBinding.imgCard)


    }
    // Get element from your dataset at this position and replace the
    // contents of the view with that element


    override fun getItemCount(): Int {
        //Mutable.Size
        return news.size
    }
    fun updateAdapter(listNueva: MutableList<NewsDataClass>) {
        news = listNueva
        notifyDataSetChanged()
    }

    }

