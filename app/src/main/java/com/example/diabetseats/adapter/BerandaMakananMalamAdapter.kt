package com.example.diabetseats.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.diabetseats.data.response.HintsItem
import com.example.diabetseats.databinding.ItemMakananBerandaBinding
import com.example.diabetseats.local.entity.MakananEntity
import com.example.diabetseats.viewmodel.BerandaViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class BerandaMakananMalamAdapter(dataList: List<HintsItem>, private val berandaViewModel: BerandaViewModel,
                                 private val coroutineScope: CoroutineScope)
    : RecyclerView.Adapter<BerandaMakananMalamAdapter.BerandaMakananMalamViewHolder>() {
    private val dataList = ArrayList<MakananEntity>()

    fun setData(data: List<MakananEntity>) {
        this.dataList.clear()
        this.dataList.addAll(data)
        notifyDataSetChanged()
    }


    inner class BerandaMakananMalamViewHolder(val binding: ItemMakananBerandaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(makananItem: MakananEntity) {
            binding.apply {
                tvItemLabelMakananBeranda.text = makananItem.namaMakanan
                tvItemEnergiBeranda.text = "Energi: ${makananItem.kaloriMakanan} kkal"
                tvItemKarbohidratBeranda.text = "Karbohidrat: ${makananItem.karbohidratMakanan} g"
                tvItemLemakBeranda.text = "Lemak: ${makananItem.lemakMakanan} g"
                tvItemProteinBeranda.text = "Protein: ${makananItem.proteinMakanan} g"

                Glide.with(binding.root)
                    .load(makananItem.image)
                    .into(binding.imgItemFotoMakananBeranda)

                binding.btnFavoriteBeranda.setOnClickListener {
                    coroutineScope.launch {
                        berandaViewModel.deleteFood(makananItem)
                    }
                }
            }
        }

    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BerandaMakananMalamAdapter.BerandaMakananMalamViewHolder {
        val binding = ItemMakananBerandaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BerandaMakananMalamViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: BerandaMakananMalamAdapter.BerandaMakananMalamViewHolder,
        position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}