
package com.example.sampleproject.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.sampleproject.R
import java.io.File

internal class ImageAdapter(private val context: Context) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    private var selectedImagePath = mutableListOf<File>()
    private var selectedDelPath = mutableListOf<File>()
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val options = RequestOptions().placeholder(R.drawable.image_placeholder)
        .error(R.drawable.document)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(inflater.inflate(R.layout.item_image, parent, false))
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val uri = this.selectedImagePath[position]

        Glide.with(context)
            .load(uri)
            .apply(options)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.imageView)

        holder.checkbox.apply {
            holder.checkbox.setOnCheckedChangeListener(null)
            holder.checkbox.isChecked = false

            setOnCheckedChangeListener { _, isChecked  ->
                try {
                    if (!isChecked){
                        selectedDelPath.remove(selectedImagePath[position])
                    }else{
                        selectedDelPath.add(selectedImagePath[position])
                    }
                }catch (e: Exception){
                    Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return this.selectedImagePath.size
    }

    fun addSelectedImages(images: List<File>) {
        this.selectedImagePath = images as MutableList<File>
    }

    fun clearAllImage(){
        if (this.selectedImagePath.isNotEmpty()){
            this.selectedImagePath.clear()
            notifyItemRangeRemoved(0,this.selectedImagePath.size)
        }
    }

    fun clearSelectedImages() {
        if (this.selectedDelPath.isNotEmpty()){
            this.selectedDelPath.filter { it.isFile }
                .forEach {
                    if(removeItems(it)){
                        this.selectedDelPath.clear()
                    }}
        }
    }

    private fun removeItems(model: File): Boolean {
        val index = this.selectedImagePath.indexOf(model)
        val removed = this.selectedImagePath.remove(model)
        if (removed) {
            notifyItemRemoved(index)
            notifyItemRangeChanged(index, this.selectedImagePath.size)
        }
        return removed
    }

    internal class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.image_thumbnail)
        var checkbox: CheckBox = itemView.findViewById(R.id.checkBox)
    }

}

