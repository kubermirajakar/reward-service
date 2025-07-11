package com.kubertech.rewardsystem.model;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerBasicDTO {
    private String id;
    private String name;
}

