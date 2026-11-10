# 🚗 Lakshmi Narayana Travels (LNT) - Car Rental System

A modern, full-stack dual-application car rental enterprise system built with **Spring Boot 3 (Java 21)**, **Thymeleaf**, and **Google Sheets / Google Drive** as the database and document storage backend.

The platform is split into two specialized applications running simultaneously:

1. **Customer Portal** (`lakshmi-narayana-travels-customer`): Runs on `http://localhost:8081`
2. **Admin & Staff Portal** (`lakshmi-narayana-travels-admin`): Runs on `http://localhost:8082`

---

## 🌟 Key Highlights & Feature Matrix

### 1. 🧑‍💼 Customer Project (`http://localhost:8081`)
- **Modern Light Theme & Smooth Animations**: Clean porcelain & sapphire blue palette, floating hero car elements, 3D perspective hover tilt on vehicle cards, and smooth scroll interactions.
- **Interactive Car Anatomy & Parts Explorer**: Clickable glowing hotspots on a sleek vehicle blueprint to inspect key mechanical systems in real time:
  - ⚙️ **Smart BS6 Phase 2 Engine & Powertrain**
  - 🛡️ **Aerodynamic High-Tensile Steel Safety Cage**
  - 🛑 **Ventilated Disc Brakes & Precision Alloy Wheels**
  - 💺 **Ergonomic Luxury Cabin & Multi-Zone Climate Control**
  - 🚗 **6 Airbags & Advanced Driver Safety (ADAS)**
  - 🧳 **Luggage Boot Capacity with Foldable Third Row**
- **Dynamic Multi-Criteria Filters**:
  - **Seating Capacity**: 5-Seater (Sedans/SUVs), 7-Seater (Family MUVs/Large SUVs).
  - **Fuel Types**: High-mileage CNG, Diesel Power, Turbo Petrol, Electric (EV).
  - **Transmission**: Automatic & Manual.
  - **Daily Rate Slider**: Real-time client-side and server-side range filter.
- **Customer Profile & KYC Proofs**:
  - Full Name, Phone, and Residential Address.
  - **Aadhaar Card** number + document upload/preview.
  - **Driving License** number + document upload/preview.
  - Real-time verification badge: `VERIFIED`, `PENDING REVIEW`, or `ACTION REQUIRED`.
- **Add-on Travel Customization**:
  - Live GPS Navigation (+₹200/day)
  - ISOFIX Child Safety Seat (+₹150/day)
  - Additional Driver Insurance (+₹300 flat)
  - Zero-Depreciation Bumper-to-Bumper Insurance (+₹499/day)
  - Fastag Highway Toll Auto-Refill (+₹500 refundable deposit)
- **Multi-Method Interactive Checkout**:
  - **Instant UPI**: Realistic QR code with timer for Google Pay, PhonePe, Paytm, and BHIM.
  - **Debit / Credit Card**: Interactive 3D credit card with real-time card number formatting, cardholder name, and expiry date mirror.
  - **Net Banking**: Direct integration with HDFC, SBI, ICICI, etc.
  - **Pay at Car Handover / Pickup**: Cash or card swipe on vehicle delivery.
- **Printable Tax Invoices**:
  - Official Tax Invoice with GSTIN, company branding, itemized charges, and 18% GST (CGST 9% + SGST 9%).
  - One-click Print or Save as PDF.
- **My Bookings Page**:
  - Track upcoming, ongoing, and completed trips with cancellation options.

---

### 2. 🛡️ Admin & Staff Project (`http://localhost:8082`)
- **PowerBI Executive Dashboard**:
  - Top KPI Summary Cards (Gross Revenue, Active Trips, Fleet Utilization, Pending KYC).
  - **4 Interactive Chart.js Visualizations**:
    - 📈 **Monthly & Weekly Revenue Trend**: Gradient filled area chart.
    - 📊 **Fleet Composition by Fuel Type**: CNG vs Diesel vs Petrol vs EV bar chart.
    - 🍩 **5-Seater vs 7-Seater Fleet Share**: High-contrast doughnut chart.
    - 🎯 **Booking Status Funnel**: Bar breakdown of Approved, Pending, Ongoing, Completed, and Cancelled trips.
