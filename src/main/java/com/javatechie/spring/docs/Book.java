package com.javatechie.spring.docs;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Book {

    private int id;
    private String name;
    private double price;
}
