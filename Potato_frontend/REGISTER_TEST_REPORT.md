# TEST REPORT - REGISTER MODULE (ACCESS CONTROL)

## THÔNG TIN CHUNG

| Thông tin | Giá trị |
|-----------|--------|
| **Module** | Access Control - Register Function |
| **Tester** | [Your Name] |
| **Test Date** | December 15, 2025 |
| **No. of Test Cases** | 4 |
| **Pass** | 4 |
| **Fail** | 0 |
| **Untested** | 0 |
| **N/A** | 0 |
| **Success Rate** | 100% |

---

## CHI TIẾT TỪNG TEST CASE

### TEST 1: REGISTER WITH VALID CREDENTIALS

| Trường | Chi tiết |
|--------|---------|
| **ID** | TC-REG-001 |
| **Description** | Verify user registration with valid email and password |
| **Module** | Access Control - Register |
| **Procedure** | Open register popup → Fill name, unique email (testuser_{timestamp}@example.com), valid password (Test@1234) → Check checkbox → Submit → Verify success alert |
| **Expected Output** | Alert "Đăng ký thành công!" appears, user auto-logged in, profile icon visible, popup closes |
| **Inter-Test Dependence** | None (standalone test)<br/>Provides logged-in state for Test 2 setup |
| **Result** | PASS |
| **Note** | Uses timestamp to generate unique email. After successful registration, user is auto-logged in. Test 2+ will logout before proceeding |

---

### TEST 2: REGISTER WITH INVALID PASSWORD

| Trường | Chi tiết |
|--------|---------|
| **ID** | TC-REG-002 |
| **Description** | Verify registration is rejected when password does not meet requirements |
| **Module** | Access Control - Register |
| **Procedure** | Auto logout → Open register popup → Fill form with invalid password "weak" (4 chars, no requirements met) → Submit → Verify error alert |
| **Expected Output** | Alert error about password requirements, registration fails, user stays on popup, not logged in |
| **Inter-Test Dependence** | Runs after TC-REG-001<br/>setUp() handles auto-logout |
| **Result** | PASS |
| **Note** | Backend password requirements: 8-20 chars, at least one uppercase, lowercase, digit, special character. Test uses "weak" password that violates all requirements |

---

### TEST 3: REGISTER WITH INVALID EMAIL

| Trường | Chi tiết |
|--------|---------|
| **ID** | TC-REG-003 |
| **Description** | Verify registration is rejected when email format is invalid |
| **Module** | Access Control - Register |
| **Procedure** | Open register popup → Fill form with invalid email "notanemail" (no @) and valid password → Submit → Check HTML5 validation or backend error |
| **Expected Output** | HTML5 validation blocks submission OR backend alert error about invalid email format, registration fails |
| **Inter-Test Dependence** | Runs after TC-REG-002<br/>Independent of previous test results |
| **Result** | PASS |
| **Note** | Test checks BOTH HTML5 client-side validation AND backend validation. Input type="email" should trigger browser validation before submission |

---

### TEST 4: REGISTER WITH EXISTING EMAIL

| Trường | Chi tiết |
|--------|---------|
| **ID** | TC-REG-004 |
| **Description** | Verify registration is rejected when email already exists in database |
| **Module** | Access Control - Register |
| **Procedure** | Open register popup → Fill form with existing email "demo@example.com" and valid password → Submit → Verify duplicate error alert |
| **Expected Output** | Alert error "Email đã tồn tại" or similar, registration fails, no account created |
| **Inter-Test Dependence** | Runs after TC-REG-003<br/>Depends on "demo@example.com" existing in database (from previous login tests) |
| **Result** | PASS |
| **Note** | Uses demo@example.com which was registered in previous tests. Backend should check email uniqueness and return appropriate error |

---

## TECHNICAL SUMMARY

### Helper Methods

| Method | Purpose | Implementation |
|--------|---------|---------------|
| `open_register_popup()` | Open and switch to register mode | Click login button → Wait for popup → Click "Đăng ký tại đây" → Verify "Sign Up" title |
| `fill_register_form()` | Fill name, email, password | Clear each input → send_keys() → Check checkbox |
| `submit_form()` | Submit registration form | Find submit button → Click |
| `handle_alert_if_present()` | Capture and dismiss alerts | Wait for alert (timeout 3s) → Get text → Accept → Return text |
| `close_popup_if_present()` | Close popup (cleanup) | Find cross icon → Click |
| `logout_if_logged_in()` | Auto logout between tests | Check profile icon → Hover → Click logout → Handle alert → Verify |

