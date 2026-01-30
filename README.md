# Petcare Admin - Java Swing Application

Hệ thống quản lý phòng khám thú cưng - Phần Admin (Java Swing)

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Swing](https://img.shields.io/badge/Swing-GUI-blue.svg)](https://docs.oracle.com/javase/tutorial/uiswing/)
[![Maven](https://img.shields.io/badge/Maven-3.8+-red.svg)](https://maven.apache.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue.svg)](https://www.mysql.com/)

---

## 📋 Mục lục

- [Tổng quan](#tổng-quan)
- [Công nghệ sử dụng](#công-nghệ-sử-dụng)
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

- **Java 17** - Ngôn ngữ lập trình chính
- **Java Swing** - Framework xây dựng giao diện người dùng (GUI)
- **Maven 3.8+** - Quản lý dự án và dependencies
- **MySQL 8.0+** - Hệ quản trị cơ sở dữ liệu quan hệ
- **JDBC** - API kết nối và tương tác với cơ sở dữ liệu
- **JFreeChart 1.5.4** - Thư viện tạo biểu đồ thống kê
- **FlatLaf 3.1.1** - Look and Feel hiện đại cho Swing (hỗ trợ Light/Dark theme)

---

## 📁 Cấu trúc dự án

```
PetcareAdmin/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── petcare/
│                   ├── gui/                    # Giao diện người dùng
│                   │   ├── panels/            # 15 Panel quản lý
│                   │   │   ├── CustomerManagementPanel.java
│                   │   │   ├── PetManagementPanel.java
│                   │   │   ├── DoctorManagementPanel.java
│                   │   │   ├── DashboardPanel.java
│                   │   │   └── ...
│                   │   ├── dialogs/           # 17 Dialog
│                   │   │   ├── AddEditCustomerDialog.java
│                   │   │   ├── AddEditPetDialog.java
│                   │   │   └── ...
│                   │   ├── DashboardFrame.java
│                   │   ├── LoginFrame.java
│                   │   ├── Main.java
│                   │   └── Sidebar.java
│                   ├── model/                 # Models
│                   │   ├── domain/           # Domain models với validation
│                   │   │   ├── Customer.java
│                   │   │   ├── Doctor.java
│                   │   │   └── Pet.java
│                   │   ├── entity/           # Entity DTOs
│                   │   │   ├── CustomerEntity.java
│                   │   │   ├── DoctorEntity.java
│                   │   │   └── PetEntity.java
│                   │   ├── exception/        # Custom exceptions
│                   │   │   └── PetcareException.java
│                   │   └── [legacy models]   # Các model cũ
│                   ├── repository/           # Data access layer
│                   │   ├── DatabaseConnection.java
│                   │   ├── ICustomerRepository.java
│                   │   ├── CustomerRepository.java
│                   │   ├── IPetRepository.java
│                   │   ├── PetRepository.java
│                   │   ├── IDoctorRepository.java
│                   │   └── DoctorRepository.java
│                   ├── service/              # Business logic layer
│                   │   ├── CustomerService.java
│                   │   ├── PetService.java
│                   │   └── DoctorService.java
│                   └── util/                 # Utilities
│                       ├── DashboardService.java
│                       └── ThemeManager.java
├── pom.xml                                   # Maven configuration
├── README.md                                 # File này
└── GIOI_THIEU_DO_AN.md                      # Tài liệu giới thiệu chi tiết
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

### 3. **Service Layer Pattern**
- Tách biệt business logic khỏi presentation và data access
- Chứa business rules và validation phức tạp

### 4. **DTO Pattern (Data Transfer Object)**
- Entity classes: Mapping với database
- Domain classes: Chứa business logic

### 5. **Dependency Injection**
- Service classes có thể inject Repository thông qua setter
- Dễ dàng test với mock objects

### 6. **MVC Pattern**
- **Model**: Domain models và Entity classes
- **View**: GUI components (Panels, Dialogs)
- **Controller**: Service layer

---

## 🚀 Cài đặt và chạy

### Yêu cầu hệ thống

- **Java 17+**
- **Maven 3.8+**
- **MySQL 8.0+**
- **Database `petcare`** đã được tạo và import dữ liệu

### Bước 1: Clone hoặc tải dự án

```bash
cd PetcareAdmin
```

### Bước 2: Cấu hình Database

Sửa file `src/main/java/com/petcare/repository/DatabaseConnection.java`:

```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/petcare";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "your_password";
```

### Bước 3: Import Database

Import file SQL vào MySQL:

```bash
mysql -u root -p petcare < petcare_mysql_database.sql
```

### Bước 4: Build project

```bash
# Clean và compile
mvn clean compile

# Hoặc build JAR
mvn clean package
```

### Bước 5: Chạy ứng dụng

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
- [x] **Đăng nhập/Authentication** - Xác thực người dùng với MD5 hashing
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
- Thêm/Sửa/Xóa người dùng
- Đổi mật khẩu
- Phân quyền (nếu có)

#### 12. Cài đặt
- Cấu hình hệ thống
- Quản lý thông tin phòng khám

### Theme Toggle

- Click nút **"🌙 Giao diện tối"** hoặc **"☀️ Giao diện sáng"** ở sidebar để chuyển đổi theme
- Theme được lưu tự động và áp dụng lại khi mở lại ứng dụng

---

## 🔒 Bảo mật

- ✅ **PreparedStatement**: Tránh SQL Injection
- ✅ **Password Hashing**: MD5 (có thể nâng cấp lên BCrypt)
- ✅ **Input Validation**: Ở nhiều tầng (GUI, Domain Model)
- ✅ **Exception Handling**: Custom exception với message rõ ràng

---

## 📝 Code Quality

- ✅ **Separation of Concerns**: Tách biệt rõ ràng giữa các tầng
- ✅ **Single Responsibility**: Mỗi class có trách nhiệm rõ ràng
- ✅ **DRY Principle**: Không lặp lại code
- ✅ **Clean Code**: Code dễ đọc, dễ maintain
- ✅ **Comments**: Javadoc cho các methods quan trọng

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

- [GIOI_THIEU_DO_AN.md](./GIOI_THIEU_DO_AN.md) - Tài liệu giới thiệu chi tiết về đồ án, thiết kế, design patterns

---

## 👥 Đóng góp

Dự án này được phát triển như một đồ án môn học. Mọi đóng góp và góp ý đều được chào đón!

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
**Môn học**: Công nghệ Java  
**Năm học**: 2024-2025
