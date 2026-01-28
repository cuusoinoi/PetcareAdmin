# Petcare Admin - Java Swing Application

Hệ thống quản lý phòng khám thú cưng - Phần Admin (Java Swing)

## Công nghệ sử dụng

- **Java 17**
- **Java Swing** - Giao diện người dùng
- **Maven** - Quản lý dependencies
- **MySQL 8.1** - Cơ sở dữ liệu
- **JFreeChart 1.5.4** - Biểu đồ thống kê
- **FlatLaf 3.1.1** - Look and Feel hiện đại

## Cấu trúc dự án

```
PetcareAdmin/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/petcare/
│   │   │       ├── model/      # Model classes
│   │   │       ├── gui/        # GUI components
│   │   │       ├── util/       # Utility classes
│   │   │       └── style/      # UI styling
│   │   └── resources/          # Resources (images, etc.)
│   └── test/
│       └── java/
│           └── com/petcare/
├── pom.xml                     # Maven configuration
└── README.md
```

## Cài đặt và chạy

### Yêu cầu
- Java 17+
- Maven 3.6+
- MySQL 8.0+
- Database `petcare` đã được tạo và import dữ liệu

### Cấu hình Database

Sửa file `src/main/java/com/petcare/model/Database.java`:
```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/petcare";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "your_password";
```

### Build và chạy

```bash
# Build project
mvn clean compile

# Run application
mvn exec:java -Dexec.mainClass="com.petcare.gui.Main"

# Hoặc tạo JAR và chạy
mvn clean package
java -jar target/PetcareAdmin-1.0-SNAPSHOT.jar
```

## Đăng nhập

- **Username**: `admin`
- **Password**: `123456`

## Tính năng (đang phát triển)

### ✅ Đã hoàn thành
- [x] Cấu trúc dự án Maven
- [x] Database connection với PreparedStatement (an toàn)
- [x] Model classes (User, Customer, Pet)
- [x] Login screen với authentication
- [x] Main frame structure

### 🚧 Đang phát triển
- [ ] Dashboard với 4 charts và 5 stat cards
- [ ] Sidebar navigation
- [ ] Customer Management (CRUD)
- [ ] Pet Management (CRUD)
- [ ] Doctor Management (CRUD)
- [ ] Medical Record Management
- [ ] Pet Enclosure Management (Check-in/Check-out)
- [ ] Invoice Management
- [ ] Appointment Management

## Dashboard Charts

1. **Line Chart** - Lượt khám (7 ngày gần nhất)
2. **Bar Chart** - Check-in/Check-out (7 ngày gần nhất)
3. **Line Chart** - Doanh thu theo tháng (12 tháng)
4. **Doughnut Chart** - Tỷ trọng doanh thu theo loại dịch vụ

## Stat Cards

1. Tổng khách hàng
2. Tổng thú cưng
3. Lượt khám (với % thay đổi)
4. Lượt lưu chuồng (với % thay đổi)
5. Doanh thu (với % thay đổi)

## License

MIT License
