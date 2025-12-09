package com.onlineshop.service;

import com.onlineshop.model.CartItem;
import com.onlineshop.model.User;
import com.onlineshop.model.Product;
import com.onlineshop.repository.CartItemRepository;
import com.onlineshop.repository.ProductRepository;
import com.onlineshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

/**
 * 購物車服務類
 */
@Service
@Transactional
public class CartService {
    
    @Autowired
    private CartItemRepository cartItemRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * 添加商品到購物車
     */
    public CartItem addToCart(Long userId, Long productId, Integer quantity) {
        User user = getUserById(userId);
        Product product = getProductById(productId);
        
        // 檢查庫存
        if (!product.hasSufficientStock(quantity)) {
            throw new IllegalArgumentException("庫存不足");
        }
        
        // 檢查是否已在購物車中
        CartItem existingItem = cartItemRepository.findByUserAndProduct(user, product).orElse(null);
        
        if (existingItem != null) {
            // 更新數量
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            return cartItemRepository.save(existingItem);
        } else {
            // 創建新項目
            CartItem newItem = new CartItem();
            newItem.setUser(user);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            return cartItemRepository.save(newItem);
        }
    }
    
    /**
     * 更新購物車項目數量
     */
    public CartItem updateCartItemQuantity(Long userId, Long productId, Integer quantity) {
        User user = getUserById(userId);
        Product product = getProductById(productId);
        
        // 檢查庫存
        if (!product.hasSufficientStock(quantity)) {
            throw new IllegalArgumentException("庫存不足");
        }
        
        CartItem cartItem = cartItemRepository.findByUserAndProduct(user, product)
            .orElseThrow(() -> new IllegalArgumentException("購物車項目不存在"));
        
        cartItem.setQuantity(quantity);
        return cartItemRepository.save(cartItem);
    }
    
    /**
     * 從購物車移除商品
     */
    public void removeFromCart(Long userId, Long productId) {
        User user = getUserById(userId);
        Product product = getProductById(productId);
        
        cartItemRepository.deleteByUserAndProduct(user, product);
    }
    
    /**
     * 清空購物車
     */
    public void clearCart(Long userId) {
        User user = getUserById(userId);
        cartItemRepository.deleteByUser(user);
    }
    
    /**
     * 獲取用戶購物車內容
     */
    public List<CartItem> getCartItems(Long userId) {
        User user = getUserById(userId);
        return cartItemRepository.findByUser(user);
    }
    
    /**
     * 計算購物車總金額
     */
    public BigDecimal calculateCartTotal(Long userId) {
        User user = getUserById(userId);
        BigDecimal total = cartItemRepository.calculateCartTotal(user);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    /**
     * 計算購物車項目數量
     */
    public Integer getCartItemCount(Long userId) {
        User user = getUserById(userId);
        Integer count = cartItemRepository.countItemsByUser(user);
        return count != null ? count : 0;
    }
    
    /**
     * 檢查購物車中是否有足夠庫存
     */
    public boolean validateCartStock(Long userId) {
        List<CartItem> cartItems = getCartItems(userId);
        
        for (CartItem item : cartItems) {
            if (!item.getProduct().hasSufficientStock(item.getQuantity())) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 根據ID獲取用戶
     */
    private User getUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("用戶不存在"));
    }
    
    /**
     * 根據ID獲取商品
     */
    private Product getProductById(Long productId) {
        return productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("商品不存在"));
    }
}
