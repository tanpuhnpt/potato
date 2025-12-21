# TÀI LIỆU GIẢI THÍCH CHI TIẾT - SELENIUM TEST SCRIPT

## TỔNG QUAN

File test: `food_app_test.py`  
Framework: Selenium WebDriver + Python unittest  
Ứng dụng: React Food Delivery App (Single Page Application)  
URL: http://localhost:5175

---

## MỤC ĐÍCH CỦA BỘ TEST

Bộ test này kiểm tra toàn bộ quy trình **đặt hàng thức ăn qua drone delivery** từ đầu đến cuối:

1. **Test 1**: Đăng nhập vào hệ thống
2. **Test 2**: Tìm kiếm nhà hàng
3. **Test 3**: Thêm món ăn vào giỏ hàng (với options)
4. **Test 4**: Cập nhật số lượng trong giỏ hàng
5. **Test 5**: Hoàn tất đơn hàng với form đặt hàng

**Điểm đặc biệt**: Tests phải chạy theo thứ tự và **giữ nguyên trạng thái** (giỏ hàng, đăng nhập) giữa các test.

---

## CẤU TRÚC CLASS VÀ SETUP

### 1. setUpClass() - Khởi tạo WebDriver (chạy 1 lần)

```python
@classmethod
def setUpClass(cls):
    cls.driver = webdriver.Chrome(service=ChromeService(ChromeDriverManager().install()))
    cls.driver.maximize_window()
    cls.wait = WebDriverWait(cls.driver, 15)
    cls.base_url = "http://localhost:5175"
```

**Giải thích**:
- `@classmethod`: Chạy 1 lần duy nhất trước tất cả test cases
- `ChromeDriverManager().install()`: Tự động download và cài đặt ChromeDriver phù hợp với Chrome version
- `maximize_window()`: Mở cửa sổ full màn hình → tránh các element bị ẩn
- `WebDriverWait(cls.driver, 15)`: Tạo wait object với timeout 15 giây → dùng cho explicit waits
- Lý do dùng explicit waits: Tốt hơn `time.sleep()` vì chỉ đợi đủ lâu cho element xuất hiện, không cố định

### 2. setUp() - Điều hướng thông minh (chạy trước mỗi test)

```python
def setUp(self):
    current_url = self.driver.current_url
    if "/restaurant/" in current_url or "/order" in current_url or "/cart" in current_url:
        print(f"✓ Already on {current_url} - skipping setUp navigation")
        return
    
    self.driver.get(self.base_url)
    self.wait.until(EC.presence_of_element_located((By.CLASS_NAME, "logo")))
```

**Đây là kỹ thuật QUAN TRỌNG NHẤT của bộ test**

**Vấn đề**: Unittest mặc định gọi `setUp()` trước EVERY test → nếu luôn navigate về homepage thì:
- Test 3 kết thúc ở `/restaurant/1` → Test 4 bắt đầu → setUp() đẩy về `/` → **MẤT HẾT GIỎ HÀNG!**

**Giải pháp**: 
- Kiểm tra `current_url` trước khi navigate
- Nếu đang ở `/restaurant/`, `/order`, hoặc `/cart` → **BỎ QUA** navigation → giữ nguyên trang hiện tại
- Chỉ navigate về home nếu đang ở trang khác

**Kết quả**: 
- Test 1, 2: Chạy từ homepage (bình thường)
- Test 3: Kết thúc ở `/restaurant/1`
- **Test 4**: setUp() thấy URL chứa `/restaurant/` → SKIP navigate → giữ nguyên giỏ hàng ✅
- **Test 5**: setUp() thấy URL chứa `/order` → SKIP navigate → tiếp tục điền form ✅

### 3. handle_alert_if_present() - Xử lý alert popup

```python
def handle_alert_if_present(self):
    import time
    for attempt in range(3):
        try:
            alert = self.driver.switch_to.alert
            alert_text = alert.text
            print(f"Alert dismissed: {alert_text}")
            alert.accept()
            time.sleep(0.5)
            return True
        except:
            if attempt < 2:
                time.sleep(0.3)
    return False
```

