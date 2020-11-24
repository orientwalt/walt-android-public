package com.yjy.wallet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.yjy.wallet.R
import com.yjy.wallet.bean.Coin

class CoinAdapter(var mList: List<Coin>, var context: Context, var textColorRes: Int) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var v = LayoutInflater.from(context).inflate(R.layout.sp_item, parent, false)
        v.findViewById<TextView>(R.id.tv_text).text = getItem(position).unit.toUpperCase()
        v.findViewById<TextView>(R.id.tv_text).setTextColor(context.resources.getColor(textColorRes))
        return v
    }

    override fun getItem(position: Int): Coin {
        return mList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return mList.size
    }
}