package org.aicart.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class QueryParamConverter {

    public static List<Long> toLongList(String commaSeparatedValues) {
        if (commaSeparatedValues == null || commaSeparatedValues.isEmpty()) {
            return List.of();  // Return an empty list if the input is null or empty
        }

        return Arrays.stream(commaSeparatedValues.split(","))
                .map(String::trim)  // Remove any surrounding spaces
                .map(Long::valueOf) // Convert to Long
                .collect(Collectors.toList());
    }
}