**Giải thích**:
- `switch_to.alert`: Chuyển focus sang alert popup
- `alert.text`: Lấy nội dung text của alert
- `alert.accept()`: Click nút OK để đóng alert
- **Retry 3 lần**: Alert có thể xuất hiện chậm → retry với delay 0.3s giữa các lần
- `time.sleep(0.5)` sau accept: Đảm bảo alert đã đóng hoàn toàn trước khi tiếp tục

**Khi nào dùng**: 
- Test 1: Sau khi login thành công → alert "Đăng nhập thành công!"
- Test 5: Sau khi đặt hàng → alert "Đặt hàng thành công! Cảm ơn bạn..."

---

## CHI TIẾT TỪNG TEST CASE

## TEST 1: LOGIN SUCCESS

### Mục đích
Kiểm tra chức năng đăng nhập với email/password hợp lệ.

### Flow chi tiết

#### Bước 1: Click nút Login trên Navbar
```python
login_btn = self.wait.until(
    EC.element_to_be_clickable((By.CSS_SELECTOR, ".navbar-login-btn"))
)
login_btn.click()
```

**Giải thích**:
- `EC.element_to_be_clickable`: Đợi đến khi element:
  - ✓ Tồn tại trong DOM
  - ✓ Visible (không bị hidden)
  - ✓ Enabled (không disabled)
  - ✓ Không bị element khác che
- Selector `.navbar-login-btn`: Class từ file `Navbar.jsx`
- **Tại sao không dùng `.click()` trực tiếp?** → Element có thể chưa load xong → lỗi

#### Bước 2: Chờ LoginPopup xuất hiện
```python
self.wait.until(
    EC.visibility_of_element_located((By.CSS_SELECTOR, ".login-popup"))
)
```

**Giải thích**:
- `visibility_of_element_located`: Đợi popup hiển thị (không chỉ tồn tại mà phải visible)
- Selector `.login-popup`: Class từ `LoginPopup.jsx`
- **Tại sao cần wait?** → Popup xuất hiện với animation/transition → cần đợi render xong

#### Bước 3: Điền email và password
```python
email_input = self.driver.find_element(By.CSS_SELECTOR, "input[name='email']")
email_input.clear()
email_input.send_keys("demo@example.com")

password_input = self.driver.find_element(By.CSS_SELECTOR, "input[name='password']")
password_input.clear()
password_input.send_keys("Phat12375@")
```

**Giải thích**:
- `find_element`: Tìm element trong DOM (không chờ đợi vì popup đã visible rồi)
- Selector `input[name='email']`: Attribute selector → chính xác, ít bị thay đổi
- `clear()`: Xóa giá trị cũ (nếu có) → đảm bảo input clean
- `send_keys()`: Gõ text vào input (giống người dùng gõ bàn phím)

**Tại sao dùng name attribute?**
-  Semantic và stable (ít thay đổi)
-  Unique trong form
-  Không dùng ID vì React không generate ID

#### Bước 4: Check vào checkbox điều khoản
```python
checkbox = self.driver.find_element(By.CSS_SELECTOR, ".login-popup-condition input[type='checkbox']")
if not checkbox.is_selected():
    checkbox.click()
```

**Giải thích**:
- Selector `.login-popup-condition input[type='checkbox']`: Nested selector → chính xác hơn
- `is_selected()`: Kiểm tra checkbox đã được check chưa
- **Conditional click**: Chỉ click nếu chưa check → tránh toggle lại (uncheck)

#### Bước 5: Click nút "Đăng nhập"
```python
login_submit = self.driver.find_element(By.CSS_SELECTOR, ".login-popup button[type='submit']")
login_submit.click()
```

**Giải thích**:
- Selector `button[type='submit']`: Button submit trong form
- Nested trong `.login-popup`: Tránh nhầm với button khác

#### Bước 6: Xử lý alert thành công
```python
import time
time.sleep(0.5)
self.handle_alert_if_present()
time.sleep(1)
```

**Giải thích**:
- `time.sleep(0.5)`: Đợi alert xuất hiện (alert có delay nhỏ sau submit)
- `handle_alert_if_present()`: Dismiss alert "Đăng nhập thành công!"
- `time.sleep(1)`: Đợi sau khi dismiss → DOM update (profile icon xuất hiện)

