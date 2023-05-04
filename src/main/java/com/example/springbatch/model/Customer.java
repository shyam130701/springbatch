package com.example.springbatch.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Setter
public class Customer {

    @Id
    private Long id;

    private String name;

    private Integer age;
}
