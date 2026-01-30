# Đánh giá cấu trúc source code PetcareAdmin

## Tổng quan: **Đã ổn, đúng hướng chuẩn layered**

Cấu trúc hiện tại phù hợp dự án Java Swing thực tế: tách lớp rõ (GUI → Service → Repository → Persistence), model có domain/entity/exception, config và SQL trong `resources`.

---

## Cấu trúc hiện tại

```
com.petcare
├── App.java                    # Entry point ✓
├── config/                     # Cấu hình (DB, app...) ✓
│   └── DatabaseConfig.java
├── gui/                        # Presentation layer ✓
│   ├── DashboardFrame.java
│   ├── LoginFrame.java
│   ├── Sidebar.java
│   ├── dialogs/                # Các dialog (Add/Edit, Details, Checkout...)
│   └── panels/                 # Các màn quản lý (Customer, Pet, Invoice...)
├── model/                      # Domain & data transfer ✓
│   ├── domain/                 # Domain model (business entities, validation)
│   ├── entity/                 # DB mapping + DTO (Entity, ListDto, InfoDto...)
│   └── exception/              # PetcareException ✓
├── persistence/                # Kết nối DB ✓
│   ├── Database.java           # Helper query/update
│   └── DatabaseConnection.java
├── repository/                 # Data access (Interface + Impl) ✓
├── service/                    # Business logic (Interface qua Repository) ✓
└── util/                       # Tiện ích (Theme, Print, Helper UI...)
```

**Resources:** `database.properties`, `schema-and-data-h2.sql`, `images/` — hợp lý ✓

---

## Điểm đạt chuẩn

| Chuẩn | Trạng thái |
|-------|------------|
| Entry point gọn (App.java) | ✓ |
| Tách lớp: GUI / Service / Repository / Persistence | ✓ |
| Model: domain (logic) vs entity (DB/DTO) | ✓ |
| Repository: interface (I*Repository) + implementation | ✓ |
| Service dùng Repository, GUI gọi Service | ✓ |
| Config/SQL ngoài code (resources) | ✓ |
| GUI: frame, panels, dialogs tách thư mục | ✓ |

---

## Đề xuất chỉnh nhỏ (không bắt buộc)

### 1. File trùng/orphan trong `model/`

Ba file **ngay dưới `model/`** (không nằm trong `domain/`):

- `model/Customer.java`
- `model/Doctor.java`
- `model/Pet.java`

**Không có chỗ nào import** `com.petcare.model.Customer` (hay `Doctor`, `Pet`). Toàn bộ code dùng `com.petcare.model.domain.*`. Ba file này là bản cũ/trùng với domain, có thể **xóa** để tránh nhầm lẫn.

### 2. `DashboardService` nằm trong `util/`

`DashboardService` là lớp nghiệp vụ (tổng hợp thống kê dashboard), về bản chất giống các class trong `service/`. Để thống nhất:

- **Có thể chuyển** `util/DashboardService.java` → `service/DashboardService.java` (và sửa import trong `DashboardPanel`).
- **Hoặc giữ** trong `util` nếu xem nó là “helper cho GUI” — vẫn chấp nhận được.

### 3. ~~`DatabaseConfig` trong `util/`~~ → Đã chuyển sang `config/`

---

## Đã chỉnh

- **Thư mục `config/`**: Thêm package `com.petcare.config`, chuyển `DatabaseConfig.java` từ `util` sang `config`.
- **Xóa file không dùng**: `model/Customer.java`, `model/Doctor.java`, `model/Pet.java`.

## Kết luận

- **Cấu trúc đã ổn** và **hợp lý theo chuẩn dự án Java** (layered, domain/entity, repository, **config** tách riêng, persistence, util).