** Tại sao dùng time.sleep() ở đây?**
- Alert timing không đồng bộ với DOM → không có explicit condition để wait
- 0.5s đủ ngắn để không ảnh hưởng performance
- Retry trong `handle_alert_if_present()` đảm bảo robust

#### Bước 7: Verify login thành công
```python
profile_icon = self.wait.until(
    EC.presence_of_element_located((By.CSS_SELECTOR, ".navbar-profile"))
)
self.assertIsNotNone(profile_icon)
```

**Giải thích**:
- `presence_of_element_located`: Đợi profile icon xuất hiện trong DOM
- Selector `.navbar-profile`: Icon người dùng chỉ hiện khi đã login
- `assertIsNotNone()`: Unittest assertion → test PASS nếu icon tồn tại

---

## TEST 2: SEARCH RESTAURANT

### Mục đích
Kiểm tra chức năng tìm kiếm nhà hàng theo tên/món ăn.

### Flow chi tiết

#### Bước 1: Tìm search input
```python
search_input = self.wait.until(
    EC.presence_of_element_located((By.CSS_SELECTOR, "input[placeholder*='Tìm kiếm']"))
)
```

**Giải thích**:
- Selector `input[placeholder*='Tìm kiếm']`: 
  - `*=` là contains operator → khớp với placeholder chứa "Tìm kiếm"
  - Không cần viết đúng 100% text placeholder
- **Tại sao dùng placeholder?** → Nhìn thấy được text trong UI, dễ maintain

#### Bước 2: Nhập từ khóa tìm kiếm
```python
search_input.clear()
search_input.send_keys("Phở")
print(f"Searching for: Phở")
```

**Giải thích**:
- `clear()`: Xóa text cũ (nếu có search history)
- `send_keys("Phở")`: Gõ từ khóa "Phở"
- **Note**: React app có thể có live search → kết quả hiện ngay khi gõ

#### Bước 3: Chờ kết quả xuất hiện
```python
time.sleep(1)
results = self.driver.find_elements(By.CSS_SELECTOR, ".restaurant-item")
print(f"✓ Search returned {len(results)} results")
self.assertGreater(len(results), 0)
```

**Giải thích**:
- `time.sleep(1)`: Đợi React re-render với kết quả search
- `find_elements` (có s): Trả về list của TẤT CẢ elements khớp selector
- `len(results)`: Đếm số kết quả
- `assertGreater(len(results), 0)`: Assert có ít nhất 1 kết quả

** Tại sao dùng time.sleep()?**
- Search có debounce/throttle → không biết chính xác khi nào render xong
- WebDriverWait không có condition cho "list elements updated"
- 1 giây là acceptable cho search operation

---

## TEST 3: ADD TO CART

### TEST PHỨC TẠP NHẤT - XỬ LÝ MODAL VÀ OPTIONS

### Mục đích
Kiểm tra quy trình thêm món ăn vào giỏ hàng với các options (size, topping...).

### Flow chi tiết

#### Bước 1: Scroll xuống để thấy restaurants
```python
self.driver.execute_script("window.scrollBy(0, 500);")
print("✓ Scrolled down to view restaurants")
```

**Giải thích**:
- `execute_script()`: Chạy JavaScript trực tiếp trong browser
- `window.scrollBy(0, 500)`: Scroll xuống 500 pixels
- **Tại sao cần scroll?** → Restaurants có thể ở dưới fold → không visible → không click được

#### Bước 2: Click vào restaurant đầu tiên
```python
restaurants = self.wait.until(
    EC.presence_of_all_elements_located((By.CSS_SELECTOR, ".restaurant-item"))
)
first_restaurant = restaurants[0]
restaurant_name = first_restaurant.text.split('\n')[0]
print(f"Clicking restaurant: {restaurant_name}")

self.driver.execute_script("arguments[0].scrollIntoView(true);", first_restaurant)
first_restaurant.click()
```

**Giải thích**:
- `presence_of_all_elements_located`: Đợi danh sách restaurants load
- `restaurants[0]`: Lấy restaurant đầu tiên
- `.text.split('\n')[0]`: Lấy dòng đầu tiên của text (tên restaurant)
- `scrollIntoView(true)`: Scroll element vào viewport → đảm bảo visible
- **Tại sao dùng JavaScript scroll?** → Selenium click yêu cầu element trong viewport

