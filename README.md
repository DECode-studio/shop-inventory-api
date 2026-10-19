# Shop Warehouse Inventory API (`shop-inventory-api`)

RESTful API untuk sistem manajemen inventaris gudang toko menggunakan **Java 21/17**, **Spring Boot 3.3.x**, **Hibernate ORM / Spring Data JPA**, dan **SQLite**.

---

## Fitur Utama & Kepatuhan Requirement

- **Item Management**: Manajemen katalog barang induk (Parent Product).
- **Variant Management**: Setiap barang dapat memiliki variasi (misal ukuran, warna, material) dengan **SKU unik**.
- **Pricing Strategy**: Harga ditentukan di tingkat varian (karena ukuran/warna khusus dapat memiliki harga berbeda).
- **Inventory Tracking & Audit Trail**: Setiap perubahan stok (penambahan, pengurangan, maupun penjualan) dicatat ke dalam log mutasi (`inventory_transactions`) yang mencatat kuantitas, tipe mutasi, dan sisa stok setelah transaksi.
- **Pencegahan Overselling (Anti-Overselling)**: 
  - Validasi ketat ketersediaan stok sebelum transaksi penjualan diproses.
  - Pengurangan stok secara atomik di dalam `@Transactional` boundary.
  - **Optimistic Locking (`@Version`)** pada entity `ItemVariant` untuk mencegah *race condition* bila terjadi penjualan serentak (*concurrent checkouts*).
  - Jika stok tidak mencukupi, seluruh transaksi dibatalkan (*rollback*) dan API mengembalikan response RFC 7807 (`422 Unprocessable Entity` atau `409 Conflict`).
- **Data Persistence**: Menggunakan SQLite dengan Hibernate Community Dialects dan *foreign keys enforcement*.
- **Sample Data Seeder**: Otomatis mengisi data awal barang, varian (termasuk contoh in-stock dan out-of-stock) saat aplikasi pertama kali dijalankan.
- **Interactive Swagger / OpenAPI 3**: Tersedia UI dokumentasi dan pengujian interaktif di browser.

---

## Tech Stack & Tools

- **Language**: Java 21 (kompatibel Java 17+)
- **Framework**: Spring Boot 3.3.4 (Spring Web, Spring Data JPA, Spring Validation)
- **Database & ORM**: SQLite (`org.xerial:sqlite-jdbc`), Hibernate 6 (`hibernate-community-dialects`)
- **Documentation**: Springdoc OpenAPI / Swagger UI 2.6.0
- **Build Tool**: Apache Maven (dengan Maven Wrapper `./mvnw`)
- **Testing**: JUnit 5, MockMvc, Spring Boot Test

---

## Arsitektur & Pola Desain (Architecture & Design Patterns)

Proyek ini menerapkan **Layered Clean Architecture**:

```
Presentation Layer (Controllers, OpenAPI Docs)
       ↓
Validation & DTO Layer (Java Records, Bean Validation RFC 7807)
       ↓
Application Service Layer (Business Use Cases, Concurrency Control, Anti-Overselling Guard)
       ↓
Domain & Persistence Layer (JPA Entities, Optimistic Lock @Version, Spring Data JPA Repositories)
       ↓
Database Layer (SQLite)
```

### Design Patterns yang Digunakan:
1. **Repository Pattern**: Memisahkan logika manipulasi database dari business logic menggunakan Spring Data JPA.
2. **Data Transfer Object (DTO) & Immutability**: Menggunakan Java `record` untuk transfer data agar *thread-safe*, *immutable*, serta mencegah paparan entity JPA secara langsung.
3. **Optimistic Locking Pattern (`@Version`)**: Kolom `version` pada tabel `item_variants` mendeteksi tabrakan pembaruan data secara konkuren tanpa perlu memblokir database secara kaku.
4. **Global Exception Handling (RFC 7807 ProblemDetail)**: Standarisasi format respons error JSON untuk semua kasus (404 Not Found, 422 Out of Stock, 400 Bad Request, 409 Conflict).
5. **Audit Trail / Event Log Pattern**: Mutasi stok dicatat di tabel terpisah (`inventory_transactions`), menjamin jejak audit historis (inbound/outbound/sales).

