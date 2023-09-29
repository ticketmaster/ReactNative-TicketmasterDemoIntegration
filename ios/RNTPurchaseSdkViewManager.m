//
//  PurchaseSdkViewManager.m
//  RNTicketmasterDemoIntegration
//
//  Created by Daniel Olugbade on 24/08/2023.
//

#import <React/RCTViewManager.h>
// Needed to use Swift files in Objective-C files
#import "RNTicketmasterDemoIntegration-Swift.h"

@interface RNTPurchaseSdkViewManager : RCTViewManager
@end

@implementation RNTPurchaseSdkViewManager

- (dispatch_queue_t)methodQueue
{
  return dispatch_get_main_queue();
}

+ (BOOL)requiresMainQueueSetup {
  return true;
}

RCT_EXPORT_MODULE(RNTPurchaseSdkView)
RCT_EXPORT_VIEW_PROPERTY(eventIdProp, NSString)

- (UIView *)view
{
  PurchaseSdkViewController *vc = [[PurchaseSdkViewController alloc] init];
  return vc.view;
}

@end