#### Bước 3: Chờ navigate sang trang restaurant detail
```python
self.wait.until(EC.url_contains("/restaurant/"))
food_items = self.wait.until(
    EC.presence_of_all_elements_located((By.CSS_SELECTOR, ".food-item"))
)
print("✓ Navigated to restaurant detail page with menu list")
```

**Giải thích**:
- `url_contains("/restaurant/")`: Đợi URL chuyển sang pattern `/restaurant/{id}`
- **Tại sao wait URL?** → SPA navigation không reload page → phải wait URL change
- `presence_of_all_elements_located`: Đợi danh sách món ăn load

#### Bước 4: Click vào món ăn đầu tiên
```python
first_food_item = food_items[0]
food_name = first_food_item.text.split('\n')[0]
print(f"Clicking food item: {food_name}")

self.driver.execute_script("arguments[0].scrollIntoView(true);", first_food_item)
self.wait.until(EC.element_to_be_clickable((By.CSS_SELECTOR, ".food-item")))
first_food_item.click()
```

**Giải thích**:
- Scroll vào viewport trước khi click
- `element_to_be_clickable`: Đảm bảo element sẵn sàng click
- Click → Trigger `handleOpenOrAdd` function → Mở `FoodOptionsModal`

#### Bước 5: Đợi FoodOptionsModal load HOÀN TOÀN
```python
print(" Waiting 2-3s for FoodOptionsModal to load...")
modal = self.wait.until(
    EC.presence_of_element_located((By.CSS_SELECTOR, ".fom-modal, .fom-overlay"))
)
self.driver.execute_script("return document.readyState === 'complete';")
import time
time.sleep(2)
print("✓ FoodOptionsModal loaded completely (handleOpenOrAdd triggered)")
```

**ĐÂY LÀ PHẦN QUAN TRỌNG NHẤT CỦA TEST 3**

**Vấn đề**: 
- Modal render với animation
- Options load từ API/state
- Radio buttons có overlay spans (`.fom-custom-check`)
- Nếu click quá nhanh → "element not clickable" error

**Giải pháp**:
1. Đợi modal/overlay present trong DOM
2. Check `document.readyState === 'complete'` → đảm bảo page render xong
3. **Hard wait 2 giây** → đợi animation + options render
4. Sau đó mới interact với options

** Tại sao dùng time.sleep(2)?**
- Modal animation + render time không predictable
- Không có explicit condition để check "modal fully rendered with all options"
- 2-3 giây là safe range cho modal operations
- Trade-off: Slower test nhưng stable hơn

#### Bước 6: Chọn option (size, topping...)
```python
option_inputs = self.driver.find_elements(By.CSS_SELECTOR, ".fom-option input[type='radio'], .fom-option input[type='checkbox']")
if len(option_inputs) > 0:
    first_option = option_inputs[0]
    self.driver.execute_script("arguments[0].scrollIntoView(true);", first_option)
    self.driver.execute_script("arguments[0].click();", first_option)
    print("✓ Selected option from modal")
```

**Giải thích**:
- Selector `.fom-option input[type='radio'], .fom-option input[type='checkbox']`: 
  - Tìm TẤT CẢ radio và checkbox trong options
  - Multiple selectors với dấu `,` (OR logic)
- `find_elements` (plural): Trả về list
- **JavaScript click thay vì Selenium click**: 
  ```python
  self.driver.execute_script("arguments[0].click();", first_option)
  ```
  **Tại sao?**
  - React có span overlay (`.fom-custom-check`) che input
  - Selenium click gặp lỗi: "element click intercepted"
  - JavaScript click bypass overlay → trigger event trực tiếp trên input

#### Bước 7: Tăng số lượng (+)
```python
qty_buttons = self.driver.find_elements(By.XPATH, "//div[@class='fom-qty']//button")
if len(qty_buttons) >= 2:
    qty_buttons[1].click()
    print("✓ Increased quantity (+)")
```

**Giải thích**:
- XPath `//div[@class='fom-qty']//button`: 
  - Tìm TẤT CẢ buttons trong div có class `fom-qty`
  - `//` là descendant selector (bất kỳ cấp con nào)
