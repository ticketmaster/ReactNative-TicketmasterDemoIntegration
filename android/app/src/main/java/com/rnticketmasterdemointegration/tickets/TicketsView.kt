package com.rnticketmasterdemointegration.tickets

import android.content.Context
import android.widget.FrameLayout
import com.rnticketmasterdemointegration.R

class TicketsView(context: Context) : FrameLayout(context) {
    init {
        FrameLayout(context)
        addView(FrameLayout(context).apply { id = R.id.container })
    }
}
