package com.generator.wildfyre.activity

import android.annotation.SuppressLint
import android.app.ActionBar
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.generator.pog.services.IdleChecker
import com.generator.wildfyre.R
import com.generator.wildfyre.defaultSettings
import com.generator.wildfyre.dialog.AddUrlDialog
import com.generator.wildfyre.dialog.CloseDialog
import com.generator.wildfyre.dialog.UrlCheckerDialog
import com.generator.wildfyre.enum.WebOpenerDB
import com.generator.wildfyre.events.AddUrlEvent
import com.generator.wildfyre.events.IdleCheckerEvent
import com.generator.wildfyre.local_db.DatabaseHandler
import com.generator.wildfyre.model.URLData
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_action_bar.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.startActivity
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    var db = DatabaseHandler(this)
    var urlData: MutableList<URLData.Details> = ArrayList()
    var total = 0
    var closeDialog = CloseDialog()
    lateinit var urlCheckerDialog: UrlCheckerDialog

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        actionBar?.hide()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        urlCheckerDialog = UrlCheckerDialog(this)
        try {
            supportActionBar?.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM)
            supportActionBar?.setCustomView(R.layout.custom_action_bar)
            action_bar_title.setText("WILDFYRE Generator")
            action_bar_subtitle.setText("v.20220709.1")
            //WILDFYRE_20220709.1
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        bind()
    }

    override fun onStart() {
        super.onStart()
        println("=============onStart")
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        start.imageResource = R.drawable.round_play_button
        startService(Intent(this, IdleChecker::class.java))
    }

    override fun onStop() {
        super.onStop()
        stopService(Intent(this,IdleChecker::class.java))
    }

    private fun bind() {
        checkDataBase()
        setClickEvent()
    }

    private fun setClickEvent() {
        addURL.setOnClickListener {
            addURLEditText(null)
            if (total == 25) {
                addURL.isEnabled = false
            }
        }

        start.setOnClickListener {
            start.imageResource = R.drawable.pause
            saveUrl()
            saveFactor()
            startActivity<WordpressLoaderActivity>()
        }

        shuffleBtn.setOnClickListener {
            shuffleUrl()
        }

        arrangeBtn.setOnClickListener {
            var tempData = urlData.filter { item ->
                item.url != ""
            }.sortedBy { item -> item.url }
            removeUrlView()
            tempData.forEach { item ->
                addURLEditText(item.url)
            }
        }

        resetBtn.setOnClickListener {
            removeUrlView()
            defaultSettings?.url?.forEach { item ->
                addURLEditText(item)
            }
        }

        addBtn.setOnClickListener {
            AddUrlDialog().show(this)
        }
    }

    private fun saveFactor() {
        db.deleteDatabase(WebOpenerDB.TABLE_FACTOR.getValue())
        db.insertFactor(factor.text.toString())
    }

    private fun saveUrl() {
        db.deleteDatabase(WebOpenerDB.TABLE_URL.getValue())
        urlData.forEach { item ->
            db.insertURL(item.url!!)
        }
    }

    private fun shuffleUrl() {
        var shuffledUrl = urlData.shuffled()
            .filter { data ->
                data.url != ""
            }
        removeUrlView()
        shuffledUrl.forEach { item ->
            addURLEditText(item.url)
        }
    }

    private fun removeUrlView() {
        urlData.clear()
        urlCon.removeAllViews()
    }

    private fun checkDataBase() {
        checkURL()
        checkFactor()
    }

    private fun checkFactor() {
        var data = db.getFactor()
        if (data == 0) {
            factor.setText(defaultSettings?.factor)
        } else {
            factor.setText(data.toString())
        }
    }

    private fun checkURL() {
        var data = db.getURL()
        if (data.isEmpty()) {
            defaultSettings!!.url.forEach { item ->
                addURLEditText(item)
            }
        } else {
            data.forEach { item ->
                addURLEditText(item)
            }
        }
    }

    private fun addURLEditText(url: String?) {
        addURL.text = "add URL (${++total})"
        var editText = EditText(this)
        var horizontalCon = LinearLayout(this)
        var removeBtn = ImageView(this)
        var layout = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        editText.layoutParams = layout
        editText.id = View.generateViewId()
        editText.hint = "Enter URL"
        editText.setText(url)
        var data = URLData.Details(url, editText.id.toString())
        urlData.add(data)
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                checkArray(editText.id.toString(), s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })

        horizontalCon.layoutParams = layout
        horizontalCon.orientation = LinearLayout.HORIZONTAL
        horizontalCon.id = View.generateViewId()

        var imageLayout = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        imageLayout.setMargins(0,20,10,-10)

        removeBtn.layoutParams = imageLayout
        removeBtn.setImageResource(R.drawable.ic_baseline_delete_24)
        removeBtn.setPadding(0,0,0,0)

        removeBtn.setOnClickListener {
            var tempData = urlData.filter { item ->
                item != data
            }
            removeUrlView()
            tempData.forEach { item ->
                addURLEditText(item.url)
            }
        }

        horizontalCon.addView(removeBtn)
        horizontalCon.addView(editText)


        urlCon.addView(horizontalCon)
    }

    private fun checkArray(id: String, url: String) {
        var index = -1
        urlData.forEach { data ->
            index++
            if (id == data.id) {
                var newData = URLData.Details(url, id)
                urlData[index] = newData
            }
        }
    }

    override fun onBackPressed() {
        closeDialog.showDialog(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAddUrlEvent(event: AddUrlEvent) {
        addURLEditText(event.url)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onIdleCheckerEvent(event: IdleCheckerEvent) {
        shuffleUrl()

        start.imageResource = R.drawable.pause
        saveUrl()
        saveFactor()
        startActivity<WordpressLoaderActivity>()
    }


}