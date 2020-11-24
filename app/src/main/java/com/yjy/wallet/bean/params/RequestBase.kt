package com.yjy.wallet.bean.params

class RequestBase {
    var jsonrpc: String = "1.0"
    var id: String = "curltest"
    var method: String? = ""
    var params: String? = ""
}