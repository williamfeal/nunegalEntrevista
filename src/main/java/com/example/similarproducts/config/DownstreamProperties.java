package com.example.similarproducts.config;

import jakarta.validation.constraints.NotBlank;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "catalog")
public record DownstreamProperties(
        @NotBlank String baseUrl,
        @DefaultValue("PT2S") Duration connectTimeout,
        @DefaultValue("PT3S") Duration readTimeout) {
}
