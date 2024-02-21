//
//  AccountsSDK.swift
//  RNTicketmasterDemoIntegration
//
//  Created by Daniel Olugbade on 24/08/2023.
//

import TicketmasterAuthentication
import TicketmasterTickets


@objc(AccountsSDK)
class AccountsSDK: RCTEventEmitter, TMAuthenticationDelegate  {
  
  @objc public func configureAccountsSDK(_ resolve: @escaping (String) -> Void, reject: @escaping (_ code: String, _ message: String, _ error: NSError) -> Void) {
    
    TMAuthentication.shared.delegate = self
    
    
    // build a combination of Settings and Branding
    let apiKey = RNCConfig.env(for: "API_KEY") ?? ""
    let tmxServiceSettings = TMAuthentication.TMXSettings(apiKey: apiKey,
                                                          region: .US)
    
    let branding = TMAuthentication.Branding(displayName: "My Team",
                                             backgroundColor: .red,
                                             theme: .light)
    
    let brandedServiceSettings = TMAuthentication.BrandedServiceSettings(tmxSettings: tmxServiceSettings,
                                                                         branding: branding)
    
    // configure TMAuthentication with Settings and Branding
    print("Authentication SDK Configuring...")
    
    TMAuthentication.shared.configure(brandedServiceSettings: brandedServiceSettings) { backendsConfigured in
      // your API key may contain configurations for multiple backend services
      // the details are not needed for most common use-cases
      print(" - Authentication SDK Configured: \(backendsConfigured.count)")
      resolve("Authentication SDK configuration successful")
    } failure: { error in
      // something went wrong, probably the wrong apiKey+region combination
      print(" - Authentication SDK Configuration Error: \(error.localizedDescription)")
      reject( "Authentication SDK Configuration Error:", error.localizedDescription, error as NSError)
    }
  }
  
  override func supportedEvents() -> [String]! {
    // replace return [""]; with below to send Native Event to React Native side
    // return ["loginStarted"];
    return [""];
  }
  
  
  @objc public func login(_ resolve: @escaping ([String: Any]) -> Void, reject: @escaping (_ code: String, _ message: String, _ error: NSError) -> Void) {
    
    TMAuthentication.shared.login { authToken in
      print("Login Completed")
      print(" - AuthToken: \(authToken.accessToken.prefix(20))...")
      let data = ["accessToken": authToken.accessToken]
      resolve(data)
    } aborted: { oldAuthToken, backend in
      let data = ["accessToken": ""]
      resolve(data)
      print("Login Aborted by User")
    } failure: { oldAuthToken, error, backend in
      print("Login Error: \(error.localizedDescription)")
      reject( "Accounts SDK Login Error", error.localizedDescription, error as NSError)
    }
  }
  
  
  @objc public func logout(_ resolve: @escaping (String) -> Void, reject: @escaping (_ code: String, _ message: String, _ error: NSError) -> Void) {
    
    TMAuthentication.shared.logout { backends in
      resolve("Logout Successful")
      print("Logout Completed")
      print(" - Backends Count: \(backends?.count ?? 0)")
    }
  }
  
  @objc public func refreshToken(_ resolve: @escaping ([String: Any]) -> Void, reject: @escaping (_ code: String, _ message: String, _ error: NSError) -> Void) {
    
    TMAuthentication.shared.validToken { authToken in
      print("Token Refreshed (if needed)")
      print(" - AuthToken: \(authToken.accessToken.prefix(20))...")
      let data = ["accessToken": authToken.accessToken]
      resolve(data)
    } aborted: { oldAuthToken, backend in
      print("Refresh Login Aborted by User")
      let data = ["accessToken": ""]
      resolve(data)
    } failure: { oldAuthToken, error, backend in
      print("Refresh Error: \(error.localizedDescription)")
      reject( "Accounts SDK Refresh Token Error", error.localizedDescription, error as NSError)
    }
  }
  