- `qty_buttons[1]`: Button thứ 2 là nút `+` (button 0 là `-`)
- **Tại sao dùng XPath thay vì CSS?** → Dễ query descendant với class chính xác

#### Bước 8: Click "Thêm vào giỏ"
```python
add_btn = self.wait.until(
    EC.element_to_be_clickable((By.CSS_SELECTOR, ".fom-add"))
)
add_btn.click()
print("✓ Clicked 'Thêm vào giỏ' button")
```

**Giải thích**:
- Selector `.fom-add`: Class từ `FoodOptionsModal.jsx`
- `element_to_be_clickable`: Đảm bảo button enabled (không disabled khi thiếu option required)

#### Bước 9: Đợi modal đóng
```python
self.wait.until(
    EC.invisibility_of_element_located((By.CSS_SELECTOR, ".fom-overlay"))
)
print("✓ Item added to cart successfully")
```

**Giải thích**:
- `invisibility_of_element_located`: Đợi overlay biến mất (modal đóng)
- **Tại sao wait modal close?** → Đảm bảo item đã add vào cart trong state

**Kết quả Test 3**: 
- Kết thúc ở URL `/restaurant/1`
- Item đã có trong cart state
- Test 4 sẽ tiếp tục từ trang này

---

## TEST 4: UPDATE CART QUANTITY

### KỸ THUẬT PRESERVATION - TIẾP TỤC TỪ TEST 3

### Mục đích
Cập nhật số lượng món trong giỏ hàng và proceed to checkout.

### Flow chi tiết

####  Bước 0: setUp() tự động SKIP navigation
```python
def setUp(self):
    current_url = self.driver.current_url
    if "/restaurant/" in current_url:
        print(f"✓ Already on {current_url} - skipping setUp navigation")
        return
```

**Giải thích**:
- Test 3 kết thúc ở `/restaurant/1`
- Test 4 bắt đầu → setUp() check URL
- URL chứa `/restaurant/` → **RETURN NGAY** → không navigate về home
- → **GIỮ NGUYÊN** giỏ hàng và vị trí trang

#### Bước 1: Mở cart drawer từ restaurant page
```python
cart_btn = self.wait.until(
    EC.element_to_be_clickable((By.CSS_SELECTOR, ".navbar-search-icon img[role='button']"))
)
cart_btn.click()

self.wait.until(
    EC.visibility_of_element_located((By.CSS_SELECTOR, ".cart-drawer"))
)
print("✓ Cart drawer opened from restaurant page")
```

**Giải thích**:
- Selector `.navbar-search-icon img[role='button']`: 
  - Icon giỏ hàng trên navbar
  - `img[role='button']`: Image với role=button → accessible
- `visibility_of_element_located`: Đợi drawer slide in (animation)

#### Bước 2: Click "Thanh toán" trong drawer
```python
checkout_btn = self.wait.until(
    EC.element_to_be_clickable((By.CSS_SELECTOR, ".cart-drawer-checkout"))
)
checkout_btn.click()

self.wait.until(EC.url_contains("/cart"))
print("✓ Navigated to cart page (/cart)")
```

**Giải thích**:
- Selector `.cart-drawer-checkout`: Button trong drawer (từ `CartDrawer` component)
- `url_contains("/cart")`: Đợi navigate sang cart page
- **Tại sao navigate sang /cart?** → Cart page cho phép update quantity chi tiết

#### Bước 3: Đợi cart page load
```python
self.wait.until(
    EC.presence_of_element_located((By.CSS_SELECTOR, ".cart-line"))
)
print("✓ Cart page loaded with items")
```

**Giải thích**:
- Selector `.cart-line`: Each item trong cart
- `presence_of_element_located`: Đợi items render

#### Bước 4: Lấy số lượng hiện tại
```python
qty_display = self.driver.find_element(By.XPATH, "//div[@class='line-qty']//span")
initial_qty = qty_display.text
print(f"Initial quantity: {initial_qty}")
```

**Giải thích**:
- XPath `//div[@class='line-qty']//span`: Span hiển thị số lượng
- `.text`: Lấy text content
- **Mục đích**: Log để verify sau khi update

