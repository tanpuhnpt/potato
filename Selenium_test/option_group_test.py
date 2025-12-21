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
from selenium.webdriver.common.action_chains import ActionChains


class OptionGroupManagementTest(unittest.TestCase):
    """
    Test Suite: Manage Option Groups
    Module: Restaurant Dashboard - Option Groups Management
    Prerequisites: App running, account logged in
    """

    created_group_name = None

    @classmethod
    def setUpClass(cls):
        cls.driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()))
        cls.driver.maximize_window()
        cls.wait = WebDriverWait(cls.driver, 15)
        cls.base_url = "http://localhost:5174/option-groups"

        print("\n=== AUTO LOGIN (Prerequisite) ===")
        cls._login_and_navigate()

    @classmethod
    def tearDownClass(cls):
        cls.driver.quit()

    @classmethod
    def _login_and_navigate(cls):
        """Login and navigate to option-groups page"""
        try:
            # Login
            cls.driver.get("http://localhost:5174/login")
            cls.wait.until(EC.presence_of_element_located((By.CLASS_NAME, "login-form")))

            email_input = cls.wait.until(EC.presence_of_element_located(
                (By.CSS_SELECTOR, "input[type='text'][placeholder='Email']")
            ))
            email_input.clear()
            email_input.send_keys("nhiknh112233@gmail.com")

            password_input = cls.driver.find_element(By.CSS_SELECTOR, "input[type='password'][placeholder='Mật khẩu']")
            password_input.clear()
            password_input.send_keys("12345678")

            cls.driver.find_element(By.CSS_SELECTOR, "button[type='submit']").click()
            time.sleep(3)

            # Navigate to option-groups
            cls.wait.until(EC.presence_of_element_located((By.CLASS_NAME, "sidebar")))
            option_groups_link = cls.wait.until(
                EC.element_to_be_clickable((By.CSS_SELECTOR, "a[href='/option-groups']"))
            )
            option_groups_link.click()
            cls.wait.until(EC.url_contains("/option-groups"))
            cls.wait.until(EC.presence_of_element_located((By.CLASS_NAME, "option-groups-container")))
            print("✓ Logged in and on /option-groups")
        except Exception as e:
            print(f"✗ Auto login failed: {e}")
            raise

    def setUp(self):
        """Just close modals before each test - don't reload page"""
        self.close_all_modals()
        self.pause(0.5)

    def pause(self, seconds=1):
        """Pause between actions for stability"""
        time.sleep(seconds)

    def navigate_via_sidebar(self, href):
        """Navigate using sidebar link without reloading"""
        try:
            link = self.wait.until(EC.element_to_be_clickable((By.CSS_SELECTOR, f"a[href='{href}']")))
            link.click()
            self.wait.until(EC.url_contains(href))
            self.pause(1)
            return True
        except Exception as e:
            print(f"⚠ Sidebar navigation to {href} failed: {e}")
            return False

    def close_all_modals(self):
        """Close any open modals/dialogs"""
        try:
            # Close assign modal if present
            backdrop = self.driver.find_element(By.CLASS_NAME, "aog-modal-backdrop")
            if backdrop.is_displayed():
                try:
                    close_btn = backdrop.find_element(By.CSS_SELECTOR, "button.aog-ghost")
                    close_btn.click()
                    self.pause(0.5)
                except:
                    pass
        except:
            pass

        try:
            # Close manage modal if present
            backdrop = self.driver.find_element(By.CLASS_NAME, "mog-modal-backdrop")
            if backdrop.is_displayed():
                try:
                    close_btn = backdrop.find_element(By.CLASS_NAME, "mog-btn-close")
                    close_btn.click()
                    self.pause(0.5)
                except:
                    pass
        except:
            pass

    def handle_alert_if_present(self, timeout=3):
        """Handle alert/confirm dialog if present"""
        try:
            alert = WebDriverWait(self.driver, timeout).until(EC.alert_is_present())
            txt = alert.text
            alert.accept()
            self.pause(0.3)
            return txt
        except:
            return None

    # Helper methods for Create flow
    def open_create_form(self):
        """Click 'Tạo mới' button to show AddOptionGroup form"""
        try:
            create_btn = self.wait.until(EC.element_to_be_clickable((By.CLASS_NAME, "option-groups-add-btn")))
            btn_text = create_btn.text
            if "Đóng Form" in btn_text:
                print("✓ Form already open")
                return True
            create_btn.click()
            self.pause(1)
            # Verify form appears
            self.wait.until(EC.presence_of_element_located((By.CLASS_NAME, "aog-wrap")))
            print("✓ Create form opened")
            return True
        except Exception as e:
            print(f"✗ Failed to open create form: {e}")
            return False

    def fill_create_form(self, title, option_type='single', required=False, options=None):
        """
        Fill AddOptionGroup form
        Args:
            title: Group title
            option_type: 'single' or 'multi'
            required: True/False
            options: list of dict [{'label': 'Option 1', 'priceDelta': 5000}, ...]
        """
        try:
            # Fill title
            title_input = self.driver.find_element(By.CSS_SELECTOR, ".aog-field input[placeholder='Nhập tên nhóm tùy chọn']")
            title_input.clear()
            title_input.send_keys(title)
            print(f"✓ Filled title: {title}")

            # Select type
            type_select = self.driver.find_element(By.CSS_SELECTOR, ".aog-selection-dropdown select")
            Select(type_select).select_by_value('1' if option_type == 'single' else 'multi')
            print(f"✓ Selected type: {option_type}")

            # Set required toggle
            required_toggle = self.driver.find_element(By.CSS_SELECTOR, ".aog-required-section input[type='checkbox']")
            if required != required_toggle.is_selected():
                required_toggle.click()
            print(f"✓ Set required: {required}")

            # Fill options
            if options:
                # First, clear existing options except first one
                existing_options = self.driver.find_elements(By.CLASS_NAME, "aog-opt-row")
                for i in range(len(existing_options) - 1, 0, -1):
                    try:
                        delete_btn = existing_options[i].find_element(By.CLASS_NAME, "aog-danger")
                        delete_btn.click()
                        self.pause(0.3)
                    except:
                        pass

                # Fill options
                for idx, opt in enumerate(options):
                    # Get current option rows
                    opt_rows = self.driver.find_elements(By.CLASS_NAME, "aog-opt-row")
                    
                    # If need more rows, click add button
                    if idx >= len(opt_rows):
                        add_btn = self.driver.find_element(By.CLASS_NAME, "aog-add")
                        add_btn.click()
                        self.pause(0.3)
                        opt_rows = self.driver.find_elements(By.CLASS_NAME, "aog-opt-row")

                    # Fill this option
                    row = opt_rows[idx]
                    label_input = row.find_element(By.CLASS_NAME, "aog-opt-label")
                    label_input.clear()
                    label_input.send_keys(opt.get('label', ''))

                    price_input = row.find_element(By.CLASS_NAME, "aog-opt-price")
                    price_input.clear()
                    price_input.send_keys(str(opt.get('priceDelta', 0)))

                print(f"✓ Filled {len(options)} option(s)")
            return True
        except Exception as e:
            print(f"✗ Failed to fill create form: {e}")
            return False

    def submit_create_form(self):
        """Click 'Tạo nhóm' button"""
        try:
            submit_btn = self.driver.find_element(By.CSS_SELECTOR, "button.aog-primary")
            submit_btn.click()
            print("✓ Clicked 'Tạo nhóm'")
            self.pause(2)
            return True
        except Exception as e:
            print(f"✗ Failed to submit: {e}")
            return False

    def skip_assign_modal(self):
        """Close assign modal by clicking 'Để sau'"""
        try:
            # Wait for assign modal
            self.wait.until(EC.presence_of_element_located((By.CLASS_NAME, "aog-modal-backdrop")))
            skip_btn = self.driver.find_element(By.XPATH, "//button[contains(@class, 'aog-ghost') and contains(text(), 'Để sau')]")
            skip_btn.click()
            self.pause(1)
            print("✓ Skipped assign modal")
            return True
        except:
            print("⚠ No assign modal or already closed")
            return False

    def find_group_in_current_list(self, group_name):
        """Find group in current page by checking page source"""
        try:
            self.wait.until(EC.presence_of_element_located((By.CLASS_NAME, "option-groups-content")))
            self.pause(0.5)
            page_text = self.driver.page_source
            if group_name in page_text:
                print(f"✓ Found group '{group_name}' in page")
                return True
            return False
        except Exception as e:
            print(f"⚠ Error checking group existence: {e}")
            return False

    def click_group_to_select(self, group_element):
        """Click group to select and show details"""
        try:
            group_element.click()
            self.pause(1)
            # Verify group header appears
            self.wait.until(EC.presence_of_element_located((By.CLASS_NAME, "mog-group-header")))
            print("✓ Group selected")
            return True
        except Exception as e:
            print(f"✗ Failed to select group: {e}")
            return False

    # Tests
    def test_01_create_option_group(self):
        """
        TC-OPT-001: Create new option group
        Data: title, type=single, required=false, 2 options
        Expected: Group created, appears in list
        """
        print("\n=== TEST OPT-001: Create Option Group ===")

        group_name = f"Test Group {int(time.time())}"
        OptionGroupManagementTest.created_group_name = group_name

        # Open create form
        self.open_create_form()
        self.pause(1)

        # Fill form
        options = [
            {'label': 'Size M', 'priceDelta': 0},
            {'label': 'Size L', 'priceDelta': 5000}
        ]
        self.fill_create_form(
            title=group_name,
            option_type='single',
            required=False,
            options=options
        )
        self.pause(1)

        # Submit
        self.submit_create_form()
        self.pause(2)

        # Skip assign modal
        self.skip_assign_modal()
        self.pause(1)

        # Verify: check if group appears in current page
        found = self.find_group_in_current_list(group_name)
        self.assertTrue(found, f"Group '{group_name}' not found after creation")
        print(f"✓✓✓ TEST PASSED: Created group '{group_name}'")

    def test_02_update_option_group(self):
        """
        TC-OPT-002: Update option group - add new option value
        Data: add new option value to group
        Expected: Option value added successfully
        """
        print("\n=== TEST OPT-002: Update Option Group ===")

        group_name = OptionGroupManagementTest.created_group_name
        if not group_name:
            self.skipTest("No group from TC-001")

        # Find the group item and click on the edit button (icon edit) to open Edit Modal
        try:
            self.driver.execute_script("window.scrollTo(0, document.body.scrollHeight);")
            self.pause(1)
            
            # Find the ogt-item that contains the group name, then click its edit button
            # The structure is: .ogt-item > .ogt-item-head > .ogt-title (contains name) + .ogt-action-btn-edit (edit button)
            group_items = self.driver.find_elements(By.CLASS_NAME, "ogt-item")
            edit_btn = None
            for item in group_items:
                try:
                    title_el = item.find_element(By.CLASS_NAME, "ogt-title")
                    if group_name in title_el.text:
                        # Found the group, now click the edit button (img with alt="Chỉnh sửa nhóm")
                        edit_btn = item.find_element(By.CSS_SELECTOR, ".ogt-action-btn-edit img[alt='Chỉnh sửa nhóm']")
                        break
                except:
                    continue
            
            if edit_btn:
                edit_btn.click()
                self.pause(2)
                print(f"✓ Clicked edit button for group '{group_name}'")
            else:
                self.fail(f"Edit button not found for group '{group_name}'")
            
            # Wait for Edit Modal to appear (ogt-modal-backdrop)
            self.wait.until(EC.presence_of_element_located((By.CLASS_NAME, "ogt-modal-backdrop")))
            self.pause(1)
            
            # Verify modal header contains group name
            modal_header = self.wait.until(EC.presence_of_element_located((By.CSS_SELECTOR, ".ogt-modal-head h3")))
            self.assertIn(group_name, modal_header.text, "Modal header should contain group name")
            print(f"✓ Edit modal opened for group '{group_name}'")

            # Add new option value
            new_option_name = "Size XL"
            new_option_price = "10000"
            
            # Find input for option name
            name_input = self.driver.find_element(By.CSS_SELECTOR, ".ogt-modal-add-form input[placeholder='Tên option']")
            name_input.clear()
            name_input.send_keys(new_option_name)
            print(f"✓ Entered option name: {new_option_name}")
            
            # Find input for option price
            price_input = self.driver.find_element(By.CSS_SELECTOR, ".ogt-modal-price-input input[type='number']")
            price_input.clear()
            price_input.send_keys(new_option_price)
            print(f"✓ Entered option price: {new_option_price}")
            
            # Click "Thêm" button
            add_btn = self.driver.find_element(By.CSS_SELECTOR, ".ogt-modal-btn-add")
            add_btn.click()
            self.pause(2)
            print("✓ Clicked 'Thêm' button")

            # Handle alert if present
            alert_text = self.handle_alert_if_present(timeout=2)
            if alert_text:
                print(f"✓ Alert: {alert_text}")

            # Close modal
            close_btn = self.driver.find_element(By.CSS_SELECTOR, ".ogt-modal-close")
            close_btn.click()
            self.pause(1)
            print("✓ Closed edit modal")

            # Verify: expand the group and check if new option appears
            self.driver.execute_script("window.scrollTo(0, document.body.scrollHeight);")
            self.pause(1)
            
            # Find and click on the group to expand it
            group_items = self.driver.find_elements(By.CLASS_NAME, "ogt-item")
            for item in group_items:
                try:
                    title_el = item.find_element(By.CLASS_NAME, "ogt-title")
                    if group_name in title_el.text:
                        # Click on header to expand
                        header = item.find_element(By.CLASS_NAME, "ogt-item-head")
                        header.click()
                        self.pause(1)
                        break
                except:
                    continue
            
            # Check if new option value appears in the expanded list
            page_source = self.driver.page_source
            self.assertIn(new_option_name, page_source, f"New option '{new_option_name}' not found after adding")
            print(f"✓✓✓ TEST PASSED: Added new option value '{new_option_name}'")

        except Exception as e:
            self.fail(f"Update failed: {e}")

    def test_03_delete_option_group(self):
        """
        TC-OPT-003: Delete option group
        Expected: Group deleted, confirm dialog shown
        """
        print("\n=== TEST OPT-003: Delete Option Group ===")

        group_name = OptionGroupManagementTest.created_group_name
        if not group_name:
            self.skipTest("No group from previous tests")

        # Find the group and click delete button directly
        try:
            self.driver.execute_script("window.scrollTo(0, document.body.scrollHeight);")
            self.pause(1)
            
            # Find the ogt-item that contains the group name, then click its delete button
            group_items = self.driver.find_elements(By.CLASS_NAME, "ogt-item")
            delete_btn = None
            for item in group_items:
                try:
                    title_el = item.find_element(By.CLASS_NAME, "ogt-title")
                    if group_name in title_el.text:
                        # Found the group, now click the delete button (img with alt="Xóa nhóm")
                        delete_btn = item.find_element(By.CSS_SELECTOR, ".ogt-action-btn-delete img[alt='Xóa nhóm']")
                        break
                except:
                    continue
            
            if delete_btn:
                delete_btn.click()
                self.pause(1)
                print(f"✓ Clicked delete button for group '{group_name}'")
            else:
                self.fail(f"Delete button not found for group '{group_name}'")

            # Handle confirm alert (first) and possible success alert (second)
            first_alert = self.handle_alert_if_present(timeout=3)
            if first_alert:
                print(f"✓ Confirmed: {first_alert}")

            # App may show a success alert after deletion; accept it if present
            self.pause(0.5)
            for _ in range(2):  # attempt to clear any subsequent alerts
                follow_up = self.handle_alert_if_present(timeout=2)
                if follow_up:
                    print(f"✓ Alert: {follow_up}")
                    self.pause(0.3)
                else:
                    break

            # Wait a bit for UI to refresh
            self.pause(1.5)

            # Verify group no longer visible in list (search titles to avoid stale page_source during alerts)
            def group_absent(drv):
                try:
                    titles = drv.find_elements(By.CLASS_NAME, "ogt-title")
                    for el in titles:
                        try:
                            if group_name in (el.text or ''):
                                return False
                        except Exception:
                            continue
                    return True
                except Exception:
                    return False

            try:
                WebDriverWait(self.driver, 5).until(lambda d: group_absent(d))
            except Exception:
                # Final fallback check
                titles = self.driver.find_elements(By.CLASS_NAME, "ogt-title")
                still_exists = any(group_name in (el.text or '') for el in titles)
                self.assertFalse(still_exists, f"Group '{group_name}' still exists after deletion")

            print(f"✓✓✓ TEST PASSED: Deleted group '{group_name}'")

        except Exception as e:
            self.fail(f"Delete failed: {e}")

    def test_04_create_invalid_option_group(self):
        """
        TC-OPT-004: Create option group with invalid data
        Data: empty title, empty option labels
        Expected: Validation error (alert or blocked submission)
        """
        print("\n=== TEST OPT-004: Create Invalid Option Group ===")

        # Navigate back to option-groups page via sidebar
        self.navigate_via_sidebar('/option-groups')
        self.pause(1)
        self.pause(1)

        # Open create form
        self.open_create_form()
        self.pause(1)

        # Fill with invalid data: empty title, empty option labels
        try:
            title_input = self.driver.find_element(By.CSS_SELECTOR, ".aog-field input[placeholder='Nhập tên nhóm tùy chọn']")
            title_input.clear()

            # Leave options empty
            opt_rows = self.driver.find_elements(By.CLASS_NAME, "aog-opt-row")
            for row in opt_rows:
                label_input = row.find_element(By.CLASS_NAME, "aog-opt-label")
                label_input.clear()

            print("✓ Filled invalid data (empty fields)")
            self.pause(1)

            # Try to submit
            submit_btn = self.driver.find_element(By.CSS_SELECTOR, "button.aog-primary")
            submit_btn.click()
            self.pause(1)

            # Check for alert
            alert_text = self.handle_alert_if_present(timeout=2)

            # Check if modal still open (submission blocked)
            modal_still_open = False
            try:
                self.driver.find_element(By.CLASS_NAME, "aog-wrap")
                modal_still_open = True
            except:
                pass

            self.assertTrue(alert_text or modal_still_open, "Expected validation error but none shown")
            
            if alert_text:
                print(f"✓✓✓ TEST PASSED: Validation alert shown: {alert_text}")
            else:
                print(f"✓✓✓ TEST PASSED: Form submission blocked (form still open)")

        except Exception as e:
            self.fail(f"Invalid test failed: {e}")


if __name__ == "__main__":
    unittest.main(verbosity=2)
