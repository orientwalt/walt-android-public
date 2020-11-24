package com.yjy.wallet.bean.waltbean

data class FeedBackItem(
                        val question_type: Int = 0,
                        val title: String = "",
                        val text: String = "",
                        val type: Int = 0,
                        val userid: Int = 0,
                        val id: Int = 0,
                        var param:ArrayList<FeedBackItem2>)