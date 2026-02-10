package com.vaadin.lab.ui;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
class CustomRedirectController {
    // TODO: vaadin navigation?
    @RequestMapping(value = "/custom-redirect", method = {RequestMethod.GET, RequestMethod.POST})
    public String doRedirect(@RegisteredOAuth2AuthorizedClient("authserver") OAuth2AuthorizedClient authorizedClient) {
        return "<script>window.close('','_parent','');;</script>";
    }
}
