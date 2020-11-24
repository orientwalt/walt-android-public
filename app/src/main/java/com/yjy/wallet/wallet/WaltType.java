package com.yjy.wallet.wallet;

import com.yjy.wallet.R;

/**
 * weiweiyu
 * 2019/8/21
 * 575256725@qq.com
 * 13115284785
 */
public enum WaltType {
    htdf("OrientWalt", R.mipmap.htdf_icon),
    usdp("UP Digital Assets", R.mipmap.usdp_icon),
    ETH("Ethereum", R.mipmap.eth_icon),
    BTC("Bitcoin Core", R.mipmap.btc_icon),
    HET("Hetbi Token", R.mipmap.het_icon),
    USDT("Tether US", R.mipmap.ousdt_icon),
    BCH("Bitcoin Cash", R.mipmap.bch_icon),
    BSV("Bitcoin SV", R.mipmap.bsv_icon),
    LTC("Litecoin", R.mipmap.ltc_icon),
    DASH("Dash", R.mipmap.dash_icon),
    XRP("Ripple", R.mipmap.xrp_icon),
    TRX("TRON", R.mipmap.trx_icon),
    NEO("NEO", R.mipmap.neo_icon),
    CXC("CAPITAL X CELL", R.mipmap.cxc_icon),
    QTUM("QTUM", R.mipmap.qtum_icon),
    EOS("EOS", R.mipmap.eos_icon),
    ETC("Ethereum Classic", R.mipmap.etc_icon),
    XLM("Stellar Lumens", R.mipmap.xlm_icon);

    private String fall_name;
    private int drawable;

    WaltType(String fall_name, int drawable) {
        this.fall_name = fall_name;
        this.drawable = drawable;
    }

    public int getDrawable() {
        return drawable;
    }

    public String getFall_name() {
        return fall_name;
    }
}