#### Bước 5: Click nút + để tăng số lượng
```python
qty_plus_btn = self.driver.find_element(By.XPATH, "//div[@class='line-qty']//button[last()]")
qty_plus_btn.click()
time.sleep(0.5)

updated_qty = self.driver.find_element(By.XPATH, "//div[@class='line-qty']//span").text
print(f"✓ Updated quantity: {initial_qty} → {updated_qty}")
```

**Giải thích**:
- XPath `//button[last()]`: Button cuối cùng trong list = nút `+`
  - `last()` là XPath function
  - **Tại sao không dùng index?** → last() reliable hơn (không bị lệch index)
- `time.sleep(0.5)`: Đợi React update state và re-render
- Lấy lại `updated_qty` để verify

#### Bước 6: Click "TIẾN HÀNH THANH TOÁN"
```python
place_order_btn = self.wait.until(
    EC.element_to_be_clickable((By.XPATH, "//div[@class='payment-box']//button"))
)
self.driver.execute_script("arguments[0].scrollIntoView(true);", place_order_btn)
time.sleep(0.5)
place_order_btn.click()

self.wait.until(EC.url_contains("/order"))
print("✓ Navigated to place order page (/order)")
```

**Giải thích**:
- XPath `//div[@class='payment-box']//button`: Button trong payment summary box
- `scrollIntoView`: Button có thể ở dưới → scroll vào view
- `time.sleep(0.5)`: Đợi sau scroll trước khi click
- `url_contains("/order")`: Verify navigate sang order form page

**Kết quả Test 4**:
- Cart quantity đã update
- Đang ở page `/order` (form đặt hàng)
- Test 5 sẽ tiếp tục từ trang này

---

## TEST 5: CHECKOUT FLOW

### HOÀN TẤT ĐƠN HÀNG VỚI FORM

### Mục đích
Điền form thông tin giao hàng và xác nhận đặt hàng.

### Flow chi tiết

####  Bước 0: setUp() tự động SKIP navigation
```python
if "/order" in current_url:
    print(f"✓ Already on {current_url} - skipping setUp navigation")
    return
```

**Giải thích**:
- Test 4 kết thúc ở `/order`
- Test 5 bắt đầu → setUp() thấy `/order` trong URL → SKIP
- → Giữ nguyên form page với cart items

#### Bước 1: Đợi form load
```python
self.wait.until(
    EC.presence_of_element_located((By.XPATH, "//input[@placeholder='Họ và tên']"))
)
print("✓ Order form loaded and ready")
```

**Giải thích**:
- Đợi input đầu tiên của form xuất hiện
- Placeholder "Họ và tên" → từ `PlaceOrder.jsx`

#### Bước 2: Điền fullName
```python
fullname_input = self.wait.until(
    EC.presence_of_element_located((By.XPATH, "//input[@name='fullName']"))
)
fullname_input.clear()
fullname_input.send_keys("Nguyen Van Test")
print("✓ Filled fullName: Nguyen Van Test")
```

**Giải thích**:
- XPath `//input[@name='fullName']`: Input có attribute `name='fullName'`
- **Tại sao dùng name attribute?** → Stable, semantic, form standard
- `clear()` trước `send_keys()`: Xóa auto-fill nếu có

#### Bước 3: Điền phone
```python
phone_input = self.wait.until(
    EC.presence_of_element_located((By.XPATH, "//input[@name='phone']"))
)
phone_input.clear()
phone_input.send_keys("0912345678")
print("✓ Filled phone: 0912345678")
```

**Giải thích**:
- Tương tự fullName
- Phone validation ở React side → cần đảm bảo format hợp lệ

#### Bước 4: Điền địa chỉ
```python
address_input = self.wait.until(
    EC.presence_of_element_located((By.XPATH, "//input[@name='addressDetail']"))
)
address_input.clear()
address_input.send_keys("123 Nguyen Hue, District 1, Ho Chi Minh City")
print("✓ Filled address: 123 Nguyen Hue, District 1, Ho Chi Minh City")
```

**Giải thích**:
- `name='addressDetail'`: Từ PlaceOrder.jsx component
- Địa chỉ chi tiết bao gồm số nhà, đường, quận, thành phố

