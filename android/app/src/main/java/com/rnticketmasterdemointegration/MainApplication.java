package com.rnticketmasterdemointegration;

import static com.facebook.react.defaults.DefaultNewArchitectureEntryPoint.load;

import android.app.Application;
import com.facebook.react.PackageList;
import com.facebook.react.ReactApplication;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.defaults.DefaultReactNativeHost;
import com.facebook.soloader.SoLoader;

import java.util.ArrayList;
import java.util.List;

public class MainApplication extends Application implements ReactApplication {
    private final ReactNativeHost mReactNativeHost = new DefaultReactNativeHost(this) {
        @Override
        protected List<ReactPackage> getPackages() {
            ArrayList<ReactPackage> packageList = new PackageList(this).getPackages();
            packageList.add(new SampleAppPackage());
            return packageList;
        }

        @Override
        protected String getJSMainModuleName() {
            return "index";
        }

        @Override
        public boolean getUseDeveloperSupport() {
            return BuildConfig.DEBUG;
        }

        @Override
        public boolean isNewArchEnabled() {
            return BuildConfig.IS_NEW_ARCHITECTURE_ENABLED;
        }

        @Override
        public Boolean isHermesEnabled() {
            return BuildConfig.IS_HERMES_ENABLED;
        }
    };

    @Override
    public ReactNativeHost getReactNativeHost() {
        return mReactNativeHost;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SoLoader.init(this, false);
        if (BuildConfig.IS_NEW_ARCHITECTURE_ENABLED) {
            load();
        }
        ReactNativeFlipper.initializeFlipper(this, mReactNativeHost.getReactInstanceManager());
    }
}
