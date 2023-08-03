package com.psijuego.core.components

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.psijuego.R

class NoConnectionLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    var mView: View? = null
    var tvTitle: TextView? = null
    var llWithoutConnection: LinearLayout? = null
    var retryButton: Button? = null

    init {
        init(context)
    }

    private fun init(context: Context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mView = inflater.inflate(R.layout.no_connection_layout, this, true)
        tvTitle = findViewById(R.id.tvTitle)
        llWithoutConnection = findViewById(R.id.llWithoutConnection)
        retryButton = findViewById(R.id.btnRetry)
    }

    fun setTitle(title: String?) {
        tvTitle!!.text = title
    }
}