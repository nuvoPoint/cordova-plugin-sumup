#import "CDVSumUp.h"
#import <SumUpSDK/SumUpSDK.h>

@implementation CDVSumUp

-(void) login:(CDVInvokedUrlCommand *)command {
  [[NSBundle mainBundle] infoDictionary];
  NSDictionary* infoDict = [[NSBundle mainBundle] infoDictionary];
  NSString* apikey = [infoDict objectForKey:@"SUMUP_API_KEY"];
  [SMPSumUpSDK setupWithAPIKey:apikey];
  
  if (command.arguments && [command.arguments count] > 0) {
    NSString* accessToken = [command.arguments objectAtIndex:0];
    [SMPSumUpSDK loginWithToken:accessToken completion:^(BOOL success, NSError *error) {
      CDVPluginResult* pluginResult = nil;
      if (success) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
      } else {
        NSInteger errorCode = [error code];
        NSDictionary *dict = @{
                               @"code" : @(errorCode),
                               @"message" : @"Login failed",
                               };
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:dict];
      }
      [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
  } else {
    [SMPSumUpSDK presentLoginFromViewController:self.viewController
        animated:YES
        completionBlock:^(BOOL success, NSError *error) {

        CDVPluginResult* pluginResult = nil;
        if (success) {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        } else {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
        }
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
  }
}

-(void) logout:(CDVInvokedUrlCommand *)command {
  [SMPSumUpSDK logoutWithCompletionBlock:^(BOOL success, NSError *error) {
      CDVPluginResult* pluginResult = nil;
      if (success) {
          pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
      } else {
        NSDictionary *dict = @{
                               @"code" : @0,
                               @"message" : @"Logout failed"
                               };
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:dict];
      }
      [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
  }];
}

-(void) settings:(CDVInvokedUrlCommand *)command {
  [SMPSumUpSDK presentCheckoutPreferencesFromViewController:self.viewController
   animated:YES
   completion:^(BOOL success, NSError * _Nullable error) {
     CDVPluginResult* pluginResult = nil;
     if (success) {
       pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
     } else {
       NSInteger errorCode = [error code];
       NSString *msg = @"";
       
       if (errorCode == SMPSumUpSDKErrorAccountNotLoggedIn) {
         msg = @"User is not logged in";
       }
       
       NSDictionary *dict = @{
                              @"code" : @(errorCode),
                              @"message" : msg
                              };
       pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:dict];
     }
     [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
   }];
}

-(void) isLoggedIn:(CDVInvokedUrlCommand *)command {
  BOOL isLoggedIn = [SMPSumUpSDK isLoggedIn];

  CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:isLoggedIn];
  [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

-(void) prepare:(CDVInvokedUrlCommand *)command {
  [SMPSumUpSDK prepareForCheckout];
  CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
  [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

-(void) auth:(CDVInvokedUrlCommand *)command {
  [[NSBundle mainBundle] infoDictionary];
  NSDictionary* infoDict = [[NSBundle mainBundle] infoDictionary];
  NSString* apikey = [infoDict objectForKey:@"SUMUP_API_KEY"];
  [SMPSumUpSDK setupWithAPIKey:apikey];
  
  if (command.arguments && [command.arguments count] > 0) {
    NSString* accessToken = [command.arguments objectAtIndex:0];
    [SMPSumUpSDK loginWithToken:accessToken completion:^(BOOL success, NSError *error) {
      CDVPluginResult* pluginResult = nil;
      if (success) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
      } else {
        NSInteger errorCode = [error code];
        NSDictionary *dict = @{
                               @"code" : @(errorCode),
                               @"message" : @"Login failed",
                               };
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:dict];
      }
      [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
  } else {
    [SMPSumUpSDK presentLoginFromViewController:self.viewController
        animated:YES
        completionBlock:^(BOOL success, NSError *error) {

        CDVPluginResult* pluginResult = nil;
        if (success) {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        } else {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
        }
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
  }
}

-(void) close:(CDVInvokedUrlCommand *)command {
  CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
  [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

-(void) pay:(CDVInvokedUrlCommand *)command {
    NSDecimal total = [(NSNumber*)[command.arguments objectAtIndex:0] decimalValue];
    NSString* currency = [command.arguments objectAtIndex:1];
    NSString* title = [command.arguments objectAtIndex:2];

    CDVPluginResult* pluginResult = nil;
    SMPCheckoutRequest *request = [SMPCheckoutRequest requestWithTotal:[NSDecimalNumber decimalNumberWithDecimal:total] title:title
        currencyCode:currency
        paymentOptions:SMPPaymentOptionAny];

    [request setSkipScreenOptions:SMPSkipScreenOptionSuccess];

    [SMPSumUpSDK checkoutWithRequest:request fromViewController:self.viewController completion:^(SMPCheckoutResult *result, NSError *error) {
        CDVPluginResult* pluginResult = nil;

        if (result.success) {
//          for(NSString *key in [result.additionalInfo allKeys]) {
//            NSLog(@"%@ : %@", key, [result.additionalInfo objectForKey:key]);
//          }
          NSDictionary *card = result.additionalInfo[@"card"];
          NSDictionary *dict = @{
                                 @"transaction_code" : result.additionalInfo[@"transaction_code"],
                                 @"card_type" : card[@"type"],
                                 @"merchant_code" : result.additionalInfo[@"merchant_code"],
                                 @"amount" : result.additionalInfo[@"amount"],
                                 @"tip_amount" : result.additionalInfo[@"tip_amount"],
                                 @"vat_amount" : result.additionalInfo[@"vat_amount"],
                                 @"currency" : result.additionalInfo[@"currency"],
                                 @"status" : result.additionalInfo[@"status"],
                                 @"payment_type" : result.additionalInfo[@"payment_type"],
                                 @"entry_mode" : result.additionalInfo[@"entry_mode"],
                                 @"installments" : result.additionalInfo[@"installments"],
                                 };
          pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:dict];
        } else {
          NSInteger errorCode = [error code];
          NSDictionary *dict = @{
                                 @"code" : @(errorCode),
                                 @"message" : @"",
                                 };
          pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:dict];
        }

        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];

    if (![SMPSumUpSDK checkoutInProgress]) {
      NSDictionary *dict = @{
                             @"code" : @51,
                             @"message" : @""
                             };
      pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:dict];
      [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

@end
