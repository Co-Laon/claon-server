package com.claon.user.common.domain;

import lombok.Data;

@Data
public class SwaggerPageable {
    private final Integer page;
    private final Integer size;
}
