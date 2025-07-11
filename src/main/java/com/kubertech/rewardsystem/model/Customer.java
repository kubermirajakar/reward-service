package com.kubertech.rewardsystem.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {
    @Id
    @NotBlank(message = "Customer ID is mandatory")
    private String id;

    @NotBlank(message = "Customer name is mandatory")
    private String name;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)

    @JsonManagedReference
    private List<Transaction> transactions;
}
