# GIỚI THIỆU ĐỒ ÁN
## HỆ THỐNG QUẢN LÝ PHÒNG KHÁM THÚ CƯNG - PHẦN ADMIN

---

## 1. TỔNG QUAN ĐỒ ÁN

### 1.1. Mô tả dự án

**PetcareAdmin** là một ứng dụng quản lý phòng khám thú cưng được xây dựng bằng **Java Swing**, cung cấp giao diện quản lý toàn diện cho các chức năng của phòng khám bao gồm:

- Quản lý khách hàng và thú cưng
- Quản lý bác sĩ và nhân viên
- Quản lý hồ sơ khám bệnh, tiêm chủng, liệu trình điều trị
- Quản lý dịch vụ lưu chuồng và lịch hẹn
- Quản lý hóa đơn và thanh toán
- Quản lý danh mục (dịch vụ, thuốc, vaccine)
- Dashboard thống kê với biểu đồ
- Quản lý người dùng và cài đặt hệ thống

### 1.2. Công nghệ sử dụng

- **Java 17**: Ngôn ngữ lập trình chính
- **Java Swing**: Framework xây dựng giao diện người dùng (GUI)
- **Maven 3.8+**: Công cụ quản lý dự án và dependencies
- **MySQL 8.0+**: Hệ quản trị cơ sở dữ liệu quan hệ
- **JDBC**: API kết nối và tương tác với cơ sở dữ liệu
- **JFreeChart 1.5.4**: Thư viện tạo biểu đồ thống kê
- **FlatLaf 3.1.1**: Thư viện Look and Feel hiện đại cho Swing

---

## 2. PHÂN TÍCH THIẾT KẾ HỆ THỐNG

### 2.1. Kiến trúc tổng thể

Dự án được thiết kế theo **kiến trúc đa tầng (Multi-tier Architecture)** với các tầng được phân tách rõ ràng:

```
┌─────────────────────────────────────┐
│         PRESENTATION LAYER          │
│  (GUI - Swing Components)           │
│  - Panels, Dialogs, Frames          │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│         SERVICE LAYER               │
│  (Business Logic)                   │
│  - CustomerService, PetService, ... │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│       REPOSITORY LAYER              │
│  (Data Access)                      │
│  - CustomerRepository, PetRepo, ... │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│         DATABASE LAYER              │
│  (MySQL Database)                   │
│  - Tables, Relationships            │
└─────────────────────────────────────┘
```

### 2.2. Các tầng trong hệ thống

#### 2.2.1. Presentation Layer (Tầng giao diện)

**Vai trò**: Xử lý tương tác với người dùng, hiển thị dữ liệu và nhận input từ người dùng.

**Các thành phần chính**:
- **Frames**: `LoginFrame`, `DashboardFrame` - Các cửa sổ chính của ứng dụng
- **Panels**: 15 panel quản lý các module khác nhau (Customer, Pet, Doctor, ...)
- **Dialogs**: 17 dialog cho các thao tác thêm/sửa/xem chi tiết
- **Components**: `Sidebar` - Thanh điều hướng, `DashboardPanel` - Bảng điều khiển

**Đặc điểm**:
- Sử dụng **CardLayout** để quản lý việc chuyển đổi giữa các panel
- Tách biệt hoàn toàn với logic nghiệp vụ, chỉ gọi Service layer
- Xử lý sự kiện (Event Handling) theo mô hình Event-Driven

#### 2.2.2. Service Layer (Tầng nghiệp vụ)

**Vai trò**: Chứa các logic nghiệp vụ, quy tắc kinh doanh và điều phối các Repository.

**Các thành phần chính**:
- `CustomerService`: Quản lý logic nghiệp vụ cho khách hàng
- `PetService`: Quản lý logic nghiệp vụ cho thú cưng
- `DoctorService`: Quản lý logic nghiệp vụ cho bác sĩ

