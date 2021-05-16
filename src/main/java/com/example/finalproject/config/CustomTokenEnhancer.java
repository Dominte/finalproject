//package com.example.finalproject.config;
//
//import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
//import org.springframework.security.oauth2.common.OAuth2AccessToken;
//import org.springframework.security.oauth2.provider.OAuth2Authentication;
//import org.springframework.security.oauth2.provider.token.TokenEnhancer;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//
//public class CustomTokenEnhancer implements TokenEnhancer {
//    @Override
//    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
//        Map<String, Object> additionalInfo = new HashMap<>();
//
//        String browserId = UUID.randomUUID().toString();
//
//        if (oAuth2Authentication.getOAuth2Request().getScope().contains("mobile"))
//            additionalInfo.put("nodeId", oAuth2Authentication.getOAuth2Request().getRequestParameters().get("nodeId"));
//        else {
//            additionalInfo.put("browserId", browserId);
//        }
//
//        ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(additionalInfo);
//
//        return oAuth2AccessToken;
//    }
//}
