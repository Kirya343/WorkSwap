package org.workswap.review.controllers;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.workswap.review.dto.MyReviews;
import org.workswap.review.dto.ReviewCreateDTO;
import org.workswap.review.dto.ReviewDTO;
import org.workswap.review.services.ReviewCommandService;
import org.workswap.review.services.ReviewQueryService;
import org.workswap.sso.security.annotations.controllers.Authenticated;
import org.workswap.sso.security.annotations.controllers.PublicEndpoint;
import org.workswap.sso.security.annotations.controllers.RequiredPermission;
import org.workswap.sso.security.annotations.parameters.AuthUser;
import org.workswap.sso.security.dto.UserAuthData;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Profile("server")
@RequestMapping("/review")
public class ReviewController {

    private final ReviewCommandService reviewCommandService;
    private final ReviewQueryService reviewQueryService;

    @PostMapping
    @RequiredPermission("CREATE_REVIEW")
    public void addReview(
        @AuthUser UserAuthData authData,
        @RequestBody ReviewCreateDTO dto
    ) {
        reviewCommandService.createReview(dto, authData);
    }

    @GetMapping("/list")
    @PublicEndpoint
    public List<ReviewDTO> getRewiewsByItem(
        @RequestParam(required = false) Long listingId,
        @RequestParam(required = false) String profileSub
    ) {
        return reviewQueryService.getRewiewsList(listingId, profileSub);
    }

    @GetMapping("/my")
    @Authenticated
    public MyReviews getMyReviews(@AuthUser UserAuthData authData) {
        return reviewQueryService.getMyReviews(authData);
    }

    @GetMapping("/page")
    @RequiredPermission("GET_REVIEWS_PAGE")
    public Page<ReviewDTO> getRewiewsPage(
        @RequestParam int page,
        @RequestParam int size,
        @RequestParam String sortParam
    ) {
        return reviewQueryService.getRewiewsPage(page, size, sortParam);
    }
}
