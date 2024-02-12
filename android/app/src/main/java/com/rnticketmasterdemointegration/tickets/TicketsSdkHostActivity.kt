package com.rnticketmasterdemointegration.tickets

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.rnticketmasterdemointegration.BuildConfig
import com.rnticketmasterdemointegration.R
import com.ticketmaster.authenticationsdk.AuthSource
import com.ticketmaster.authenticationsdk.TMAuthentication
import com.ticketmaster.authenticationsdk.TMXDeploymentEnvironment
import com.ticketmaster.authenticationsdk.TMXDeploymentRegion
import com.ticketmaster.tickets.TicketsModuleDelegate
import com.ticketmaster.tickets.event_tickets.DirectionsModule
import com.ticketmaster.tickets.event_tickets.ModuleBase
import com.ticketmaster.tickets.eventanalytic.UserAnalyticsDelegate
import com.ticketmaster.tickets.ticketssdk.TicketsColors
import com.ticketmaster.tickets.ticketssdk.TicketsSDKClient
import com.ticketmaster.tickets.ticketssdk.TicketsSDKSingleton
import com.ticketmaster.tickets.util.TMTicketsBrandingColor
import kotlinx.coroutines.launch

class TicketsSdkHostActivity : AppCompatActivity() {

    private val color: String = "#ef3e42"
    private val mGettingStartedContainer: ConstraintLayout by lazy { findViewById(R.id.getting_started_container) }
    private val mProgressDialog: AlertDialog by lazy {
        AlertDialog.Builder(this)
            .setView(LayoutInflater.from(this).inflate(R.layout.tickets_layout_loading_view, null, false))
            .setCancelable(false).create().apply {
                setCanceledOnTouchOutside(false)
            }
    }
    private val mCancelledDialog: AlertDialog by lazy {
        AlertDialog.Builder(this@TicketsSdkHostActivity)
            .setTitle("Configuration Error")
            .setMessage("Something went wrong setting up your configuration")
            .setPositiveButton("retry") { _, _ ->
                mProgressDialog.show()
                setupAuthenticationSDK()
            }.setNegativeButton("Cancel", null).setCancelable(false).create().apply {
                setCanceledOnTouchOutside(false)
            }
    }

    private val ticketColor: Int by lazy { android.graphics.Color.parseColor(color) }

    private val brandingColor: Int by lazy { android.graphics.Color.parseColor(color) }

