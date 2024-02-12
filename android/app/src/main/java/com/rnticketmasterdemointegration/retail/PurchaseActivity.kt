package com.rnticketmasterdemointegration.retail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.rnticketmasterdemointegration.BuildConfig
import com.rnticketmasterdemointegration.R
import com.ticketmaster.authenticationsdk.TMXDeploymentRegion
import com.ticketmaster.discoveryapi.enums.TMMarketDomain
import com.ticketmaster.foundation.entity.TMAuthenticationParams
import com.ticketmaster.purchase.TMPurchase
import com.ticketmaster.purchase.TMPurchaseFragmentFactory
import com.ticketmaster.purchase.TMPurchaseWebsiteConfiguration

class PurchaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.venue_layout)

        if (savedInstanceState == null) {
            val tmPurchase = TMPurchase(
                apiKey = BuildConfig.API_KEY,
                brandColor = ContextCompat.getColor(
                    this,
                    R.color.black
                )
            )

            val tmPurchaseWebsiteConfiguration = TMPurchaseWebsiteConfiguration(
                intent.getStringExtra("eventId").orEmpty(),
                TMMarketDomain.US,
                showInfoToolbarButton = true,
                showShareToolbarButton = true
            )

            val factory = TMPurchaseFragmentFactory(
                tmPurchaseNavigationListener = PurchaseNavigationListener {
                    finish()
                }
            ).apply {
                supportFragmentManager.fragmentFactory = this
            }

            val tmAuthenticationParams = TMAuthenticationParams(
                apiKey = BuildConfig.API_KEY,
                clientName = "client name",
                region = TMXDeploymentRegion.US
            )

            val bundle = tmPurchase.getPurchaseBundle(
                tmPurchaseWebsiteConfiguration,
                tmAuthenticationParams
            )

            val purchaseEDPFragment = factory.instantiatePurchase(ClassLoader.getSystemClassLoader()).apply {
                arguments = bundle
            }
            supportFragmentManager.beginTransaction()
                .add(R.id.venue_container, purchaseEDPFragment)
                .commit()
        }
    }
}
