package com.cryptoswarm.customer;

import java.util.Map;

public record ProblemDetailResponse(
        String type,
        String title,
        int status,
        String detail,
        String instance,
        String timestamp,
        Map<String, String> errors) {
}
