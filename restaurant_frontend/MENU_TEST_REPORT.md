# TEST REPORT - MENU MANAGEMENT MODULE

## Test Information

| Field | Details |
|-------|---------|
| Module | Manage Menu (List + AddDishModal + EditDishModal) |
| App | Restaurant Dashboard |
| Test Type | UI Functional (Selenium) |
| Tooling | Python + Selenium + webdriver-manager |
| Test Date | December 15, 2025 |
| Tester | [Your Name] |
| Total Cases | 4 |
| Result | 4/4 Passed |

---

## Environment

| Item | Details |
|------|---------|
| Base URL | http://localhost:5174 |
| Pages | /login → /list |
| Browser | Chrome (latest) |
| Wait | 15s explicit waits + short pauses |
| Account | nhiknh112233@gmail.com / 12345678 |
| Image source | Local file from D:\\SourceStudy\\picfood (fallback: URL) |

---

## Prerequisites
1) App running at localhost:5174
2) Test account has merchant access
3) Folder D:\\SourceStudy\\picfood contains at least one image file (jpg/png/webp) for create/update
4) Categories exist so select dropdown is not empty

---

## Test Cases Summary

| ID | Name | Status | Notes |
|----|------|--------|-------|
| MENU-001 | Add new dish | ✅ Passed | Created dish with local image; waited 10s for server |
| MENU-002 | Update dish | ✅ Passed | Renamed, changed price/desc, selected new image | 
| MENU-003 | Delete dish | ✅ Passed | Handled confirm + success alerts, dish removed |
| MENU-004 | Add dish invalid data | ✅ Passed | Empty required fields → validation caught (alert or HTML5) |

---

## Inter-Test Case Dependence
- Execution order: 001 → 002 → 003 → 004
- MENU-002 depends on dish created in MENU-001
- MENU-003 depends on dish updated in MENU-002
- MENU-004 independent (creates nothing)

---

## Detailed Cases (concise)

### MENU-001: Add new dish
- Data: name "Auto Dish {timestamp}", price 55,000, category first option, image local file (fallback URL), desc "Tự động thêm món test"
- Procedure: Open Add modal → Fill fields → Submit → Wait 10s → Search card
- Expected: Dish card appears in list; alert may show
- Result: ✅ Created dish

### MENU-002: Update dish
- Data: dish from MENU-001 → name "... Updated", price 60,000, desc "Cap nhat tu dong", pick new local image
- Procedure: Open Edit modal → Change fields → Upload image → Save → Wait modal close → Search updated card
- Expected: Card shows updated name/price; edit modal closes
- Result: ✅ Updated dish

### MENU-003: Delete dish
- Data: dish from MENU-002
- Procedure: Click Delete → Accept confirm → Handle success alert → Search dish
- Expected: Dish removed; alerts handled
- Result: ✅ Deleted dish

### MENU-004: Add dish with invalid data
- Data: name empty, price empty, category empty, no image
- Procedure: Open Add modal → Leave required fields blank → Submit
- Expected: Alert or HTML5 validation message shown; no dish created
- Result: ✅ Validation caught (alert or native message)

---

## Techniques & Notes
- Explicit waits (presence, clickable, url_contains) + short pauses for stability
- Modal handling: close lingering edit/add modals before actions
- Alert handling: confirm + possible success alert after delete
- Image handling: prefer local file; fallback URL only for create
- Validation check: alert text or `validationMessage` on required inputs

---

## Execution Log (key points)
- Auto login → sidebar to /list
- MENU-001: Created dish successfully after 10s wait
- MENU-002: Edit modal selectors (#dish-name, #dish-price, #dish-description, #dish-image) used; new image required
- MENU-003: Confirm + success alert handled; dish removed
- MENU-004: Empty required fields → validation surfaced

---

## Recommendations
1) Keep a small test image in D:\\SourceStudy\\picfood for reliable uploads
2) If backend adds stronger validation, expect explicit error alert on invalid create
3) If server slow, keep 10s wait after create; adjust as needed

---

**Report generated:** December 15, 2025
