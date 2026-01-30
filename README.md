# Petcare Admin - Java Swing Application

Hệ thống quản lý phòng khám thú cưng (Java Swing)

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Swing](https://img.shields.io/badge/Swing-GUI-blue.svg)](https://docs.oracle.com/javase/tutorial/uiswing/)
[![Maven](https://img.shields.io/badge/Maven-3.8+-red.svg)](https://maven.apache.org/)
[![H2](https://img.shields.io/badge/H2-2.2-green.svg)](https://www.h2database.com/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue.svg)](https://www.mysql.com/)

---

## 📋 Mục lục

- [Tổng quan](#tổng-quan)
- [Công nghệ sử dụng](#công-nghệ-sử-dụng)
- [Công cụ và thư viện giao diện](#công-cụ-và-thư-viện-giao-diện)
- [Công nghệ và kỹ thuật](#công-nghệ-và-kỹ-thuật)
- [Cấu trúc dự án](#cấu-trúc-dự-án)
- [Kiến trúc hệ thống](#kiến-trúc-hệ-thống)
- [Design Patterns](#design-patterns)
- [Cài đặt và chạy](#cài-đặt-và-chạy)
- [Tính năng](#tính-năng)
- [Hướng dẫn sử dụng](#hướng-dẫn-sử-dụng)

---

## 🎯 Tổng quan

**PetcareAdmin** là ứng dụng quản lý phòng khám thú cưng được xây dựng bằng **Java Swing**, cung cấp giao diện quản lý toàn diện cho các chức năng của phòng khám. Ứng dụng được thiết kế theo **kiến trúc đa tầng (Multi-tier Architecture)** với separation of concerns rõ ràng, áp dụng các design patterns phù hợp và tuân thủ best practices trong Java development.

---

## 🛠 Công nghệ sử dụng

- **Java 17** – Ngôn ngữ lập trình chính
- **Java Swing** – Framework xây dựng giao diện người dùng (GUI)
- **Maven 3.8+** – Quản lý dự án và dependencies
- **H2 2.2** – Cơ sở dữ liệu nhúng (mặc định, cấu hình trong `database.properties`)
- **MySQL 8.0+** – Hệ quản trị CSDL quan hệ (tùy chọn, cấu hình trong `database.properties`)
- **JDBC** – API kết nối và tương tác với cơ sở dữ liệu
- **JFreeChart 1.5.4** – Thư viện biểu đồ thống kê
- **FlatLaf 3.1.1** – Look and Feel hiện đại cho Swing (Light/Dark theme)
- **FlatLaf IntelliJ Themes 3.1.1** – Bộ theme bổ sung cho FlatLaf
- **BCrypt (jbcrypt)** – Băm mật khẩu an toàn

---

## 🖥 Công cụ và thư viện giao diện

| Công cụ / Thư viện | Mục đích |
|--------------------|----------|
| **FlatLaf** | Look and Feel phẳng, hiện đại; bo góc (arc), font Segoe UI; hỗ trợ Light/Dark. |
| **FlatLaf IntelliJ Themes** | Theme bổ sung tương thích FlatLaf. |
| **JFreeChart** | Biểu đồ: Line, Bar, Ring (doughnut); tiêu đề và trục theo theme. |
| **ThemeManager** | Chuyển đổi Light/Dark, lưu preference (Preferences API); áp dụng màu nền, chữ, viền, font toàn cục (UIManager). |
| **RoundedPanel** | Panel tùy chỉnh vẽ nền và viền bo góc (RoundRectangle2D, antialiasing) cho card thống kê và khung biểu đồ. |
| **EmojiFontHelper** | Hiển thị emoji/icon trên nút (Sidebar, dialogs) tương thích font hệ thống. |
| **GUIUtil** | Kích thước chuẩn nút toolbar và sidebar; độ rộng ô nhập trong dialog (TEXT_FIELD_COLUMNS). |
| **PrintHelper** | Tạo HTML in hóa đơn, phiếu khám, giấy cam kết; mở trong trình duyệt (Ctrl+P in). |
| **LogoHelper** | Tải và scale logo từ resources cho màn hình đăng nhập và sidebar. |

Các component Swing dùng trong dự án: `JFrame`, `JDialog`, `JPanel`, `JTable`, `JTextField`, `JComboBox`, `JButton`, `JToggleButton`, `JScrollPane`, `JEditorPane` (xem trước HTML); layout: `BorderLayout`, `GridLayout`, `FlowLayout`, `CardLayout`; event: `ActionListener`, `MouseListener`, `ItemListener`.

---

## 📚 Công nghệ và kỹ thuật

- **Lập trình giao diện (Swing)**: Container và component (JFrame, JPanel, JTable, JTextField, …), Layout Manager (BorderLayout, GridLayout, FlowLayout, CardLayout), xử lý sự kiện (ActionListener, MouseListener, ItemListener).
- **Truy cập dữ liệu (JDBC)**: Kết nối qua `DriverManager`, cấu hình ngoài file (`database.properties`), `PreparedStatement` tránh SQL Injection, xử lý `ResultSet` và map sang Entity/DTO, quản lý tài nguyên (try-with-resources).
- **Kiến trúc phần mềm**: Kiến trúc đa tầng (Presentation – Service – Repository – Database), tách biệt trách nhiệm (Separation of Concerns).
- **Design patterns**: Singleton (Service, kết nối DB), Repository (interface + implementation), Service Layer, DTO/Entity, Strategy (khởi tạo DB: H2 chạy schema/data, MySQL chỉ kết nối), MVC (Model–View–Controller), Proxy/AOP (annotation @RequireAdmin + PermissionHandler cho phân quyền theo vai trò ADMIN), Factory (tạo connection, strategy).
- **Xử lý ngoại lệ**: Ngoại lệ tùy biến (`PetcareException`), truyền và bắt ở từng tầng, thông báo rõ ràng cho người dùng.
- **Validation**: Kiểm tra dữ liệu ở Domain Model (setter), ở Service (quy tắc nghiệp vụ), và ở GUI (phản hồi ngay).
- **Trực quan hóa dữ liệu**: JFreeChart (dataset, ChartFactory, CategoryPlot, PiePlot), tùy biến tiêu đề/trục/legend theo theme.
- **Look and Feel và theme**: FlatLaf, UIManager để đặt font/arc/màu toàn cục, ThemeManager để chuyển và lưu theme Light/Dark.

---

## 📁 Cấu trúc dự án

```
PetcareAdmin/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── petcare/
│                   ├── aop/                   # Phân quyền AOP (RequireAdmin, PermissionHandler)
│                   ├── config/                # Cấu hình (DatabaseConfig)
│                   ├── gui/                   # Giao diện người dùng
│                   │   ├── panels/            # Các panel quản lý
│                   │   ├── dialogs/           # Các dialog thêm/sửa/chi tiết
│                   │   ├── DashboardFrame.java
│                   │   ├── LoginFrame.java
│                   │   └── Sidebar.java
│                   ├── model/                 # Domain, entity, exception
│                   ├── persistence/           # Kết nối DB, strategy khởi tạo
│                   │   ├── strategy/          # H2 (schema+data), MySQL (chỉ kết nối)
│                   │   ├── DatabaseConnection.java
│                   │   └── Database.java
│                   ├── repository/            # Data access (interface + impl)
│                   ├── service/               # Business logic layer
│                   └── util/                  # ThemeManager, GUIUtil, PrintHelper, RoundedPanel, ...
├── pom.xml                                   # Maven configuration
└── README.md                                 # File này
```

---

## 🏗 Kiến trúc hệ thống

Dự án được thiết kế theo **kiến trúc đa tầng (Multi-tier Architecture)**:

```
┌─────────────────────────────────────┐
│      PRESENTATION LAYER (GUI)       │
│  - Panels, Dialogs, Frames          │
│  - Event Handling                   │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│      SERVICE LAYER                  │
│  - Business Logic                    │
│  - Business Rules                    │
│  - Entity ↔ Domain Conversion        │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│      REPOSITORY LAYER               │
│  - Data Access                      │
│  - SQL Queries                      │
│  - ResultSet Mapping                │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│      DATABASE LAYER                 │
│  - MySQL Database                   │
│  - Tables, Relationships            │
└─────────────────────────────────────┘
```

### Các tầng chi tiết:

1. **Presentation Layer (GUI)**
   - Xử lý tương tác với người dùng
   - Hiển thị dữ liệu
   - Không chứa business logic

2. **Service Layer**
   - Chứa business logic và business rules
   - Validation phức tạp
   - Điều phối Repository calls
   - Chuyển đổi Entity ↔ Domain Model

3. **Repository Layer**
   - Truy cập cơ sở dữ liệu
   - Sử dụng PreparedStatement (an toàn)
   - Map ResultSet thành Entity objects

4. **Domain Model Layer**
   - Đại diện cho business objects
   - Chứa validation logic trong setters
   - Độc lập với database structure

5. **Entity Layer (DTO)**
   - Mapping trực tiếp với database tables
   - Không chứa business logic

---

## 🎨 Design Patterns

Dự án áp dụng các design patterns sau:

### 1. **Singleton Pattern**
- `CustomerService`, `PetService`, `DoctorService`
- `DatabaseConnection` - Quản lý kết nối database duy nhất

### 2. **Repository Pattern**
- Interface-based data access (`ICustomerRepository`, `IPetRepository`, ...)
- Tách biệt data access logic khỏi business logic
- Dễ dàng thay đổi data source

### 3. **Strategy Pattern**
- `DatabaseInitStrategy`: H2 chạy schema+data, MySQL chỉ kết nối
- `DatabaseInitStrategyFactory` chọn strategy theo driver trong `database.properties`

### 4. **Service Layer Pattern**
- Tách biệt business logic khỏi presentation và data access
- Chứa business rules và validation phức tạp

### 5. **DTO Pattern (Data Transfer Object)**
- Entity classes: Mapping với database
- Domain classes: Chứa business logic

### 6. **Dependency Injection**
- Service classes có thể inject Repository thông qua setter
- Dễ dàng test với mock objects

### 7. **MVC Pattern**
- **Model**: Domain models và Entity classes
- **View**: GUI components (Panels, Dialogs)
- **Controller**: Service layer

### 8. **Proxy (AOP thủ công)**
- Annotation `@RequireAdmin` đánh dấu method chỉ dành cho ADMIN
- `PermissionHandler` (InvocationHandler) tạo proxy cho IUserService, IServiceTypeService, IMedicineService, IVaccineTypeService, IGeneralSettingService
- Trước khi gọi method thật, proxy kiểm tra tham số User trong args và role ADMIN, ném PetcareException nếu không đủ quyền
- GUI: Sidebar ẩn menu (Dashboard, Dịch vụ, Thuốc, Vaccine, Người dùng, Cài đặt) với user STAFF; DashboardFrame chặn truy cập các màn tương ứng nếu không phải ADMIN

---

## 🚀 Cài đặt và chạy

### Yêu cầu hệ thống

- **Java 17+**
- **Maven 3.8+**
- **H2** (mặc định, dùng file nhúng) hoặc **MySQL 8.0+** (tùy chọn)

### Bước 1: Cấu hình Database

Chỉnh file `src/main/resources/database.properties`:

- **H2 (mặc định)**: dùng `db.driver=org.h2.Driver`, `db.url`, `db.user`, `db.password` tương ứng. Ứng dụng tự chạy script `schema-and-data-h2.sql` lần đầu.
- **MySQL**: đổi driver sang `com.mysql.cj.jdbc.Driver`, cấu hình url/user/password cho MySQL; tự tạo database và import dữ liệu.

### Bước 2: Build project

```bash
# Clean và compile
mvn clean compile

# Hoặc build JAR
mvn clean package
```

### Bước 3: Chạy ứng dụng

**Cách 1: Chạy trực tiếp với Maven**
```bash
mvn exec:java -Dexec.mainClass="com.petcare.App"
```

**Cách 2: Chạy từ JAR**
```bash
java -jar target/PetcareAdmin-1.0-SNAPSHOT.jar
```

**Cách 3: Chạy từ IDE**
- Mở project trong IntelliJ IDEA hoặc Eclipse
- Run class `com.petcare.App`

---

## ✨ Tính năng

### ✅ Đã hoàn thành

#### Quản lý cơ bản
- [x] **Đăng nhập/Authentication** - Xác thực người dùng với BCrypt
- [x] **Dashboard** - Bảng điều khiển với 4 biểu đồ và 5 stat cards
- [x] **Sidebar Navigation** - Điều hướng giữa các module

#### Quản lý khách hàng và thú cưng
- [x] **Customer Management** - CRUD đầy đủ với validation
- [x] **Pet Management** - CRUD đầy đủ với validation
- [x] **Doctor Management** - CRUD đầy đủ với validation

#### Quản lý khám và điều trị
- [x] **Medical Record Management** - Quản lý hồ sơ khám bệnh
- [x] **Vaccination Management** - Quản lý tiêm chủng
- [x] **Treatment Management** - Quản lý liệu trình điều trị
- [x] **Appointment Management** - Quản lý lịch hẹn

#### Quản lý dịch vụ
- [x] **Pet Enclosure Management** - Quản lý lưu chuồng (Check-in/Check-out)
- [x] **Invoice Management** - Quản lý hóa đơn và thanh toán
- [x] **Service Type Management** - Quản lý loại dịch vụ

#### Quản lý danh mục
- [x] **Medicine Management** - Quản lý thuốc
- [x] **Vaccine Type Management** - Quản lý loại vaccine

#### Hệ thống
- [x] **User Management** - Quản lý người dùng
- [x] **Settings Management** - Cài đặt hệ thống
- [x] **Theme Toggle** - Chuyển đổi Light/Dark theme
- [x] **Mẫu in lưu chuồng** - Xem giấy cam kết, mẫu hóa đơn, in trang

### 📊 Dashboard Features

1. **Line Chart** - Lượt khám (7 ngày gần nhất)
2. **Bar Chart** - Check-in/Check-out (7 ngày gần nhất)
3. **Line Chart** - Doanh thu theo tháng (12 tháng)
4. **Doughnut Chart** - Tỷ trọng doanh thu theo loại dịch vụ

### 📈 Stat Cards

1. Tổng khách hàng
2. Tổng thú cưng
3. Lượt khám (với % thay đổi)
4. Lượt lưu chuồng (với % thay đổi)
5. Doanh thu (với % thay đổi)

---

## 📖 Hướng dẫn sử dụng

### Đăng nhập

- **Username**: `admin`
- **Password**: `123456`

### Các chức năng chính

#### 1. Quản lý Khách hàng
- Xem danh sách khách hàng
- Thêm/Sửa/Xóa khách hàng
- Tìm kiếm khách hàng
- Validation: Số điện thoại và email phải unique

#### 2. Quản lý Thú cưng
- Xem danh sách thú cưng
- Thêm/Sửa/Xóa thú cưng
- Liên kết với khách hàng
- Validation: Tên, loài, giới tính bắt buộc

#### 3. Quản lý Bác sĩ
- Xem danh sách bác sĩ
- Thêm/Sửa/Xóa bác sĩ
- Validation: Số điện thoại phải unique

#### 4. Quản lý Hồ sơ khám bệnh
- Tạo hồ sơ khám mới
- Xem lịch sử khám bệnh
- Liên kết với thú cưng và bác sĩ

#### 5. Quản lý Tiêm chủng
- Ghi nhận tiêm chủng cho thú cưng
- Xem lịch sử tiêm chủng
- Quản lý vaccine types

#### 6. Quản lý Liệu trình điều trị
- Tạo liệu trình điều trị
- Quản lý các buổi điều trị trong liệu trình
- Theo dõi trạng thái điều trị

#### 7. Quản lý Lưu chuồng
- Check-in thú cưng vào chuồng
- Check-out và tính tiền
- Tự động tạo hóa đơn khi checkout

#### 8. Quản lý Hóa đơn
- Xem danh sách hóa đơn
- Tạo hóa đơn thủ công
- Xem chi tiết hóa đơn
- In hóa đơn

#### 9. Quản lý Lịch hẹn
- Tạo lịch hẹn mới
- Xem lịch hẹn theo ngày
- Cập nhật trạng thái lịch hẹn

#### 10. Quản lý Danh mục
- Quản lý loại dịch vụ
- Quản lý thuốc
- Quản lý loại vaccine

#### 11. Quản lý Người dùng
- Thêm/Sửa/Xóa người dùng (chỉ ADMIN)
- Đổi mật khẩu (bản thân hoặc ADMIN đổi cho người khác)
- Phân quyền theo vai trò: ADMIN (toàn quyền), STAFF (ẩn menu Dashboard, Dịch vụ, Thuốc, Vaccine, Người dùng, Cài đặt); kiểm tra ở GUI và ở tầng Service (AOP @RequireAdmin)

#### 12. Cài đặt
- Cấu hình hệ thống
- Quản lý thông tin phòng khám

### Theme Toggle

- Click nút **"🌙 Giao diện tối"** hoặc **"☀️ Giao diện sáng"** ở sidebar để chuyển đổi theme
- Theme được lưu tự động và áp dụng lại khi mở lại ứng dụng

---

## 🔒 Bảo mật

- ✅ **PreparedStatement**: Tránh SQL Injection
- ✅ **Password Hashing**: BCrypt (jbcrypt)
- ✅ **Phân quyền**: Vai trò ADMIN/STAFF; AOP (@RequireAdmin) ở Service; ẩn menu và chặn truy cập màn chỉ ADMIN ở GUI
- ✅ **Input Validation**: Ở nhiều tầng (GUI, Domain Model)
- ✅ **Exception Handling**: Custom exception với message rõ ràng

---

## 📝 Code Quality

- ✅ **Separation of Concerns**: Tách biệt rõ ràng giữa các tầng
- ✅ **Single Responsibility**: Mỗi class có trách nhiệm rõ ràng
- ✅ **DRY Principle**: Không lặp lại code
- ✅ **Clean Code**: Code dễ đọc, dễ maintain
- ✅ **Clean structure**: Code rõ ràng, dễ bảo trì

---

## 🧪 Testing

### Compilation Test

```bash
mvn clean compile
```

### Build JAR

```bash
mvn clean package
```

### Run Application

```bash
mvn exec:java -Dexec.mainClass="com.petcare.App"
```

---

## 📚 Tài liệu tham khảo

- Tài liệu môn Công nghệ Java – Trường Đại học Công nghệ Thông tin
- Oracle Java Documentation – Java Swing, JDBC
- FlatLaf – https://www.formdev.com/flatlaf/
- JFreeChart – https://www.jfree.org/jfreechart/
- H2 Database – https://www.h2database.com/

---

## 👥 Đóng góp

Mọi đóng góp và góp ý đều được chào đón!

---

## 📄 License

MIT License

---

## 🙏 Lời cảm ơn

Cảm ơn các thầy cô đã hướng dẫn và cung cấp tài liệu học tập về:
- Java Swing GUI Programming
- JDBC và Database Programming
- Design Patterns
- Software Architecture

---

**Phát triển bởi**: [Tên sinh viên]  
**Lớp**: [Lớp]  
**Năm học**: 2024-2025
