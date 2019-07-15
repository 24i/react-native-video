//
//  NSObject+AVPlayerViewController_Fullscreen.h
//  RCTVideo
//
//  Created by Ash on 2019-04-24.
//  Copyright © 2019 Facebook. All rights reserved.
//

#import <AVKit/AVKit.h>
#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface AVPlayerViewController (Fullscreen)

-(void)goFullscreenWithCompletionHandler:(void (^)(void)) completionBlock;

@end

NS_ASSUME_NONNULL_END
