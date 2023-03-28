package com.example.pricecompare.Service;

import com.example.pricecompare.Vo.Keyword;
import com.example.pricecompare.Vo.Product;
import com.example.pricecompare.Vo.ProductGroup;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@AllArgsConstructor
public class LowestPriceService {

  private final RedisTemplate myProductPriceRedis;

  /* 주어진 key에 해당하는 상품 정보를 Redis에서 가져옵니다. */
  public Set getZsetValue(String key) {
    return myProductPriceRedis.opsForZSet().rangeWithScores(key, 0, 9);
  }

  /* 새 상품을 Redis에 추가하고, 상품의 순위를 반환합니다. */
  public int setNewProduct(Product newProduct) {
    myProductPriceRedis
        .opsForZSet()
        .add(newProduct.getProductId(), newProduct.getProductId(), newProduct.getPrice());
    return myProductPriceRedis
        .opsForZSet()
        .rank(newProduct.getProductId(), newProduct.getProductId())
        .intValue();
  }

  /* 새 상품 그룹을 Redis에 추가하고, 상품 그룹의 크기를 반환합니다. */
  public int setNewProductGroup(ProductGroup newProductGroup) {
    List<Product> product = newProductGroup.getProductList();
    String productId = product.get(0).getProductId();
    int price = product.get(0).getPrice();
    myProductPriceRedis.opsForZSet().add(newProductGroup.getProductGroupId(), productId, price);
    return myProductPriceRedis.opsForZSet().zCard(newProductGroup.getProductGroupId()).intValue();
  }

  /* 새 상품 그룹을 키워드에 추가하고, 상품 그룹의 순위를 반환합니다. */
  public int setNewProductGrpToKeyword(String keyword, String prodGroupId, double score) {
    myProductPriceRedis.opsForZSet().add(keyword, prodGroupId, score);
    return myProductPriceRedis.opsForZSet().rank(keyword, prodGroupId).intValue();
  }

  /* 주어진 키워드에 해당하는 가장 저렴한 상품을 가져옵니다. */
  public Keyword getLowestPriceProductByKeyword(String keyword) {
    Keyword returnInfo = new Keyword();
    List<ProductGroup> tempProductGrp = getProdGrpUsingKeyword(keyword);


    returnInfo.setKeyword(keyword);
    returnInfo.setProductGroups(tempProductGrp);

    return returnInfo;
  }

  /* 주어진 키워드에 해당하는 상품 그룹 목록을 가져옵니다. */
  public List<ProductGroup> getProdGrpUsingKeyword(String keyword) {
    List<ProductGroup> productGroups = new ArrayList<>();
    // 상품 그룹 ID 목록을 가져옵니다.
    List<String> productGrpIdList =
        List.copyOf(myProductPriceRedis.opsForZSet().reverseRange(keyword, 0, 9));
    ObjectMapper objectMapper = new ObjectMapper();

    // 각 상품 그룹 ID에 대해
    for (final String productGrpId : productGrpIdList) {
      // 상품과 가격 목록을 가져옵니다.
      Set productAndPriceList =
          myProductPriceRedis.opsForZSet().rangeWithScores(productGrpId, 0, 9);
      List<Product> productList = new ArrayList<>();

      // 상품과 가격 목록을 순회하며 상품 객체를 생성하고 상품 목록에 추가합니다.
      productAndPriceList.forEach(
          productPriceObj -> {
            Map<String, Object> productPriceMap =
                objectMapper.convertValue(productPriceObj, Map.class);

            Product tempProduct = new Product();
            tempProduct.setProductId(productPriceMap.get("value").toString());
            tempProduct.setPrice((int) Double.parseDouble(productPriceMap.get("score").toString()));
            productList.add(tempProduct);
          });

      // 상품 그룹 객체를 생성하고 결과 목록에 추가합니다.
      ProductGroup productGroup = new ProductGroup(productGrpId, productList);
      productGroups.add(productGroup);
    }

    return productGroups;
  }
}
