# Kế hoạch refactor các module còn lại — Todo List

**Mục tiêu:** Chuyển 11 module + Settings từ gọi trực tiếp `Database` trong Panel sang kiến trúc **Repository + Service + Domain + Entity** (theo pattern Customer / Pet / Doctor).

**Tham chiếu:** `CUSTOMER_REFACTOR_TEST.md`, `CustomerRepository`, `CustomerService`, `model/domain/Customer`, `model/entity/CustomerEntity`.

---

## Thứ tự refactor (ưu tiên)

| # | Module | Phụ thuộc | Độ phức tạp |
|---|--------|------------|-------------|
| 1 | User | — | Thấp |
| 2 | Service Type | — | Thấp |
| 3 | Medicine | — | Thấp |
| 4 | Vaccine Type | — | Thấp |
| 5 | Settings | — | Thấp (1 bảng, ít trường) |
| 6 | Appointment | Customer, Pet, Doctor ✅ | Trung bình |
| 7 | Medical Record | Pet, Doctor ✅ | Trung bình |
| 8 | Vaccination | Pet, VaccineType (sau #4) | Trung bình |
| 9 | Treatment Course | Medical Record, Pet, Doctor ✅ | Cao (nhiều bảng/session) |
| 10 | Pet Enclosure | Pet ✅, Service Type (sau #2) | Trung bình |
| 11 | Invoice | Customer ✅, InvoiceDetail, ServiceType, ... | Cao (master + detail) |

---

## Todo list chi tiết theo module

### Phase 1 — Module đơn giản (standalone)

---

#### 1. User ✅ (đã xong)

- [x] **1.1** Tạo `model/entity/UserEntity.java` (map bảng `users`: id, username, password, fullname, avatar, role)
- [x] **1.2** Tạo `model/domain/User.java` (validation: username, fullname, role)
- [x] **1.3** Tạo `repository/IUserRepository.java`
- [x] **1.4** Tạo `repository/UserRepository.java`
- [x] **1.5** Tạo `service/UserService.java` (authenticate, createUser, updateUser, changePassword)
- [x] **1.6** Sửa `UserManagementPanel`: dùng `UserService`
- [x] **1.7** Sửa `AddEditUserDialog`: Domain `User` + `UserService`
- [x] **1.8** Sửa `ChangePasswordDialog`: `UserService.changePassword`
- [x] **1.9** LoginFrame/DashboardFrame dùng `com.petcare.model.domain.User`; xóa `model/User.java` cũ

---

#### 2. Service Type ✅ (đã xong)

- [x] **2.1** Tạo `model/entity/ServiceTypeEntity.java` (map bảng `service_types`)
- [x] **2.2** Tạo `model/domain/ServiceType.java` (validation tên, giá >= 0)
- [x] **2.3** Tạo `repository/IServiceTypeRepository.java` (+ findByNameStartsWith)
- [x] **2.4** Tạo `repository/ServiceTypeRepository.java`
- [x] **2.5** Tạo `service/ServiceTypeService.java` (getAllServiceTypes, getServiceTypeByName, getServiceTypeByNamePrefix)
- [x] **2.6** Sửa `ServiceTypeManagementPanel`: dùng `ServiceTypeService`
- [x] **2.7** Sửa `AddEditServiceTypeDialog`: Domain + Service
- [x] **2.8** AddEditAppointmentDialog, AddInvoiceDialog, CheckoutDialog dùng ServiceTypeService; xóa `model/ServiceType.java` cũ

---

#### 3. Medicine ✅ (đã xong)

- [x] **3.1** Tạo `model/entity/MedicineEntity.java` (map bảng `medicines`)
- [x] **3.2** Tạo `model/domain/Medicine.java` (validation + Route enum PO, IM, IV, SC)
- [x] **3.3** Tạo `repository/IMedicineRepository.java`
- [x] **3.4** Tạo `repository/MedicineRepository.java`
- [x] **3.5** Tạo `service/MedicineService.java`
- [x] **3.6** Sửa `MedicineManagementPanel`: dùng `MedicineService`
- [x] **3.7** Sửa `AddEditMedicineDialog`: Domain + Service
- [x] **3.8** Xóa `model/Medicine.java` cũ

---

#### 4. Vaccine Type ✅ (đã xong)

- [x] **4.1** Tạo `model/entity/VaccineTypeEntity.java` (map bảng `vaccines`)
- [x] **4.2** Tạo `model/domain/VaccineType.java` (validation)
- [x] **4.3** Tạo `repository/IVaccineTypeRepository.java`
- [x] **4.4** Tạo `repository/VaccineTypeRepository.java`
- [x] **4.5** Tạo `service/VaccineTypeService.java`
- [x] **4.6** Sửa `VaccineTypeManagementPanel`: dùng `VaccineTypeService`
- [x] **4.7** Sửa `AddEditVaccineTypeDialog`: Domain + Service
- [x] **4.8** Xóa `model/Vaccine.java` cũ

---

#### 5. Settings (General Setting) ✅ (đã xong)

- [x] **5.1** Tạo `model/entity/GeneralSettingEntity.java` (map bảng `general_settings`)
- [x] **5.2** Tạo `model/domain/GeneralSetting.java` (validate())
- [x] **5.3** Tạo `repository/IGeneralSettingRepository.java` (findFirst, exists, insert, update)
- [x] **5.4** Tạo `repository/GeneralSettingRepository.java`
- [x] **5.5** Tạo `service/GeneralSettingService.java` (getSettings, saveSettings, getCheckoutHour, getOvertimeFeePerHour)
- [x] **5.6** Sửa `SettingsManagementPanel`: dùng `GeneralSettingService`
- [x] **5.7** Sửa `CheckoutDialog.loadSettings()`: dùng `GeneralSettingService`

---

### Phase 2 — Module phụ thuộc Customer/Pet/Doctor

---

#### 6. Appointment ✅ (đã xong)

- [x] **6.1** Tạo `model/entity/AppointmentEntity.java` và `AppointmentListDto.java` (list có JOIN)
- [x] **6.2** Tạo `model/domain/Appointment.java` (enums AppointmentType, Status)
- [x] **6.3** Tạo `repository/IAppointmentRepository.java` (findAllForList, findById, insert, update, updateStatus, delete)
- [x] **6.4** Tạo `repository/AppointmentRepository.java`
- [x] **6.5** Tạo `service/AppointmentService.java`
- [x] **6.6** Sửa `AppointmentManagementPanel`: dùng `AppointmentService`
- [x] **6.7** Sửa `AddEditAppointmentDialog`: domain Appointment + AppointmentService; loadCustomers/Doctors/Pets dùng CustomerService, DoctorService, PetService
- [x] **6.8** Xóa `model/Appointment.java` cũ

---

#### 7. Medical Record ✅

- [x] **7.1** Tạo `model/entity/MedicalRecordEntity.java` (map bảng `medical_records`, FK pet, doctor)
- [x] **7.2** Tạo `model/domain/MedicalRecord.java` (validation)
- [x] **7.3** Tạo `repository/IMedicalRecordRepository.java`
- [x] **7.4** Tạo `repository/MedicalRecordRepository.java`
- [x] **7.5** Tạo `service/MedicalRecordService.java`
- [x] **7.6** Sửa `MedicalRecordManagementPanel`: dùng `MedicalRecordService`
- [x] **7.7** Sửa `AddEditMedicalRecordDialog`: dùng Domain + Service
- [x] **7.8** DashboardService dùng MedicalRecordService cho count; xóa `model/MedicalRecord.java`

---

#### 8. Vaccination (Pet Vaccination) ✅

- [x] **8.1** Tạo `model/entity/PetVaccinationEntity.java` (map bảng pet_vaccinations, FK vaccine, customer, pet, doctor)
- [x] **8.2** Tạo `model/domain/PetVaccination.java`
- [x] **8.3** Tạo `repository/IPetVaccinationRepository.java`
- [x] **8.4** Tạo `repository/PetVaccinationRepository.java`
- [x] **8.5** Tạo `service/PetVaccinationService.java`
- [x] **8.6** Sửa `VaccinationManagementPanel`: dùng `PetVaccinationService`
- [x] **8.7** Sửa `AddEditVaccinationDialog`: Domain + VaccineTypeService, CustomerService, PetService, DoctorService; xóa `model/PetVaccination.java`
- [ ] **8.7** Sửa `AddEditVaccinationDialog`: dùng Domain + Service
- [ ] **8.8** Kiểm tra: CRUD Vaccination

---

#### 9. Treatment Course ✅

- [x] **9.1** Tạo `model/entity/TreatmentCourseEntity.java`, `TreatmentCourseListDto`, `TreatmentCourseInfoDto`, `TreatmentSessionListDto`
- [x] **9.2** Tạo `model/domain/TreatmentCourse.java` (Status enum)
- [x] **9.3** Tạo `repository/ITreatmentCourseRepository.java` (findCourseInfoForSessions, findSessionsByCourseId)
- [x] **9.4** Tạo `repository/TreatmentCourseRepository.java`
- [x] **9.5** Tạo `service/TreatmentCourseService.java` (completeCourse, getCourseInfoForSessions, getSessionsByCourseId)
- [x] **9.6** Sửa `TreatmentManagementPanel`: dùng `TreatmentCourseService`
- [x] **9.7** Sửa `AddEditTreatmentDialog`: Domain + CustomerService, PetService, TreatmentCourseService; `TreatmentSessionsDialog`: TreatmentCourseService
- [x] **9.8** Xóa `model/TreatmentCourse.java`

---

#### 10. Pet Enclosure ✅

- [x] **10.1** Tạo `model/entity/PetEnclosureEntity.java`, `PetEnclosureListDto.java` (map pet_enclosures, FK customer, pet)
- [x] **10.2** Tạo `model/domain/PetEnclosure.java` (Status enum Check In / Check Out)
- [x] **10.3** Tạo `repository/IPetEnclosureRepository.java` (updateCheckOut, countThisMonth, countByDate, getCheckinCheckoutStats)
- [x] **10.4** Tạo `repository/PetEnclosureRepository.java`
- [x] **10.5** Tạo `service/PetEnclosureService.java` (updateCheckOut; DashboardService dùng cho count/stats)
- [x] **10.6** Sửa `PetEnclosureManagementPanel`: dùng `PetEnclosureService`
- [x] **10.7** Sửa `AddEditPetEnclosureDialog`: Domain + CustomerService, PetService, PetEnclosureService; `CheckoutDialog`: domain PetEnclosure, PetEnclosureService.updateCheckOut, CustomerService/PetService cho loadCustomerAndPetInfo
- [x] **10.8** Xóa `model/PetEnclosure.java`

---

#### 11. Invoice ✅

- [x] **11.1** Tạo `model/entity/InvoiceEntity.java`, `InvoiceDetailEntity.java`, `InvoiceListDto`, `InvoiceInfoDto`, `InvoiceDetailListDto`, `ServiceRevenueDto`
- [x] **11.2** Tạo `model/domain/Invoice.java`, `InvoiceDetailItem.java`
- [x] **11.3** Tạo `repository/IInvoiceRepository.java` (findAllForList, findInfoById, findDetailsByInvoiceId, insert, insertDetail, delete; sumTotalAmountThisYear, sumTotalAmountByMonth, getRevenueByServiceTypeThisYear)
- [x] **11.4** Tạo `repository/InvoiceRepository.java`
- [x] **11.5** Tạo `service/InvoiceService.java` (createInvoice với List<InvoiceDetailItem>; getMonthlyRevenueStats; DashboardService dùng cho revenue)
- [x] **11.6** Sửa `InvoiceManagementPanel`: dùng `InvoiceService`
- [x] **11.7** Sửa `AddInvoiceDialog`: CustomerService, PetService, InvoiceService.createInvoice; `InvoiceDetailsDialog`: InvoiceService.getInvoiceInfo, getInvoiceDetails; `CheckoutDialog`: InvoiceService.createInvoice; xóa `model/Invoice.java`
- [ ] **11.8** Kiểm tra: Tạo hóa đơn, xem chi tiết, xóa, thanh toán

---

## Checklist chung mỗi module

- [ ] Entity: map đúng bảng, tên cột; không business logic
- [ ] Domain: validation trong setter; message lỗi tiếng Việt
- [ ] Repository: interface + impl; PreparedStatement; try-with-resources; wrap SQLException → PetcareException
- [ ] Service: Singleton; Entity ↔ Domain; business rules; gọi Repository
- [ ] Panel: chỉ gọi Service, catch PetcareException, không SQL
- [ ] Dialog: nhận/trả Domain, gọi Service khi lưu
- [ ] Không còn `Database.executeQuery`/`executeUpdate` trong panel/dialog của module đó

---

## Ghi chú

- Làm xong Phase 1 (User, Service Type, Medicine, Vaccine Type, Settings) rồi làm Phase 2 để tránh phụ thuộc ngược.
- Mỗi module nên commit riêng sau khi test ổn.
- Nếu bảng DB khác tên/trường so với mô tả trên, chỉnh Entity/Domain cho khớp schema hiện tại.
