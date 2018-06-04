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
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
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
          pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
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
       pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
     }
     [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
   }];
}

-(void) checkoutPreferences:(CDVInvokedUrlCommand *)command {
  [SMPSumUpSDK presentCheckoutPreferencesFromViewController:self.viewController
      animated:YES
      completion:^(BOOL success, NSError *_Nullable error) {

      CDVPluginResult* pluginResult = nil;
      if (success) {
          pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
      } else {
          pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
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
}

-(void) pay:(CDVInvokedUrlCommand *)command {
    NSDecimal total = [(NSNumber*)[command.arguments objectAtIndex:0] decimalValue];
    NSString* currency = [command.arguments objectAtIndex:1];
    NSString* title = [command.arguments objectAtIndex:2];

    CDVPluginResult* pluginResult = nil;
    SMPCheckoutRequest *request = [SMPCheckoutRequest requestWithTotal:[NSDecimalNumber decimalNumberWithDecimal:total] title:title
        //currencyCode:[[SMPSumUpSDK currentMerchant] currencyCode]
        currencyCode:currency
        paymentOptions:SMPPaymentOptionAny];

    [request setSkipScreenOptions:SMPSkipScreenOptionSuccess];
    //[request setForeignTransactionID:[NSString stringWithFormat:foreignTransactionID]];

    [SMPSumUpSDK checkoutWithRequest:request fromViewController:self.viewController completion:^(SMPCheckoutResult *result, NSError *error) {
        CDVPluginResult* pluginResult = nil;

        if (result.success) {
          for(NSString *key in [result.additionalInfo allKeys]) {
            NSLog(@"%@ : %@", key, [result.additionalInfo objectForKey:key]);
          }
          NSDictionary *card = result.additionalInfo[@"card"];
          NSArray *array = @[result.additionalInfo[@"transaction_code"], card[@"type"]];
          pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:array];
        } else {
          NSLog(@"%@", [error localizedDescription]);
          pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
        }

        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];

    if (![SMPSumUpSDK checkoutInProgress]) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

@end
