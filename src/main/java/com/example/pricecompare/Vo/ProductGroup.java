package com.example.pricecompare.Vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class ProductGroup {
    private String productGroupId; // FPG0001
    private List<Product> productList; // [{key, value}, {}, ... ]}. "FPG0002"}
}
