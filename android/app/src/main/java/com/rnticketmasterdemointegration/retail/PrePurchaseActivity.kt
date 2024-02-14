package com.rnticketmasterdemointegration.retail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.rnticketmasterdemointegration.BuildConfig
import com.rnticketmasterdemointegration.R
import com.ticketmaster.discoveryapi.enums.TMMarketDomain
import com.ticketmaster.discoveryapi.models.DiscoveryAbstractEntity
import com.ticketmaster.discoveryapi.models.DiscoveryAttraction
import com.ticketmaster.discoveryapi.models.DiscoveryVenue
import com.ticketmaster.prepurchase.TMPrePurchase
import com.ticketmaster.prepurchase.TMPrePurchaseFragmentFactory
import com.ticketmaster.prepurchase.TMPrePurchaseWebsiteConfiguration

class PrePurchaseActivity : AppCompatActivity() {
    private lateinit var fragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.venue_layout)

        val tmPrePurchase = TMPrePurchase(
            discoveryAPIKey = BuildConfig.API_KEY,
            brandColor = ContextCompat.getColor(
                this@PrePurchaseActivity,
                R.color.black
            )
        )
        val venueId = intent.getStringExtra("venueId")
        val attractionId = intent.getStringExtra("attractionId")

        val discoveryVenue: DiscoveryAbstractEntity = if (!venueId.isNullOrEmpty()) {
            DiscoveryVenue(hostID = venueId)
        } else {
            DiscoveryAttraction(hostID = attractionId)
        }

        val tmPrePurchaseWebsiteConfiguration = TMPrePurchaseWebsiteConfiguration(
            discoveryVenue,
            TMMarketDomain.US,
        )

        val bundle = tmPrePurchase.getPrePurchaseBundle(
            tmPrePurchaseWebsiteConfiguration
        )

        val factory = TMPrePurchaseFragmentFactory(
            tmPrePurchaseNavigationListener = PrePurchaseNavigationListener(
                context = this,
                apiKey = tmPrePurchase.discoveryAPIKey.orEmpty(),
            ) {
                finish()
            }
        ).apply {
            supportFragmentManager.fragmentFactory = this
        }

        fragment = factory.instantiatePrePurchase(ClassLoader.getSystemClassLoader()).apply {
            arguments = bundle
        }

        supportFragmentManager.beginTransaction()
            .add(R.id.venue_container, fragment)
            .commit()
    }
}
