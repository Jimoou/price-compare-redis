package com.example.pricecompare.Controller;

import com.example.pricecompare.Service.LowestPriceService;
import com.example.pricecompare.Vo.Keyword;
import com.example.pricecompare.Vo.Product;
import com.example.pricecompare.Vo.ProductGroup;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Set;

@RestController
@RequestMapping("/")
@AllArgsConstructor
public class LowestPriceController {

  private LowestPriceService lowestPriceService;

  @Operation(summary = "key에 해당하는 상품 정보", description = "주어진 key에 해당하는 상품 정보를 Redis에서 가져옵니다.")
  @GetMapping("/get-zset-value")
  public Set GetZsetValue(String key) {
    return lowestPriceService.getZsetValue(key);
  }

  @Operation(summary = "새 상품 추가", description = "새 상품을 Redis에 추가하고, 상품의 순위를 반환합니다")
  @PutMapping("/product-add")
  public int SetNewProduct(@RequestBody Product newProduct) {
    return lowestPriceService.setNewProduct(newProduct);
  }

  @Operation(summary = "새 상품 그룹 추가", description = "새 상품 그룹을 Redis에 추가하고, 상품 그룹의 크기를 반환합니다.")
  @PutMapping("/propduct-group")
  public int SetNewProductGroup(@RequestBody ProductGroup newProduct) {
    return lowestPriceService.setNewProductGroup(newProduct);
  }

  @Operation(summary = "새 상품 그룹을 키워드에 추가", description = "새 상품 그룹을 키워드에 추가하고, 상품 그룹의 순위를 반환합니다.")
  @PutMapping("/product-group-to-keyword")
  public int SetNewProductGrpToKeyword(String keyword, String prodGroupId, double score) {
    return lowestPriceService.setNewProductGrpToKeyword(keyword, prodGroupId, score);
  }

  @Operation(summary = "키워드에 해당하는 최저가 상품", description = "주어진 키워드에 해당하는 가장 저렴한 상품을 가져옵니다.")
  @GetMapping("/prodcut-price/lowest")
  public Keyword GetLowestPriceProductByKeyword(String keyword) {
    return lowestPriceService.getLowestPriceProductByKeyword(keyword);
  }
}
