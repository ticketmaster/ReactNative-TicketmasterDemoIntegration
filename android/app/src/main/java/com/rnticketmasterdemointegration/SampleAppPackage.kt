package com.rnticketmasterdemointegration

import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.rnticketmasterdemointegration.retail.PrePurchaseViewManager
import com.rnticketmasterdemointegration.retail.PurchaseViewManager
import com.rnticketmasterdemointegration.tickets.TicketsViewManager

class SampleAppPackage : ReactPackage {
    override fun createViewManagers(
        reactContext: ReactApplicationContext
    ) = listOf(PurchaseViewManager(reactContext), PrePurchaseViewManager(reactContext), TicketsViewManager(reactContext))

    override fun createNativeModules(
        reactContext: ReactApplicationContext
    ): MutableList<NativeModule> = listOf(AccountsSDKModule(reactContext)).toMutableList()
}