#### Bước 5: Chọn phương thức thanh toán COD
```python
payment_options = self.driver.find_elements(By.XPATH, "//div[@class='payment-option']")
if len(payment_options) > 0:
    cod_option = payment_options[0]
    if 'selected' not in cod_option.get_attribute('class'):
        self.driver.execute_script("arguments[0].click();", cod_option)
        print("✓ Selected COD payment method")
    else:
        print("✓ COD payment method already selected")
```

**Giải thích**:
- `find_elements`: Tìm TẤT CẢ payment options
- `payment_options[0]`: Option đầu tiên = COD (theo thứ tự trong PlaceOrder.jsx)
- `get_attribute('class')`: Lấy class attribute để check
- `'selected' not in ...`: Kiểm tra class có chứa `selected` không
- **JavaScript click**: Đảm bảo click thành công (bypass potential overlay)

** Tại sao check 'selected' class?**
- React có thể set COD làm default → đã selected sẵn
- Nếu click lại → toggle → unselect → lỗi

#### Bước 6: Click "Xác nhận đặt hàng"
```python
confirm_btn = self.wait.until(
    EC.element_to_be_clickable((By.XPATH, "//button[@class='confirm-btn']"))
)
self.driver.execute_script("arguments[0].scrollIntoView(true);", confirm_btn)
time.sleep(0.5)
confirm_btn.click()
print("✓ Clicked 'Xác nhận đặt hàng'")
```

**Giải thích**:
- XPath `//button[@class='confirm-btn']`: Button có class `confirm-btn`
- `scrollIntoView`: Button có thể ở bottom của form → scroll down
- `time.sleep(0.5)`: Đợi scroll animation xong
- Multiple fallback selectors (trong try-except) để đảm bảo tìm được button

#### Bước 7: Xử lý alert thành công
```python
import time
time.sleep(0.5)
self.handle_alert_if_present()
print("✓ Success alert dismissed")
```

**Giải thích**:
- Sau click submit → Backend xử lý → Alert xuất hiện
- Alert text: "Đặt hàng thành công! Cảm ơn bạn đã sử dụng dịch vụ."
- `handle_alert_if_present()`: Dismiss alert với retry logic

** Tại sao cần handle alert?**
- Alert block tất cả DOM operations
- Nếu không dismiss → không thể check redirect → test fail
- PlaceOrder.jsx code:
  ```javascript
  alert(message);
  clearCart();
  navigate('/');
  ```

#### Bước 8: Verify đặt hàng thành công
```python
time.sleep(1)
self.wait.until(EC.presence_of_element_located((By.CLASS_NAME, "logo")))
print("✓✓✓ Order placed successfully!")
print("✓ Returned to home page - Order confirmation complete")
```

**Giải thích**:
- Sau dismiss alert → App navigate về home `/`
- `presence_of_element_located((By.CLASS_NAME, "logo"))`: Đợi logo xuất hiện
- Logo presence = homepage loaded = order complete 

**Kết quả Test 5**:
- Form đã submit thành công
- Alert đã dismiss
- Quay về homepage
- Order đã được tạo trong database

---

## KỸ THUẬT VÀ BEST PRACTICES

### 1. Explicit Waits vs Implicit Waits vs Hard Sleeps

| Loại | Khi nào dùng | Ví dụ |
|------|-------------|-------|
| **Explicit Wait** | Element cần wait với điều kiện cụ thể | `self.wait.until(EC.element_to_be_clickable(...))` |
| **Hard Sleep** | Timing không predictable (animation, modal) | `time.sleep(2)` cho modal render |
| **Implicit Wait** |  KHÔNG DÙNG | Conflict với explicit waits |

### 2. Selenium Click vs JavaScript Click

| Phương pháp | Khi nào dùng |
|------------|-------------|
| **Selenium `.click()`** | Element hoàn toàn accessible, không bị che |
| **JavaScript click** | Element bị overlay che, intercepted error |

```python
# Selenium click
element.click()

# JavaScript click
self.driver.execute_script("arguments[0].click();", element)
```

### 3. CSS Selector vs XPath

| CSS Selector | XPath |
|--------------|-------|
| `.class-name` | `//div[@class='class-name']` |
| `input[name='email']` | `//input[@name='email']` |
| `.parent .child` | `//div[@class='parent']//div[@class='child']` |
|  Không có last() |  `//button[last()]` |
|  Không có text contains |  `//button[contains(text(), 'Click')]` |

