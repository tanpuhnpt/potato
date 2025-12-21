# TEST REPORT - FOOD DELIVERY APP (POTATO)

## THÔNG TIN CHUNG

| Thông tin | Giá trị |
|-----------|--------|
| **Module** | Order Management / Food Delivery System |
| **Tester** | [Your Name] |
| **Test Date** | December 15, 2025 |
| **No. of Test Cases** | 5 |
| **Pass** | 5 |
| **Fail** | 0 |
| **Untested** | 0 |
| **N/A** | 0 |
| **Success Rate** | 100% |

---

## CHI TIẾT TỪNG TEST CASE

### TEST 1: LOGIN SUCCESS

| Trường | Chi tiết |
|--------|---------|
| **ID** | TC-001 |
| **Description** | Verify login functionality with valid credentials |
| **Module** | Authentication |
| **Procedure** | 1. Click login button on navbar<br/>2. Wait for LoginPopup to appear<br/>3. Fill email: demo@example.com<br/>4. Fill password: Phat12375@<br/>5. Check checkbox "Điều khoản"<br/>6. Click "Đăng nhập" button<br/>7. Handle success alert<br/>8. Verify profile icon appears |
| **Expected Output** | - Alert "Đăng nhập thành công!" appears and dismisses<br/>- Profile icon (.navbar-profile) is displayed<br/>- User is logged in successfully |
| **Inter-Test Dependence** | None (standalone test) |
| **Result** | PASS |
| **Note** | Uses handle_alert_if_present() with 3-attempt retry for robust alert handling |

---

### TEST 2: SEARCH RESTAURANT

| Trường | Chi tiết |
|--------|---------|
| **ID** | TC-002 |
| **Description** | Verify search functionality for restaurants |
| **Module** | Search |
| **Procedure** | 1. Wait for search input to be visible<br/>2. Click on search input<br/>3. Type search term: "Phở"<br/>4. Press Enter key<br/>5. Wait 1 second for results to render<br/>6. Verify result list appears |
| **Expected Output** | - Search input accepts text input<br/>- At least 1 restaurant result is displayed (.restaurant-item)<br/>- Search completes successfully |
| **Inter-Test Dependence** | Should run after TC-001 (logged in state) |
| **Result** | PASS |
| **Note** | Uses multiple fallback selectors for search input to handle UI variations |

---

### TEST 3: ADD TO CART

| Trường | Chi tiết |
|--------|---------|
| **ID** | TC-003 |
| **Description** | Verify adding food item to cart with options selection |
| **Module** | Cart Management |
| **Procedure** | 1. Scroll down 500px to view restaurants<br/>2. Click first restaurant item (.restaurant-item)<br/>3. Wait for restaurant detail page (/restaurant/{id}) to load<br/>4. Wait for food items list to load<br/>5. Click first food item to trigger FoodOptionsModal<br/>6. Wait 2 seconds for modal to fully render (critical)<br/>7. Select first available option (radio/checkbox)<br/>8. Click quantity increase button (+)<br/>9. Click "Thêm vào giỏ" button (.fom-add)<br/>10. Wait for modal to close (.fom-overlay invisibility) |
| **Expected Output** | - Modal FoodOptionsModal opens with options<br/>- Option can be selected via JavaScript click (bypass overlay)<br/>- Quantity can be increased<br/>- Item added to cart successfully<br/>- Modal closes after adding item<br/>- URL remains at /restaurant/{id}<br/>- Cart state persists (item in cart) |
| **Inter-Test Dependence** | Depends on TC-001 and TC-002 (logged in + searched)<br/>TEST 4 will depend on this test's cart state |
| **Result** | PASS |
| **Note** | Critical: Wait 2 seconds for FoodOptionsModal animation + render. Uses JS click to bypass span overlay (.fom-custom-check) that intercepts normal clicks |

---

### TEST 4: UPDATE CART QUANTITY

