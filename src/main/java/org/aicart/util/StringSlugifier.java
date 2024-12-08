package org.aicart.util;

public class StringSlugifier {
    public static String slugify(String input) {
        String slug = input.toLowerCase();
        slug = slug.replaceAll("[^a-z0-9\\s-]", ""); // Remove unwanted characters
        slug = slug.replaceAll("\\s+", "-"); // Replace multiple spaces with a single dash
        slug = slug.replaceAll("[-]+", "-"); // Replace multiple dashes with a single dash
        slug = slug.trim(); // Trim leading/trailing dashes
        return slug;
    }
}
