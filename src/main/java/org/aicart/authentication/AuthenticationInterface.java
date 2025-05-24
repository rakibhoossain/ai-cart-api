package org.aicart.authentication;

import jakarta.ws.rs.core.Response;
import org.aicart.auth.dto.LoginCredentialDTO;
import org.aicart.auth.dto.OauthLoginDTO;

public interface AuthenticationInterface {
    Response login(LoginCredentialDTO loginCredentialDTO);
    Response oauthLogin(OauthLoginDTO oauthLoginDTO);
}
