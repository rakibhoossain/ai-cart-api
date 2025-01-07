package org.aicart.auth;

import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.Path;

import java.util.Arrays;
import java.util.HashSet;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.jwt.Claims;

import io.smallrye.jwt.build.Jwt;

//import org.jboss.resteasy.reactive.NoCache;
//
//import io.quarkus.security.Authenticated;
//import io.quarkus.security.identity.SecurityIdentity;
//import jakarta.inject.Inject;
//import jakarta.ws.rs.GET;
//import jakarta.ws.rs.Path;
//import org.jboss.resteasy.reactive.NoCache;

import org.eclipse.microprofile.jwt.JsonWebToken;

@Path("/auth")
public class AuthResource {

    @Inject
    SecurityIdentity identity;

    @Inject
    JsonWebToken jwt;

    @GET
    @Path("/login")
    public String login() {
        String token =
                Jwt.issuer("https://quarkus.io/using-jwt-rbac")
                        .subject("1234")
                        .upn("jdoe@quarkus.io") // User principal name (username)
                        .groups(new HashSet<>(Arrays.asList("User", "Admin"))) // User roles
                        .claim(Claims.exp, Long.MAX_VALUE)
                        .sign();
        return token;
    }


    @GET
    @Path("roles-allowed")
//    @RolesAllowed({ "User", "Admin" })
    @Produces(MediaType.TEXT_PLAIN)
    public String helloRolesAllowed(@Context SecurityContext ctx) {
        return getResponseString(ctx) + ", birthdate: " + jwt.getClaim("birthdate").toString();
    }


    private String getResponseString(SecurityContext ctx) {
        String name;
        if (ctx.getUserPrincipal() == null) {
            name = "anonymous";
        } else if (!ctx.getUserPrincipal().getName().equals(jwt.getName())) {
            throw new InternalServerErrorException("Principal and JsonWebToken names do not match");
        } else {
            name = ctx.getUserPrincipal().getName();
        }
        return String.format("hello %s,"
                        + " isHttps: %s,"
                        + " authScheme: %s,"
                        + " hasJWT: %s",
                name, ctx.isSecure(), ctx.getAuthenticationScheme(), hasJwt());
    }

    private boolean hasJwt() {
        return jwt.getClaimNames() != null;
    }
}