**Chức năng**:
- **Business Rules**: Kiểm tra tính duy nhất (unique constraints), validation phức tạp
- **Data Transformation**: Chuyển đổi giữa Entity (DTO) và Domain Model
- **Transaction Management**: Quản lý các giao dịch phức tạp
- **Exception Handling**: Xử lý và chuyển đổi exceptions thành business exceptions

**Ví dụ Business Rules**:
```java
// Kiểm tra số điện thoại không được trùng
if (repository.existsByPhone(customer.getCustomerPhoneNumber())) {
    throw new PetcareException("Số điện thoại đã được sử dụng");
}
```

#### 2.2.3. Repository Layer (Tầng truy cập dữ liệu)

**Vai trò**: Cung cấp interface và implementation cho việc truy cập cơ sở dữ liệu.

**Các thành phần chính**:
- **Interfaces**: `ICustomerRepository`, `IPetRepository`, `IDoctorRepository`
- **Implementations**: `CustomerRepository`, `PetRepository`, `DoctorRepository`
- **Connection Manager**: `DatabaseConnection` - Quản lý kết nối database

**Đặc điểm**:
- Sử dụng **PreparedStatement** để tránh SQL Injection
- Try-with-resources để đảm bảo đóng kết nối tự động
- Map ResultSet thành Entity objects
- Xử lý exceptions và wrap thành `PetcareException`

**Ví dụ**:
```java
@Override
public CustomerEntity findById(int id) throws PetcareException {
    String query = "SELECT * FROM customers WHERE customer_id = ?";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(query)) {
        ps.setInt(1, id);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return mapResultSetToEntity(rs);
            }
        }
    } catch (SQLException ex) {
        throw new PetcareException("Failed to find customer by ID: " + id, ex);
    }
    return null;
}
```

#### 2.2.4. Domain Model Layer (Tầng mô hình nghiệp vụ)

**Vai trò**: Đại diện cho các đối tượng nghiệp vụ với validation logic.

**Các thành phần chính**:
- `Customer` (Domain Model): Chứa validation rules cho khách hàng
- `Pet` (Domain Model): Chứa validation rules cho thú cưng
- `Doctor` (Domain Model): Chứa validation rules cho bác sĩ

**Đặc điểm**:
- Validation được thực hiện trong các setter methods
- Đảm bảo tính toàn vẹn dữ liệu ở tầng domain
- Tách biệt với Entity (DTO) - chỉ chứa dữ liệu từ database

**Ví dụ Validation**:
```java
public final void setCustomerPhoneNumber(String phoneNumber) throws PetcareException {
    if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
        throw new PetcareException("Số điện thoại không được để trống");
    }
    String cleaned = phoneNumber.trim().replaceAll("[\\s-]", "");
    if (cleaned.length() < 10 || cleaned.length() > 11) {
        throw new PetcareException("Số điện thoại phải có từ 10 đến 11 chữ số");
    }
    if (!cleaned.matches("^[0-9]+$")) {
        throw new PetcareException("Số điện thoại chỉ được chứa chữ số");
    }
    this.customerPhoneNumber = cleaned;
}
```

#### 2.2.5. Entity Layer (Tầng DTO)

**Vai trò**: Đại diện cho cấu trúc dữ liệu từ database (Data Transfer Object).

**Các thành phần chính**:
- `CustomerEntity`: Mapping trực tiếp với bảng `customers`
- `PetEntity`: Mapping trực tiếp với bảng `pets`
- `DoctorEntity`: Mapping trực tiếp với bảng `doctors`

**Đặc điểm**:
- Không chứa business logic
- Chỉ là POJO (Plain Old Java Object) với getters/setters
- Mapping 1-1 với database schema

---

## 3. TỔ CHỨC SOURCE CODE

### 3.1. Cấu trúc thư mục

