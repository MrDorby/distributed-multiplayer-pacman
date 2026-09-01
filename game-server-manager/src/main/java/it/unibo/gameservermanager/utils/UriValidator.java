package it.unibo.gameservermanager.utils;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Class responsible for the validation of URI strings.
 */
public class UriValidator {
    /**
     * Validates the specified URI string, returning the corresponding validated URI.
     * The specified URI is considered invalid if there are any syntax errors in it, or in case it's not an absolute URI.
     * @param uriString the URI string to validate.
     * @param uriName the name of the URI to be shown in error messages.
     * @return the corresponding validated URI.
     * @throws IllegalArgumentException if the URI is not absolute, or if there are any syntax errors in the URI.
     */
    public static URI validateURI(final String uriString, final String uriName) {
        try {
            final URI uri = new URI(uriString);
            if (!uri.isAbsolute()) {
                throw new IllegalArgumentException("The specified " + uriName + " is not absolute.");
            }
            return uri;
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Syntax error in the specified " + uriName + ": " + e);
        }
    }
}