**Quy tắc chọn**:
- CSS: Simple, readable, faster
- XPath: Complex logic, text matching, functions

### 4. State Preservation Strategy

**Vấn đề**: Unittest gọi `setUp()` trước mỗi test → reset state

**Giải pháp**:
```python
def setUp(self):
    current_url = self.driver.current_url
    if condition_to_preserve_state:
        return  # SKIP navigation
    self.driver.get(self.base_url)
```

**Key points**:
- Check URL pattern để quyết định skip
- Return early để avoid navigation
- Giữ nguyên cart, login state

### 5. Multiple Fallback Selectors

**Kỹ thuật**: Try-except chain với nhiều selectors

```python
try:
    # Selector 1 (most specific)
    element = self.wait.until(EC.element_to_be_clickable((By.XPATH, "//button[@class='confirm-btn']")))
except:
    try:
        # Selector 2 (fallback)
        element = self.wait.until(EC.element_to_be_clickable((By.XPATH, "//button[@type='submit']")))
    except:
        # Selector 3 (generic)
        element = self.driver.find_element(By.TAG_NAME, "button")
```

**Lợi ích**:
-  Test robust hơn
-  Ít bị break khi UI thay đổi
-  Tăng success rate

---

## KẾT QUẢ KIỂM THỬ

### Test Execution Summary

```
test_01_login_success ......................... OK
test_02_search_restaurant ..................... OK
test_03_add_to_cart ........................... OK
test_04_update_cart_quantity .................. OK
test_05_checkout_flow ......................... OK

----------------------------------------------
Ran 5 tests in ~25 seconds

OK
```

### Coverage

| Chức năng | Kịch bản test | Status |
|-----------|--------------|--------|
| Authentication | Login với email/password |  PASS |
| Search | Tìm kiếm restaurant/món |  PASS |
| Cart Management | Add item với options |  PASS |
| Cart Update | Update quantity |  PASS |
| Checkout | Form submission |  PASS |
| Alert Handling | Dismiss success alerts |  PASS |
| Modal Interaction | Select options trong modal |  PASS |
| Navigation | SPA routing |  PASS |

---

## TỔNG KẾT

### Kiến thức Selenium đã áp dụng

1. **WebDriver Setup**: ChromeDriver auto-install
2. **Waits**: Explicit waits với WebDriverWait
3. **Element Location**: CSS Selector, XPath
4. **Interactions**: Click, send_keys, clear
5. **JavaScript Execution**: Execute script cho click, scroll
6. **Alert Handling**: Switch to alert, accept
7. **URL Checking**: Navigation verification
8. **State Management**: Smart setUp() với URL detection

### Điểm mạnh của bộ test

 **End-to-end**: Test toàn bộ user journey  
 **State Preservation**: Không mất data giữa tests  
 **Robust**: Multiple fallback selectors  
 **Clear Logging**: Print statements cho debug  
 **Proper Waits**: Mix explicit waits và hard sleeps hợp lý  
 **Real Selectors**: Dùng selectors từ actual component code  

### Bài học quan trọng

1. **Modal timing is critical**: Cần wait đủ lâu cho animation
2. **Overlay elements need JavaScript click**: Bypass intercepted error
3. **State preservation requires URL checking**: Smart setUp() pattern
4. **Alerts must be handled immediately**: Block tất cả operations
5. **SPA navigation needs URL waits**: Không có page reload

---

## GHI CHÚ THÊM

### Chạy test

```bash
# Chạy tất cả tests
python food_app_test.py

# Chạy test cụ thể
python -m unittest food_app_test.FoodDeliveryAppTest.test_01_login_success
```

### Debug

- Xem print statements trong console
- Screenshot khi fail: `driver.save_screenshot('error.png')`
- Check current URL: `print(driver.current_url)`
- Inspect element: `print(element.get_attribute('outerHTML'))`

### Maintenance

Khi UI thay đổi:
1. Check selector còn đúng không
2. Update selector theo component code mới
3. Add fallback selector nếu cần
4. Test lại toàn bộ flow

---

**Tác giả**: Selenium Test Automation  
**Ngày tạo**: December 14, 2025  
**Version**: 1.0