---

## Database Schema Design

```
+-------------------------------------------------------------+
|                            ITEMS                            |
+-------------------+--------------------+--------------------+
| id (PK)           | BIGINT             | Auto-increment     |
| code              | VARCHAR(50)        | UNIQUE, NOT NULL   |
| name              | VARCHAR(150)       | NOT NULL           |
| description       | VARCHAR(500)       | NULLABLE           |
| category          | VARCHAR(100)       | NULLABLE           |
| is_active         | BOOLEAN            | NOT NULL           |
| created_at        | TIMESTAMP          | NOT NULL           |
| updated_at        | TIMESTAMP          | NULLABLE           |
+-------------------+--------------------+--------------------+
                                | 1
                                |
                                | *
+-------------------------------+-----------------------------+
|                        ITEM_VARIANTS                        |
+-------------------+--------------------+--------------------+
| id (PK)           | BIGINT             | Auto-increment     |
| item_id (FK)      | BIGINT             | REFERENCES items   |
| sku               | VARCHAR(50)        | UNIQUE, NOT NULL   |
| variant_name      | VARCHAR(150)       | NOT NULL           |
| price             | DECIMAL(12,2)      | NOT NULL           |
| stock_quantity    | INTEGER            | NOT NULL, >= 0     |
| version           | BIGINT             | Optimistic Lock    |
| is_active         | BOOLEAN            | NOT NULL           |
| created_at        | TIMESTAMP          | NOT NULL           |
| updated_at        | TIMESTAMP          | NULLABLE           |
+-------------------+--------------------+--------------------+
             | 1                                  | 1
             |                                    |
             | *                                  | *
+------------+--------------------+   +-----------+--------------------+
|   INVENTORY_TRANSACTIONS        |   |         ORDER_ITEMS        |
+-------------------+-------------+   +-------------------+------------+
| id (PK)           | BIGINT      |   | id (PK)           | BIGINT     |
| variant_id (FK)   | BIGINT      |   | order_id (FK)     | BIGINT     |
| transaction_type  | VARCHAR(30) |   | variant_id (FK)   | BIGINT     |
| quantity          | INTEGER     |   | quantity          | INTEGER    |
| stock_after       | INTEGER     |   | unit_price        | DECIMAL    |
| reference_no      | VARCHAR(100)|   | subtotal          | DECIMAL    |
| remarks           | VARCHAR(300)|   +-------------------+------------+
| created_at        | TIMESTAMP   |                    | *
+-------------------+-------------+                    |
                                                       | 1
                                      +----------------+---------------+
                                      |             ORDERS             |
                                      +-------------------+------------+
                                      | id (PK)           | BIGINT     |
                                      | order_no          | VARCHAR(50)|
                                      | customer_name     | VARCHAR(150|
                                      | status            | VARCHAR(30)|
                                      | total_amount      | DECIMAL    |
                                      | created_at        | TIMESTAMP  |
                                      +-------------------+------------+
```

---

## Cara Menjalankan Aplikasi (How to Run)

### Prasyarat:
- Java JDK 17 atau 21 terpasang.
- Port `8080` tersedia.

### 1. Menjalankan Unit & Integration Tests:
```bash
./mvnw clean test
```

### 2. Menjalankan Server Aplikasi:
```bash
./mvnw spring-boot:run
```
Aplikasi akan aktif di `http://localhost:8080`. File database SQLite `shop_inventory.db` akan dibuat secara otomatis di root folder proyek.

### 3. Mengakses Swagger UI / Dokumentasi Interaktif:
Buka browser ke alamat:
- **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

---

## Asumsi yang Diambil (Design Assumptions)

1. **Harga & Stok di Level Varian**:
   Barang induk (`Item`) bertindak sebagai payung produk katalog (misal: "Cotton T-Shirt"), sedangkan harga dan stok fisik gudang dikelola di level `ItemVariant` (misal: "Size S - Black" vs "Size XXL - Black" yang dapat memiliki harga berbeda).
