//
//  PurchaseSdkViewController.swift
//  RNTicketmasterDemoIntegration
//
//  Created by Daniel Olugbade on 24/08/2023.
//

import TicketmasterAuthentication
import TicketmasterTickets
import TicketmasterPurchase
import TicketmasterDiscoveryAPI

class PurchaseSdkViewController: UIViewController {
  var eventId: String = "eventId"
  
  override func viewDidLoad() {
    super.viewDidLoad()
    
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

        TMPurchase.shared.brandColor = UIColor(red: 0.19, green: 0.02, blue: 0.16, alpha: 1.00)
        
        
        let edpNav = TMPurchaseNavigationController.eventDetailsNavigationController(eventIdentifier: self.eventId, marketDomain: .US)
        edpNav.modalPresentationStyle = .fullScreen
        self.present(edpNav, animated: false)
        
      } failure: { error in
        // something went wrong, probably TMAuthentication was not configured correctly
        print(" - Tickets SDK Configuration Error: \(error.localizedDescription)")
      }
    } failure: { error in
      // something went wrong, probably the wrong apiKey+region combination
      print(" - Authentication SDK Configuration Error: \(error.localizedDescription)")
    }
    
  }
  
  func sendEventIdFromView(eventIdProp: String) {
    print("Received data in UIViewController: \(eventIdProp)")
    eventId = eventIdProp
  }
  
  override func viewDidAppear(_ animated: Bool) {
    super.viewDidAppear(animated)
    print("PurchaseSdkViewController viewDidAppear")
  }
  
  @objc(setEventIdProp:)
  public func setEventIdProp(_ eventIdProp: NSString) {
    eventId = eventIdProp as String
  }
}
