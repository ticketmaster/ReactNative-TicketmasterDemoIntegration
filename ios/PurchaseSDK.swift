//
//  PurchaseSDK.swift
//  RNTicketmasterDemoIntegration
//
//  Created by justyna zygmunt on 27/03/2024.
//

import TicketmasterAuthentication
import TicketmasterTickets
import TicketmasterPurchase
import TicketmasterDiscoveryAPI


@objc(PurchaseSDK)
class PurchaseSDK: NSObject {
  @objc public static func loadSDKView(_ eventId: String) {
    let apiKey = RNCConfig.env(for: "API_KEY") ?? ""
    let tmxServiceSettings = TMAuthentication.TMXSettings(apiKey: apiKey,
                                                          region: .US)
    
    let branding = TMAuthentication.Branding(displayName: "My Team",
                                             backgroundColor: .init(hexString: "#026cdf"),
                                             theme: .light)
    
    let brandedServiceSettings = TMAuthentication.BrandedServiceSettings(tmxSettings: tmxServiceSettings,
                                                                         branding: branding)
    
    TMPurchase.shared.configure(apiKey: apiKey, completion: {
      isPurchaseApiSet in
      print("Purchase api key set result: \(isPurchaseApiSet)")
      
      TMDiscoveryAPI.shared.configure(apiKey: apiKey, completion: { isDiscoveryApiSet in
        print("Discovery api key set result: \(isDiscoveryApiSet)")
        TMAuthentication.shared.configure(brandedServiceSettings: brandedServiceSettings) { backendsConfigured in
          
          TMPurchase.shared.brandColor = UIColor(red: 0.19, green: 0.02, blue: 0.16, alpha: 1.00)
          
          TMTickets.shared.configure {
            
            let edpNav = TMPurchaseNavigationController.eventDetailsNavigationController(eventIdentifier: eventId, marketDomain: .US)
            edpNav.modalPresentationStyle = .fullScreen
            UIApplication.shared.windows.filter {$0.isKeyWindow}.first?.rootViewController?.present(edpNav, animated: true)

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

