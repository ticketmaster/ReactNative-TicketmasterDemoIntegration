//
//  PrePurchaseSDK.swift
//  RNTicketmasterDemoIntegration
//
//  Created by justyna zygmunt on 27/03/2024.
//

import TicketmasterAuthentication
import TicketmasterTickets
import TicketmasterPurchase
import TicketmasterDiscoveryAPI
import TicketmasterPrePurchase


@objc(PrePurchaseSDK)
class PrePurchaseSDK: NSObject {
  @objc public static func loadSDKView(_ venueId: String, attractionId: String) {
    let apiKey = RNCConfig.env(for: "API_KEY") ?? ""
    let tmxServiceSettings = TMAuthentication.TMXSettings(apiKey: apiKey,
                                                          region: .US)
    
    let branding = TMAuthentication.Branding(displayName: "My Team",
                                             backgroundColor: .init(hexString: "#026cdf"),
                                             theme: .light)
    
    let brandedServiceSettings = TMAuthentication.BrandedServiceSettings(tmxSettings: tmxServiceSettings,
                                                                         branding: branding)
    
    TMPrePurchase.shared.configure(apiKey: apiKey, completion: { isPrePurchaseApiSet in
      print("PrePurchase api key set result: \(isPrePurchaseApiSet)")
      TMDiscoveryAPI.shared.configure(apiKey: apiKey, completion: { isDiscoveryApiSet in
        print("Discovery api key set result: \(isDiscoveryApiSet)")
        TMPrePurchase.shared.brandColor = UIColor(red: 0.19, green: 0.02, blue: 0.16, alpha: 1.00)
        TMAuthentication.shared.configure(brandedServiceSettings: brandedServiceSettings) { backendsConfigured in
          // Tickets is configured, now we are ready to present TMTicketsViewController or TMTicketsView
          print(" - Tickets SDK Configured")
          TMTickets.shared.configure {
            let viewController = !venueId.isEmpty ? TMPrePurchaseViewController.venueDetailsViewController(venueIdentifier: venueId, enclosingEnvironment: .modalPresentation) : TMPrePurchaseViewController.attractionDetailsViewController(attractionIdentifier: attractionId, enclosingEnvironment: .modalPresentation)
            
            viewController.modalPresentationStyle = .fullScreen
            
           UIApplication.shared.windows.filter {$0.isKeyWindow}.first?.rootViewController?.present(viewController, animated: true)
            
          } failure: { error in
            // something went wrong, probably TMAuthentication was not configured correctly
            print(" - Tickets SDK Configuration Error: \(error.localizedDescription)")
          }
        } failure: { error in
          // something went wrong, probably the wrong apiKey+region combination
          print(" - Authentication SDK Configuration Error: \(error.localizedDescription)")
        }
      })
    })
  }
}