```
PetcareAdmin/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── petcare/
│                   ├── gui/                    # Giao diện người dùng
│                   │   ├── panels/            # Các panel quản lý (15 files)
│                   │   ├── dialogs/           # Các dialog (17 files)
│                   │   ├── DashboardFrame.java
│                   │   ├── LoginFrame.java
│                   │   ├── Main.java
│                   │   └── Sidebar.java
│                   ├── model/                  # Models
│                   │   ├── domain/            # Domain models với validation
│                   │   │   ├── Customer.java
│                   │   │   ├── Doctor.java
│                   │   │   └── Pet.java
│                   │   ├── entity/            # Entity DTOs
│                   │   │   ├── CustomerEntity.java
│                   │   │   ├── DoctorEntity.java
│                   │   │   └── PetEntity.java
│                   │   ├── exception/         # Custom exceptions
│                   │   │   └── PetcareException.java
│                   │   └── [legacy models]   # Các model cũ (compatibility)
│                   ├── repository/            # Data access layer
│                   │   ├── DatabaseConnection.java
│                   │   ├── ICustomerRepository.java
│                   │   ├── CustomerRepository.java
│                   │   ├── IPetRepository.java
│                   │   ├── PetRepository.java
│                   │   ├── IDoctorRepository.java
│                   │   └── DoctorRepository.java
│                   ├── service/               # Business logic layer
│                   │   ├── CustomerService.java
│                   │   ├── PetService.java
│                   │   └── DoctorService.java
│                   └── util/                  # Utilities
│                       ├── DashboardService.java
│                       └── ThemeManager.java
├── pom.xml                                  # Maven configuration
└── README.md
```

### 3.2. Nguyên tắc tổ chức code

#### 3.2.1. Package Organization

- **Theo chức năng**: Mỗi package đại diện cho một tầng hoặc một nhóm chức năng
- **Separation of Concerns**: Mỗi package chỉ chứa các class có cùng mục đích
- **Naming Convention**: Sử dụng tên package rõ ràng, dễ hiểu

#### 3.2.2. GUI Package Organization

**Panels** (`gui/panels/`):
- Tất cả các panel quản lý được đặt trong package `panels`
- Mỗi panel tương ứng với một module quản lý
- Naming: `*ManagementPanel.java`

**Dialogs** (`gui/dialogs/`):
- Tất cả các dialog được đặt trong package `dialogs`
- Phân loại theo chức năng: Add/Edit, Details, Special dialogs
- Naming: `AddEdit*Dialog.java`, `*DetailsDialog.java`

**Frames** (`gui/`):
- Các frame chính được đặt ở root của package `gui`
- `LoginFrame`: Màn hình đăng nhập
- `DashboardFrame`: Màn hình chính sau khi đăng nhập
- `Main`: Entry point của ứng dụng

#### 3.2.3. Model Package Organization

**Domain Models** (`model/domain/`):
- Chứa các domain model với business logic và validation
- Được sử dụng trong Service layer
- Có validation trong setters

**Entity DTOs** (`model/entity/`):
- Chứa các DTO mapping trực tiếp với database
- Được sử dụng trong Repository layer
- Không có business logic

**Exception** (`model/exception/`):
- Custom exceptions cho ứng dụng
- `PetcareException`: Base exception cho toàn bộ ứng dụng

#### 3.2.4. Repository Package Organization

- **Interfaces**: Định nghĩa contract cho data access (`I*Repository.java`)
- **Implementations**: Triển khai các interface (`*Repository.java`)
- **Connection Manager**: `DatabaseConnection.java` - Quản lý kết nối

#### 3.2.5. Service Package Organization

- Mỗi service tương ứng với một domain model
- Naming: `*Service.java`
- Sử dụng Singleton pattern

---

## 4. DESIGN PATTERNS ĐƯỢC SỬ DỤNG

### 4.1. Singleton Pattern

**Mục đích**: Đảm bảo chỉ có một instance duy nhất của một class trong toàn bộ ứng dụng.

**Áp dụng**:
- **Service Classes**: `CustomerService`, `PetService`, `DoctorService`
- **DatabaseConnection**: Quản lý kết nối database duy nhất

**Ví dụ**:
```java
public class CustomerService {
    private static CustomerService instance;
    private ICustomerRepository repository;
    
    private CustomerService() {
        this.repository = new CustomerRepository();
    }
    
    public static CustomerService getInstance() {
        if (instance == null) {
            instance = new CustomerService();
        }
        return instance;
    }
}
```

