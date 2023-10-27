package com.claon.user.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.UUIDSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

import java.util.List;
import java.util.Optional;

@OpenAPIDefinition(info = @Info(title = "CLAON User API", version = "v1"))
@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi customTestOpenAPi() {
        String[] paths = {"/**"};

        return GroupedOpenApi
                .builder()
                .group("User API")
                .pathsToMatch(paths)
                .addOperationCustomizer(new GlobalOperationCustomizer())
                .build();
    }

    public static class GlobalOperationCustomizer implements OperationCustomizer {
        @Override
        public Operation customize(Operation operation, HandlerMethod handlerMethod) {
            List<Parameter> parameterList = new java.util.ArrayList<>(Optional.ofNullable(operation.getParameters())
                    .orElseGet(List::of)
                    .stream()
                    .map(p -> {
                        if (p.getName().equals("userInfo")) {
                            return new Parameter()
                                    .in(ParameterIn.HEADER.toString())
                                    .name("X-USER-ID")
                                    .schema(new UUIDSchema());
                        }
                        if (p.getName().equals("pageable")) {
                            var schema = new ObjectSchema();
                            schema.addProperty("page", new IntegerSchema());
                            schema.addProperty("size", new IntegerSchema());
                            return new Parameter()
                                    .in(ParameterIn.QUERY.toString())
                                    .required(true)
                                    .name("pageable")
                                    .schema(schema);
                        }
                        return p;
                    })
                    .toList());

            operation.setParameters(parameterList);
            return operation;
        }
    }
}