    private val headerColor: Int by lazy { android.graphics.Color.parseColor(color) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tickets_sdk_host)
        mGettingStartedContainer.visibility = View.VISIBLE
        mProgressDialog.show()
        setupAuthenticationSDK()
        setupAnalytics()
    }

    private fun setupAuthenticationSDK() {
        /** After creating the TMAuthentication.Builder object, the build() function is a suspend function,
         * that is way it is required to be called inside a coroutine.
         *
         * When calling the build function there are two options:
         * - By calling the build(fragmentActivity) without the callback, will return a TMAuthentication object
         * - using build(fragmentActivity, callback), in the callback will retrieve a TMAuthentication object
         * **/
        lifecycleScope.launch {
            // After creating the TMAuthentication.Builder object, the build() function is a suspend function,
            // that is way it is required to be called inside a coroutine.
            val authentication = createTMAuthenticationBuilder().build(this@TicketsSdkHostActivity)
            setupTicketsSDKClient(authentication)
        }
    }

    private fun createTMAuthenticationBuilder(): TMAuthentication.Builder =
        TMAuthentication.Builder().apiKey(BuildConfig.API_KEY) // Your consumer key
            .clientName("team name") // Team name to be displayed
            //Optional value to show screen previous to login
            .modernAccountsAutoQuickLogin(false)
            //Optional value to define the colors for the Authentication page
            .colors(createTMAuthenticationColors(brandingColor))
            .region(TMXDeploymentRegion.US) // Region that the SDK will use. Default is US
            .environment(TMXDeploymentEnvironment.Production) // Environment that the SDK will use. Default is Production

    @SuppressLint("ConflictingOnColor")
    private fun createTMAuthenticationColors(color: Int): TMAuthentication.ColorTheme =
        TMAuthentication.ColorTheme(
            //The Color class is part of the Compose library
            lightColors(
                primary = Color(color),
                primaryVariant = Color(color),
                secondary = Color(color),
                onPrimary = Color.White // Color used for text and icons displayed on top of the primary color.
            ), darkColors(
                primary = Color(color),
                primaryVariant = Color(color),
                secondary = Color(color),
                onPrimary = Color.White // Color used for text and icons displayed on top of the primary color.
            )
        )

    private suspend fun setupTicketsSDKClient(authentication: TMAuthentication) {
        //After called the build function of TMAuthentication.Builder object, we validate if configuration is different
        //from null, to verify if it was retrieved satisfactory a configuration file from the given params.
        authentication.configuration?.let {
            val tokenMap = validateAuthToken(authentication)

            TicketsSDKClient.Builder()
                .authenticationSDKClient(authentication) //Authentication object
                //Optional value to define the colors for the Tickets page
                .colors(createTicketsColors(android.graphics.Color.parseColor(color)))
                //Function that generates a TicketsSDKClient object
                .build(this@TicketsSdkHostActivity).apply {
                    //After creating the TicketsSDKClient object, add it into the TicketsSDKSingleton
                    TicketsSDKSingleton.setTicketsSdkClient(this)

                    setupTicketsColors()

                    //Validate if there is an active token.
                    if (tokenMap.isNotEmpty()) {
                        //If there is an active token, it launches the event fragment
                        launchTicketsView()
                    } else {
                        //If there is no active token, it launches a login intent. Launch an ActivityForResult, if result
                        //is RESULT_OK, there is an active token to be retrieved.
                        resultLauncher.launch(TicketsSDKSingleton.getLoginIntent(this@TicketsSdkHostActivity))
                    }
                }
        }
        if (authentication.configuration == null) {
            mProgressDialog.dismiss()
            mCancelledDialog.show()
        }
    }

    private suspend fun validateAuthToken(authentication: TMAuthentication): Map<AuthSource, String> {
        val tokenMap = mutableMapOf<AuthSource, String>()
        AuthSource.values().forEach {
            //Validate if there is an active token for the AuthSource, if not it returns null.
            authentication.getToken(it)?.let { token ->
                tokenMap[it] = token
            }
        }
        return tokenMap
    }

    private fun createTicketsColors(color: Int): TicketsColors = TicketsColors(
        lightColors(
            primary = Color(color), primaryVariant = Color(color), secondary = Color(color)
        ), darkColors(
            primary = Color(color), primaryVariant = Color(color), secondary = Color(color)
        )
    )

    private fun setupTicketsColors() {
        //Affects the color of the container of ticket.
        TMTicketsBrandingColor.setTicketColor(this@TicketsSdkHostActivity, ticketColor)

        //Affects the branding color, like the color of the buttons
        TMTicketsBrandingColor.setBrandingColor(this@TicketsSdkHostActivity, brandingColor)

        //Affects the header color
        TMTicketsBrandingColor.setHeaderColor(this@TicketsSdkHostActivity, headerColor)
    }

    private fun launchTicketsView() {
        mGettingStartedContainer.visibility = View.GONE
        mProgressDialog.dismiss()
        //Retrieve an EventFragment
        TicketsSDKSingleton.getEventsFragment(this@TicketsSdkHostActivity)?.let {
            supportFragmentManager.beginTransaction().replace(R.id.tickets_sdk_view, it).commit()
        }

    }

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when (result.resultCode) {
            RESULT_OK -> {
                launchTicketsView()
            }

            RESULT_CANCELED -> {
                mProgressDialog.dismiss()
            }
        }
    }

    private fun setupAnalytics() {
        //Initialize observer that will handle the analytics events
        //Must be called the observeForever as this will kept alive the observer until
        //the Activity is destroyed
        UserAnalyticsDelegate.handler.getLiveData().observeForever(userAnalyticsObserver)
    }

    override fun onDestroy() {
        super.onDestroy()
        //Remove the observer in the onDestroy, as it won't be needed to keep traking
        //the analytics events.
        UserAnalyticsDelegate.handler.getLiveData().removeObserver(userAnalyticsObserver)
    }

    private val userAnalyticsObserver = Observer<UserAnalyticsDelegate.AnalyticsData?> {
        it?.let {
            Log.d("Analytics", "Action name: ${it.actionName}, data: ${it.data}")
        }
    }

    private fun getDirectionsModule(
        latLng: TicketsModuleDelegate.LatLng?
    ): ModuleBase {
        return DirectionsModule(
            this, latLng?.latitude, latLng?.longitude
        ).build()
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            //Validates if there is an active user logged in.
            if (isLoggedIn()) {
                mGettingStartedContainer.visibility = View.GONE
            } else {
                mGettingStartedContainer.visibility = View.VISIBLE
            }
        }
    }

    //Validates if there is an access token for each AuthSource, if there is one active, returns true.
    private suspend fun isLoggedIn(): Boolean =
        TicketsSDKSingleton.getTMAuthentication()?.let { authentication ->
            AuthSource.values().forEach {
                if (authentication.getToken(it)?.isNotBlank() == true) {
                    return true
                }
            }
            return false
        } ?: false

}


