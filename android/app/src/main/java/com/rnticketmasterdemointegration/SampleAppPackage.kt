package com.rnticketmasterdemointegration

import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.rnticketmasterdemointegration.tickets.TicketsViewManager

class SampleAppPackage : ReactPackage {
    override fun createViewManagers(
        reactContext: ReactApplicationContext
    ) = listOf(TicketsViewManager(reactContext))

    override fun createNativeModules(
        reactContext: ReactApplicationContext
    ): MutableList<NativeModule> = listOf(AccountsSDKModule(reactContext)).toMutableList()
}