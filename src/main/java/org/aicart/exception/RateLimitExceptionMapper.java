package org.aicart.exception;

import io.smallrye.faulttolerance.api.RateLimitException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;

@Provider
public class RateLimitExceptionMapper implements ExceptionMapper<RateLimitException> {

    @Override
    public Response toResponse(RateLimitException exception) {

        long retryAfterSeconds = exception.getRetryAfterMillis() / 1000;
        retryAfterSeconds = retryAfterSeconds == 0 ? 1 : retryAfterSeconds;

        return Response.status(Response.Status.TOO_MANY_REQUESTS)
                .header("Retry-After", retryAfterSeconds) // Standard HTTP header
                .entity(Map.of(
                        "error", "Rate limit exceeded. Please try again later.",
                        "retry_after_seconds", retryAfterSeconds
                ))
                .build();
    }
}