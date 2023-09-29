//
//  PurchaseView.swift
//  RNTicketmasterDemoIntegration
//
//  Created by Taka Goto on 9/25/23.
//

import Foundation
import UIKit

class PurchaseView: UIView {
  weak var delegate: SendEventIdDelegate?
  var eventId: String = "eventId"

  @objc(setEventIdProp:)
  public func setEventIdProp(_ eventIdProp: NSString) {
    eventId = eventIdProp as String
  }
  
  public func getEventId() {
    delegate?.sendEventIdFromView(eventIdProp: eventId)
  }
}

protocol SendEventIdDelegate: AnyObject {
  func sendEventIdFromView(eventIdProp: String)
}
