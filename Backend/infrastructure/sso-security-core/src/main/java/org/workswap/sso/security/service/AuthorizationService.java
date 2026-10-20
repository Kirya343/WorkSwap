package org.workswap.sso.security.service;

import java.lang.reflect.Method;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.workswap.sso.security.annotations.controllers.Authenticated;
import org.workswap.sso.security.annotations.controllers.PublicEndpoint;
import org.workswap.sso.security.annotations.controllers.RequiredPermission;
import org.workswap.sso.security.annotations.controllers.RequiredRole;
import org.workswap.sso.security.exceptions.ForbiddenException;
import org.workswap.sso.security.exceptions.UnauthenticatedException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component 
@Slf4j
@RequiredArgsConstructor 
public class AuthorizationService {
    
    public void authorize(
        Method method,
        Authentication authentication
    ) {

        Package controllerPackage =
                method.getDeclaringClass().getPackage();

        if (!controllerPackage.getName().startsWith("org.workswap")) {
            return;
        }
        
        log.debug("Handled method: {}", method.getName());

        boolean isPublic =
                method.isAnnotationPresent(PublicEndpoint.class);

        log.debug("Is public: {}", isPublic);

        boolean authenticated =
                authentication != null
                        && authentication.isAuthenticated();

        boolean requiresAuthentication =
                method.isAnnotationPresent(Authenticated.class);

        boolean requiresPermission =
                method.isAnnotationPresent(RequiredPermission.class);

        boolean requiresRole =
                method.isAnnotationPresent(RequiredRole.class);

        int securityAnnotations =
                (isPublic ? 1 : 0)
                + (requiresAuthentication ? 1 : 0)
                + (requiresPermission ? 1 : 0)
                + (requiresRole ? 1 : 0);

        if (securityAnnotations == 0) {
            throw new IllegalStateException(
                    "Endpoint must have a security annotation: "
                    + method.getDeclaringClass().getName()
                    + "#"
                    + method.getName()
            );
        }

        if (securityAnnotations > 1) {
            throw new IllegalStateException(
                    "Endpoint has multiple security annotations: "
                    + method.getDeclaringClass().getName()
                    + "#"
                    + method.getName()
            );
        }

                    
        log.debug("Is authenticated: {}", authenticated);

        if (isPublic) {
            // if (!authenticated) {
            //     response.setHeader(
            //             "X-User-Refresh",
            //             "true"
            //     );
            // }

            return;
        }

        if (!authenticated) {
            
            throw new UnauthenticatedException();
        }

        if (requiresPermission && !checkPermission(authentication, method)) {

            throw new ForbiddenException();
        }

        if (requiresRole && !checkRole(authentication, method)) {

            throw new ForbiddenException();
        }
    }

    public boolean hasRole(Authentication authentication, String role) {

        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }

    public boolean hasPermission(Authentication authentication, String permission) {

        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(permission));
    }

    private boolean checkPermission(Authentication authentication, Method method) {

        RequiredPermission requiredPermission =
            method.getAnnotation(RequiredPermission.class);
        
        log.debug("Need permission: {}", requiredPermission.value());

        if (!hasPermission(authentication, requiredPermission.value())) {
            return false;
        }

        log.debug("User has permission");

        return true;
    }

    private boolean checkRole(Authentication authentication, Method method) {

        RequiredRole requiredRole =
            method.getAnnotation(RequiredRole.class);

        log.debug("Need role: {}", requiredRole.value());

        if (!hasRole(authentication, requiredRole.value())) {
            return false;
        }

        log.debug("User has role");

        return true;
    }
}
