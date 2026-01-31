package com.petcare.gui.dialogs;

import com.formdev.flatlaf.FlatClientProperties;
import com.petcare.model.domain.MedicalRecord;
import com.petcare.model.domain.Medicine;
import com.petcare.model.domain.ServiceType;
import com.petcare.model.entity.MedicalRecordMedicineListDto;
import com.petcare.model.entity.MedicalRecordServiceItemListDto;
import com.petcare.service.CustomerService;
import com.petcare.service.DoctorService;
import com.petcare.service.MedicalRecordMedicineService;
import com.petcare.service.MedicalRecordService;
import com.petcare.service.MedicalRecordServiceItemService;
import com.petcare.service.MedicineService;
import com.petcare.service.PetService;
import com.petcare.service.ServiceTypeService;
import com.petcare.util.DatePickerHelper;
import com.petcare.util.EmojiFontHelper;
import com.petcare.util.GUIUtil;
import com.petcare.util.ThemeManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Dialog for adding/editing medical record
 */
public class AddEditMedicalRecordDialog extends JDialog {
    private JComboBox<String> customerCombo;
    private JComboBox<String> petCombo;
    private JComboBox<String> doctorCombo;
    private JComboBox<String> typeCombo;
    private JTextField visitDateField;
    private JTextArea summaryArea;
    private JTextArea detailsArea;
    private JButton saveButton;
    private JButton cancelButton;
    private boolean saved = false;
    private MedicalRecord record;
    private JTable medicineTable;
    private DefaultTableModel medicineTableModel;
    private JComboBox<String> medicineCombo;
    private JSpinner medicineQuantitySpinner;
    private JTextField medicineUnitPriceField;
    private JTable serviceTable;
    private DefaultTableModel serviceTableModel;
    private JComboBox<String> serviceCombo;
    private JSpinner serviceQuantitySpinner;
    private JTextField serviceUnitPriceField;

    public AddEditMedicalRecordDialog(JDialog parent, MedicalRecord record) {
        super(parent, true);
        this.record = record;
        initComponents();
        loadCustomers();
        loadDoctors();

        if (record != null) {
            loadRecordData();
            setTitle("Sửa hồ sơ khám bệnh");
        } else {
            setTitle("Thêm hồ sơ khám bệnh mới");
            visitDateField.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        }
    }

    private static final int LABEL_MIN_WIDTH = 165;

