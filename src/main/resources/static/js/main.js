/* 線上購物系統JavaScript */

// 文檔加載完成後執行
document.addEventListener('DOMContentLoaded', function() {
    // 初始化工具提示
    initTooltips();
    
    // 初始化彈出框
    initPopovers();
    
    // 表單驗證
    initFormValidation();
    
    // 購物車功能
    initCartFunctions();
    
    // 商品搜索
    initProductSearch();
});

/**
 * 初始化Bootstrap工具提示
 */
function initTooltips() {
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
}

/**
 * 初始化Bootstrap彈出框
 */
function initPopovers() {
    var popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
    var popoverList = popoverTriggerList.map(function (popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl);
    });
}

/**
 * 初始化表單驗證
 */
function initFormValidation() {
    // 獲取所有需要驗證的表單
    var forms = document.querySelectorAll('.needs-validation');
    
    // 遍歷每個表單
    Array.prototype.slice.call(forms).forEach(function(form) {
        form.addEventListener('submit', function(event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            
            form.classList.add('was-validated');
        }, false);
    });
}

/**
 * 初始化購物車功能
 */
function initCartFunctions() {
    // 更新購物車數量
    updateCartItemCount();
    
    // 數量輸入框事件
    var quantityInputs = document.querySelectorAll('.quantity-input');
    quantityInputs.forEach(function(input) {
        input.addEventListener('change', function() {
            var productId = this.dataset.productId;
            var quantity = this.value;
            updateCartQuantity(productId, quantity);
        });
    });
    
    // 移除商品按鈕
    var removeButtons = document.querySelectorAll('.remove-from-cart');
    removeButtons.forEach(function(button) {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            var productId = this.dataset.productId;
            removeFromCart(productId);
        });
    });
}

/**
 * 更新購物車項目數量
 */
function updateCartItemCount() {
    // 這裡可以從服務器獲取購物車數量
    // 暫時使用本地存儲
    var cartCount = localStorage.getItem('cartItemCount') || 0;
    var cartBadges = document.querySelectorAll('.cart-badge');
    
    cartBadges.forEach(function(badge) {
        badge.textContent = cartCount;
        if (cartCount > 0) {
            badge.style.display = 'inline-block';
        } else {
            badge.style.display = 'none';
        }
    });
}

/**
 * 更新購物車商品數量
 */
function updateCartQuantity(productId, quantity) {
    // 發送AJAX請求更新購物車
    fetch('/cart/update', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: 'productId=' + productId + '&quantity=' + quantity
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            showToast('購物車已更新', 'success');
            updateCartItemCount();
            // 更新頁面上的小計和總計
            updateCartTotals();
        } else {
            showToast(data.message || '更新失敗', 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showToast('網絡錯誤，請重試', 'error');
    });
}

/**
 * 從購物車移除商品
 */
function removeFromCart(productId) {
    if (!confirm('確定要從購物車移除這個商品嗎？')) {
        return;
    }
    
    fetch('/cart/remove', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: 'productId=' + productId
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            showToast('商品已移除', 'success');
            updateCartItemCount();
            // 重新加載頁面或移除DOM元素
            location.reload();
        } else {
            showToast(data.message || '移除失敗', 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showToast('網絡錯誤，請重試', 'error');
    });
}

/**
 * 更新購物車總計
 */
function updateCartTotals() {
    // 這裡可以重新計算購物車總金額
    // 實際應用中應該從服務器獲取
}

/**
 * 初始化商品搜索
 */
function initProductSearch() {
    var searchInput = document.querySelector('.search-input');
    if (searchInput) {
        // 實時搜索建議（可選）
        searchInput.addEventListener('input', debounce(function() {
            var query = this.value;
            if (query.length >= 2) {
                fetchSearchSuggestions(query);
            }
        }, 300));
    }
}

/**
 * 獲取搜索建議
 */
function fetchSearchSuggestions(query) {
    fetch('/api/products/search-suggestions?q=' + encodeURIComponent(query))
    .then(response => response.json())
    .then(data => {
        // 顯示搜索建議
        showSearchSuggestions(data);
    })
    .catch(error => console.error('Error:', error));
}

/**
 * 顯示搜索建議
 */
function showSearchSuggestions(suggestions) {
    // 實現搜索建議下拉列表
}

/**
 * 防抖函數
 */
function debounce(func, wait) {
    var timeout;
    return function() {
        var context = this, args = arguments;
        clearTimeout(timeout);
        timeout = setTimeout(function() {
            func.apply(context, args);
        }, wait);
    };
}

/**
 * 顯示Toast消息
 */
function showToast(message, type) {
    // 創建Toast元素
    var toastContainer = document.getElementById('toast-container');
    if (!toastContainer) {
        toastContainer = document.createElement('div');
        toastContainer.id = 'toast-container';
        toastContainer.className = 'toast-container position-fixed bottom-0 end-0 p-3';
        document.body.appendChild(toastContainer);
    }
    
    var toastId = 'toast-' + Date.now();
    var toastClass = 'bg-' + (type === 'error' ? 'danger' : type === 'success' ? 'success' : 'info');
    
    var toastHtml = `
        <div id="${toastId}" class="toast ${toastClass} text-white" role="alert">
            <div class="d-flex">
                <div class="toast-body">
                    ${message}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
            </div>
        </div>
    `;
    
    toastContainer.insertAdjacentHTML('beforeend', toastHtml);
    
    var toastElement = document.getElementById(toastId);
    var toast = new bootstrap.Toast(toastElement, {
        autohide: true,
        delay: 3000
    });
    
    toast.show();
    
    // 自動移除
    toastElement.addEventListener('hidden.bs.toast', function () {
        this.remove();
    });
}

/**
 * 添加商品到購物車
 */
function addToCart(productId, quantity = 1) {
    fetch('/cart/add', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: 'productId=' + productId + '&quantity=' + quantity
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            showToast('商品已添加到購物車！', 'success');
            updateCartItemCount();
        } else {
            showToast(data.message || '添加失敗', 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showToast('網絡錯誤，請重試', 'error');
    });
}

/**
 * 格式化價格
 */
function formatPrice(price) {
    return '$' + parseFloat(price).toFixed(2);
}

/**
 * 計算折扣價格
 */
function calculateDiscountPrice(originalPrice, discountPercent) {
    var discount = originalPrice * (discountPercent / 100);
    return originalPrice - discount;
}