### State Management Strategy

| Scenario | Action |
|----------|--------|
| After TC-REG-001 | User is auto-logged in (profile icon appears) |
| Before TC-REG-002 | setUp() calls `logout_if_logged_in()` → Auto logout with alert handling |
| Before TC-REG-003 | setUp() checks login state → Logout if needed |
| Before TC-REG-004 | setUp() ensures logged out state |

**Key Technique**: `logout_if_logged_in()` method handles:
1. Check if `.navbar-profile` exists
2. If yes: Hover profile → Wait dropdown → Click logout → **Handle "Đã đăng xuất thành công!" alert** → Verify logout
3. If no: Skip (already logged out)

### Password Validation Rules (Backend)

```
Requirements:
- Length: 8-20 characters
- Must contain:
  ✓ At least one uppercase letter (A-Z)
  ✓ At least one lowercase letter (a-z)
  ✓ At least one digit (0-9)
  ✓ At least one special character (!@#$%^&*...)

Valid Examples:
✓ Test@1234    (8 chars, all requirements met)
✓ Valid@123    (9 chars, all requirements met)
✓ Pass@word1   (10 chars, all requirements met)

Invalid Examples:
✗ weak         (too short, missing uppercase, digit, special)
✗ password     (no uppercase, no digit, no special)
✗ PASSWORD123  (no lowercase, no special)
✗ Test1234     (no special character)
```

### Test Data Strategy

| Test Case | Email Strategy | Password | Reason |
|-----------|---------------|----------|--------|
| TC-REG-001 | `testuser_{timestamp}@example.com` | `Test@1234` | Timestamp ensures uniqueness on every run |
| TC-REG-002 | `testinvalid_{timestamp}@example.com` | `weak` | Intentionally violates all password rules |
| TC-REG-003 | `notanemail` | `Valid@123` | Missing @ to trigger email validation |
| TC-REG-004 | `demo@example.com` | `Test@1234` | Known existing email from previous tests |

---

## SELECTORS REFERENCE

### Critical Selectors Used

| Element | Selector | From Component |
|---------|----------|---------------|
| Login Button | `.navbar-login-btn` | Navbar.jsx |
| Popup Container | `.login-popup-container` | LoginPopup.jsx |
| Sign Up Link | `//span[contains(text(), 'Đăng ký tại đây')]` | LoginPopup.jsx |
| Popup Title | `.login-popup-title h2` | LoginPopup.jsx |
| Name Input | `input[name='name']` | LoginPopup.jsx |
| Email Input | `input[name='email']` | LoginPopup.jsx |
| Password Input | `input[name='password']` | LoginPopup.jsx |
| Checkbox | `.login-popup-condition input[type='checkbox']` | LoginPopup.jsx |
| Submit Button | `.login-popup-container button[type='submit']` | LoginPopup.jsx |
| Close Button | `.login-popup-title img` | LoginPopup.jsx |
| Profile Icon | `.navbar-profile` | Navbar.jsx |
| Logout Dropdown | `.nav-profile-dropdown` | Navbar.jsx |
| Logout Button | `//ul[@class='nav-profile-dropdown']//li[contains(., 'Đăng xuất')]` | Navbar.jsx |

---

## TEST EXECUTION FLOW

```
Test 1: Register Valid
├─ Open popup → Switch to Sign Up mode
├─ Fill form (valid data)
├─ Submit → Alert "Đăng ký thành công!"
└─ Auto login → Profile icon appears ✓

↓ (Test 2 setUp())

Auto Logout Flow:
├─ Detect profile icon (logged in)
├─ Hover profile → Dropdown appears
├─ Click "Đăng xuất"
├─ Handle alert "Đã đăng xuất thành công!"
└─ Verify profile icon gone ✓

Test 2: Register Invalid Password
├─ Open popup (fresh state, logged out)
├─ Fill form (weak password)
├─ Submit → Alert error (password requirements)
└─ Registration rejected ✓

↓ (Test 3 setUp())

Logout check → Not logged in → Skip

Test 3: Register Invalid Email
├─ Open popup
├─ Fill form (invalid email format)
├─ Submit → HTML5 validation OR backend error
└─ Registration rejected ✓

↓ (Test 4 setUp())

Logout check → Not logged in → Skip

Test 4: Register Existing Email
├─ Open popup
├─ Fill form (demo@example.com)
├─ Submit → Alert error (email exists)
└─ Registration rejected ✓

Complete
```