| Trường | Chi tiết |
|--------|---------|
| **ID** | TC-004 |
| **Description** | Verify updating cart item quantity and proceeding to checkout |
| **Module** | Cart Management |
| **Procedure** | 1. setUp() detects /restaurant/ in URL and skips navigation (PRESERVES CART STATE)<br/>2. Click cart button (.navbar-search-icon img[role='button'])<br/>3. Wait for cart drawer (.cart-drawer) to open<br/>4. Click "Thanh toán" button (.cart-drawer-checkout)<br/>5. Wait for cart page (/cart) to load<br/>6. Get initial item quantity<br/>7. Click quantity increase button (//button[last()] in .line-qty)<br/>8. Verify quantity increased<br/>9. Click "TIẾN HÀNH THANH TOÁN" button<br/>10. Wait for /order page to load |
| **Expected Output** | - Cart drawer opens successfully<br/>- Cart page loads with items from TC-003<br/>- Quantity can be updated (increment works)<br/>- Navigates to /order page<br/>- Order form is ready for input<br/>- All cart items persist |
| **Inter-Test Dependence** | MUST run after TC-003<br/>Uses state preservation: setUp() checks URL and skips navigation if on /restaurant/<br/>Provides state for TC-005 |
| **Result** | PASS |
| **Note** | KEY TECHNIQUE: Smart setUp() with URL detection - if URL contains /restaurant/, /order, or /cart, skip navigation to preserve state. This is how tests chain together |

---

### TEST 5: CHECKOUT FLOW

| Trường | Chi tiết |
|--------|---------|
| **ID** | TC-005 |
| **Description** | Verify complete checkout flow with order form submission |
| **Module** | Order Management |
| **Procedure** | 1. setUp() detects /order in URL and skips navigation (PRESERVES FORM STATE)<br/>2. Wait for order form to load<br/>3. Fill fullName input: "Nguyen Van Test"<br/>4. Fill phone input: "0912345678"<br/>5. Fill addressDetail input: "123 Nguyen Hue, District 1, Ho Chi Minh City"<br/>6. Verify/Select COD payment option (first payment-option div)<br/>7. Click "Xác nhận đặt hàng" button (with 3 fallback selectors)<br/>8. Handle success alert "Đặt hàng thành công!"<br/>9. Wait for redirect to home page (/)<br/>10. Verify logo appears (order complete) |
| **Expected Output** | - Order form loads successfully with all inputs visible<br/>- Form accepts all required fields (fullName, phone, addressDetail)<br/>- COD payment method is selected<br/>- Button click succeeds (with fallback selectors)<br/>- Success alert appears and is dismissed<br/>- Page redirects to home page (/)<br/>- Logo is displayed (confirms home page loaded)<br/>- Order created in backend |
| **Inter-Test Dependence** | MUST run after TC-004<br/>Uses state preservation: setUp() checks URL and skips navigation if on /order<br/>Final step in order flow |
| **Result** | PASS |
| **Note** | Alert handling: PlaceOrder.jsx shows alert BEFORE redirect, so must handle alert then wait for logo. Uses 3 fallback selectors for confirm button: .confirm-btn > submit in payment-box > form submit. Scroll button into view before click to ensure visibility |

---

## TECHNICAL SUMMARY

### Techniques Used

| Technique | Application |
|-----------|-------------|
| **Explicit Waits** | EC.element_to_be_clickable, EC.presence_of_element_located, EC.visibility_of_element_located |
| **JavaScript Execution** | scrollIntoView, click (bypass overlay), document.readyState check |
| **Alert Handling** | switch_to.alert, alert.accept(), 3-attempt retry |
| **State Preservation** | Smart setUp() with URL detection to skip navigation |
| **Fallback Selectors** | Multiple try-except chains for robustness |
| **Modal Timing** | 2-second hard wait for FoodOptionsModal animation + render |

### Key Issues & Solutions

| Issue | Root Cause | Solution |
|-------|-----------|----------|
| Element click intercepted | Span overlay (.fom-custom-check) covers input | Use JavaScript click: execute_script("arguments[0].click();", element) |
| Cart state lost between tests | setUp() always navigates home | Check current_url and skip navigation if on /restaurant/, /order, /cart |
| Alert blocks URL checking | Alert appears before redirect | Handle alert first, then wait for element to verify completion |
| Modal elements not clickable | Animation not complete | Hard wait 2 seconds after modal presence detected |

---

## TEST EXECUTION FLOW

```
Test 1: LOGIN (Home → Login → Home)
        ↓
Test 2: SEARCH (Home → Search results)
        ↓
Test 3: ADD TO CART (Restaurant detail → Modal → Restaurant detail)
        ↓ [setUp() skips navigation - cart preserved]
Test 4: UPDATE QUANTITY (Restaurant → Cart drawer → /cart → /order)
        ↓ [setUp() skips navigation - form preserved]
Test 5: CHECKOUT (Order form → Fill → Submit → Home)
        ↓
Complete
```

---

## STATISTICS

| Metric | Value |
|--------|-------|
| **Total Test Cases** | 5 |
| **Passed** | 5 |
| **Failed** | 0 |
| **Skipped** | 0 |
| **Success Rate** | 100% |
| **Average Execution Time** | ~25 seconds |
| **Dependencies Chain Length** | Test 3 → Test 4 → Test 5 |

---

## SELECTORS REFERENCE

### Critical Selectors Used

| Component | Selector | From |
|-----------|----------|------|
| Login Button | `.navbar-login-btn` | Navbar.jsx |
| Email Input | `input[name='email']` | LoginPopup.jsx |
| Modal | `.fom-modal, .fom-overlay` | FoodOptionsModal.jsx |
| Option Radio | `.fom-option input[type='radio']` | FoodOptionsModal.jsx |
| Quantity Increase | `//div[@class='fom-qty']//button[last()]` | FoodOptionsModal.jsx |
| Add to Cart Button | `.fom-add` | FoodOptionsModal.jsx |
| Cart Drawer | `.cart-drawer` | CartDrawer.jsx |
| Confirm Button | `//button[@class='confirm-btn']` | PlaceOrder.jsx |
| Address Input | `input[name='addressDetail']` | PlaceOrder.jsx |

---

## FILL INTO SPREADSHEET

### Header Row
- **Module**: Order Management / Food Delivery System
- **Tester**: [Tuấn Kiệt / Your Name]
- **No. of Test Cases**: 5
- **Pass**: 5
- **Fail**: 0
- **Untested**: 0
- **N/A**: 0

### For Each Test Case Row:

**TC-001:**
- ID: TC-001
- Description: Verify login functionality with valid email/password
- Procedure: Click login → Fill email/password → Check checkbox → Submit → Handle alert
- Expected Output: Profile icon appears, user logged in
- Inter-Test Dependence: None
- Result: PASS
- Note: Alert handling with retry

**TC-002:**
- ID: TC-002
- Description: Verify search restaurant by keyword
- Procedure: Type search term → Press Enter → Wait for results
- Expected Output: At least 1 restaurant result displayed
- Inter-Test Dependence: After TC-001
- Result: PASS
- Note: Multiple fallback selectors

**TC-003:**
- ID: TC-003
- Description: Add food item to cart with options
- Procedure: Click restaurant → Select food → Modal → Select option → Add to cart
- Expected Output: Item added to cart, modal closes
- Inter-Test Dependence: After TC-002, provides state for TC-004
- Result: PASS
- Note: 2-second wait for modal render, JS click for overlay bypass

**TC-004:**
- ID: TC-004
- Description: Update cart quantity and proceed to checkout
- Procedure: Open drawer → Click checkout → Go to /cart → Update qty → Proceed
- Expected Output: Navigates to /order page, form ready
- Inter-Test Dependence: After TC-003 (state preservation), provides state for TC-005
- Result: PASS
- Note: Smart setUp() preserves /restaurant/ state

**TC-005:**
- ID: TC-005
- Description: Complete checkout with order form submission
- Procedure: Fill form (name/phone/address) → Select COD → Submit → Handle alert
- Expected Output: Redirects to home page, order created
- Inter-Test Dependence: After TC-004 (state preservation)
- Result: PASS
- Note: 3 fallback selectors for confirm button, alert handling required

---

**Report Generated**: December 15, 2025
**Status**: All Tests Passed (5/5)
**Ready for**: Production Deployment
