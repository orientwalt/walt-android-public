package com.yjy.wallet.wallet;

import com.yjy.wallet.R;

import java.math.BigDecimal;

/**
 * weiweiyu
 * 2019/8/21
 * 575256725@qq.com
 * 13115284785
 */
public enum HTDFERCType {
    HFH(18, "htdf1vek5hjdqhcgj8ljqewgcex8y9eg9agwa3f69ez", "火凤凰", R.mipmap.hfh_icon),
    CVS(18, "htdf1dp4n20f7tksdxeugqfgvclx5u6lzkx52w53vrm", "彩威斯", R.mipmap.cvs_icon),
    BWC(18, "htdf1j8apljndmma2vjxvqtz4rd6dk3fe0yuyyakhun", "币维链", R.mipmap.bwc_icon),
    BZC(18, "htdf1g9azjd3f9qc6sqyqx0s6a7fmppx83x4dcg62nd", "珠宝币", R.mipmap.bzc_icon),
    CKC(18, "htdf12z83gpdq0wfhkqwzarvnq5actnws24pnkhecfr", "创客空间", R.mipmap.ckc_icon),
    AKC(18, "htdf1yxlk2j9uv5m8xlqm3vru5fjs4phtqy20099wta", "传奇链", R.mipmap.akc_icon),
    BLC(18, "htdf1ptj3ecrmjmupquyk9ksujz9ckjyd6x0geu000e", "宝来币", R.mipmap.blc_icon),
    CBV(18, "htdf1c8yv7nmzzqf8ljxz7z06zl9e0mzdcflgtwvvpy", "世优链", R.mipmap.cbv_icon),
    CXM(18, "htdf1zhx5huxpntmhzz42y24nf6guxpwp749waldgyt", "贝宝链", R.mipmap.cxm_icon),
    DDUC(18, "htdf179ndvnuaalw43gy83ng3pwksgeg0wdt22kzsex", "天天链", R.mipmap.dduc_icon),
    BOSS(18, "htdf1c5ennhmh9u2yp9g2c5l64sw4e8yl3dk78uq5sa", "领袖币", R.mipmap.boss_icon),
    DFD(18, "htdf1kupppj7hxv27qpglcuhqaqyv2dgau6m6rw4q4a", "达发币", R.mipmap.dfd_icon),
    CBH(18, "htdf1u2wh0t752kvg2jdr07w800ynmgce0c6vw0zf9l", "雏丙链", R.mipmap.cbh_icon),
    UKC(18, "htdf1fpmq3yg3rkzg6wqzr830p825rzt30n5qq45zs2", "优库币", R.mipmap.ukc_icon),
    LS(18, "htdf1l04kz2vqf8zsemyey2cs82et567ucmfgcv4ysu", "劳力士", R.mipmap.ls_icon),
    BTCC(18, "htdf15rgpkka5dp9ng4j4rw7qx7ncaaympgjzxa0ety", "比特量子", R.mipmap.btcc_icon),
    BTCD(18, "htdf1eh3k2a884msn2ejzuu4ju82dhq08r9mrk78h8z", "比特钻石", R.mipmap.btcd_icon),
    DDV(18, "htdf1alefj7fd8hj22k6xz8d2sf45rwgn3mj8kvv7de", "东东", R.mipmap.ddv_icon),
    USDP(18, "htdf14mc59zt5r2v0u40vdjec64323ypp08lslqc9uv", "UP Digital Assets", R.mipmap.usdp_icon),
    HET(18, "htdf1war5w36s3ym6qv2vnfd7k803wu3lkuwksh8md8", "Hetbi Token", R.mipmap.het_icon),
    BEI(18, "htdf1rcl9x2akjuy3tyqyxwk7sataldzmxy6jm06hq4", "贝唯币", R.mipmap.bei_icon),
    AQC(18, "htdf1lsj9xuxyczru272je6wjd9aeg8vkg23qae5ug2", "达扶币", R.mipmap.aqc_icon),
    BKL(18, "htdf15xsj43lssr26kw0pxn9qrl3yau2n5wu4dhk5rp", "客洛币", R.mipmap.bkl_icon),
    SJC(18, "htdf1f23nuwptf7rqns3f73lu9xpseql0je8twn2zuk", "垒三金", R.mipmap.sjc_icon),
    XL(18, "htdf1kw4hncahfd2n2xlymw036zcecln6r83vn80ts3", "星链", R.mipmap.xl_icon),
    BTU(18, "htdf1w43aazq9sjlcrwj4y7nnsck7na5zljzu4x5nrq", "比特果", R.mipmap.btu_icon),
    SVU(18, "htdf18y0ks24fat9unc4ezf83zvqu470dtrsvldyev7", "易尔链", R.mipmap.svu_icon),
    JXC(18, "htdf1u9cwqeq4tcnar8y8xk8udn5mll3dt5t8xlxqt0", "金项链", R.mipmap.jxc_icon),
    KL168(18, "htdf14ar3e0z26zakn8l6skmd5085rvr6f8e8j8zjnw", "快拉", R.mipmap.l168_icon),
    AGG(18, "htdf18xq0p7h8rwxykfyrfytylhmrtusmzfwst4s6jx", "暗波链", R.mipmap.agg_icon),
    HHG(18, "htdf16kz9ts938wramc4gtn8fz8stnha6nm64w5tra8", "华特黄金", R.mipmap.hhg_icon),
    HTD(18, "htdf1le2awsf84s37ar4fx9wc8ruwsljgrrzz8agzsn", "华特股", R.mipmap.htd_icon),
    KML(18, "htdf1ehhd5qugg7y02rdnvlxcy92qg02m6dqhttauvh", "科莫搭", R.mipmap.kml_icon),
    MSL(18, "htdf1ldkdf84lr0qeydcp6la90hvcww5hx0z4u5zvja", "莫斯卡", R.mipmap.msl_icon),
    DFQ(18, "htdf10yxugqes29eahqk227zv0uayt0qedvzaxl24jy", "逹分歧", R.mipmap.dfq_icon),
    TTB(18, "htdf1kg362pxynmkvvr5cutwf5ksp5qh7lhpzrxesp9", "泰坦币", R.mipmap.ttb_icon),
    SNA(18, "htdf138e0ej2prf7w88ry26jn30a5skkd6l0k75el29", "水牛链", R.mipmap.sna_icon),
    DBD(18, "htdf1rzr0z33v0lzp8r6mumpszg7vyfgsud3asquq7m", "待比特", R.mipmap.dbd_icon),
    KLI(18, "htdf1ea8nag24mhcr0gn4dhyzzkxpvz09gqlkhx4ezc", "凯莉", R.mipmap.kli_icon),
    HHA(18, "htdf1vnup9a5m4xlqmw63dwctq2hzzvvxm4yyltc8dn", "华特钻石", R.mipmap.hha_icon),
    HBB(18, "htdf1u0hxwgjawmwfdxwzaq9g4ucg00026cvvq02jh4", "化比特", R.mipmap.hbb_icon),
    KYL(18, "htdf14f89zkgentdxy0gp52t7m94uw9uvrhrtky9sh8", "刻意浓", R.mipmap.kyl_icon),
    SKL(18, "htdf1px357q74nhc4w9sf0wah4n95dhte5m2g2lpyah", "苏卡币", R.mipmap.skl_icon),
    LTU(18, "htdf1v3efne0lc769mr94dwp47j3jzakmsqyyghf2yp", "立特币", R.mipmap.ltu_icon),
    FTL(18, "htdf1xhqazar7ly5e2zygucempq6y7ajmvdhtjyavhn", "飞天链", R.mipmap.ftl_icon),
    HGL(18, "htdf1nyu8tju9u7y2h0pzlyd2kf89kx3kn4hzhhgamf", "花火币", R.mipmap.hgl_icon),
    HRB(18, "htdf1y296rsaezn26g9c695tfst96lhlghtsyrxkdv2", "好日子", R.mipmap.hrb_icon),
    XYB(18, "htdf102vhrdfzmhu99pyhwg0d3hgas7h4zfk9w0z59v", "向阳币", R.mipmap.xyb_icon),
    BYL(18, "htdf1r3nya0n7zkjrm3m6ame432qwnvmzcrwsu20tdr", "奔月币", R.mipmap.byl_icon),
    GZC(18, "htdf1l54khftk7kxr3g9jja8a7m7tq5ak8zyx9egrnc", "古峰币", R.mipmap.gzc_icon),
    QLL(18, "htdf1uhl0ulcgq9egwyd73lmmzpsldph0tvzjs5l6v9", "丘零币", R.mipmap.qll_icon),
    SFB(18, "htdf1zv0h69vk5gv750rkqp6rphdqxp0m3hej35haac", "斯芙币", R.mipmap.sfb_icon),
    SDB(18, "htdf1gyn4hs0qjyy6kw8paufxgcm0g6hw2a0w0mkv0a", "法典链", R.mipmap.sdb_icon),
    MYL(18, "htdf1zmq40c5f4wfdgqkz2gtrxzjtt9lpqtdynp82z5", "陌友币", R.mipmap.myl_icon),
    TZL(18, "htdf15kmxwh9jdx2e45m4zn5z4tgn5gwtrzsn934tt9", "太子币", R.mipmap.tzl_icon),
    DFW(18, "htdf1ms5hrdramkgnx74f0r9lu0c6sfk5rn6gyadyrw", "道夫币", R.mipmap.dfw_icon),
    SMB(18, "htdf16vd93xvya6effupe0h8wpsu2286c3nt0g9hem0", "什码币", R.mipmap.smb_icon);


    private BigDecimal weiFactor;
    private int factor;
    private String address;
    private String fall_name;
    private int drawable;

    HTDFERCType(int factor, String address, String fall_name, int drawable) {
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

    public static HTDFERCType fromAddress(String address) {
        if (address != null) {
            for (HTDFERCType unit : HTDFERCType.values()) {
                if (address.equalsIgnoreCase(unit.address)) {
                    return unit;
                }
            }
        }
        return null;
    }
}