---

## VALIDATION SUMMARY

### Client-Side Validation (HTML5)

| Field | Validation |
|-------|-----------|
| Email | `type="email"` → Browser checks @ symbol and format |
| Password | `required` attribute → Cannot be empty |
| Checkbox | `required` → Must be checked |
| Name | `required` (only in Sign Up mode) |

### Backend Validation

| Check | Error Message Expected |
|-------|----------------------|
| Password too short | "Password must be 8-20 characters..." |
| Password missing requirements | "...contain at least one uppercase..." |
| Invalid email format | Email validation error |
| Email already exists | "Email đã tồn tại" or similar |
| Missing required fields | Generic error |

---

## STATISTICS

| Metric | Value |
|--------|-------|
| **Total Test Cases** | 4 |
| **Passed** | 4 |
| **Failed** | 0 |
| **Skipped** | 0 |
| **Success Rate** | 100% |
| **Average Execution Time** | ~15 seconds |
| **Unique Emails Generated** | 2 (TC-001, TC-002) |
| **Existing Emails Used** | 1 (TC-004) |

---

## FILL INTO SPREADSHEET

### Header Row
- **Module**: Access Control - Register Function
- **Tester**: [Tuấn Kiệt / Your Name]
- **No. of Test Cases**: 4
- **Pass**: 4
- **Fail**: 0
- **Untested**: 0
- **N/A**: 0

### For Each Test Case Row:

**TC-REG-001:**
- ID: TC-REG-001
- Description: Verify user registration with valid email and password
- Procedure: Open popup → Switch to Sign Up → Fill form with valid data → Submit
- Expected Output: Alert "Đăng ký thành công!", auto login, profile icon appears
- Inter-Test Dependence: None (standalone)
- Result: PASS
- Note: Timestamp generates unique email, auto login after success

**TC-REG-002:**
- ID: TC-REG-002
- Description: Verify registration rejected with invalid password
- Procedure: Open popup → Fill form with password "weak" (4 chars, no rules met) → Submit
- Expected Output: Alert error about password requirements, registration fails
- Inter-Test Dependence: After TC-REG-001, auto logout in setUp()
- Result: PASS
- Note: Password must be 8-20 chars with uppercase, lowercase, digit, special char

**TC-REG-003:**
- ID: TC-REG-003
- Description: Verify registration rejected with invalid email format
- Procedure: Open popup → Fill form with email "notanemail" (no @) → Submit
- Expected Output: HTML5 validation blocks submission OR backend error
- Inter-Test Dependence: After TC-REG-002, independent
- Result: PASS
- Note: Tests both client-side HTML5 validation and backend validation

**TC-REG-004:**
- ID: TC-REG-004
- Description: Verify registration rejected with existing email
- Procedure: Open popup → Fill form with demo@example.com (existing) → Submit
- Expected Output: Alert error "Email đã tồn tại", registration fails
- Inter-Test Dependence: After TC-REG-003, depends on demo@example.com existing
- Result: PASS
- Note: Uses demo@example.com from previous tests, backend checks uniqueness

---

## KEY ACHIEVEMENTS

✅ **Complete Coverage**: All 4 register scenarios tested (valid, invalid password, invalid email, duplicate email)

✅ **State Management**: Automatic logout between tests ensures clean state

✅ **Robust Validation**: Tests both client-side (HTML5) and server-side validation

✅ **Unique Data**: Timestamp-based email generation prevents test collision

✅ **Alert Handling**: Both success and error alerts properly handled and verified

✅ **Real Selectors**: All selectors verified against actual component code (LoginPopup.jsx, Navbar.jsx)

---

## ISSUES & SOLUTIONS

| Issue | Root Cause | Solution |
|-------|-----------|----------|
| Test 2+ blocked by logged-in state | TC-REG-001 auto-logs in after registration | Added `logout_if_logged_in()` in setUp() |
| Logout alert blocks execution | "Đã đăng xuất thành công!" alert not handled | Added alert handling in logout method |
| Email collision on re-run | Same test data used multiple times | Use timestamp: `testuser_{int(time.time())}@example.com` |
| HTML5 validation inconsistent | Browser may or may not enforce | Check both validationMessage AND backend error |

---

**Report Generated**: December 15, 2025  
**Status**: All Tests Passed (4/4)  
**Module Status**: Register Function - READY FOR PRODUCTION
