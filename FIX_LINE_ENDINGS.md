# Fix "modified" giả do line ending (CRLF vs LF)

Nhiều file hiện "modified" dù nội dung giống nhau — do Windows dùng CRLF, repo dùng LF.

## Đã làm sẵn

- **`.gitattributes`**: Chuẩn hóa LF cho `*.java`, `*.xml`, `*.properties`, `*.md`, `*.txt`.
- **`.git/config`**: `autocrlf = input` — checkout giữ LF, không convert sang CRLF (tránh "modified" ảo sau này).

## Bạn cần chạy (trong terminal, tại thư mục PetcareAdmin)

1. **Xóa lock (nếu có lỗi "index.lock"):**
   ```bash
   del .git\index.lock
   ```

2. **Stage các file đã xóa trước** (để `--renormalize` không cố đọc file không tồn tại):
   ```bash
   git add .DS_Store
   git add src/main/java/com/petcare/gui/Main.java
   ```

3. **Chuẩn hóa line ending:**
   ```bash
   git add --renormalize .
   ```

4. **Nếu vẫn báo "unable to stat '...'"**, stage thêm file đó rồi chạy lại `git add --renormalize .`. Hoặc renormalize từng phần:
   ```bash
   git add --renormalize src/
   git add --renormalize pom.xml README.md REFACTOR_PLAN.md TINH_HINH_CHUYEN_DOI.md dependency-reduced-pom.xml mvnw.cmd
   git add ".mvn/wrapper/maven-wrapper.properties"
   ```

5. **Kiểm tra:**
   ```bash
   git status
   ```
   Các file chỉ khác line ending sẽ nằm trong "Changes to be committed". Commit một lần (ví dụ: "Normalize line endings to LF") là xong.

Sau khi commit, `git status` sẽ không còn hàng loạt "modified" giả do CRLF/LF nữa.
