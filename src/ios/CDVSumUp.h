#import <Cordova/CDV.h>

@interface CDVSumUp : CDVPlugin

-(void) login:(CDVInvokedUrlCommand*)command;
-(void) logout:(CDVInvokedUrlCommand*)command;
-(void) settings:(CDVInvokedUrlCommand*)command;
-(void) isLoggedIn:(CDVInvokedUrlCommand*)command;
-(void) pay:(CDVInvokedUrlCommand*)command;

@end
