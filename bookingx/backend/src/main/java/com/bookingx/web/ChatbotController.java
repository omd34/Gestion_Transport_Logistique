package com.bookingx.web;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    record ChatRequest(BigDecimal budget, String city, String dateRange, List<String> preferences) {}

    @PostMapping("/suggest")
    public Map<String, Object> suggest(@RequestBody ChatRequest req) {
        // Placeholder: logic to compute best services by budget
        return Map.of(
                "budget", req.budget(),
                "suggestions", List.of(
                        Map.of("type", "hotel", "name", "Hotel Central", "price", req.budget().multiply(new BigDecimal("0.6"))),
                        Map.of("type", "transport", "name", "Airport Shuttle", "price", req.budget().multiply(new BigDecimal("0.1"))),
                        Map.of("type", "activity", "name", "City Tour", "price", req.budget().multiply(new BigDecimal("0.3")))
                )
        );
    }
}