  @objc public func presentPrePurchaseVenue(_ venueId: String) {
    let viewController = PrePurchaseSdkViewController()
    viewController.setVenueId(venueIdProp: venueId)
    UIApplication.shared.windows.filter {$0.isKeyWindow}.first?.rootViewController?.present(viewController, animated: true)
  }
  
  @objc public func presentPrePurchaseAttraction(_ attractionId: String) {
    let viewController = PrePurchaseSdkViewController()
    viewController.setAttractionId(attractionIdProp: attractionId)
    UIApplication.shared.windows.filter {$0.isKeyWindow}.first?.rootViewController?.present(viewController, animated: true)
  }
  
  @objc public func presentPurchase(_ venueId: String) {
    let viewController = PurchaseSdkViewController()
    viewController.sendEventIdFromView(eventIdProp: venueId)
    UIApplication.shared.windows.filter {$0.isKeyWindow}.first?.rootViewController?.present(viewController, animated: true)
  }
  
  @objc public func getMemberInfo(_ resolve: @escaping ([String: Any]) -> Void, reject: @escaping (_ code: String, _ message: String, _ error: NSError) -> Void) {
    
    TMAuthentication.shared.memberInfo { memberInfo in
      print("MemberInfo Completed")
      print(" - UserID: \(memberInfo.localID ?? "<nil>")")
      print(" - Email: \(memberInfo.email ?? "<nil>")")
      let data = ["memberInfoId": memberInfo.localID, "memberInfoEmail": memberInfo.email]
      resolve(data as [String : Any])
    } failure: { oldMemberInfo, error, backend in
      print("MemberInfo Error: \(error.localizedDescription)")
      reject( "Accounts SDK Member Info Error", error.localizedDescription, error as NSError)
    }
  }
  
  @objc public func getToken(_ resolve: @escaping ([String: Any]) -> Void, reject: @escaping (_ code: String, _ message: String, _ error: NSError) -> Void) {
    TMAuthentication.shared.validToken(showLoginIfNeeded: false) { authToken in
      print("Token Retrieved")
      let data = ["accessToken": authToken.accessToken]
      resolve(data)
    } aborted: { oldAuthToken, backend in
      print("Token Retrieval Aborted ")
      let data = ["accessToken": ""]
      resolve(data)
    } failure: { oldAuthToken, error, backend in
      print("Token Retrieval Error: \(error.localizedDescription)")
      reject( "Accounts SDK Token Retrieval Error", error.localizedDescription, error as NSError)
    }
  }
  
  @objc public func isLoggedIn(_ resolve: @escaping ([String: Bool]) -> Void, reject: @escaping (_ code: String, _ message: String, _ error: NSError) -> Void) {
    
    TMAuthentication.shared.memberInfo { memberInfo in
      guard let id = memberInfo.globalID else {
        resolve(["result": false])
        return
      }
      
      let hasToken = TMAuthentication.shared.hasToken()
      resolve(["result": hasToken])
      
    } failure: { oldMemberInfo, error, backend in
      reject("Accounts SDK Is Logged In Error", error.localizedDescription, error as NSError)
    }
  }
  
  func onStateChanged(backend: TicketmasterAuthentication.TMAuthentication.BackendService?, state: TicketmasterAuthentication.TMAuthentication.ServiceState, error: (Error)?) {
    print("Backend TicketmasterAuthentication \(state.rawValue)")
    switch state{
    case .serviceConfigurationStarted:
      return
    case .serviceConfigured:
      return
    case .serviceConfigurationCompleted:
      return
    case .loginStarted:
      // Replace return with below to send Native event to React Native side
      // self.sendEvent(withName: "loginStarted", body: ["loginStarted:": "loginStarted event sent"])
      return
    case .loginPresented:
      return
    case .loggedIn:
      return
    case .loginAborted:
      return
    case .loginFailed:
      return
    case .loginLinkAccountPresented:
      return
    case .loginCompleted:
      return
    case .tokenRefreshed:
      return
    case .logoutStarted:
      return
    case .loggedOut:
      return
    case .logoutCompleted:
      return
    @unknown default:
      return
    }
  }
}



