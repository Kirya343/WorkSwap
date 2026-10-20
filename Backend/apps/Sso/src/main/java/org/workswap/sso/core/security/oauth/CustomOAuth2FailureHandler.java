package org.workswap.sso.core.security.oauth;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomOAuth2FailureHandler implements AuthenticationFailureHandler {

    @Value("${app.url}")
    private String baseUrl;

    @Override
    public void onAuthenticationFailure(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException exception
    ) throws IOException, ServletException {

        response.setContentType("text/html;charset=UTF-8");
        String script = """
            <html>
            <body>
                <script>
                const targetOrigin = '%s';

                if (window.opener) {
                    window.opener.postMessage({ type: 'oauthFailure' }, targetOrigin || '*');
                    window.close();
                } else {
                    window.location.href = redirect;
                }
                </script>
            </body>
            </html>
        """.formatted(baseUrl);

        response.getWriter().write(script);
    }
}