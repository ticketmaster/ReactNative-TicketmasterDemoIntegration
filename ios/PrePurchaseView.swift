//
//  PrePurchaseView.swift
//  RNTicketmasterDemoIntegration
//
//  Created by Taka Goto on 9/25/23.
//

import Foundation
import UIKit

class PrePurchaseView: UIView {
  weak var delegate: SendAttractionIdDelegate?
  var attractionId: String = "attractionId"

  @objc(setAttractionIdProp:)
  public func setAttractionIdProp(_ attractionIdProp: NSString) {
    attractionId = attractionIdProp as String
  }
  
  public func getAttractionId() {
    delegate?.sendAttractionIdFromView(attractionIdProp: attractionId)
  }
}

protocol SendAttractionIdDelegate: AnyObject {
  func sendAttractionIdFromView(attractionIdProp: String)
}
