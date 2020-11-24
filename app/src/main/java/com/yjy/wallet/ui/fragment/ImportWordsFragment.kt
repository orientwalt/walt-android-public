package com.yjy.wallet.ui.fragment

import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import com.weiyu.baselib.base.BaseFragment
import com.weiyu.baselib.util.StringUtils
import com.yjy.wallet.Constant
import com.yjy.wallet.R
import com.yjy.wallet.activity.AddrTypeActivity
import com.yjy.wallet.wordlist.Chinese_simplified
import com.yjy.wallet.wordlist.English
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.ResourceSubscriber
import kotlinx.android.synthetic.main.fragment_import_words.*
import java.util.concurrent.TimeUnit

/**
 *Created by weiweiyu
 *on 2019/4/29
 * 导入钱包
 */
class ImportWordsFragment : BaseFragment() {
    override fun getContentLayoutResId(): Int = R.layout.fragment_import_words
    var is2 = 0
    var pathArray: Array<String>? = null
    val e = English.words.toMutableList()
    val z = Chinese_simplified.words.toMutableList()
    override fun initializeContentViews() {

    }

    override fun viewCreated() {
        pathArray = resources.getStringArray(R.array.path_type)
//        setEditTextInhibitInputSpace(et_remark,Constant.NAME_SIZE)
        setEditTextInhibitInputSpace(et_pwd1, Constant.PWD_SIZE)
        setEditTextInhibitInputSpace(et_pwd2, Constant.PWD_SIZE)
        tv_language.text = pathArray!![is2]
        setEdittxtForHw(et_pwd1)
        setEdittxtForHw(et_pwd2)
        rl_choice.setOnClickListener {
            val intent = Intent(activity, AddrTypeActivity::class.java)
            intent.putExtra("type", 3)
            intent.putExtra("position", is2)
            startActivityForResult(intent, 101)
        }
        Flowable.interval(2000, TimeUnit.MILLISECONDS)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : ResourceSubscriber<Long>() {
                    override fun onComplete() {
                    }

                    override fun onNext(t: Long?) {
                        val input = word_il.getList1()
                        if(input.isNotEmpty()) {
                            var n: MutableList<String> = mutableListOf()
                            if (input.size == 1) {
                                n = if (StringUtils.isChinese(input[0])) {
                                    input.subtract(z).toMutableList()
                                } else {
                                    input.subtract(e).toMutableList()
                                }
                            } else {
                                if (e.intersect(input).isNotEmpty()) {
                                    n = input.subtract(e).toMutableList()
                                } else {
                                    if (z.intersect(input).isNotEmpty()) {
                                        n = input.subtract(z).toMutableList()
                                    }
                                }
                            }
                            word_il.setText(n)
                        }
                    }

                    override fun onError(t: Throwable?) {
                    }
                })
        et_pwd1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!TextUtils.isEmpty(et_pwd1.text)) {
                    rl_hint.visibility = View.VISIBLE
                    if (et_pwd1.text.length < 8) {
                        tv_pwd_hint.visibility = View.VISIBLE
                    } else {
                        tv_pwd_hint.visibility = View.INVISIBLE
                    }
                    val s = StringUtils.passwordStrong(et_pwd1.text.toString())
                    rb_type.progress = s
                    when (s) {
                        20 -> tv_type.text = resources.getStringArray(R.array.pwd_type)[0]
                        60 -> tv_type.text = resources.getStringArray(R.array.pwd_type)[1]
                        90 -> tv_type.text = resources.getStringArray(R.array.pwd_type)[2]
                    }
                } else {
                    rl_hint.visibility = View.GONE
                }
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 101) {
                if (data != null) {
                    setPath(data.getIntExtra("position", 0))
                }
            }
        }
    }

    fun getRemark(): String {
        return et_remark.text.toString()
    }

    fun getList(): String? {
        return word_il.getList()
    }

    fun getPwd(): String {
        return et_pwd1.text.toString().trim()
    }

    fun getPwd2(): String {
        return et_pwd2.text.toString()
    }

    fun setPath(p: Int) {
        is2 = p
        tv_language.text = pathArray!![is2]
    }
}