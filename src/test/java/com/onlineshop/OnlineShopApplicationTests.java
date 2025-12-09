package com.onlineshop;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 應用程序測試類
 */
@SpringBootTest
@ActiveProfiles("test")
class OnlineShopApplicationTests {

    @Test
    void contextLoads() {
        // 測試Spring上下文加載
    }

    @Test
    void testApplicationStartup() {
        // 測試應用程序啟動
        OnlineShopApplication.main(new String[]{});
    }
}