**Lợi ích**:
- Tiết kiệm tài nguyên (không tạo nhiều instance không cần thiết)
- Đảm bảo tính nhất quán của dữ liệu
- Dễ dàng quản lý state

### 4.2. Repository Pattern

**Mục đích**: Tách biệt logic truy cập dữ liệu khỏi business logic.

**Áp dụng**:
- Tất cả các Repository classes: `CustomerRepository`, `PetRepository`, `DoctorRepository`
- Sử dụng Interface (`ICustomerRepository`) để định nghĩa contract

**Cấu trúc**:
```java
// Interface
public interface ICustomerRepository {
    List<CustomerEntity> findAll() throws PetcareException;
    CustomerEntity findById(int id) throws PetcareException;
    int insert(CustomerEntity entity) throws PetcareException;
    // ...
}

// Implementation
public class CustomerRepository implements ICustomerRepository {
    // Implementation details
}
```

**Lợi ích**:
- Dễ dàng thay đổi data source (có thể switch từ MySQL sang PostgreSQL)
- Dễ dàng test bằng cách mock repository
- Tách biệt rõ ràng giữa data access và business logic

### 4.3. Service Layer Pattern

**Mục đích**: Tách biệt business logic khỏi presentation layer và data access layer.

**Áp dụng**:
- Tất cả các Service classes: `CustomerService`, `PetService`, `DoctorService`

**Vai trò**:
- Chứa business rules và validation phức tạp
- Điều phối các repository calls
- Chuyển đổi giữa Entity và Domain Model
- Xử lý transactions

**Lợi ích**:
- Business logic được tập trung, dễ maintain
- GUI layer không cần biết về database structure
- Dễ dàng thay đổi business rules mà không ảnh hưởng đến các layer khác

### 4.4. Data Transfer Object (DTO) Pattern

**Mục đích**: Tách biệt cấu trúc dữ liệu database với domain model.

**Áp dụng**:
- Entity classes: `CustomerEntity`, `PetEntity`, `DoctorEntity`
- Domain classes: `Customer`, `Pet`, `Doctor`

**Sự khác biệt**:
- **Entity**: Mapping trực tiếp với database, không có business logic
- **Domain Model**: Chứa business logic và validation, độc lập với database

**Lợi ích**:
- Database schema có thể thay đổi mà không ảnh hưởng đến business logic
- Domain model có thể có cấu trúc khác với database
- Dễ dàng test business logic độc lập với database

### 4.5. Dependency Injection (DI)

**Mục đích**: Giảm sự phụ thuộc giữa các class bằng cách inject dependencies từ bên ngoài.

**Áp dụng**:
- Service classes có thể inject Repository thông qua setter method

**Ví dụ**:
```java
public class CustomerService {
    private ICustomerRepository repository;
    
    // Constructor injection (default)
    private CustomerService() {
        this.repository = new CustomerRepository();
    }
    
    // Setter injection (for testing)
    public void setRepository(ICustomerRepository repository) {
        this.repository = repository;
    }
}
```

**Lợi ích**:
- Dễ dàng test bằng cách inject mock repository
- Loose coupling giữa Service và Repository
- Linh hoạt trong việc thay đổi implementation

### 4.6. MVC Pattern (Model-View-Controller)

**Mục đích**: Tách biệt presentation, business logic và data.

**Áp dụng**:
- **Model**: Domain models và Entity classes
- **View**: GUI components (Panels, Dialogs, Frames)
- **Controller**: Service layer đóng vai trò controller

**Luồng hoạt động**:
```
User Action (View) 
    → Service (Controller) 
    → Repository (Model Access) 
    → Database
    → Repository returns Entity
    → Service converts to Domain Model
    → View displays Domain Model
```

### 4.7. Factory Pattern (Implicit)

**Mục đích**: Tạo objects mà không cần chỉ định class cụ thể.

**Áp dụng**:
- `DatabaseConnection.getConnection()`: Factory method tạo Connection
- Service `getInstance()`: Factory method tạo Service instance

