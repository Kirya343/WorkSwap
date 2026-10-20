package org.workswap.listing.exceptions;

import org.springframework.security.access.AccessDeniedException;

public class ListingAccessDeniedException extends AccessDeniedException {
    
    public ListingAccessDeniedException() {
        super("You don't have enougth permission to access to this listing");
    }

    public ListingAccessDeniedException(String message) {
        super(message);
    }

    public ListingAccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}