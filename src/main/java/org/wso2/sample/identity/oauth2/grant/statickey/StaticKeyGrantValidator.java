package org.wso2.sample.identity.oauth2.grant.statickey;

import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.validators.AbstractValidator;
import javax.servlet.http.HttpServletRequest;

public class StaticKeyGrantValidator extends AbstractValidator<HttpServletRequest> {

    public StaticKeyGrantValidator() {
        requiredParams.add("static_key");
        requiredParams.add(OAuth.OAUTH_CLIENT_ID);
    }

    @Override
    public void validateMethod(HttpServletRequest request) throws OAuthProblemException {
        String method = request.getMethod();

        if (!OAuth.HttpMethod.POST.equals(method)) {
            throw OAuthProblemException.error(OAuthError.CodeResponse.INVALID_REQUEST, "Method not supported: " + method);
        }
    }

    @Override
    public void validateContentType(HttpServletRequest request) throws OAuthProblemException {
        String contentType = request.getContentType();

        if (!OAuth.ContentType.URL_ENCODED.equals(contentType)) {
            throw OAuthProblemException.error(OAuthError.CodeResponse.INVALID_REQUEST, "Content type not supported: " + contentType);
        }
    }
}
