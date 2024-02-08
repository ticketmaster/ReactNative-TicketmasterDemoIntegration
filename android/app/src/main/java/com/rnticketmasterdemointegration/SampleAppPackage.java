package com.rnticketmasterdemointegration;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.rnticketmasterdemointegration.retail.PrePurchaseViewManager;
import com.rnticketmasterdemointegration.retail.PurchaseViewManager;
import com.rnticketmasterdemointegration.tickets.TicketsViewManager;

import java.util.ArrayList;
import java.util.List;

public class SampleAppPackage implements ReactPackage {
    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        List<ViewManager> viewManagers = new ArrayList<>();
        viewManagers.add(new PurchaseViewManager(reactContext));
        viewManagers.add(new PrePurchaseViewManager(reactContext));
        viewManagers.add(new TicketsViewManager(reactContext));
        return viewManagers;
    }

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        List<NativeModule> nativeModules = new ArrayList<>();
        nativeModules.add(new AccountsSDKModule(reactContext));
        return nativeModules;
    }
}