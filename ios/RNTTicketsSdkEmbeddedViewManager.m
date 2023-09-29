//
//  RNTTicketsSdkViewManager.m
//  RNTicketmasterDemoIntegration
//
//  Created by Daniel Olugbade on 24/08/2023.
//

#import <React/RCTViewManager.h>
// Needed to use Swift files in Objective-C files
#import "RNTicketmasterDemoIntegration-Swift.h"

@interface RNTTicketsSdkEmbeddedViewManager : RCTViewManager
@end

@implementation RNTTicketsSdkEmbeddedViewManager

- (dispatch_queue_t)methodQueue
{
  return dispatch_get_main_queue();
}

+ (BOOL)requiresMainQueueSetup {
  return true;
}

RCT_EXPORT_MODULE(RNTTicketsSdkEmbeddedView)


- (UIView *)view
{
  TicketsSdkEmbeddedViewController *vc = [[TicketsSdkEmbeddedViewController alloc] init];
  return vc.view;
}




@end