- **Dedicated Administration Login Only**:
  - Secure authentication (no public signup) for authorized staff.
- **Fleet & Vehicle Inventory Management**:
  - Add new cars with specs, seating, mileage, registration number, and feature tags.
  - Upload car images to **Google Drive** or shared cloud storage.
  - Toggle vehicle availability (Available vs Maintenance/In-Service).
- **Orders & Reservations Management**:
  - Filter orders by **Date Range** (`startDate` to `endDate`) and **Status** (`PENDING`, `APPROVED`, `ONGOING`, `COMPLETED`, `CANCELLED`).
  - One-click status actions (Approve Booking, Start Trip, Complete Rental, Cancel).
  - View and print official tax invoices on behalf of customers.
- **Customer Directory & KYC Proofs Verification**:
  - Inspect uploaded Aadhaar and Driving License documents in an embedded high-resolution modal.
  - "Approve KYC" or "Reject KYC with Staff Remarks" with immediate status sync.
- **Financial Ledger & Payments History**:
  - Audit trail of all transactions, transaction reference IDs, gateways, and timestamps.
- **Data Export Features**:
  - 📥 **Export Bookings to CSV** (Excel / PowerBI ready).
  - 📥 **Export Payments Ledger to CSV**.
  - 📥 **Export Fleet Inventory to CSV**.
- **Google Sheets & Google Drive Integration Hub**:
  - Live status monitor for Google Sheets API v4 and Google Drive API v3.
  - One-click "Trigger Real-Time Sync" button.

---

## 🗄️ Google Sheets & Google Drive Database Architecture

Instead of requiring an SQL database like MySQL or PostgreSQL, this system uses **Google Sheets** as the database table store and **Google Drive** for document storage!

### Google Sheets Schema (4 Tabs):
| Tab Name | Columns |
| :--- | :--- |
| **`Cars`** | `ID, Name, Brand, Category, Seaters, FuelType, Transmission, PricePerDay, ImageUrl, Features, Mileage, LuggageCapacity, RegistrationNumber, Available, Rating, TripsCompleted` |
| **`Customers`** | `ID, FullName, Email, Password, Phone, Address, AadhaarNumber, AadhaarDocUrl, DrivingLicenseNumber, DrivingLicenseDocUrl, KYCStatus, KYCRemarks, CreatedAt` |
| **`Bookings`** | `ID, CustomerId, CustomerName, CustomerEmail, CarId, CarName, PickupDate, DropDate, PickupLocation, DropLocation, TotalDays, DailyRate, SelectedFeatures, BaseAmount, AddOnAmount, GSTAmount, TotalAmount, Status, PaymentStatus, PaymentMethod, TransactionId, CreatedAt` |
| **`Payments`** | `ID, BookingId, CustomerName, Amount, PaymentMethod, TransactionRef, PaymentDate, Status` |

### Resilient Dual-Engine:
- **Instant Out-of-the-Box Mode**: The system includes a shared auto-synced JSON database in `shared-data/`. Both Customer and Admin portals read and write to this shared store in real-time without requiring any setup!
- **Live Google Sheets Mode**: When you provide your Google Cloud Service Account `credentials.json` and set `google.sheets.enabled=true`, the system reads and updates Google Sheets directly via the Google Sheets API v4!

---

## 🚀 How to Run the Projects

### Prerequisites:
- Java 21 or Java 25 installed
- Both projects include their own embedded Maven Wrapper (`./mvnw` or `mvnw.cmd`)

---

### Step 1: Start Customer Portal (Port 8081)
Open a terminal in `Car-Rental/lakshmi-narayana-travels-customer`:
```powershell
# Set JAVA_HOME (if not globally set)
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"

# Run Spring Boot
.\mvnw.cmd spring-boot:run
```
Visit: **`http://localhost:8081`**

---

### Step 2: Start Admin Portal (Port 8082)
Open a second terminal in `Car-Rental/lakshmi-narayana-travels-admin`:
```powershell
# Set JAVA_HOME (if not globally set)
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"

# Run Spring Boot
.\mvnw.cmd spring-boot:run
```
Visit: **`http://localhost:8082/login`**

---

