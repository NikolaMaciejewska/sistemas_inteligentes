package org.example.models;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Ingredient {
    private String name;
    private double amount;
    private String unit;

    public Ingredient(String name, double amount, String unit) {
        this.name = name;
        this.amount = amount;
        this.unit = unit;
    }
}