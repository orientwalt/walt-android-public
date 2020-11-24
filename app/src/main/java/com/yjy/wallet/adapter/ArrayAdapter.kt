package com.yjy.wallet.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.yjy.wallet.R
import com.yjy.wallet.bean.htdf.PoitInfo

class ArrayAdapter(var mList: List<PoitInfo>, var context: Context, var textColorRes: Int) : BaseAdapter() {
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var v = LayoutInflater.from(context).inflate(R.layout.adapter_sp_item, parent, false)
        val textView = v.findViewById<TextView>(R.id.text1)
        textView.text = getItem(position).description.moniker
        textView.setTextColor(context.resources.getColor(textColorRes))
        return v
    }

    override fun getItem(position: Int): PoitInfo {
        return mList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return mList.size
    }
}