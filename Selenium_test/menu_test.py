import unittest
import time
import os
import glob
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait, Select
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager


class MenuManagementTest(unittest.TestCase):
    """
    Test Suite: Manage Menu (List + AddDishModal)
    Prereq: App running at localhost:5174, account has access
    Note: Login runs once in setUpClass (not counted as a test)
    """

    created_dish_name = None

    @classmethod
    def setUpClass(cls):
        cls.driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()))
        cls.driver.maximize_window()
        cls.wait = WebDriverWait(cls.driver, 15)
        cls.base_list_url = "http://localhost:5174/list"

        print("\n=== AUTO LOGIN (Prerequisite) ===")
        cls._login_and_go_to_list()

    @classmethod
    def tearDownClass(cls):
        cls.driver.quit()

    @classmethod
    def _login_and_go_to_list(cls):
        try:
            cls.driver.get("http://localhost:5174/login")
            cls.wait.until(EC.presence_of_element_located((By.CLASS_NAME, "login-form")))

            email_input = cls.wait.until(EC.presence_of_element_located((By.CSS_SELECTOR, "input[type='text'][placeholder='Email']")))
            email_input.clear()
            email_input.send_keys("nhiknh112233@gmail.com")

            password_input = cls.driver.find_element(By.CSS_SELECTOR, "input[type='password'][placeholder='Mật khẩu']")
            password_input.clear()
            password_input.send_keys("12345678")

            cls.driver.find_element(By.CSS_SELECTOR, "button[type='submit']").click()
            time.sleep(3)

            cls.wait.until(EC.presence_of_element_located((By.CLASS_NAME, "sidebar")))
            list_link = cls.wait.until(EC.element_to_be_clickable((By.CSS_SELECTOR, "a[href='/list']")))
            list_link.click()
            cls.wait.until(EC.url_contains("/list"))
            cls.wait.until(EC.presence_of_element_located((By.CLASS_NAME, "list-container")))
            print("✓ Logged in and on /list")
        except Exception as e:
            print(f"✗ Auto login failed: {e}")
            raise

    def setUp(self):
        current = self.driver.current_url
        if "/list" not in current:
            try:
                list_link = self.wait.until(EC.element_to_be_clickable((By.CSS_SELECTOR, "a[href='/list']")))
                list_link.click()
                self.wait.until(EC.url_contains("/list"))
            except Exception:
                self.driver.get(self.base_list_url)
                self.wait.until(EC.presence_of_element_located((By.CLASS_NAME, "list-container")))
        self.wait.until(EC.presence_of_element_located((By.CLASS_NAME, "list-container")))
        self.close_all_modals()

    def close_all_modals(self):
        """Close any lingering edit/add modals/backdrops."""
        # Close edit modal if present
        try:
            backdrop = self.driver.find_element(By.CLASS_NAME, "edit-dish-modal-backdrop")
            if backdrop.is_displayed():
                try:
                    close_btn = self.driver.find_element(By.CLASS_NAME, "edit-dish-close")
                    close_btn.click()
                    self.pause(0.8)
                except Exception:
                    backdrop.click()
                    self.pause(0.8)
        except Exception:
            pass

        # Close add modal if present
        try:
            overlay = self.driver.find_element(By.CLASS_NAME, "add-dish-modal-overlay")
            if overlay.is_displayed():
                try:
                    close_btn = self.driver.find_element(By.CLASS_NAME, "add-dish-modal-close")
                    close_btn.click()
                    self.pause(0.5)
                except Exception:
                    overlay.click()
                    self.pause(0.5)
        except Exception:
            pass

    # Helpers
    def open_add_modal(self):
        btn = self.wait.until(EC.element_to_be_clickable((By.CLASS_NAME, "list-add-btn")))
        btn.click()
        modal = self.wait.until(EC.presence_of_element_located((By.CLASS_NAME, "add-dish-modal-content")))
        return modal

    def pause(self, seconds=1):
        """Small pause between UI actions for stability."""
        time.sleep(seconds)

    def close_add_modal(self):
        try:
            close_btn = self.driver.find_element(By.CLASS_NAME, "add-dish-modal-close")
            close_btn.click()
            time.sleep(0.5)
        except Exception:
            try:
                overlay = self.driver.find_element(By.CLASS_NAME, "add-dish-modal-overlay")
                overlay.click()
                time.sleep(0.5)
            except Exception:
                pass

    def get_local_image(self):
        folder = r"D:\SourceStudy\picfood"
        exts = ["*.jpg", "*.jpeg", "*.png", "*.webp"]
        for pattern in exts:
            files = glob.glob(os.path.join(folder, pattern))
            if files:
                return files[0]
        return None

    def fill_add_form(self, name, price, category_value, description="", image_url=None, image_file=None):
        name_input = self.driver.find_element(By.CSS_SELECTOR, "input[name='name']")
        name_input.clear()
        name_input.send_keys(name)

        price_input = self.driver.find_element(By.CSS_SELECTOR, "input[name='price']")
        price_input.clear()
        price_input.send_keys(str(price))

        desc_input = self.driver.find_element(By.CSS_SELECTOR, "textarea[name='description']")
        desc_input.clear()
        desc_input.send_keys(description)

        select_el = self.driver.find_element(By.CSS_SELECTOR, "select[name='category']")
        Select(select_el).select_by_value(str(category_value))

        url_input = self.driver.find_element(By.CSS_SELECTOR, "input.add-dish-url-input")
        file_input = self.driver.find_element(By.CSS_SELECTOR, "input#dish-image")

        # Prefer local file if provided/found
        if image_file and os.path.exists(image_file):
            url_input.clear()
            file_input.send_keys(image_file)
        elif image_url:
            url_input.clear()
            url_input.send_keys(image_url)
        else:
            url_input.clear()

    def submit_add_form(self):
        submit_btn = self.driver.find_element(By.CLASS_NAME, "add-dish-btn-submit")
        submit_btn.click()
        time.sleep(1)

    def handle_alert_if_present(self, timeout=3):
        try:
            alert = WebDriverWait(self.driver, timeout).until(EC.alert_is_present())
            txt = alert.text
            alert.accept()
            time.sleep(0.5)
            return txt
        except Exception:
            return None

    def find_dish_card(self, name):
        cards = self.driver.find_elements(By.CLASS_NAME, "list-item-card")
        for card in cards:
            try:
                title = card.find_element(By.CLASS_NAME, "list-item-name").text
                if name.lower() in title.lower():
                    return card
            except Exception:
                continue
        return None

    # Tests
    def test_01_add_new_dish(self):
        print("\n=== TEST MENU-001: Add new dish ===")
        dish_name = f"Auto Dish {int(time.time())}"
        MenuManagementTest.created_dish_name = dish_name

        self.open_add_modal()
        self.pause(1)
        # Use first category option (excluding placeholder)
        select_el = self.driver.find_element(By.CSS_SELECTOR, "select[name='category']")
        options = select_el.find_elements(By.TAG_NAME, "option")
        category_val = None
        for opt in options:
            if opt.get_attribute("value"):
                category_val = opt.get_attribute("value")
                break
        # Try local image first; fallback to URL if none found
        local_img = self.get_local_image()
        self.fill_add_form(
            name=dish_name,
            price=55000,
            category_value=category_val,
            description="Tự động thêm món test",
            image_file=local_img,
            image_url="https://gongcha.com.vn/wp-content/uploads/2024/09/MANGO-SOGO-WEB.png" if not local_img else None
        )
        self.pause(1)
        self.submit_add_form()
        self.pause(10)  # server chậm, đợi phản hồi
        alert_txt = self.handle_alert_if_present(timeout=2)
        if alert_txt:
            print(f"Alert: {alert_txt}")
        self.pause(2)
        card = self.find_dish_card(dish_name)
        self.assertIsNotNone(card, "Dish not found after creation")
        print(f"✓ Created dish: {dish_name}")

    def test_02_update_dish(self):
        print("\n=== TEST MENU-002: Update dish ===")
        base_name = MenuManagementTest.created_dish_name or "Auto Dish"
        card = self.find_dish_card(base_name)
        if not card:
            self.skipTest("Dish from TC1 not found; cannot update")

        edit_btn = card.find_element(By.CLASS_NAME, "list-item-edit-btn")
        edit_btn.click()
        # Wait for edit modal
        self.wait.until(EC.presence_of_element_located((By.CLASS_NAME, "edit-dish-modal")))
        self.pause(1)

        try:
            name_input = self.wait.until(EC.presence_of_element_located((By.CSS_SELECTOR, "input#dish-name")))
            name_input.clear()
            new_name = f"{base_name} Updated"
            name_input.send_keys(new_name)

            price_input = self.driver.find_element(By.CSS_SELECTOR, "input#dish-price")
            price_input.clear()
            price_input.send_keys("60000")

            desc_input = self.driver.find_element(By.CSS_SELECTOR, "textarea#dish-description")
            desc_input.clear()
            desc_input.send_keys("Cap nhat tu dong")

            # Must pick a new image when editing
            local_img = self.get_local_image()
            if not local_img:
                self.fail("No local image found at D:\\SourceStudy\\picfood for update")
            file_input = self.driver.find_element(By.CSS_SELECTOR, "input#dish-image")
            file_input.send_keys(local_img)
            self.pause(1)

            save_btn = self.driver.find_element(By.CSS_SELECTOR, ".edit-dish-btn.primary")
            save_btn.click()
            self.pause(2)
            MenuManagementTest.created_dish_name = new_name
        except Exception as e:
            self.fail(f"Update flow failed: {e}")

        # Wait for edit modal to close
        try:
            self.wait.until(EC.invisibility_of_element_located((By.CLASS_NAME, "edit-dish-modal-backdrop")))
        except Exception:
            pass
        self.pause(1.5)
        card_updated = self.find_dish_card(MenuManagementTest.created_dish_name)
        self.assertIsNotNone(card_updated, "Updated dish not found")
        print(f"✓ Updated dish to: {MenuManagementTest.created_dish_name}")

    def test_03_delete_dish(self):
        print("\n=== TEST MENU-003: Delete dish ===")
        target_name = MenuManagementTest.created_dish_name or "Auto Dish"
        card = self.find_dish_card(target_name)
        if not card:
            self.skipTest("Dish not found for deletion")

        # Ensure no edit modal is covering
        self.close_all_modals()
        self.pause(0.5)

        delete_btn = card.find_element(By.CLASS_NAME, "list-item-delete-btn")
        delete_btn.click()
        self.pause(1)
        alert_txt = self.handle_alert_if_present(timeout=4)
        if alert_txt:
            print(f"Alert: {alert_txt}")
        self.pause(2)

        # Some backends show a second success alert; clear it if present
        alert_txt2 = self.handle_alert_if_present(timeout=3)
        if alert_txt2:
            print(f"Alert: {alert_txt2}")
        self.pause(1)

        card_after = self.find_dish_card(target_name)
        self.assertIsNone(card_after, "Dish still exists after deletion")
        print(f"✓ Deleted dish: {target_name}")

    def test_04_add_invalid_dish(self):
        print("\n=== TEST MENU-004: Add dish with invalid data ===")
        self.close_all_modals()
        self.pause(0.5)
        self.open_add_modal()
        self.pause(1)

        # Intentionally leave required fields invalid: empty name, empty price, empty category, no image/url
        name_input = self.driver.find_element(By.CSS_SELECTOR, "input[name='name']")
        price_input = self.driver.find_element(By.CSS_SELECTOR, "input[name='price']")
        desc_input = self.driver.find_element(By.CSS_SELECTOR, "textarea[name='description']")
        select_el = self.driver.find_element(By.CSS_SELECTOR, "select[name='category']")
        url_input = self.driver.find_element(By.CSS_SELECTOR, "input.add-dish-url-input")
        file_input = self.driver.find_element(By.CSS_SELECTOR, "input#dish-image")

        name_input.clear()
        price_input.clear()
        desc_input.clear()
        url_input.clear()
        select_el.send_keys("\ue009\ue003")  # CTRL+Backspace to ensure empty, though placeholder already empty
        try:
            file_input.clear()
        except Exception:
            pass

        self.pause(1)
        self.submit_add_form()
        self.pause(1)

        # Check for alert first
        alert_txt = self.handle_alert_if_present(timeout=2)

        # If no alert, check native validation messages
        validation_msgs = []
        for el in [name_input, price_input, select_el]:
            try:
                msg = el.get_attribute("validationMessage")
                if msg:
                    validation_msgs.append(msg)
            except Exception:
                pass

        self.close_add_modal()

        self.assertTrue(alert_txt or validation_msgs, "Expected validation error but none shown")
        if alert_txt:
            print(f"✓ Validation alert shown: {alert_txt}")
        else:
            print(f"✓ Native validation message(s): {validation_msgs}")


if __name__ == "__main__":
    unittest.main(verbosity=2)
