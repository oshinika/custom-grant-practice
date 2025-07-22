package org.wso2.sample.identity.oauth2.grant.statickey;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.validators.AbstractValidator;
import javax.servlet.http.HttpServletRequest;

public class StaticKeyGrantValidator extends AbstractValidator<HttpServletRequest> {

    private static final Log log = LogFactory.getLog(StaticKeyGrantValidator.class);

    public StaticKeyGrantValidator() {

    }

    @Override
    public void validateMethod(HttpServletRequest request) throws OAuthProblemException {
        String method = request.getMethod();
        if (!OAuth.HttpMethod.POST.equals(method)) {
            log.warn("Invalid HTTP method: '" + method + "'. Expected: POST.");
            throw OAuthProblemException.error(OAuthError.CodeResponse.INVALID_REQUEST, "Method not supported: " + method);
        }
    }

    @Override
    public void validateContentType(HttpServletRequest request) throws OAuthProblemException {
        String contentType = request.getContentType();

        if (!"application/json".equals(contentType)) {
            log.warn("Invalid content type: '" + contentType + "'. Expected: application/json.");
            throw OAuthProblemException.error(OAuthError.CodeResponse.INVALID_REQUEST, "Content type not supported: " + contentType + ". Expected: application/json.");
        }
    }

    @Override
    public void validateRequiredParameters(HttpServletRequest request) throws OAuthProblemException {
        if (log.isDebugEnabled()) {
            log.debug("StaticKeyGrantValidator: Skipping specific parameter validation as request is JSON.");
        }
    }
}