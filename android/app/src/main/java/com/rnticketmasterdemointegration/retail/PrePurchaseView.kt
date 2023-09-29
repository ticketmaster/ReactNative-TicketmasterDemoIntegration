package com.rnticketmasterdemointegration.retail

import android.content.Context
import android.widget.FrameLayout
import com.rnticketmasterdemointegration.R

class PrePurchaseView(context: Context) : FrameLayout(context){
    init {
        FrameLayout(context)
        addView(FrameLayout(context)).apply { id = R.id.venue_container }
    }
}
