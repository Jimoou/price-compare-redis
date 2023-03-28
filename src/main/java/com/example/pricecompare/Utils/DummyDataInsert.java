package com.example.pricecompare.Utils;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import java.util.UUID;

public class DummyDataInsert {

  public static void main(String[] args) {
    RedisClient redisClient = RedisClient.create("redis://<your-redis-host>:<your-redis-port>");
    StatefulRedisConnection<String, String> connection = redisClient.connect();
    RedisCommands<String, String> syncCommands = connection.sync();

    String[] keywords = {
      "keyword1", "keyword2","keyword3","keyword4","keyword5","keyword6","keyword7","keyword8",
     "keyword9", "keyword10","keyword11","keyword12","keyword13","keyword14","keyword15","keyword16",
    };

    for (String keyword : keywords) {
      for (int prodGrpIdx = 1; prodGrpIdx <= 10; prodGrpIdx++) {
        String prodGrpId = String.format("FPG%04d", prodGrpIdx);
        double minPrice = Double.MAX_VALUE;

        for (int prodIdx = 1; prodIdx <= 10; prodIdx++) {
          String productId = UUID.randomUUID().toString();
          int price = 10000 + (int) (Math.random() * 40000);

          // 제품 그룹에 제품 추가
          syncCommands.zadd(prodGrpId, price, productId);

          // 제품 그룹의 최소 가격 업데이트
          minPrice = Math.min(minPrice, price);
        }

        // 키워드에 제품 그룹 추가
        syncCommands.zadd(keyword, minPrice, prodGrpId);
      }
    }

    connection.close();
    redisClient.shutdown();
  }
}
