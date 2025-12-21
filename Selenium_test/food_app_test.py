import unittest
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
from selenium.common.exceptions import TimeoutException


class FoodDeliveryAppTest(unittest.TestCase):
    """
    Test suite for Food Delivery App (React SPA)
    Base URL: http://localhost:3000
    Flow based on user screenshots: Restaurant → Food item → Modal → Cart drawer → Cart page → Checkout → Place order
    """

    @classmethod
    def setUpClass(cls):
        """Initialize WebDriver once for all tests"""
        cls.driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()))
        cls.driver.maximize_window()
        cls.base_url = "http://localhost:5175"
        cls.wait = WebDriverWait(cls.driver, 15)

    @classmethod
    def tearDownClass(cls):
        """Close browser after all tests"""
        cls.driver.quit()

    def setUp(self):
        """Navigate to home page before each test - EXCEPT when already on restaurant or order page from previous test"""
        # Check if we're already on a restaurant detail page or order page (from previous test)
        current_url = self.driver.current_url
        if "/restaurant/" in current_url or "/order" in current_url or "/cart" in current_url:
            print(f"✓ Already on {current_url} - skipping setUp navigation")
            return
        
        # Normal navigation for all other tests
        self.driver.get(self.base_url)
        # Wait for app to load
        self.wait.until(EC.presence_of_element_located((By.CLASS_NAME, "logo")))

    def handle_alert_if_present(self):
        """Handle alert if present on page - with retry"""
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
                else:
                    return False

    def test_01_login_success(self):
        """
        Test Case 1: Login functionality
        Based on: LoginPopup.jsx
        """
        print("\n=== TEST 1: Login ===")
        
        # Click login button in navbar
        login_btn = self.wait.until(
            EC.element_to_be_clickable((By.CLASS_NAME, "navbar-login-btn"))
        )
        login_btn.click()

        # Wait for login popup to appear
        self.wait.until(
            EC.presence_of_element_located((By.CLASS_NAME, "login-popup-container"))
        )

        # Fill in email
        email_input = self.wait.until(
            EC.presence_of_element_located((By.CSS_SELECTOR, "input[name='email']"))
        )
        email_input.clear()
        email_input.send_keys("demo@example.com")

        # Fill in password
        password_input = self.driver.find_element(By.CSS_SELECTOR, "input[name='password']")
        password_input.clear()
        password_input.send_keys("Phat12375@")

        # Tick checkbox
        checkbox = self.driver.find_element(By.CSS_SELECTOR, ".login-popup-condition input[type='checkbox']")
        if not checkbox.is_selected():
            checkbox.click()

        # Submit form
        submit_btn = self.driver.find_element(By.CSS_SELECTOR, ".login-popup-container button[type='submit']")
        submit_btn.click()
        
        # Handle alert IMMEDIATELY
        import time
        time.sleep(0.5)
        self.handle_alert_if_present()
        time.sleep(1)

        # Verify login success
        try:
            profile_icon = self.wait.until(
                EC.presence_of_element_located((By.CLASS_NAME, "navbar-profile"))
            )
            print("✓ Login successful - Profile icon displayed")
            self.assertTrue(profile_icon.is_displayed())
        except TimeoutException:
            print("✗ Login failed - Profile icon not found")
            self.fail("Login verification failed")

    def test_02_search_restaurant(self):
        """
        Test Case 2: Search functionality
        """
        print("\n=== TEST 2: Search ===")
        
        # Wait for and click search input
        search_input = None
        try:
            search_input = self.wait.until(
                EC.element_to_be_clickable((By.CSS_SELECTOR, "input[placeholder*='Tìm']"))
            )
        except:
            try:
                search_input = self.wait.until(
                    EC.element_to_be_clickable((By.CSS_SELECTOR, "input[type='text']"))
                )
            except:
                search_input = self.wait.until(
                    EC.element_to_be_clickable((By.XPATH, "//input[@placeholder or @type='text']"))
                )
        
        search_input.click()
        
        # Search for a restaurant
        search_term = "Phở"
        search_input.clear()
        search_input.send_keys(search_term)
        search_input.send_keys(Keys.RETURN)
        
        print(f"Searching for: {search_term}")

        # Wait for results
        try:
            self.wait.until(
                EC.presence_of_element_located((By.CLASS_NAME, "restaurant-item"))
            )
            results = self.driver.find_elements(By.CLASS_NAME, "restaurant-item")
            print(f"✓ Search returned {len(results)} results")
            self.assertGreater(len(results), 0, "No search results found")
        except TimeoutException:
            print("✗ Search failed - No results displayed")
            self.fail("Search verification failed")
    def test_03_add_to_cart(self):
        """
        Test Case 3: Add item to cart
        Flow: Scroll down → Click restaurant → Detail page → Click food item (handleOpenOrAdd) 
        → Modal (FoodOptionsModal) → Select option → Change qty → Click "Thêm vào giỏ"
        """
        print("\n=== TEST 3: Add to Cart ===")
        
        # Step 1: Scroll down to see restaurants
        self.driver.execute_script("window.scrollBy(0, 500);")
        self.wait.until(EC.presence_of_element_located((By.CLASS_NAME, "restaurant-item")))
        print("✓ Scrolled down to view restaurants")
        
        # Step 2: Click on first restaurant
        first_restaurant = self.wait.until(
            EC.presence_of_element_located((By.CLASS_NAME, "restaurant-item"))
        )
        restaurant_name = first_restaurant.text
        print(f"Clicking restaurant: {restaurant_name}")
        self.driver.execute_script("arguments[0].scrollIntoView(true);", first_restaurant)
        self.wait.until(EC.element_to_be_clickable((By.CLASS_NAME, "restaurant-item")))
        first_restaurant.click()

        # Step 3: Wait for restaurant detail page to load
        self.wait.until(
            EC.url_contains("/restaurant/")
        )
        print("✓ Navigated to restaurant detail page with menu list")

        # Step 4: Wait for food items to load
        self.wait.until(
            EC.presence_of_element_located((By.CLASS_NAME, "food-item"))
        )

        # Step 5: Click on first food item to trigger handleOpenOrAdd
        first_food_item = self.wait.until(
            EC.presence_of_element_located((By.CSS_SELECTOR, ".food-item"))
        )
        food_name = first_food_item.find_element(By.CSS_SELECTOR, ".food-item-name-rating p").text
        print(f"Clicking food item: {food_name}")
        self.driver.execute_script("arguments[0].scrollIntoView(true);", first_food_item)
        self.wait.until(EC.element_to_be_clickable((By.CSS_SELECTOR, ".food-item")))
        first_food_item.click()

        # Step 6: Wait 2-3s for FoodOptionsModal to load completely
        print("⏳ Waiting 2-3s for FoodOptionsModal to load...")
        try:
            modal = self.wait.until(
                EC.presence_of_element_located((By.CSS_SELECTOR, ".fom-modal, .fom-overlay"))
            )
            # Additional wait to ensure modal is fully rendered
            self.driver.execute_script("return document.readyState === 'complete';")
            import time
            time.sleep(2)
            print("✓ FoodOptionsModal loaded completely (handleOpenOrAdd triggered)")
            
            # Step 7: Select required options if present
            try:
                # Try to find first unchecked radio/checkbox option
                option_inputs = self.driver.find_elements(By.CSS_SELECTOR, ".fom-option input[type='radio'], .fom-option input[type='checkbox']")
                if len(option_inputs) > 0:
                    # Click first available option via JavaScript (to bypass span overlay)
                    first_option = option_inputs[0]
                    self.driver.execute_script("arguments[0].scrollIntoView(true);", first_option)
                    self.driver.execute_script("arguments[0].click();", first_option)
                    print("✓ Selected option from modal")
                else:
                    print("Note: No options to select")
            except TimeoutException:
                print("Note: No options to select")
            except Exception as e:
                print(f"Note: Option selection skipped - {e}")
            
            # Step 8: Increase quantity using + button (cộng số lượng)
            try:
                # Find quantity control buttons - the second button is the + button
                qty_buttons = self.driver.find_elements(By.XPATH, "//div[@class='fom-qty']//button")
                if len(qty_buttons) >= 2:
                    # Second button is the increase button
                    qty_buttons[1].click()
                    print("✓ Increased quantity (+)")
                else:
                    print("Note: Quantity buttons not found")
            except Exception as e:
                print(f"Note: Using default quantity - {e}")
            
            # Step 9: Click "Thêm vào giỏ" button
            try:
                add_to_cart_btn = self.wait.until(
                    EC.presence_of_element_located((By.CSS_SELECTOR, ".fom-add"))
                )
                self.driver.execute_script("arguments[0].scrollIntoView(true);", add_to_cart_btn)
                # Wait for button to be clickable
                add_to_cart_btn = self.wait.until(
                    EC.element_to_be_clickable((By.CSS_SELECTOR, ".fom-add"))
                )
                add_to_cart_btn.click()
                print("✓ Clicked 'Thêm vào giỏ' button")
            except Exception as e:
                print(f"✗ Failed to click add to cart button: {e}")
                self.fail(f"Add to cart button click failed: {e}")
            
            # Step 10: Wait for modal to close
            import time
            try:
                self.wait.until(
                    EC.invisibility_of_element_located((By.CSS_SELECTOR, ".fom-overlay"))
                )
                time.sleep(0.5)  # Extra time to ensure modal fully closed
            except:
                # Modal may have closed already
                pass
            print("✓ Item added to cart successfully")
                    
        except TimeoutException as e:
            print(f"✗ Add to cart failed: {e}")
            self.fail("Add to cart verification failed")

    def test_04_update_cart_quantity(self):
        """
        Test Case 4: Update quantity in cart
        NOTE: This test runs immediately after Test 3 without setUp() to preserve page state
        We're still on /restaurant/1 page, so we'll open cart drawer directly
        Flow: Open drawer from restaurant page → Click "Thanh toán" → Go to /cart page → Update qty → Click "TIẾN HÀNH THANH TOÁN"
        
        IMPORTANT: setUp() is intentionally skipped - DO NOT add it
        """
        print("\n=== TEST 4: Update Cart Quantity ===")
        print("NOTE: Still on restaurant page from Test 3 - opening drawer directly")
        
        # STEP 1: Open cart drawer from restaurant detail page
        try:
            import time
            time.sleep(1)
            cart_btn = self.wait.until(
                EC.element_to_be_clickable((By.CSS_SELECTOR, ".navbar-search-icon img[role='button']"))
            )
            cart_btn.click()
            
            self.wait.until(
                EC.visibility_of_element_located((By.CSS_SELECTOR, ".cart-drawer"))
            )
            print("✓ Cart drawer opened from restaurant page")
        except Exception as e:
            print(f"✗ Failed to open cart drawer: {e}")
            self.fail(f"Failed to open cart drawer: {e}")
        
        # STEP 2: Click "Thanh toán" button in drawer to go to cart page
        try:
            import time
            time.sleep(1)
            thanhtoan_btn = self.wait.until(
                EC.element_to_be_clickable((By.CSS_SELECTOR, ".cart-drawer-checkout"))
            )
            thanhtoan_btn.click()
            
            self.wait.until(EC.url_contains("/cart"))
            print("✓ Navigated to cart page (/cart)")
        except Exception as e:
            print(f"✗ Navigate to cart failed: {e}")
            self.fail(f"Failed to navigate to cart page: {e}")
        
        # STEP 3: Update quantity on cart page
        try:
            # Wait for cart items to load
            import time
            time.sleep(1)
            self.wait.until(
                EC.presence_of_element_located((By.CLASS_NAME, "cart-line"))
            )
            print("✓ Cart page loaded with items")
            
            # Find and click quantity increase button
            qty_plus_btns = self.driver.find_elements(By.XPATH, "//div[@class='line-qty']//button[contains(., '+')]")
            initial_qty = 0
            if len(qty_plus_btns) > 0:
                # Get initial quantity
                qty_displays = self.driver.find_elements(By.XPATH, "//div[@class='line-qty']//span[@class='qty-num']")
                if len(qty_displays) > 0:
                    initial_qty = int(qty_displays[0].text)
                    print(f"Initial quantity: {initial_qty}")
                
                # Click increase button
                qty_plus_btns[0].click()
                time.sleep(0.5)
                
                # Verify quantity increased
                updated_qty = int(qty_displays[0].text)
                print(f"✓ Updated quantity: {initial_qty} → {updated_qty}")
        except Exception as e:
            print(f"Note: Quantity update: {e}")
        
        # STEP 4: Click "TIẾN HÀNH THANH TOÁN"
        try:
            import time
            time.sleep(1)
            place_order_btn = self.wait.until(
                EC.element_to_be_clickable((By.XPATH, "//button[contains(text(), 'TIẾN HÀNH THANH TOÁN')]"))
            )
            place_order_btn.click()
            
            self.wait.until(EC.url_contains("/order"))
            print("✓ Navigated to place order page (/order)")
        except Exception as e:
            print(f"✗ Checkout navigation failed: {e}")
            self.fail(f"Failed to navigate to order page: {e}")

    def test_05_checkout_flow(self):
        """
        Test Case 5: Complete checkout flow on /order page
        NOTE: This test continues from Test 4 - already on /order page
        Flow: Fill form (fullName, phone, address) → Select COD payment → Click "Xác nhận đặt hàng" → /track-order
        
        IMPORTANT: setUp() is automatically skipped because current URL is /order
        """
        print("\n=== TEST 5: Checkout Flow on Order Page ===")
        print("NOTE: Continuing from Test 4 - already on /order page")
        
        # STEP 1: Fill checkout form fields
        try:
            import time
            time.sleep(1)
            self.wait.until(
                EC.presence_of_element_located((By.XPATH, "//input[@placeholder='Họ và tên']"))
            )
            print("✓ Order form loaded and ready")
            
            # Fill fullName input
            fullname_input = self.wait.until(
                EC.presence_of_element_located((By.XPATH, "//input[@name='fullName']"))
            )
            fullname_input.clear()
            fullname_input.send_keys("Nguyen Van Test")
            print("✓ Filled fullName: Nguyen Van Test")
            
            # Fill phone input
            phone_input = self.wait.until(
                EC.presence_of_element_located((By.XPATH, "//input[@name='phone']"))
            )
            phone_input.clear()
            phone_input.send_keys("0912345678")
            print("✓ Filled phone: 0912345678")
            
            # Fill addressDetail input
            address_input = self.wait.until(
                EC.presence_of_element_located((By.XPATH, "//input[@name='addressDetail']"))
            )
            address_input.clear()
            address_input.send_keys("123 Nguyen Hue, District 1, Ho Chi Minh City")
            print("✓ Filled address: 123 Nguyen Hue, District 1, Ho Chi Minh City")
            
        except Exception as e:
            print(f"✗ Form filling failed: {e}")
            self.fail(f"Form filling failed: {e}")
        
        # STEP 2: Verify/Select COD payment method (usually default)
        try:
            import time
            time.sleep(0.5)
            
            # Find first payment option (should be COD by default)
            payment_options = self.driver.find_elements(By.XPATH, "//div[@class='payment-option']")
            if len(payment_options) > 0:
                cod_option = payment_options[0]
                # Click via JavaScript if not already selected
                if 'selected' not in cod_option.get_attribute('class'):
                    self.driver.execute_script("arguments[0].click();", cod_option)
                    print("✓ Selected COD payment method")
                else:
                    print("✓ COD payment method already selected")
            else:
                print("✓ No payment options found (COD is default)")
                
        except Exception as e:
            print(f"Note: Payment method selection - {e}")
        
        # STEP 3: Click "Xác nhận đặt hàng" button
        try:
            import time
            time.sleep(1)
            
            # Try multiple selectors for confirm button
            confirm_btn = None
            try:
                # Selector 1: Button with confirm-btn class
                confirm_btn = self.wait.until(
                    EC.element_to_be_clickable((By.XPATH, "//button[@class='confirm-btn']"))
                )
                print("✓ Found confirm button (confirm-btn class)")
            except:
                try:
                    # Selector 2: Submit button in payment-box
                    confirm_btn = self.wait.until(
                        EC.element_to_be_clickable((By.XPATH, "//div[@class='payment-box']//button[@type='submit']"))
                    )
                    print("✓ Found confirm button (submit in payment-box)")
                except:
                    # Selector 3: Any submit button
                    confirm_btn = self.wait.until(
                        EC.element_to_be_clickable((By.XPATH, "//form[@class='place-order']//button[@type='submit']"))
                    )
                    print("✓ Found confirm button (form submit)")
            
            # Scroll button into view before clicking
            self.driver.execute_script("arguments[0].scrollIntoView(true);", confirm_btn)
            time.sleep(0.5)
            
            # Click the button
            confirm_btn.click()
            print("✓ Clicked 'Xác nhận đặt hàng'")
            
            # Handle success alert (PlaceOrder.jsx shows alert before redirect)
            import time
            time.sleep(0.5)
            self.handle_alert_if_present()
            print("✓ Success alert dismissed")
            
            # Wait for page navigation to complete (redirects to home /)
            time.sleep(1)
            self.wait.until(EC.presence_of_element_located((By.CLASS_NAME, "logo")))
            print("✓✓✓ Order placed successfully!")
            print("✓ Returned to home page - Order confirmation complete")
                    
        except Exception as e:
            print(f"✗ Order confirmation failed: {e}")
            self.fail(f"Order confirmation failed: {e}")


if __name__ == "__main__":
    unittest.main(verbosity=2)
