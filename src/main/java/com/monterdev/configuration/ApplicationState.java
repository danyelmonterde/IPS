package com.monterdev.configuration;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicationState {

    private String username;
    private String productCategory;
}
