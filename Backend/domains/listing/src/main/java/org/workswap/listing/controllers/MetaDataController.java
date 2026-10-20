package org.workswap.listing.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.workswap.listing.enums.PriceType;
import org.workswap.sso.security.annotations.controllers.PublicEndpoint;

import lombok.RequiredArgsConstructor;

@RestController
@Profile("server")
@RequiredArgsConstructor
@RequestMapping("/settings")
public class MetaDataController {
    
    @GetMapping("/price-types")
    @PublicEndpoint
    public List<String> getPriceTypes() {
        List<String> types = new ArrayList<>();
        for (PriceType p : PriceType.values()) types.add(p.toString());
        return types;
    }
}
