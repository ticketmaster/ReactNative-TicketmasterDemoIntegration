package com.rnticketmasterdemointegration.retail

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.rnticketmasterdemointegration.BuildConfig
import com.rnticketmasterdemointegration.R
import com.ticketmaster.discoveryapi.enums.TMEnvironment
import com.ticketmaster.discoveryapi.enums.TMMarketDomain
import com.ticketmaster.discoveryapi.models.DiscoveryAbstractEntity
import com.ticketmaster.discoveryapi.models.DiscoveryEvent
import com.ticketmaster.discoveryapi.models.DiscoveryVenue
import com.ticketmaster.prepurchase.TMPrePurchase
import com.ticketmaster.prepurchase.TMPrePurchaseFragmentFactory
import com.ticketmaster.prepurchase.TMPrePurchaseWebsiteConfiguration
import com.ticketmaster.prepurchase.data.CoordinatesWithMarketDomain
import com.ticketmaster.prepurchase.data.Location
import com.ticketmaster.prepurchase.listener.TMPrePurchaseNavigationListener
import com.ticketmaster.purchase.TMPurchase
import com.ticketmaster.purchase.TMPurchaseFragmentFactory
import com.ticketmaster.purchase.TMPurchaseWebsiteConfiguration
import kotlinx.coroutines.launch

class PrePurchaseFragment : Fragment() {
    private lateinit var customView: PrePurchaseView
    private lateinit var attractionId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        customView = PrePurchaseView(requireNotNull(context))
        val color = ContextCompat.getColor(requireContext(), R.color.black)
        val tmPrePurchase = TMPrePurchase(TMEnvironment.Production, color, BuildConfig.API_KEY)
        val discoveryVenue: DiscoveryAbstractEntity = DiscoveryVenue(hostID = attractionId)
        val tmPrePurchaseWebsiteConfiguration = TMPrePurchaseWebsiteConfiguration(discoveryVenue, TMMarketDomain.US)
        val listener = PrePurchaseNavigationListener(
            context = requireContext(),
            apiKey = BuildConfig.API_KEY,
            fragmentManager = childFragmentManager,
            activity = requireActivity(),
            closeScreen = {
                childFragmentManager.popBackStack()
            },
            parentFragmentManager = parentFragmentManager
        )
        lifecycleScope.launch {
            val factory = TMPrePurchaseFragmentFactory(
                tmPrePurchaseNavigationListener =  listener
            ).apply {
                childFragmentManager.fragmentFactory = this
            }

            val bundle : Bundle = tmPrePurchase.getPrePurchaseBundle(tmPrePurchaseWebsiteConfiguration)

            val purchaseEDPFragment = factory
                .instantiatePrePurchase(ClassLoader.getSystemClassLoader())
                .apply {
                    arguments = bundle
                }

            childFragmentManager.beginTransaction()
                .add(R.id.venue_container, purchaseEDPFragment)
                .commit()
        }
        return customView // this CustomView could be any view that you want to render
    }

    fun setAttractionId(propAttractionId: String?) {
        attractionId = propAttractionId.toString()
    }
}

class PrePurchaseNavigationListener(
    private val closeScreen: () -> Unit,
    private val apiKey: String,
    private val context: Context,
    private val activity: Activity,
    private val fragmentManager: FragmentManager,
    private val parentFragmentManager: FragmentManager
): TMPrePurchaseNavigationListener {
    override fun onDidRequestCurrentLocation(
        globalMarketDomain: TMMarketDomain?,
        completion: (CoordinatesWithMarketDomain?) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun onDidUpdateCurrentLocation(
        globalMarketDomain: TMMarketDomain?,
        location: Location
    ) {
        TODO("Not yet implemented")
    }

    override fun onPrePurchaseClosed() {
        closeScreen.invoke()
    }

    override fun openEventDetailsPage(
        abstractEntity: DiscoveryAbstractEntity?,
        event: DiscoveryEvent
    ) {
        TMPurchaseFragmentFactory(
            tmPurchaseNavigationListener = PurchaseNavigationListener {
                fragmentManager.popBackStack()
            }
        ).apply {
            fragmentManager.fragmentFactory = this
            parentFragmentManager.fragmentFactory = this
        }

        val tmPurchase = TMPurchase(
            apiKey = apiKey,
            brandColor = ContextCompat.getColor(context, R.color.black)
        )

        val tmPurchaseWeb = TMPurchaseWebsiteConfiguration(
            event.hostID.orEmpty(),
            TMMarketDomain.US,
        )

        val bundle = Bundle()
        bundle.putParcelable(TMPurchase::class.java.name, tmPurchase)
        bundle.putParcelable(TMPurchaseWebsiteConfiguration::class.java.name, tmPurchaseWeb)

        val purchaseFragment = PurchaseFragment()
        purchaseFragment.arguments = bundle
        purchaseFragment.setEventId(event.hostID)
        fragmentManager.beginTransaction()
            .add(R.id.venue_container, purchaseFragment)
            .commit()
    }
}
