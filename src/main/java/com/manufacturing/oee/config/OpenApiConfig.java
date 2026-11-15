package com.manufacturing.oee.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI 3 documentation configuration for the Manufacturing OEE Analytics System.
 * Configures the interactive Swagger UI and REST API documentation metadata.
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Bean
    public OpenAPI manufacturingOeeOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Manufacturing OEE Analytics System API")
                        .description("""
                                RESTful API specifications for the Manufacturing Overall Equipment Effectiveness (OEE) Analytics System.
                                
                                ### Domain Scope:
                                * **Master Data:** Plant, Line, Machine, Shift, Product, and Downtime Reason hierarchies.
                                * **Production Tracking:** Work Orders, Count pulses, and Correction ledgers.
                                * **Machine State:** Real-time state event streaming and downtime classification.
                                * **Quality Management:** Inspection records, Scrap vs. Rework quantification.
                                * **OEE Analytics:** Availability, Performance, Quality, and OEE aggregation pipelines.
                                * **Anomaly Engine:** Statistical detection and Ground-truth validation.
                                * **Audit & Traceability:** Immutable audit logging and reproducible synthetic data exports.
                                """)
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Manufacturing Analytics Engineering Team")
                                .email("engineering@manufacturing.internal"))
                        .license(new License()
                                .name("Internal Proprietary")
                                .url("https://manufacturing.internal/license")))
                .servers(List.of(
                        new Server()
                                .url(contextPath.isEmpty() ? "/" : contextPath)
                                .description("Local Development Server")
                ));
    }
}
