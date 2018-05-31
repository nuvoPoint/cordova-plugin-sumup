#import <Cordova/CDV.h>

@interface CDVSumup : CDVPlugin

-(void) login:(CDVInvokedUrlCommand*)command;
-(void) logout:(CDVInvokedUrlCommand*)command;
-(void) checkoutPreferences:(CDVInvokedUrlCommand*)command;
-(void) isLoggedIn:(CDVInvokedUrlCommand*)command;
-(void) pay:(CDVInvokedUrlCommand*)command;

@end