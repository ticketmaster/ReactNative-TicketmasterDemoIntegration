package com.rnticketmasterdemointegration

import android.app.Activity
import android.content.Intent
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color
import androidx.fragment.app.FragmentActivity
import com.facebook.react.bridge.ActivityEventListener
import com.facebook.react.bridge.BaseActivityEventListener
import com.facebook.react.bridge.Callback
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.google.gson.Gson
import com.rnticketmasterdemointegration.retail.PrePurchaseActivity
import com.rnticketmasterdemointegration.retail.PurchaseActivity
import com.ticketmaster.authenticationsdk.AuthSource
import com.ticketmaster.authenticationsdk.TMAuthentication
import com.ticketmaster.authenticationsdk.TMXDeploymentEnvironment
import com.ticketmaster.authenticationsdk.TMXDeploymentRegion
import com.ticketmaster.tickets.ticketssdk.TicketsColors
import com.ticketmaster.tickets.ticketssdk.TicketsSDKClient
import com.ticketmaster.tickets.ticketssdk.TicketsSDKSingleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class AccountsSDKModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    private val CODE = 1
    override fun getName() = "AccountsSDK"
    private var mResultCallback: Callback? = null

    private val mActivityEventListener: ActivityEventListener =
        object : BaseActivityEventListener() {
            override fun onActivityResult(
                activity: Activity?,
                requestCode: Int,
                resultCode: Int,
                data: Intent?
            ) {
                if (mResultCallback != null) {
                    mResultCallback!!.invoke(resultCode)
                    mResultCallback = null
                }
            }
        }

    init {
        reactContext.addActivityEventListener(mActivityEventListener)
    }

    @ReactMethod
    fun login(resultCallback: Callback) {
        runBlocking() {
            mResultCallback = resultCallback
            val currentFragmentActivity = currentActivity as FragmentActivity
            val authentication = TMAuthentication.Builder()
                .apiKey(BuildConfig.API_KEY)
                .clientName("client name") // Team name to be displayed
                .colors(TMAuthentication.ColorTheme())
                .environment(TMXDeploymentEnvironment.Production) // Environment that the SDK will use. Default is Production
                .region(TMXDeploymentRegion.US) // Region that the SDK will use. Default is US
                .build(currentFragmentActivity)
            val intent = authentication.getLoginIntent(currentFragmentActivity)
            currentActivity?.startActivityForResult(intent, CODE)
        }
    }

    @ReactMethod
    fun isLoggedIn(promise: Promise) =
        TicketsSDKSingleton.getTMAuthentication()?.let { authentication ->
            runBlocking {
                val response = withContext(context = Dispatchers.IO) {
                    AuthSource.values().forEach {
                        if (authentication.getToken(it)?.isNotBlank() == true) {
                            return@withContext true
                        }
                    }
                    return@withContext false
                }

                promise.resolve(response)
            }
        } ?: false

    @ReactMethod
    fun configureAccountsSDK(promise: Promise)  {
        runBlocking(Dispatchers.Main) {
            val currentFragmentActivity = currentActivity as FragmentActivity
            val authentication = TMAuthentication.Builder()
                .apiKey(BuildConfig.API_KEY)
                .clientName("rnonboarding")
                .colors(TMAuthentication.ColorTheme())
                .environment(TMXDeploymentEnvironment.Production)
                .region(TMXDeploymentRegion.US)
                .build(currentFragmentActivity)

            TicketsSDKClient
                .Builder()
                .authenticationSDKClient(authentication)
                .colors(createTicketsColors(android.graphics.Color.parseColor("#231F20")))
                .build(currentFragmentActivity)
                .apply {
                    TicketsSDKSingleton.setTicketsSdkClient(this)
                    promise.resolve(true)
                }
        }
    }

    @ReactMethod
    fun logout(promise: Promise) {
        runBlocking {
            withContext(context = Dispatchers.IO) {
                try {
                    val currentFragmentActivity = currentActivity as FragmentActivity
                    val authentication = TMAuthentication.Builder()
                        .apiKey(BuildConfig.API_KEY)
                        .clientName("rnonboarding")
                        .colors(TMAuthentication.ColorTheme())
                        .environment(TMXDeploymentEnvironment.Production)
                        .region(TMXDeploymentRegion.US)
                        .build(currentFragmentActivity)
                    authentication.logout(currentFragmentActivity)
                    promise.resolve(true)
                } catch(e: Exception) {
                    promise.resolve(false)
                }
            }
        }
    }

    @ReactMethod
    fun presentPrePurchaseVenue(venueId: String) {
        val context = currentActivity
        val intent = Intent(context, PrePurchaseActivity::class.java)
        intent.putExtra("venueId", venueId)
        context?.startActivity(intent)
    }

    @ReactMethod
    fun presentPrePurchaseAttraction(attractionId: String) {
        val context = currentActivity
        val intent = Intent(context, PrePurchaseActivity::class.java)
        intent.putExtra("attractionId", attractionId)
        context?.startActivity(intent)
    }

    @ReactMethod
    fun presentPurchase(eventId: String) {
        val context = currentActivity
        val intent = Intent(context, PurchaseActivity::class.java)
        intent.putExtra("eventId", eventId)
        context?.startActivity(intent)
    }

    @ReactMethod
    fun getMemberInfo(promise: Promise) = TicketsSDKSingleton.getTMAuthentication()?.let {
        runBlocking {
            val archticsAccessToken = async { it.getToken(AuthSource.ARCHTICS) }
            val hostAccessToken = async { it.getToken(AuthSource.HOST) }
            val mfxAccessToken = async { it.getToken(AuthSource.MFX) }
            val (resArchticsAccessToken, resHostAccessToken, resMfxAccessToken) = awaitAll(
                archticsAccessToken,
                hostAccessToken,
                mfxAccessToken
            )

            if (resArchticsAccessToken.isNullOrEmpty() && resHostAccessToken.isNullOrEmpty() && resMfxAccessToken.isNullOrEmpty()) {
                promise.resolve(null)
            } else {
                val memberInfoJson = Gson().toJson(it.fetchUserDetails().getOrNull())
                promise.resolve(memberInfoJson)
            }
        }
    }

    @ReactMethod
    fun refreshToken(promise: Promise) = TicketsSDKSingleton.getTMAuthentication()?.let {
        runBlocking {
            val hostAccessToken = async { it.getToken(AuthSource.HOST) }
            val archticsAccessToken = async { it.getToken(AuthSource.ARCHTICS) }
            val mfxAccessToken = async { it.getToken(AuthSource.MFX) }
            val (resArchticsAccessToken, resHostAccessToken, resMfxAccessToken) = awaitAll(
                archticsAccessToken,
                hostAccessToken,
                mfxAccessToken
            )
            if (!resHostAccessToken.isNullOrEmpty()) {
                promise.resolve(resHostAccessToken)
            } else if (!resArchticsAccessToken.isNullOrEmpty()) {
                promise.resolve(resArchticsAccessToken)
            } else if (!resMfxAccessToken.isNullOrEmpty()) {
                promise.resolve(resMfxAccessToken)
            } else {
                promise.resolve(null)
            }
        }
    }

    private fun createTicketsColors(color: Int): TicketsColors =
        TicketsColors(
            lightColors(
                primary = Color(color),
                primaryVariant = Color(color),
                secondary = Color(color)
            ),
            darkColors(
                primary = Color(color),
                primaryVariant = Color(color),
                secondary = Color(color)
            )
        )
}