    private void initComponents() {
        setSize(640, 520);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        getContentPane().setBackground(ThemeManager.getContentBackground());

        // Form panel: GridBagLayout để cột nhãn đủ rộng, không bị cắt
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(ThemeManager.getContentBackground());
        GridBagConstraints cLabel = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 10, 12), 0, 0);
        GridBagConstraints cField = new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 10, 0), 0, 0);

        int row = 0;

        // Khách hàng
        JLabel lblCustomer = createLabel("Khách hàng *:");
        lblCustomer.setMinimumSize(new Dimension(LABEL_MIN_WIDTH, lblCustomer.getPreferredSize().height));
        cLabel.gridy = row;
        formPanel.add(lblCustomer, cLabel);
        customerCombo = new JComboBox<>();
        customerCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        customerCombo.setBackground(ThemeManager.getTextFieldBackground());
        customerCombo.setForeground(ThemeManager.getTextFieldForeground());
        customerCombo.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        customerCombo.addActionListener(e -> loadPetsByCustomer());
        cField.gridy = row++;
        formPanel.add(customerCombo, cField);

        // Thú cưng
        JLabel lblPet = createLabel("Thú cưng *:");
        lblPet.setMinimumSize(new Dimension(LABEL_MIN_WIDTH, lblPet.getPreferredSize().height));
        cLabel.gridy = row;
        formPanel.add(lblPet, cLabel);
        petCombo = new JComboBox<>();
        petCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        petCombo.setBackground(ThemeManager.getTextFieldBackground());
        petCombo.setForeground(ThemeManager.getTextFieldForeground());
        petCombo.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        cField.gridy = row++;
        formPanel.add(petCombo, cField);

        // Bác sĩ
        JLabel lblDoctor = createLabel("Bác sĩ *:");
        lblDoctor.setMinimumSize(new Dimension(LABEL_MIN_WIDTH, lblDoctor.getPreferredSize().height));
        cLabel.gridy = row;
        formPanel.add(lblDoctor, cLabel);
        doctorCombo = new JComboBox<>();
        doctorCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        doctorCombo.setBackground(ThemeManager.getTextFieldBackground());
        doctorCombo.setForeground(ThemeManager.getTextFieldForeground());
        doctorCombo.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        cField.gridy = row++;
        formPanel.add(doctorCombo, cField);

        // Loại
        JLabel lblType = createLabel("Loại *:");
        lblType.setMinimumSize(new Dimension(LABEL_MIN_WIDTH, lblType.getPreferredSize().height));
        cLabel.gridy = row;
        formPanel.add(lblType, cLabel);
        typeCombo = new JComboBox<>();
        typeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        typeCombo.setBackground(ThemeManager.getTextFieldBackground());
        typeCombo.setForeground(ThemeManager.getTextFieldForeground());
        typeCombo.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        typeCombo.addItem("Khám");
        typeCombo.addItem("Điều trị");
        typeCombo.addItem("Vaccine");
        cField.gridy = row++;
        formPanel.add(typeCombo, cField);

        // Ngày khám
        JLabel lblDate = createLabel("Ngày khám * (dd/MM/yyyy):");
        lblDate.setMinimumSize(new Dimension(LABEL_MIN_WIDTH, lblDate.getPreferredSize().height));
        cLabel.gridy = row;
        formPanel.add(lblDate, cLabel);
        visitDateField = createTextField();
        visitDateField.putClientProperty("JTextField.placeholderText", "dd/MM/yyyy");
        JPanel visitDatePanel = DatePickerHelper.wrapDateField(this, visitDateField);
        cField.gridy = row++;
        formPanel.add(visitDatePanel, cField);

        // Tóm tắt
        JLabel lblSummary = createLabel("Tóm tắt:");
        lblSummary.setMinimumSize(new Dimension(LABEL_MIN_WIDTH, lblSummary.getPreferredSize().height));
        cLabel.gridy = row;
        formPanel.add(lblSummary, cLabel);
        summaryArea = new JTextArea(2, 28);
        summaryArea.setLineWrap(true);
        summaryArea.setWrapStyleWord(true);
        summaryArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        summaryArea.setBackground(ThemeManager.getTextFieldBackground());
        summaryArea.setForeground(ThemeManager.getTextFieldForeground());
        summaryArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.getBorderColor()),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        summaryArea.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        cField.gridy = row++;
        formPanel.add(summaryArea, cField);

        // Chi tiết
        JLabel lblDetails = createLabel("Chi tiết:");
        lblDetails.setMinimumSize(new Dimension(LABEL_MIN_WIDTH, lblDetails.getPreferredSize().height));
        cLabel.gridy = row;
        formPanel.add(lblDetails, cLabel);
        detailsArea = new JTextArea(2, 28);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        detailsArea.setBackground(ThemeManager.getTextFieldBackground());
        detailsArea.setForeground(ThemeManager.getTextFieldForeground());
        detailsArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.getBorderColor()),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        detailsArea.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        cField.gridy = row;
        formPanel.add(detailsArea, cField);

        JPanel centerContent = new JPanel(new BorderLayout());
        centerContent.setBackground(ThemeManager.getContentBackground());
        centerContent.add(formPanel, BorderLayout.NORTH);

        if (record != null) {
            JPanel servicesAndMedicines = new JPanel(new BorderLayout(0, 10));
            servicesAndMedicines.setBackground(ThemeManager.getContentBackground());
            JPanel servicePanel = createServicePanel();
            servicesAndMedicines.add(servicePanel, BorderLayout.NORTH);
            JPanel medicinePanel = createMedicinePanel();
            servicesAndMedicines.add(medicinePanel, BorderLayout.CENTER);
            centerContent.add(servicesAndMedicines, BorderLayout.CENTER);
        }
        JScrollPane scrollPane = new JScrollPane(centerContent);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(ThemeManager.getContentBackground());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        buttonPanel.setBackground(ThemeManager.getContentBackground());

        saveButton = new JButton("Lưu");
        saveButton.setIcon(EmojiFontHelper.createEmojiIcon("💾", Color.WHITE));
        saveButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        saveButton.setBackground(new Color(139, 69, 19));
        saveButton.setForeground(Color.WHITE);
        saveButton.setBorderPainted(false);
        saveButton.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        saveButton.addActionListener(e -> saveRecord());
        buttonPanel.add(saveButton);

        cancelButton = new JButton(EmojiFontHelper.withEmoji("❌", "Hủy"));
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cancelButton.setBackground(ThemeManager.getButtonBackground());
        cancelButton.setForeground(ThemeManager.getButtonForeground());
        cancelButton.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(ThemeManager.getTitleForeground());
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField(GUIUtil.TEXT_FIELD_COLUMNS);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBackground(ThemeManager.getTextFieldBackground());
        field.setForeground(ThemeManager.getTextFieldForeground());
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.getBorderColor()),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        return field;
    }

    private JPanel createServicePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Dịch vụ đã thêm"));
        panel.setBackground(ThemeManager.getContentBackground());

        String[] serviceColumns = {"ID", "Dịch vụ", "Số lượng", "Đơn giá", "Thành tiền"};
        serviceTableModel = new DefaultTableModel(serviceColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        serviceTable = new JTable(serviceTableModel);
        serviceTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        serviceTable.setRowHeight(25);
        serviceTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        serviceTable.getColumnModel().getColumn(0).setMinWidth(0);
        serviceTable.getColumnModel().getColumn(0).setMaxWidth(0);
        ThemeManager.applyTableTheme(serviceTable);

        JPanel addRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addRow.setBackground(ThemeManager.getContentBackground());
        serviceCombo = new JComboBox<>();
        serviceCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        serviceCombo.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        serviceCombo.setPreferredSize(new Dimension(220, 28));
        List<ServiceType> serviceTypesList = new ArrayList<>();
        try {
            serviceCombo.addItem("-- Chọn dịch vụ --");
            for (ServiceType st : ServiceTypeService.getInstance().getAllServiceTypes()) {
                serviceTypesList.add(st);
                serviceCombo.addItem(st.getServiceTypeId() + " - " + st.getServiceName());
            }
            serviceCombo.addActionListener(e -> {
                if (serviceCombo.getSelectedIndex() > 0) {
                    String sel = (String) serviceCombo.getSelectedItem();
                    if (sel != null && sel.contains(" - ")) {
                        int id = Integer.parseInt(sel.split(" - ")[0]);
                        for (ServiceType st : serviceTypesList) {
                            if (st.getServiceTypeId() == id) {
                                serviceUnitPriceField.setText(String.valueOf((int) st.getPrice()));
                                break;
                            }
                        }
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        serviceQuantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        serviceQuantitySpinner.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        serviceUnitPriceField = new JTextField("0", 8);
        serviceUnitPriceField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        addRow.add(new JLabel("Dịch vụ:"));
        addRow.add(serviceCombo);
        addRow.add(new JLabel("SL:"));
        addRow.add(serviceQuantitySpinner);
        addRow.add(new JLabel("Đơn giá:"));
        addRow.add(serviceUnitPriceField);
        JButton addSvcBtn = new JButton("Thêm dịch vụ");
        addSvcBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        addSvcBtn.addActionListener(e -> addServiceRow());
        addRow.add(addSvcBtn);

        JButton removeSvcBtn = new JButton("Xóa dịch vụ đã chọn");
        removeSvcBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        removeSvcBtn.addActionListener(e -> {
            int row = serviceTable.getSelectedRow();
            if (row >= 0) {
                Object idObj = serviceTableModel.getValueAt(row, 0);
                if (idObj instanceof Integer) {
                    try {
                        MedicalRecordServiceItemService.getInstance().removeService((Integer) idObj);
                        loadServiceRows();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng dịch vụ cần xóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        });

        panel.add(addRow, BorderLayout.NORTH);
        panel.add(new JScrollPane(serviceTable), BorderLayout.CENTER);
        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT));
        south.setBackground(ThemeManager.getContentBackground());
        south.add(removeSvcBtn);
        panel.add(south, BorderLayout.SOUTH);

        loadServiceRows();
        return panel;
    }

    private void loadServiceRows() {
        if (serviceTableModel == null || record == null) return;
        serviceTableModel.setRowCount(0);
        try {
            List<MedicalRecordServiceItemListDto> list = MedicalRecordServiceItemService.getInstance().getServicesByMedicalRecordId(record.getMedicalRecordId());
            for (MedicalRecordServiceItemListDto dto : list) {
                serviceTableModel.addRow(new Object[]{
                        dto.getRecordServiceId(),
                        dto.getServiceName(),
                        dto.getQuantity(),
                        dto.getUnitPrice(),
                        dto.getTotalPrice()
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void addServiceRow() {
        if (serviceCombo.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dịch vụ!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String sel = (String) serviceCombo.getSelectedItem();
        int serviceTypeId = Integer.parseInt(sel.split(" - ")[0]);
        int quantity = (Integer) serviceQuantitySpinner.getValue();
        int unitPrice;
        try {
            unitPrice = Integer.parseInt(serviceUnitPriceField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Đơn giá không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            MedicalRecordServiceItemService.getInstance().addService(record.getMedicalRecordId(), serviceTypeId, quantity, unitPrice);
            loadServiceRows();
            serviceCombo.setSelectedIndex(0);
            serviceQuantitySpinner.setValue(1);
            serviceUnitPriceField.setText("0");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createMedicinePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Thuốc đã kê"));
        panel.setBackground(ThemeManager.getContentBackground());

        String[] columns = {"ID", "Thuốc", "Số lượng", "Đơn giá", "Thành tiền"};
        medicineTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        medicineTable = new JTable(medicineTableModel);
        medicineTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        medicineTable.setRowHeight(25);
        medicineTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        medicineTable.getColumnModel().getColumn(0).setMinWidth(0);
        medicineTable.getColumnModel().getColumn(0).setMaxWidth(0);
        ThemeManager.applyTableTheme(medicineTable);

        JPanel addRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addRow.setBackground(ThemeManager.getContentBackground());
        medicineCombo = new JComboBox<>();
        medicineCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        medicineCombo.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        medicineCombo.setPreferredSize(new Dimension(220, 28));
        List<Medicine> medicinesList = new ArrayList<>();
        try {
            medicineCombo.addItem("-- Chọn thuốc --");
            for (Medicine m : MedicineService.getInstance().getAllMedicines()) {
                medicinesList.add(m);
                medicineCombo.addItem(m.getMedicineId() + " - " + m.getMedicineName());
            }
            medicineCombo.addActionListener(e -> {
                if (medicineCombo.getSelectedIndex() > 0) {
                    String sel = (String) medicineCombo.getSelectedItem();
                    if (sel != null && sel.contains(" - ")) {
                        int id = Integer.parseInt(sel.split(" - ")[0]);
                        for (Medicine m : medicinesList) {
                            if (m.getMedicineId() == id) {
                                medicineUnitPriceField.setText(String.valueOf(m.getUnitPrice()));
                                break;
                            }
                        }
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        medicineQuantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        medicineQuantitySpinner.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        medicineUnitPriceField = new JTextField("0", 8);
        medicineUnitPriceField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        addRow.add(new JLabel("Thuốc:"));
        addRow.add(medicineCombo);
        addRow.add(new JLabel("SL:"));
        addRow.add(medicineQuantitySpinner);
        addRow.add(new JLabel("Đơn giá:"));
        addRow.add(medicineUnitPriceField);
        JButton addMedBtn = new JButton("Thêm thuốc");
        addMedBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        addMedBtn.addActionListener(e -> addMedicineRow());
        addRow.add(addMedBtn);

        JButton removeMedBtn = new JButton("Xóa thuốc đã chọn");
        removeMedBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        removeMedBtn.addActionListener(e -> {
            int row = medicineTable.getSelectedRow();
            if (row >= 0) {
                Object idObj = medicineTableModel.getValueAt(row, 0);
                if (idObj instanceof Integer) {
                    try {
                        MedicalRecordMedicineService.getInstance().removeMedicine((Integer) idObj);
                        loadMedicineRows();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng thuốc cần xóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        });

        panel.add(addRow, BorderLayout.NORTH);
        panel.add(new JScrollPane(medicineTable), BorderLayout.CENTER);
        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT));
        south.setBackground(ThemeManager.getContentBackground());
        south.add(removeMedBtn);
        panel.add(south, BorderLayout.SOUTH);

        loadMedicineRows();
        return panel;
    }

    private void loadMedicineRows() {
        if (medicineTableModel == null || record == null) return;
        medicineTableModel.setRowCount(0);
        try {
            List<MedicalRecordMedicineListDto> list = MedicalRecordMedicineService.getInstance().getMedicinesByMedicalRecordId(record.getMedicalRecordId());
            for (MedicalRecordMedicineListDto dto : list) {
                medicineTableModel.addRow(new Object[]{
                        dto.getRecordMedicineId(),
                        dto.getMedicineName(),
                        dto.getQuantity(),
                        dto.getUnitPrice(),
                        dto.getTotalPrice()
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void addMedicineRow() {
        if (medicineCombo.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thuốc!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String sel = (String) medicineCombo.getSelectedItem();
        int medicineId = Integer.parseInt(sel.split(" - ")[0]);
        int quantity = (Integer) medicineQuantitySpinner.getValue();
        int unitPrice;
        try {
            unitPrice = Integer.parseInt(medicineUnitPriceField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Đơn giá không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            MedicalRecordMedicineService.getInstance().addMedicine(record.getMedicalRecordId(), medicineId, quantity, unitPrice);
            loadMedicineRows();
            medicineCombo.setSelectedIndex(0);
            medicineQuantitySpinner.setValue(1);
            medicineUnitPriceField.setText("0");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadCustomers() {
        customerCombo.removeAllItems();
        customerCombo.addItem("-- Chọn khách hàng --");
        try {
            CustomerService.getInstance().getAllCustomers().forEach(c -> {
                customerCombo.addItem(c.getCustomerId() + " - " + c.getCustomerName());
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadPetsByCustomer() {
        petCombo.removeAllItems();
        petCombo.addItem("-- Chọn thú cưng --");
        if (customerCombo.getSelectedIndex() == 0) return;
        try {
            String selected = (String) customerCombo.getSelectedItem();
            int customerId = Integer.parseInt(selected.split(" - ")[0]);
            PetService.getInstance().getPetsByCustomerId(customerId).forEach(p -> {
                petCombo.addItem(p.getPetId() + " - " + p.getPetName());
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadDoctors() {
        doctorCombo.removeAllItems();
        doctorCombo.addItem("-- Chọn bác sĩ --");
        try {
            DoctorService.getInstance().getAllDoctors().forEach(d -> {
                doctorCombo.addItem(d.getDoctorId() + " - " + d.getDoctorName());
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadRecordData() {
        if (record != null) {
            // Set customer
            for (int i = 0; i < customerCombo.getItemCount(); i++) {
                String item = customerCombo.getItemAt(i);
                if (item.startsWith(String.valueOf(record.getCustomerId()))) {
                    customerCombo.setSelectedIndex(i);
                    loadPetsByCustomer();
                    break;
                }
            }

            // Set pet
            for (int i = 0; i < petCombo.getItemCount(); i++) {
                String item = petCombo.getItemAt(i);
                if (item.startsWith(String.valueOf(record.getPetId()))) {
                    petCombo.setSelectedIndex(i);
                    break;
                }
            }

            // Set doctor
            for (int i = 0; i < doctorCombo.getItemCount(); i++) {
                String item = doctorCombo.getItemAt(i);
                if (item.startsWith(String.valueOf(record.getDoctorId()))) {
                    doctorCombo.setSelectedIndex(i);
                    break;
                }
            }

            // Set type
            if (record.getMedicalRecordType() != null) {
                typeCombo.setSelectedItem(record.getMedicalRecordType().getLabel());
            }

            // Set date
            if (record.getMedicalRecordVisitDate() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                visitDateField.setText(sdf.format(record.getMedicalRecordVisitDate()));
            }

            summaryArea.setText(record.getMedicalRecordSummary() != null ? record.getMedicalRecordSummary() : "");
            detailsArea.setText(record.getMedicalRecordDetails() != null ? record.getMedicalRecordDetails() : "");
        }
    }

    private void saveRecord() {
        // Validation
        if (customerCombo.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            customerCombo.requestFocus();
            return;
        }

        if (petCombo.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thú cưng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            petCombo.requestFocus();
            return;
        }

        if (doctorCombo.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bác sĩ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            doctorCombo.requestFocus();
            return;
        }

        if (typeCombo.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn loại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            typeCombo.requestFocus();
            return;
        }

        if (visitDateField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập ngày khám!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            visitDateField.requestFocus();
            return;
        }

        try {
            // Get IDs
            String customerSelected = (String) customerCombo.getSelectedItem();
            int customerId = Integer.parseInt(customerSelected.split(" - ")[0]);

            String petSelected = (String) petCombo.getSelectedItem();
            int petId = Integer.parseInt(petSelected.split(" - ")[0]);

            String doctorSelected = (String) doctorCombo.getSelectedItem();
            int doctorId = Integer.parseInt(doctorSelected.split(" - ")[0]);

            String typeStr = (String) typeCombo.getSelectedItem();

            // Parse date
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date visitDate;
            try {
                visitDate = sdf.parse(visitDateField.getText().trim());
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(this, "Ngày khám không đúng định dạng (dd/MM/yyyy)!",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                visitDateField.requestFocus();
                return;
            }

            MedicalRecordService service = MedicalRecordService.getInstance();
            if (record == null) {
                MedicalRecord newRecord = new MedicalRecord();
                newRecord.setCustomerId(customerId);
                newRecord.setPetId(petId);
                newRecord.setDoctorId(doctorId);
                newRecord.setMedicalRecordType(MedicalRecord.RecordType.fromLabel(typeStr));
                newRecord.setMedicalRecordVisitDate(visitDate);
                newRecord.setMedicalRecordSummary(summaryArea.getText().trim().isEmpty() ? null : summaryArea.getText().trim());
                newRecord.setMedicalRecordDetails(detailsArea.getText().trim().isEmpty() ? null : detailsArea.getText().trim());
                service.createRecord(newRecord);
                JOptionPane.showMessageDialog(this, "Thêm hồ sơ khám bệnh thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                saved = true;
                dispose();
            } else {
                record.setCustomerId(customerId);
                record.setPetId(petId);
                record.setDoctorId(doctorId);
                record.setMedicalRecordType(MedicalRecord.RecordType.fromLabel(typeStr));
                record.setMedicalRecordVisitDate(visitDate);
                record.setMedicalRecordSummary(summaryArea.getText().trim().isEmpty() ? null : summaryArea.getText().trim());
                record.setMedicalRecordDetails(detailsArea.getText().trim().isEmpty() ? null : detailsArea.getText().trim());
                service.updateRecord(record);
                JOptionPane.showMessageDialog(this, "Cập nhật hồ sơ khám bệnh thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                saved = true;
                dispose();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
