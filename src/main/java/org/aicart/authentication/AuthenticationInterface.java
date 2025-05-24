package org.aicart.authentication;

import jakarta.ws.rs.core.Response;
import org.aicart.authentication.dto.LoginCredentialDTO;
import org.aicart.authentication.dto.OauthLoginDTO;

public interface AuthenticationInterface {
    Response login(LoginCredentialDTO loginCredentialDTO);
    Response oauthLogin(OauthLoginDTO oauthLoginDTO);
}
