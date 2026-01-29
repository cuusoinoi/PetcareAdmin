# Cấu hình Database - PetcareAdmin

## 1. File cấu hình: database.properties

Tất cả cấu hình kết nối DB nằm trong file này (không hard-code trong code).

- Vị trí: src/main/resources/database.properties
- Mặc định: H2 (file lưu tại ./data/petcare)

### Dùng H2 (mặc định)
- db.driver=org.h2.Driver
- db.url=jdbc:h2:file:./data/petcare;...
- db.user=sa
- db.password= (để trống)

### Đổi sang MySQL
- Sửa database.properties: bật (bỏ #) các dòng db.driver, db.url, db.user, db.password của MySQL.
- Comment (thêm #) các dòng H2.
- Chạy script MySQL: petcare_database.sql (ở thư mục gốc project).

## 2. Lần đầu dùng H2: tạo bảng và dữ liệu

Cần chạy schema một lần để tạo bảng và user admin:

- Cách 1: Dùng H2 Console (jar có sẵn trong Maven)
  - Chạy: java -cp "~/.m2/.../h2-2.2.224.jar" org.h2.tools.Shell -url "jdbc:h2:file:./data/petcare" -user sa
  - Trong Shell: \runscript từ 'src/main/resources/schema-h2.sql'
- Cách 2: Ứng dụng có thể tự chạy schema khi khởi động nếu bảng chưa tồn tại (xem DatabaseConnection hoặc Main).

Sau khi chạy schema-h2.sql:
- Đăng nhập: username = admin, password = 123456