### 4.8. Template Method Pattern (Implicit)

**Mục đích**: Định nghĩa skeleton của algorithm trong base class.

**Áp dụng**:
- Repository classes có cấu trúc tương tự:
  1. Get connection
  2. Prepare statement
  3. Execute query
  4. Map ResultSet to Entity
  5. Handle exceptions

---

## 5. CÁC KỸ THUẬT VÀ CÔNG NGHỆ ÁP DỤNG

### 5.1. Java Swing (Chương 2)

#### 5.1.1. Swing Components sử dụng

**Containers**:
- `JFrame`: Cửa sổ chính của ứng dụng (`LoginFrame`, `DashboardFrame`)
- `JDialog`: Các hộp thoại (`AddEditCustomerDialog`, ...)
- `JPanel`: Container cho các components (`CustomerManagementPanel`, ...)
- `JScrollPane`: Cuộn cho table và text area

**Components**:
- `JTable`: Hiển thị dữ liệu dạng bảng với `DefaultTableModel`
- `JTextField`: Nhập liệu văn bản
- `JTextArea`: Nhập liệu văn bản nhiều dòng
- `JComboBox`: Dropdown list
- `JButton`: Nút bấm
- `JLabel`: Nhãn hiển thị
- `JRadioButton`: Lựa chọn đơn
- `JToggleButton`: Nút toggle (dùng trong Sidebar)

**Layout Managers**:
- `BorderLayout`: Layout chính cho Frames và Panels
- `GridLayout`: Layout dạng lưới cho forms
- `FlowLayout`: Layout dòng chảy cho button groups
- `CardLayout`: Quản lý chuyển đổi giữa các panels trong `DashboardFrame`

#### 5.1.2. Event Handling

**Event Listeners sử dụng**:
- `ActionListener`: Xử lý sự kiện click button
- `MouseListener`: Xử lý sự kiện chuột (hover effects)
- `ItemListener`: Xử lý sự kiện thay đổi selection trong ComboBox

**Ví dụ**:
```java
addButton.addActionListener(e -> showAddCustomerDialog());
customerCombo.addItemListener(e -> {
    if (e.getStateChange() == ItemEvent.SELECTED) {
        loadPetsForCustomer();
    }
});
```

#### 5.1.3. Look and Feel

**FlatLaf Integration**:
- Sử dụng **FlatLaf** để có giao diện hiện đại, phẳng
- Hỗ trợ Light/Dark theme switching
- Custom styling với `FlatClientProperties`

**Ví dụ**:
```java
button.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
textField.putClientProperty("JTextField.placeholderText", "Nhập tên...");
```

### 5.2. JDBC (Chương 3)

#### 5.2.1. Database Connection

**Connection Management**:
- Sử dụng `DriverManager.getConnection()` để tạo connection
- Singleton pattern cho connection management
- Static initialization block để tự động kết nối khi class được load

**Ví dụ**:
```java
public class DatabaseConnection {
    private static Connection connection;
    
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to connect", ex);
        }
    }
}
```

#### 5.2.2. PreparedStatement

**SQL Injection Prevention**:
- Sử dụng `PreparedStatement` thay vì `Statement` để tránh SQL Injection
- Parameterized queries với placeholders (`?`)

**Ví dụ**:
```java
String query = "SELECT * FROM customers WHERE customer_id = ?";
PreparedStatement ps = conn.prepareStatement(query);
ps.setInt(1, customerId);
ResultSet rs = ps.executeQuery();
```

**Lợi ích**:
- An toàn: Tránh SQL Injection
- Hiệu suất: Prepared statements được cache và tái sử dụng
- Dễ đọc: Code rõ ràng hơn

#### 5.2.3. ResultSet Processing

**ResultSet Types**:
- Sử dụng `TYPE_SCROLL_INSENSITIVE` cho ResultSet có thể scroll
- Sử dụng `CONCUR_READ_ONLY` cho ResultSet chỉ đọc

