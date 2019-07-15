//
//  NSObject+AVPlayerViewController_Fullscreen.m
//  RCTVideo
//
//  Created by Ash on 2019-04-24.
//  Copyright © 2019 Facebook. All rights reserved.
//

#import "AVPlayerViewController+Fullscreen.h"

@implementation AVPlayerViewController (Fullscreen)

-(void)goFullscreenWithCompletionHandler:(void (^)(void)) completionBlock {
  NSString *selectorForFullscreen = @"transitionToFullScreenViewControllerAnimated:completionHandler:";
  if (@available(iOS 11.3, *)) {
    selectorForFullscreen = @"transitionToFullScreenAnimated:interactive:completionHandler:";
  } else if (@available(iOS 11.0, *)) {
    selectorForFullscreen = @"transitionToFullScreenAnimated:completionHandler:";
  }
  SEL fsSelector = NSSelectorFromString([@"_" stringByAppendingString:selectorForFullscreen]);
  if ([self respondsToSelector:fsSelector]) {
    NSInvocation *inv = [NSInvocation invocationWithMethodSignature:[self methodSignatureForSelector:fsSelector]];
    [inv setSelector:fsSelector];
    [inv setTarget:self];
    
    NSInteger index = 2; //arguments 0 and 1 are self and _cmd respectively, automatically set
    BOOL animated = YES;
    [inv setArgument:&(animated) atIndex:index];
    index++;
    
    if (@available(iOS 11.3, *)) {
      BOOL interactive = YES;
      [inv setArgument:&(interactive) atIndex:index]; //arguments 0 and 1 are self and _cmd respectively, automatically set by NSInvocation
      index++;
    }
    
    [inv setArgument:&(completionBlock) atIndex:index];
    [inv invoke];
  }
}

@end
