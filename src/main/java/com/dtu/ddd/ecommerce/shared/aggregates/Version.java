package com.dtu.ddd.ecommerce.shared.aggregates;

public record Version(int version) {
    public static Version zero() {
        return new Version(0);
    }
}
