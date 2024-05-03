package com.example.diabetseats.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.diabetseats.databinding.ItemMakananBinding
import com.example.diabetseats.data.response.HintsItem
import com.bumptech.glide.Glide
import com.example.diabetseats.R
import com.example.diabetseats.local.entity.MakananEntity
import com.example.diabetseats.local.entity.UserEntity
import com.example.diabetseats.viewmodel.MakananViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MakananAdapter(dataList: List<MakananEntity>, private val makananViewModel: MakananViewModel, private val coroutineScope: CoroutineScope) : RecyclerView.Adapter<MakananAdapter.MakananViewHolder>() {
    private var dataList = emptyList<HintsItem>()



    fun setData(data: List<HintsItem>) {
        dataList = data
        notifyDataSetChanged()
    }

    inner class MakananViewHolder(val binding: ItemMakananBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HintsItem) {
            binding.apply {
                tvItemLabelMakanan.text = item.food.label
                tvItemEnergi.text = "Energi: ${item.food.nutrients.eNERCKCAL} kkal"
                tvItemKarbohidrat.text = "Karbohidrat: ${item.food.nutrients.cHOCDF} g"
                tvItemLemak.text = "Lemak: ${item.food.nutrients.fAT} g"
                tvItemProtein.text = "Protein: ${item.food.nutrients.pROCNT} g"

                val foodImage = item.food.image ?: "" // Nilai default jika image null
                val foodEntity = MakananEntity(
                    0,
                    item.food.label.toString(),
                    item.food.nutrients.eNERCKCAL,
                    item.food.nutrients.pROCNT,
                    item.food.nutrients.fAT,
                    item.food.nutrients.cHOCDF,
                    foodImage // Menggunakan foodImage yang sudah dicek nullity
                )


                binding.btnFavorite.setOnClickListener {
                    coroutineScope.launch {
                        makananViewModel.insertFood(foodEntity)
                    }
                }

                Glide.with(itemView)
                    .load(item.food.image)
                    .into(imgItemFotoMakanan)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MakananViewHolder {
        val binding = ItemMakananBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MakananViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MakananViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }


}
