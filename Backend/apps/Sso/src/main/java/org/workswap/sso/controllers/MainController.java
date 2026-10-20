package org.workswap.sso.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.workswap.sso.security.annotations.controllers.PublicEndpoint;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainController {
    
    @GetMapping
    @PublicEndpoint
    public String redirectToAuth() {
        return "redirect:/auth";
    }

    @GetMapping("/auth")
    @PublicEndpoint
    public String getAuthPage() {
        return "auth-page";
    }
}