2. **Pencegahan Overselling**:
   Saat endpoint `POST /api/v1/orders/checkout` dipanggil:
   - Validasi memeriksa apakah kuantitas yang diminta $\le$ stok yang tersedia.
   - Jika satu saja varian di dalam keranjang belanja melebihi stok yang ada, maka **seluruh pesanan dibatalkan secara atomik**.
   - Database dilindungi dengan Optimistic Locking sehingga tidak ada race condition saat dua pelanggan membeli unit terakhir secara bersamaan.
3. **SQLite Foreign Keys**:
   SQLite secara default menonaktifkan foreign key constraints. Aplikasi ini secara eksplisit mengaktifkan `PRAGMA foreign_keys = ON` melalui konfigurasi Hibernate connection properties.

---

## Contoh Pemanggilan API (API Endpoint Examples via cURL)

### 1. Menampilkan Semua Item & Varian
```bash
curl -X GET "http://localhost:8080/api/v1/items" \
     -H "Accept: application/json"
```

### 2. Menambahkan Item Baru
```bash
curl -X POST "http://localhost:8080/api/v1/items" \
     -H "Content-Type: application/json" \
     -d '{
       "code": "JACKET-001",
       "name": "Waterproof Windbreaker Jacket",
       "description": "Lightweight breathable jacket",
       "category": "Outerwear",
       "active": true
     }'
```

### 3. Menambahkan Varian ke Item (Misal item ID 1)
```bash
curl -X POST "http://localhost:8080/api/v1/items/1/variants" \
     -H "Content-Type: application/json" \
     -d '{
       "sku": "TSHIRT-RED-XL",
       "variantName": "Crimson Red - Size XL",
       "price": 135000.00,
       "initialStock": 15,
       "active": true
     }'
```

### 4. Restock / Inbound Stok (Tambah Stok Gudang)
```bash
curl -X POST "http://localhost:8080/api/v1/stocks/variants/1/add" \
     -H "Content-Type: application/json" \
     -d '{
       "quantity": 20,
       "referenceNo": "PO-2026-001",
       "remarks": "Restock from supplier vendor"
     }'
```

### 5. Simulasi Checkout / Penjualan (Berhasil - Stok Cukup)
```bash
curl -X POST "http://localhost:8080/api/v1/orders/checkout" \
     -H "Content-Type: application/json" \
     -d '{
       "customerName": "Budi Santoso",
       "items": [
         {
           "sku": "TSHIRT-BLK-S",
           "quantity": 2
         },
         {
           "sku": "MOUSE-BLK",
           "quantity": 1
         }
       ]
     }'
```

### 6. Simulasi Pencegahan Overselling (Gagal - Stok Habis / Melebihi Kuantitas)
Mencoba membeli SKU `TSHIRT-WHT-M` yang stoknya 0:
```bash
curl -X POST "http://localhost:8080/api/v1/orders/checkout" \
     -H "Content-Type: application/json" \
     -d '{
       "customerName": "Agus Pratama",
       "items": [
         {
           "sku": "TSHIRT-WHT-M",
           "quantity": 1
         }
       ]
     }'
```
**Respons Error (HTTP 422 Unprocessable Entity - RFC 7807):**
```json
{
  "type": "https://errors.shop-inventory.com/insufficient-stock",
  "title": "Out of Stock / Insufficient Inventory",
  "status": 422,
  "detail": "Insufficient stock for SKU 'TSHIRT-WHT-M' (White - Size M (Out of Stock)). Available: 0, Requested: 1",
  "timestamp": "2026-10-19T08:50:00Z"
}
```

### 7. Melihat Daftar Varian yang Sedang Habis (Out of Stock)
```bash
curl -X GET "http://localhost:8080/api/v1/stocks/out-of-stock"
```

### 8. Melihat Histori Mutasi Stok (Audit Trail)
```bash
curl -X GET "http://localhost:8080/api/v1/stocks/transactions"
```
