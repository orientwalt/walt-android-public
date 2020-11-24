package com.yjy.wallet.bean

import org.litepal.crud.LitePalSupport

/**
 * weiweiyu
 * 2019/8/21
 * 575256725@qq.com
 * 13115284785
 */
class ERCBean(var name: String = "",
              var drawable: Int? = null,
              var address: String = "",
              var fullName: String = "") : LitePalSupport() {
    var open = false
    var decimal = 0
    var custom = false
    var type = 0
}