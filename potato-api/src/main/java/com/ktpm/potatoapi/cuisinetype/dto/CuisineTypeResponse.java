package com.ktpm.potatoapi.cuisinetype.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CuisineTypeResponse {
    Long id;
    String name;
    String imgUrl;
    boolean isVisible;
}
