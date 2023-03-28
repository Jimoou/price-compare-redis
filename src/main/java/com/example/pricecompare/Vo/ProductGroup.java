package com.example.pricecompare.Vo;

import lombok.Data;
import java.util.List;

@Data
public class ProductGroup {
    private String productGroupId; // FPG0001
    private List<Product> productList; // [{key, value}, {}, ... ]}. "FPG0002"}
}