## 🔑 Pre-Configured Test Credentials

### Admin / Staff Portal:
- **URL**: `http://localhost:8082/login`
- **Email**: `admin@lntravels.com`
- **Password**: `admin123`

### Customer Portal:
- **URL**: `http://localhost:8081/login`
- **Email**: `customer@gmail.com`
- **Password**: `password123`
*(Or click "Register" on `http://localhost:8081/register` to create a new customer account).*

---

## ⚙️ How to Connect Your Own Live Google Sheet & Google Drive (Optional)

1. Create a blank Google Sheet at [sheets.new](https://sheets.new) named **"Lakshmi Narayana Travels Data"**.
2. Copy the Spreadsheet ID from the browser address bar (e.g., `https://docs.google.com/spreadsheets/d/<SPREADSHEET_ID>/edit`).
3. In [Google Cloud Console](https://console.cloud.google.com/):
   - Enable **Google Sheets API** and **Google Drive API**.
   - Create a Service Account and download its JSON key file as `credentials.json`.
4. Place `credentials.json` in the project root or inside `lakshmi-narayana-travels-admin` / `lakshmi-narayana-travels-customer`.
5. Share your Google Sheet with the Service Account email address (give **Editor** permissions).
6. In `application.properties`:
   ```properties
   google.sheets.enabled=true
   google.sheets.spreadsheetId=YOUR_SPREADSHEET_ID_HERE
   google.sheets.credentialsPath=credentials.json
   ```
7. Open the Admin Portal at `http://localhost:8082/admin/google-sync` and click **"Trigger Real-Time Sync"**.

---

## 📁 Project Directory Structure
```
Car-Rental/
├── shared-data/                          # Shared data store & proof uploads
│   ├── cars.json                         # Fleet vehicle database
│   ├── customers.json                    # Customer profiles & KYC records
│   ├── bookings.json                     # Rental orders & reservations
│   ├── payments.json                     # Transaction ledger
│   └── uploads/                          # Uploaded Aadhaar & Driving License files
│
├── lakshmi-narayana-travels-customer/    # Customer Web Application (Port 8081)
│   ├── pom.xml
│   ├── mvnw.cmd
│   └── src/main/
│       ├── java/com/lakshminarayana/customer/
│       │   ├── CustomerPortalApplication.java
│       │   ├── model/ (Car, Customer, Booking, Payment)
│       │   ├── service/ (DataStoreService, GoogleSheetsService)
│       │   ├── controller/ (CustomerPortalController, BookingController, AuthController, ProfileController)
│       │   └── config/ (WebMvcConfig)
│       └── resources/
│           ├── application.properties    # server.port=8081
│           ├── static/css/customer-style.css (Animations, 3D tilt, light theme)
│           ├── static/js/customer-script.js  (Car anatomy explorer, QR, card flip)
│           └── templates/ (home, cars, car-details, book, payment, invoice, profile, my-bookings, login, register)
│
├── lakshmi-narayana-travels-admin/       # Admin & Staff Web Application (Port 8082)
│   ├── pom.xml
│   ├── mvnw.cmd
│   └── src/main/
│       ├── java/com/lakshminarayana/admin/
│       │   ├── AdminPortalApplication.java
│       │   ├── model/ (Car, Customer, Booking, Payment)
│       │   ├── service/ (DataStoreService, GoogleSheetsService, GoogleDriveService, AdminDashboardService)
│       │   ├── controller/ (AdminAuthController, AdminDashboardController, AdminFleetController, AdminBookingsController, AdminCustomersController, AdminPaymentsController, AdminExportController, GoogleSyncController)
│       │   └── config/ (WebMvcConfig)
│       └── resources/
│           ├── application.properties    # server.port=8082
│           ├── static/css/admin-style.css    (PowerBI styling & dark/light slate)
│           ├── static/js/admin-charts.js     (Chart.js Revenue area, Bar, Doughnut)
│           └── templates/ (login, dashboard, cars, bookings, customers, payments, google-sync, invoice-view)
│
└── README.md
```

---
&copy; 2026 Lakshmi Narayana Travels. Crafted with Spring Boot 3, Thymeleaf & Google Sheets Cloud Data Engine.

