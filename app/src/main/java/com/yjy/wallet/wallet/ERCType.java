package com.yjy.wallet.wallet;

import com.yjy.wallet.R;

import java.math.BigDecimal;

/**
 * weiweiyu
 * 2019/8/21
 * 575256725@qq.com
 * 13115284785
 */
public enum ERCType {
    USDT(6, "0xdac17f958d2ee523a2206206994597c13d831ec7", "Tether USD", R.mipmap.eusdt_icon),
    BNB(18, "0xB8c77482e45F1F44dE1745F52C74426C631bDD52", "BNB", R.mipmap.bnb_icon),
    LEO(18, "0x2af5d2ad76741191d15dfe7bf6ac92d4bd912ca3", "Bitfinex LEO Token", R.mipmap.leo_icon),
    LINK(18, "0x514910771af9ca656af840dff83e8264ecf986ca", "ChainLink Token", R.mipmap.link_icon),
    MKR(18, "0x9f8f72aa9304c8b593d555f12ef6589cc3a579a2", "Maker", R.mipmap.mkr_icon),
    USDC(6, "0xa0b86991c6218b36c1d19d4a2e9eb0ce3606eb48", "USD Coin", R.mipmap.usdc_icon),
    CRO(8, "0xa0b73e1ff0b80914ab6fe0444e65848c4c34450b", "Crypto.com Chain", R.mipmap.cro_icon),
    INO(0, "0xc9859fccc876e6b4b3c749c5d29ea04f48acb74f", "Ino Coin", R.mipmap.ino_icon),
    VEN(18, "0xd850942ef8811f2a866692a623011bde52a462c1", "VeChain", R.mipmap.ven_icon),
    HT(18, "0x6f259637dcd74c767781e37bc6133cd6a68aa161", "HuobiToken", R.mipmap.ht_icon),
    OKB(18, "0x75231f58b43240c9718dd58b4967c5114342a86c", "OKB", R.mipmap.okb_icon),
    BJC(18, "0xb618a25e74d77a51d4b71ec1dff80c6686be9d0a", "hundred chain", R.mipmap.bjc_icon),
    BEI(18, "0xe4189a5d4174ac6177b406aae8c79228a4ebe02f", "Beiwei chain", R.mipmap.bei_icon),
    SPQQ(18, "0x4b351c83c121cbbf337dbd52b104b014e4999237", "SPQQ chain", R.mipmap.spqq_icon),
    ECNY(18, "0x6dece8681928fb7feccf4c36c1f66852bf53285f", "EthereumChina Yuan", R.mipmap.ecny_icon),
    ZIL(12, "0x05f4a42e251f2d52b8ed15e9fedaacfcef1fad27", "Zilliqa", R.mipmap.zil_icon),
    ELF(18, "0xbf2179859fc6d5bee9bf9158632dc51678a4100e", "ælf", R.mipmap.elf_icon),
    XMX(8, "0x0f8c45b896784a1e408526b9300519ef8660209c", "XMAX", R.mipmap.xmx_icon),
    TNB(18, "0xf7920b0768ecb20a123fac32311d07d193381d6f", "Time New Bank", R.mipmap.tnb_icon),
    OMG(18, "0xd26114cd6EE289AccF82350c8d8487fedB8A0C07", "OmiseGO", R.mipmap.omg_icon),
    LOOM(18, "0xa4e8c3ec456107ea67d3075bf9e3df3a75823db0", "Loom", R.mipmap.loom_icon),
    MCO(8, "0xb63b606ac810a52cca15e44bb630fd42d8d1d83d", "MCO", R.mipmap.mco_icon),
    CVC(8, "0x41e5560054824ea6b0732e656e3ad64e20e94e45", "Civic", R.mipmap.cvc_icon),
    REP(18, "0x1985365e9f78359a9B6AD760e32412f4a445E862", "Reputation", R.mipmap.rep_icon),
    CTXC(18, "0xea11755ae41d889ceec39a63e6ff75a02bc1c00d", "Cortex Coin", R.mipmap.ctxc_icon),
    ABT(18, "0xb98d4c97425d9908e66e53a6fdf673acca0be986", "ArcBlock", R.mipmap.abt_icon),
    PPT(8, "0xd4fa1460f537bb9085d22c7bccb5dd450ef28e3a", "Populous", R.mipmap.ppt_icon),
    FSN(18, "0xd0352a019e9ab9d757776f532377aaebd36fd541", "Fusion", R.mipmap.fsn_icon),
    REQ(18, "0x8f8221afbb33998d8584a2b05749ba73c37a938a", "Request", R.mipmap.req_icon),
    TNT(8, "0x08f5a9235b08173b7569f83645d2c7fb55e8ccd8", "Tierion Network Token", R.mipmap.tnt_icon),
    ODEM(18, "0xbf52f2ab39e26e0951d2a02b49b7702abe30406a", "ODEM Token", R.mipmap.odem_icom),
    LILY(18, "0xcDe4b9531eea02E1a5a4cD596790661C1c181ca7", "LILY FOUNDATION LTD", R.mipmap.lily_icon),
    EUTD(18, "0xD8E97CC29552CEE2BCA5BD7F9e979c48Ae6076d6", "EUTD chain", R.mipmap.eutd_icon),
    KTV(18, "0xf6da9a11c213c6039b8d56f3a3eba8a692d6afdc", "token nightclubKTV", R.mipmap.ktv_icon),
    AJC(18, "0x38362bbc51086eb738123602059983201ef55631", "AJC chain", R.mipmap.ajc_icon),
    HPT(18, "0xa66daa57432024023db65477ba87d4e7f5f95213", "HuobiPoolToken", R.mipmap.hpt_icon);


    private BigDecimal weiFactor;
    private int factor;
    private String address;
    private String fall_name;
    private int drawable;

    ERCType(int factor, String address, String fall_name, int drawable) {
        this.weiFactor = BigDecimal.TEN.pow(factor);
        this.factor = factor;
        this.address = address;
        this.fall_name = fall_name;
        this.drawable = drawable;
    }

    public int getDrawable() {
        return drawable;
    }

    public int getFactor() {
        return factor;
    }

    public String getFall_name() {
        return fall_name;
    }

    public BigDecimal getWeiFactor() {
        return weiFactor;
    }


    public String getAddress() {
        return address;
    }

    public static ERCType fromAddress(String address) {
        if (address != null) {
            for (ERCType unit : ERCType.values()) {
                if (address.equalsIgnoreCase(unit.address)) {
                    return unit;
                }
            }
        }
        return null;
    }
}
