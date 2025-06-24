package org.aicart.util;

import jakarta.enterprise.context.ApplicationScoped;
import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

@ApplicationScoped
public class SlugGenerator {
    
    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern MULTIPLE_DASHES = Pattern.compile("-+");
    
    public String generateSlug(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String nowhitespace = WHITESPACE.matcher(normalized).replaceAll("-");
        String lowercase = nowhitespace.toLowerCase(Locale.ENGLISH);
        String nonlatin = NONLATIN.matcher(lowercase).replaceAll("");
        String result = MULTIPLE_DASHES.matcher(nonlatin).replaceAll("-");
        
        // Remove leading and trailing dashes
        if (result.startsWith("-")) {
            result = result.substring(1);
        }
        if (result.endsWith("-")) {
            result = result.substring(0, result.length() - 1);
        }
        
        return result;
    }
}