package it.unibo.frontend.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

/**
 * Gateway responsible for routing clients' requests to the correct services.
 * The supported routes are as follows:
 * <ul>
 *     <li>{@code /auth/**}: Routes to the "Authenticator" service.</li>
 *     <li>{@code /queries/**}: Routes to the "Queries" service.</li>
 *     <li>{@code /matchmaker/**}: Routes to the "Matchmaker" service.</li>
 * </ul>
 * Any route not belonging to the ones listed above will return a status {@code 404} response.<br><br>
 * This service requires the following parameters, in the form of environment variables:
 * <ul>
 *     <li>{@code AUTHENTICATOR_URI} The absolute URI of the Authenticator service.</li>
 *     <li>{@code QUERIES_URI} The absolute URI of the Queries service.</li>
 *     <li>{@code MATCHMAKER_URI} The absolute URI of the Matchmaker service.</li>
 * </ul>
 * These variables are mandatory, so this Gateway will not start if at least one of them is not specified.
 */
@SpringBootApplication
public class FrontEndGateway {
    private final String authenticatorURI;
    private final String queriesURI;
    private final String matchmakerURI;
    private final Logger logger = LoggerFactory.getLogger(FrontEndGateway.class);

    static void main(String[] args) {
        SpringApplication.run(FrontEndGateway.class, args);
    }

    public FrontEndGateway() {
        this.authenticatorURI = validateEnvironmentVariable("AUTHENTICATOR_URI");
        this.queriesURI = validateEnvironmentVariable("QUERIES_URI");
        this.matchmakerURI = validateEnvironmentVariable("MATCHMAKER_URI");
    }

    private String validateEnvironmentVariable(final String name) {
        String value = System.getenv(name);
        if (value == null) {
            throw new IllegalStateException("Environment variable \"" + name + "\" was not specified.");
        }
        this.logger.info("Setting {} to: {}", name, value);
        return value;
    }

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p.path("/auth/**")
                        .uri(this.authenticatorURI))
                .route(p -> p.path("/queries/**")
                        .uri(this.queriesURI))
                .route(p -> p.path("/matchmaker/**")
                        .uri(this.matchmakerURI))
                .build();
    }
}