**Mapping ResultSet to Objects**:
```java
private CustomerEntity mapResultSetToEntity(ResultSet rs) throws SQLException {
    CustomerEntity entity = new CustomerEntity();
    entity.setCustomerId(rs.getInt("customer_id"));
    entity.setCustomerName(rs.getString("customer_name"));
    // ...
    return entity;
}
```

#### 5.2.4. Resource Management

**Try-with-Resources**:
- Tự động đóng Connection, Statement, ResultSet
- Đảm bảo không có resource leaks

**Ví dụ**:
```java
try (Connection conn = DatabaseConnection.getConnection();
     PreparedStatement ps = conn.prepareStatement(query);
     ResultSet rs = ps.executeQuery()) {
    // Process results
} catch (SQLException ex) {
    // Handle exception
}
```

### 5.3. Exception Handling

**Custom Exception**:
- `PetcareException`: Custom exception cho toàn bộ ứng dụng
- Wrap SQLException thành PetcareException với message rõ ràng

**Exception Propagation**:
- GUI layer catch `PetcareException` và hiển thị message cho user
- Service layer throw `PetcareException` khi có business rule violation
- Repository layer wrap `SQLException` thành `PetcareException`

**Ví dụ**:
```java
try {
    customerService.createCustomer(customer);
    JOptionPane.showMessageDialog(this, "Thêm thành công!");
} catch (PetcareException ex) {
    JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
}
```

### 5.4. Data Visualization

**JFreeChart Integration**:
- Sử dụng JFreeChart để tạo biểu đồ trong Dashboard
- Các loại biểu đồ: Bar Chart, Line Chart, Pie Chart
- Real-time data updates

**Ví dụ**:
```java
DefaultCategoryDataset dataset = new DefaultCategoryDataset();
dataset.addValue(count, "Series", "Category");
JFreeChart chart = ChartFactory.createBarChart(...);
```

---

## 6. ĐIỂM MẠNH CỦA THIẾT KẾ

### 6.1. Separation of Concerns

- **GUI Layer**: Chỉ quan tâm đến hiển thị và tương tác với user
- **Service Layer**: Chỉ quan tâm đến business logic
- **Repository Layer**: Chỉ quan tâm đến data access
- **Domain Layer**: Chỉ quan tâm đến business rules và validation

### 6.2. Maintainability

- Code được tổ chức rõ ràng, dễ tìm và sửa
- Mỗi class có trách nhiệm rõ ràng (Single Responsibility Principle)
- Dễ dàng thêm tính năng mới mà không ảnh hưởng đến code cũ

### 6.3. Testability

- Service layer có thể test độc lập bằng cách inject mock repository
- Domain models có thể test validation logic độc lập
- Repository có thể test với in-memory database

### 6.4. Scalability

- Dễ dàng thêm module mới bằng cách tạo Service, Repository, Domain Model mới
- Có thể mở rộng sang các data source khác (PostgreSQL, MongoDB) bằng cách implement Repository interface mới

### 6.5. Security

- Sử dụng PreparedStatement để tránh SQL Injection
- Password được hash bằng MD5 (có thể nâng cấp lên BCrypt)
- Input validation ở nhiều tầng (GUI, Domain Model)

---

## 7. KẾT LUẬN

Dự án **PetcareAdmin** được thiết kế và triển khai theo các best practices của Java Enterprise Development:

1. **Kiến trúc đa tầng** rõ ràng với separation of concerns
2. **Design patterns** phù hợp cho từng tình huống
3. **Code organization** logic và dễ maintain
4. **Security** được đảm bảo với PreparedStatement và input validation
5. **User Experience** tốt với FlatLaf và responsive design

Dự án thể hiện sự hiểu biết sâu sắc về:
- Java Swing GUI Programming
- JDBC và Database Programming
- Design Patterns và Software Architecture
- Best Practices trong Java Development

---

**Sinh viên thực hiện**: [Tên sinh viên]  
**Lớp**: [Lớp]  
**Môn học**: Công nghệ Java  
**Năm học**: 2024-2025
