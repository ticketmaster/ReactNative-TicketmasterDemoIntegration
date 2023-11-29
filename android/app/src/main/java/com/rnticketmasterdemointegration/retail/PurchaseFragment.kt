package com.rnticketmasterdemointegration.retail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.rnticketmasterdemointegration.BuildConfig
import com.rnticketmasterdemointegration.R
import com.ticketmaster.authenticationsdk.TMAuthentication
import com.ticketmaster.authenticationsdk.TMXDeploymentEnvironment
import com.ticketmaster.authenticationsdk.TMXDeploymentRegion
import com.ticketmaster.discoveryapi.enums.TMMarketDomain
import com.ticketmaster.foundation.entity.TMAuthenticationParams
import com.ticketmaster.purchase.TMPurchase
import com.ticketmaster.purchase.TMPurchaseFragmentFactory
import com.ticketmaster.purchase.TMPurchaseWebsiteConfiguration
import com.ticketmaster.purchase.listener.TMPurchaseNavigationListener
import kotlinx.coroutines.launch

class PurchaseFragment : Fragment() {
    private lateinit var customView: PurchaseView
    private lateinit var purchaseEDPFragment: Fragment
    private lateinit var eventId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        customView = PurchaseView(requireNotNull(context))
        val tmPurchase = TMPurchase(BuildConfig.API_KEY)
        val tmPurchaseWebsiteConfiguration = TMPurchaseWebsiteConfiguration(eventId, TMMarketDomain.US)
        val tmAuthenticationParams = TMAuthenticationParams(
            apiKey = BuildConfig.API_KEY,
            clientName = "client name",
            environment = TMXDeploymentEnvironment.Production,
            region = TMXDeploymentRegion.US
        )

        val factory = TMPurchaseFragmentFactory(
            tmPurchaseNavigationListener = PurchaseNavigationListener {
                childFragmentManager.popBackStack()
            }
        ).apply {
            childFragmentManager.fragmentFactory = this
        }

        val bundle = tmPurchase.getPurchaseBundle(
            tmPurchaseWebsiteConfiguration,
            tmAuthenticationParams
        )

        purchaseEDPFragment = factory.instantiatePurchase(ClassLoader.getSystemClassLoader()).apply {
            arguments = bundle
        }

        childFragmentManager.beginTransaction()
            .add(R.id.purchase_container, purchaseEDPFragment)
            .commit()
        return customView // this could be any view that you want to render
    }

    fun setEventId(propEventId: String?) {
        eventId = propEventId.toString()
    }
}

class PurchaseNavigationListener(private val closeScreen: () -> Unit) :
    TMPurchaseNavigationListener {
    override fun errorOnEventDetailsPage(error: Exception) {}

    override fun onPurchaseClosed() {
        closeScreen.invoke()
    }

}