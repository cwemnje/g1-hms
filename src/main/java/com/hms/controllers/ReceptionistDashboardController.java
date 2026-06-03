package com.hms.controllers;

import com.hms.models.*;
import com.hms.services.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ReceptionistDashboardController implements DashboardController {

    @FXML private Label loggedInLabel;
    @FXML private Label userInfoLabel;
    @FXML private StackPane contentArea;

    private User currentUser;

    private final PatientService     patientService     = new PatientService();
    private final AppointmentService appointmentService = new AppointmentService();
    private final BillingService     billingService     = new BillingService();
    private final AdminService       adminService       = new AdminService();

    @Override
    public void initUser(User user) {
        this.currentUser = user;
        loggedInLabel.setText(user.getFirstName() + " " + user.getLastName());
        userInfoLabel.setText("👤 " + user.getFirstName() + " " + user.getLastName()
            + "\nID: " + user.getUserId()
            + "\nRole: Receptionist");
        showDashboard();
    }

    // ── DASHBOARD ─────────────────────────────────────────
    @FXML
    private void showDashboard() {
        VBox view = new VBox(24);
        view.setPadding(new Insets(32));

        Label title = sectionTitle("Receptionist Dashboard");

        List<Patient> allPatients      = patientService.getAllPatients();
        List<Appointment> todayAppts   = appointmentService.getTodaysAppointments();
        List<Invoice> unpaidInvoices   = billingService.getUnpaidInvoices();

        HBox cards = new HBox(16);
        cards.getChildren().addAll(
            statCard("Total Patients",      String.valueOf(allPatients.size()),    "#2563eb"),
            statCard("Today's Appointments",String.valueOf(todayAppts.size()),     "#0891b2"),
            statCard("Unpaid Invoices",     String.valueOf(unpaidInvoices.size()), "#dc2626")
        );

        // Today's appointments quick list
        Label apptTitle = boldLabel("Today's Appointments");
        VBox apptList = new VBox(6);
        if (todayAppts.isEmpty()) {
            apptList.getChildren().add(new Label("No appointments today."));
        } else {
            for (Appointment a : todayAppts) {
                Patient p = patientService.searchPatientById(a.getPatientId());
                String name = p != null ? p.getFirstName() + " " + p.getLastName() : a.getPatientId();
                Label l = new Label("• " + a.getDateTime().toLocalTime()
                    + " — " + name + " [" + a.getStatus() + "]");
                l.setStyle("-fx-font-size:13px;");
                apptList.getChildren().add(l);
            }
        }

        view.getChildren().addAll(title, cards, apptTitle, apptList);
        contentArea.getChildren().setAll(view);
    }

    // ── REGISTER PATIENT ──────────────────────────────────
    @FXML
    private void showRegisterPatient() {
        VBox view = new VBox(16);
        view.setPadding(new Insets(32));

        Label title = sectionTitle("Register New Patient");

        GridPane form = new GridPane();
        form.setHgap(16); form.setVgap(14);
        form.setPadding(new Insets(24));
        form.setStyle("-fx-background-color:white;-fx-background-radius:8;" +
            "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.08),10,0,0,2);");

        TextField firstNameField   = new TextField(); firstNameField.setPromptText("First Name");
        TextField lastNameField    = new TextField(); lastNameField.setPromptText("Last Name");
        DatePicker dobPicker       = new DatePicker(); dobPicker.setPromptText("Date of Birth");
        ComboBox<String> genderBox = new ComboBox<>();
        genderBox.getItems().addAll("Male", "Female", "Other");
        genderBox.setPromptText("Gender");
        TextField phoneField       = new TextField(); phoneField.setPromptText("Phone");
        TextField emailField       = new TextField(); emailField.setPromptText("Email");
        TextArea addressArea       = new TextArea(); addressArea.setPromptText("Address");
        addressArea.setPrefRowCount(2);
        TextField insuranceField   = new TextField(); insuranceField.setPromptText("Insurance Details");

        form.addRow(0, new Label("First Name:*"), firstNameField, new Label("Last Name:*"), lastNameField);
        form.addRow(1, new Label("Date of Birth:"), dobPicker,   new Label("Gender:"),      genderBox);
        form.addRow(2, new Label("Phone:"),        phoneField,   new Label("Email:"),        emailField);
        form.addRow(3, new Label("Address:"),      addressArea);
        form.addRow(4, new Label("Insurance:"),    insuranceField);

        Label formMsg = new Label();
        Button registerBtn = new Button("Register Patient");
        registerBtn.setStyle("-fx-background-color:#2563eb;-fx-text-fill:white;" +
            "-fx-background-radius:6;-fx-padding:10 28;-fx-font-size:14px;");

        registerBtn.setOnAction(e -> {
            if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty()) {
                formMsg.setText("First and last name are required.");
                formMsg.setStyle("-fx-text-fill:red;"); return;
            }

            Patient p = new Patient();
            p.setFirstName(firstNameField.getText().trim());
            p.setLastName(lastNameField.getText().trim());
            p.setDateOfBirth(dobPicker.getValue());
            p.setGender(genderBox.getValue());
            p.setPhone(phoneField.getText().trim());
            p.setEmail(emailField.getText().trim());
            p.setAddress(addressArea.getText().trim());
            p.setInsuranceDetails(insuranceField.getText().trim());
            p.setOutstandingBillAmount(BigDecimal.ZERO);
            p.setPortalAccess(false);

            boolean ok = patientService.registerPatient(p);
            if (ok) {
                formMsg.setText("Patient registered successfully. ID: " + p.getPatientId());
                formMsg.setStyle("-fx-text-fill:green;");
                firstNameField.clear(); lastNameField.clear();
                dobPicker.setValue(null); genderBox.setValue(null);
                phoneField.clear(); emailField.clear();
                addressArea.clear(); insuranceField.clear();
            } else {
                formMsg.setText("Registration failed.");
                formMsg.setStyle("-fx-text-fill:red;");
            }
        });

        view.getChildren().addAll(title, form, registerBtn, formMsg);
        contentArea.getChildren().setAll(new ScrollPane(view) {{ setFitToWidth(true); }});
    }

    // ── PATIENTS ──────────────────────────────────────────
    @FXML
    private void showPatients() {
        VBox view = new VBox(16);
        view.setPadding(new Insets(32));

        Label title = sectionTitle("Patient Records");

        HBox searchRow = new HBox(12);
        searchRow.setAlignment(Pos.CENTER_LEFT);
        TextField searchField = new TextField();
        searchField.setPromptText("Search by name or ID...");
        searchField.setPrefWidth(300);
        Button searchBtn = new Button("Search");
        searchBtn.setStyle("-fx-background-color:#2563eb;-fx-text-fill:white;" +
            "-fx-background-radius:6;-fx-padding:8 20;");
        searchRow.getChildren().addAll(searchField, searchBtn);

        TableView<Patient> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Patient, String> idCol     = new TableColumn<>("Patient ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        TableColumn<Patient, String> nameCol   = new TableColumn<>("Name");
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().getFirstName() + " " + c.getValue().getLastName()));
        TableColumn<Patient, String> dobCol    = new TableColumn<>("DOB");
        dobCol.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().getDateOfBirth() != null ?
            c.getValue().getDateOfBirth().toString() : "—"));
        TableColumn<Patient, String> genderCol = new TableColumn<>("Gender");
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
        TableColumn<Patient, String> phoneCol  = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        TableColumn<Patient, String> billCol   = new TableColumn<>("Outstanding");
        billCol.setCellValueFactory(c -> new SimpleStringProperty(
            "XAF " + c.getValue().getOutstandingBillAmount()));

        TableColumn<Patient, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> new TableCell<>() {
            final Button apptBtn    = new Button("Schedule Appt");
            final Button billingBtn = new Button("Billing");
            {
                apptBtn.setStyle("-fx-background-color:#2563eb;-fx-text-fill:white;-fx-background-radius:4;");
                billingBtn.setStyle("-fx-background-color:#d97706;-fx-text-fill:white;-fx-background-radius:4;");
                apptBtn.setOnAction(e -> showScheduleAppointment(
                    getTableView().getItems().get(getIndex()).getPatientId()));
                billingBtn.setOnAction(e -> showPatientBilling(
                    getTableView().getItems().get(getIndex()).getPatientId()));
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                setGraphic(new HBox(6, apptBtn, billingBtn));
            }
        });

        table.getColumns().addAll(idCol, nameCol, dobCol, genderCol, phoneCol, billCol, actionCol);
        table.setItems(FXCollections.observableArrayList(patientService.getAllPatients()));
        table.setPrefHeight(450);

        searchBtn.setOnAction(e -> {
            String term = searchField.getText().trim();
            if (term.isEmpty()) {
                table.setItems(FXCollections.observableArrayList(patientService.getAllPatients()));
            } else {
                List<Patient> results = patientService.searchPatientByName(term);
                Patient byId = patientService.searchPatientById(term.toUpperCase());
                if (byId != null && results.stream().noneMatch(p -> p.getPatientId().equals(byId.getPatientId())))
                    results.add(0, byId);
                table.setItems(FXCollections.observableArrayList(results));
            }
        });

        view.getChildren().addAll(title, searchRow, table);
        contentArea.getChildren().setAll(view);
    }

    // ── APPOINTMENTS ──────────────────────────────────────
    @FXML
    private void showAppointments() {
        VBox view = new VBox(16);
        view.setPadding(new Insets(32));

        Label title = sectionTitle("Appointments");

        // Today's appointments with check-in
        Label todayLabel = boldLabel("Today's Appointments");
        TableView<Appointment> todayTable = new TableView<>();
        todayTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Appointment, String> idCol     = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        TableColumn<Appointment, String> patCol    = new TableColumn<>("Patient");
        patCol.setCellValueFactory(c -> {
            Patient p = patientService.searchPatientById(c.getValue().getPatientId());
            return new SimpleStringProperty(p != null ?
                p.getFirstName() + " " + p.getLastName() : c.getValue().getPatientId());
        });
        TableColumn<Appointment, String> docCol    = new TableColumn<>("Doctor ID");
        docCol.setCellValueFactory(new PropertyValueFactory<>("doctorId"));
        TableColumn<Appointment, String> timeCol   = new TableColumn<>("Time");
        timeCol.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().getDateTime().toLocalTime().toString()));
        TableColumn<Appointment, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<Appointment, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> new TableCell<>() {
            final Button checkInBtn  = new Button("Check In");
            final Button cancelBtn   = new Button("Cancel");
            {
                checkInBtn.setStyle("-fx-background-color:#16a34a;-fx-text-fill:white;-fx-background-radius:4;");
                cancelBtn.setStyle("-fx-background-color:#dc2626;-fx-text-fill:white;-fx-background-radius:4;");
                checkInBtn.setOnAction(e -> {
                    appointmentService.checkInPatient(
                        getTableView().getItems().get(getIndex()).getAppointmentId());
                    showAppointments();
                });
                cancelBtn.setOnAction(e -> {
                    appointmentService.cancelAppointment(
                        getTableView().getItems().get(getIndex()).getAppointmentId());
                    showAppointments();
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                Appointment a = getTableView().getItems().get(getIndex());
                HBox box = new HBox(6);
                if (a.getStatus().equals("scheduled")) box.getChildren().addAll(checkInBtn, cancelBtn);
                setGraphic(box);
            }
        });

        todayTable.getColumns().addAll(idCol, patCol, docCol, timeCol, statusCol, actionCol);
        todayTable.setItems(FXCollections.observableArrayList(
            appointmentService.getTodaysAppointments()));
        todayTable.setPrefHeight(300);

        view.getChildren().addAll(title, todayLabel, todayTable);
        contentArea.getChildren().setAll(view);
    }

    // ── SCHEDULE APPOINTMENT ──────────────────────────────
    private void showScheduleAppointment(String patientId) {
        VBox view = new VBox(16);
        view.setPadding(new Insets(32));

        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        Button backBtn = new Button("← Back");
        backBtn.setStyle("-fx-background-color:#64748b;-fx-text-fill:white;" +
            "-fx-background-radius:6;-fx-padding:6 16;");
        backBtn.setOnAction(e -> showPatients());

        Patient patient = patientService.searchPatientById(patientId);
        String patName  = patient != null ?
            patient.getFirstName() + " " + patient.getLastName() : patientId;
        Label title = sectionTitle("Schedule Appointment — " + patName);
        header.getChildren().addAll(backBtn, title);

        GridPane form = new GridPane();
        form.setHgap(16); form.setVgap(14);
        form.setPadding(new Insets(24));
        form.setStyle("-fx-background-color:white;-fx-background-radius:8;" +
            "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.08),10,0,0,2);");

        // Doctor selector
        List<User> doctors = adminService.getUsersByRole(2);
        ComboBox<String> doctorBox = new ComboBox<>();
        doctors.forEach(d -> doctorBox.getItems().add(
            d.getUserId() + " — Dr. " + d.getFirstName() + " " + d.getLastName()));
        doctorBox.setPromptText("Select Doctor");

        DatePicker datePicker = new DatePicker(LocalDate.now().plusDays(1));
        TextField timeField   = new TextField(); timeField.setPromptText("Time (HH:mm:ss)");
        TextArea notesArea    = new TextArea(); notesArea.setPromptText("Notes"); notesArea.setPrefRowCount(2);

        form.addRow(0, new Label("Doctor:*"), doctorBox);
        form.addRow(1, new Label("Date:*"),   datePicker, new Label("Time:*"), timeField);
        form.addRow(2, new Label("Notes:"),   notesArea);

        Label msg = new Label();
        Button scheduleBtn = new Button("Schedule Appointment");
        scheduleBtn.setStyle("-fx-background-color:#2563eb;-fx-text-fill:white;" +
            "-fx-background-radius:6;-fx-padding:10 28;-fx-font-size:14px;");

        scheduleBtn.setOnAction(e -> {
            if (doctorBox.getValue() == null || timeField.getText().isEmpty()) {
                msg.setText("Doctor and time are required."); msg.setStyle("-fx-text-fill:red;"); return;
            }
            try {
                String doctorId = doctorBox.getValue().split(" — ")[0];
                LocalDateTime dateTime = LocalDateTime.of(
                    datePicker.getValue(),
                    java.time.LocalTime.parse(timeField.getText().trim()));

                Appointment appt = new Appointment();
                appt.setPatientId(patientId);
                appt.setDoctorId(doctorId);
                appt.setReceptionistId(currentUser.getUserId());
                appt.setDateTime(dateTime);
                appt.setNotes(notesArea.getText().trim());

                boolean ok = appointmentService.scheduleAppointment(appt);
                msg.setText(ok ? "Appointment scheduled: " + appt.getAppointmentId()
                               : "Failed — doctor may not be available at that time.");
                msg.setStyle(ok ? "-fx-text-fill:green;" : "-fx-text-fill:red;");
                if (ok) { doctorBox.setValue(null); timeField.clear(); notesArea.clear(); }
            } catch (Exception ex) {
                msg.setText("Invalid time format. Use HH:mm:ss");
                msg.setStyle("-fx-text-fill:red;");
            }
        });

        view.getChildren().addAll(header, form, scheduleBtn, msg);
        contentArea.getChildren().setAll(new ScrollPane(view) {{ setFitToWidth(true); }});
    }

    // ── BILLING ───────────────────────────────────────────
    @FXML
    private void showBilling() {
        VBox view = new VBox(16);
        view.setPadding(new Insets(32));

        Label title = sectionTitle("Billing");

        // Generate invoice form
        TitledPane genPane = new TitledPane("Generate New Invoice", null);
        genPane.setExpanded(true);
        VBox genBox = new VBox(12);
        genBox.setPadding(new Insets(16));

        TextField patIdField = new TextField(); patIdField.setPromptText("Patient ID");
        Label genMsg = new Label();
        Button genBtn = new Button("Generate Invoice");
        genBtn.setStyle("-fx-background-color:#2563eb;-fx-text-fill:white;" +
            "-fx-background-radius:6;-fx-padding:8 20;");
        genBtn.setOnAction(e -> {
            String pid = patIdField.getText().trim().toUpperCase();
            if (pid.isEmpty()) {
                genMsg.setText("Enter patient ID."); genMsg.setStyle("-fx-text-fill:red;"); return;
            }
            Invoice inv = new Invoice();
            inv.setPatientId(pid);
            inv.setReceptionistId(currentUser.getUserId());
            inv.setTotalAmount(BigDecimal.ZERO);
            inv.setInsuranceDeduction(BigDecimal.ZERO);
            inv.setAmountDue(BigDecimal.ZERO);
            boolean ok = billingService.generateInvoice(inv);
            genMsg.setText(ok ? "Invoice generated: " + inv.getInvoiceId() : "Failed.");
            genMsg.setStyle(ok ? "-fx-text-fill:green;" : "-fx-text-fill:red;");
            if (ok) { patIdField.clear(); showBilling(); }
        });
        genBox.getChildren().addAll(
            new Label("Patient ID:"), patIdField, genBtn, genMsg);
        genPane.setContent(genBox);

        // Add charge form
        TitledPane chargePane = new TitledPane("Add Charge to Invoice", null);
        chargePane.setExpanded(false);
        VBox chargeBox = new VBox(12);
        chargeBox.setPadding(new Insets(16));

        TextField invIdField   = new TextField(); invIdField.setPromptText("Invoice ID");
        TextField descField    = new TextField(); descField.setPromptText("Description");
        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("consultation","lab_test","medication","bed","theatre","other");
        typeBox.setPromptText("Charge Type");
        TextField amtField     = new TextField(); amtField.setPromptText("Amount (XAF)");
        Label chargeMsg = new Label();

        Button addChargeBtn = new Button("Add Charge");
        addChargeBtn.setStyle("-fx-background-color:#0891b2;-fx-text-fill:white;" +
            "-fx-background-radius:6;-fx-padding:8 20;");
        addChargeBtn.setOnAction(e -> {
            try {
                InvoiceItem item = new InvoiceItem();
                item.setInvoiceId(invIdField.getText().trim().toUpperCase());
                item.setDescription(descField.getText().trim());
                item.setChargeType(typeBox.getValue());
                item.setAmount(new BigDecimal(amtField.getText().trim()));
                boolean ok = billingService.addInvoiceItem(item);
                chargeMsg.setText(ok ? "Charge added." : "Failed.");
                chargeMsg.setStyle(ok ? "-fx-text-fill:green;" : "-fx-text-fill:red;");
                if (ok) { descField.clear(); amtField.clear(); }
            } catch (Exception ex) {
                chargeMsg.setText("Invalid amount."); chargeMsg.setStyle("-fx-text-fill:red;");
            }
        });

        // Insurance deduction
        HBox insRow = new HBox(12);
        insRow.setAlignment(Pos.CENTER_LEFT);
        TextField insInvField = new TextField(); insInvField.setPromptText("Invoice ID");
        TextField insAmtField = new TextField(); insAmtField.setPromptText("Deduction Amount");
        Label insMsg = new Label();
        Button insBtn = new Button("Apply Deduction");
        insBtn.setStyle("-fx-background-color:#7c3aed;-fx-text-fill:white;" +
            "-fx-background-radius:6;-fx-padding:8 20;");
        insBtn.setOnAction(e -> {
            try {
                boolean ok = billingService.applyInsuranceDeduction(
                    insInvField.getText().trim().toUpperCase(),
                    new BigDecimal(insAmtField.getText().trim()));
                insMsg.setText(ok ? "Deduction applied." : "Failed.");
                insMsg.setStyle(ok ? "-fx-text-fill:green;" : "-fx-text-fill:red;");
            } catch (Exception ex) {
                insMsg.setText("Invalid amount."); insMsg.setStyle("-fx-text-fill:red;");
            }
        });
        insRow.getChildren().addAll(
            new Label("Invoice ID:"), insInvField,
            new Label("Amount:"), insAmtField,
            insBtn, insMsg);

        chargeBox.getChildren().addAll(
            new Label("Invoice ID:"), invIdField,
            new Label("Description:"), descField,
            new Label("Type:"), typeBox,
            new Label("Amount:"), amtField,
            addChargeBtn, chargeMsg,
            new Separator(),
            new Label("Insurance Deduction:"), insRow);
        chargePane.setContent(chargeBox);

        // Unpaid invoices table
        Label unpaidLabel = boldLabel("Unpaid Invoices");
        TableView<Invoice> table = buildInvoiceTable();
        table.setItems(FXCollections.observableArrayList(billingService.getUnpaidInvoices()));
        table.setPrefHeight(300);

        view.getChildren().addAll(title, genPane, chargePane, unpaidLabel, table);
        ScrollPane scroll = new ScrollPane(view);
        scroll.setFitToWidth(true);
        contentArea.getChildren().setAll(scroll);
    }

    // ── PATIENT BILLING ───────────────────────────────────
    private void showPatientBilling(String patientId) {
        Patient patient = patientService.searchPatientById(patientId);
        if (patient == null) return;

        VBox view = new VBox(16);
        view.setPadding(new Insets(32));

        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        Button backBtn = new Button("← Back");
        backBtn.setStyle("-fx-background-color:#64748b;-fx-text-fill:white;" +
            "-fx-background-radius:6;-fx-padding:6 16;");
        backBtn.setOnAction(e -> showPatients());
        Label title = sectionTitle("Billing — " +
            patient.getFirstName() + " " + patient.getLastName());
        header.getChildren().addAll(backBtn, title);

        // Patient invoices
        List<Invoice> invoices = billingService.getInvoicesByPatient(patientId);
        Label invLabel = boldLabel("Invoices (" + invoices.size() + ")");

        TableView<Invoice> invTable = buildInvoiceTable();
        invTable.setItems(FXCollections.observableArrayList(invoices));
        invTable.setPrefHeight(250);

        // View invoice items
        Label itemsLabel = boldLabel("Invoice Items");
        TextField viewInvField = new TextField(); viewInvField.setPromptText("Enter Invoice ID to view items");
        Button viewItemsBtn = new Button("View Items");
        viewItemsBtn.setStyle("-fx-background-color:#2563eb;-fx-text-fill:white;" +
            "-fx-background-radius:6;-fx-padding:6 16;");

        TableView<InvoiceItem> itemsTable = new TableView<>();
        itemsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<InvoiceItem, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        TableColumn<InvoiceItem, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("chargeType"));
        TableColumn<InvoiceItem, String> amtCol  = new TableColumn<>("Amount");
        amtCol.setCellValueFactory(c -> new SimpleStringProperty(
            "XAF " + c.getValue().getAmount().toPlainString()));
        itemsTable.getColumns().addAll(descCol, typeCol, amtCol);
        itemsTable.setPrefHeight(200);

        viewItemsBtn.setOnAction(e -> {
            String invId = viewInvField.getText().trim().toUpperCase();
            itemsTable.setItems(FXCollections.observableArrayList(
                billingService.getInvoiceItems(invId)));
        });

        view.getChildren().addAll(header, invLabel, invTable,
            itemsLabel, new HBox(8, viewInvField, viewItemsBtn), itemsTable);
        contentArea.getChildren().setAll(new ScrollPane(view) {{ setFitToWidth(true); }});
    }

    // ── PAYMENTS ──────────────────────────────────────────
    @FXML
    private void showPayments() {
        VBox view = new VBox(16);
        view.setPadding(new Insets(32));

        Label title = sectionTitle("Process Payment");

        GridPane form = new GridPane();
        form.setHgap(16); form.setVgap(14);
        form.setPadding(new Insets(24));
        form.setStyle("-fx-background-color:white;-fx-background-radius:8;" +
            "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.08),10,0,0,2);");

        TextField invIdField   = new TextField(); invIdField.setPromptText("Invoice ID");
        TextField amtField     = new TextField(); amtField.setPromptText("Amount (XAF)");
        ComboBox<String> methodBox = new ComboBox<>();
        methodBox.getItems().addAll("cash","card","mobile_money","insurance","online");
        methodBox.setPromptText("Payment Method");

        // Invoice summary display
        Label invSummary = new Label();
        invSummary.setStyle("-fx-font-size:13px;-fx-text-fill:#374151;");
        Button lookupBtn = new Button("Look Up Invoice");
        lookupBtn.setStyle("-fx-background-color:#64748b;-fx-text-fill:white;" +
            "-fx-background-radius:6;-fx-padding:6 16;");
        lookupBtn.setOnAction(e -> {
            Invoice inv = billingService.getInvoiceById(
                invIdField.getText().trim().toUpperCase());
            if (inv == null) {
                invSummary.setText("Invoice not found.");
            } else {
                invSummary.setText("Patient: " + inv.getPatientId()
                    + " | Total: XAF " + inv.getTotalAmount().toPlainString()
                    + " | Due: XAF " + inv.getAmountDue().toPlainString()
                    + " | Status: " + inv.getStatus());
            }
        });

        form.addRow(0, new Label("Invoice ID:*"), invIdField, lookupBtn);
        form.addRow(1, invSummary);
        form.addRow(2, new Label("Amount:*"), amtField, new Label("Method:*"), methodBox);

        Label payMsg = new Label();
        Button payBtn = new Button("Process Payment");
        payBtn.setStyle("-fx-background-color:#16a34a;-fx-text-fill:white;" +
            "-fx-background-radius:6;-fx-padding:10 28;-fx-font-size:14px;");

        payBtn.setOnAction(e -> {
            if (invIdField.getText().isEmpty() || amtField.getText().isEmpty()
                || methodBox.getValue() == null) {
                payMsg.setText("Fill all fields."); payMsg.setStyle("-fx-text-fill:red;"); return;
            }
            try {
                Payment payment = new Payment();
                payment.setInvoiceId(invIdField.getText().trim().toUpperCase());
                payment.setAmountPaid(new BigDecimal(amtField.getText().trim()));
                payment.setPaymentMethod(methodBox.getValue());
                payment.setProcessedBy(currentUser.getUserId());

                boolean ok = billingService.processPayment(payment);
                payMsg.setText(ok ? "Payment processed successfully." : "Payment failed — invoice may be settled or not found.");
                payMsg.setStyle(ok ? "-fx-text-fill:green;" : "-fx-text-fill:red;");
                if (ok) {
                    invIdField.clear(); amtField.clear();
                    methodBox.setValue(null); invSummary.setText("");
                }
            } catch (Exception ex) {
                payMsg.setText("Invalid amount."); payMsg.setStyle("-fx-text-fill:red;");
            }
        });

        // Payment history
        Label histLabel = boldLabel("Recent Payments — by Invoice ID");
        HBox histRow = new HBox(12);
        histRow.setAlignment(Pos.CENTER_LEFT);
        TextField histInvField = new TextField(); histInvField.setPromptText("Invoice ID");
        Button histBtn = new Button("View Payments");
        histBtn.setStyle("-fx-background-color:#2563eb;-fx-text-fill:white;" +
            "-fx-background-radius:6;-fx-padding:6 16;");

        TableView<Payment> payTable = new TableView<>();
        payTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Payment, Integer> pidCol   = new TableColumn<>("Payment ID");
        pidCol.setCellValueFactory(new PropertyValueFactory<>("paymentId"));
        TableColumn<Payment, String> amtPCol   = new TableColumn<>("Amount Paid");
        amtPCol.setCellValueFactory(c -> new SimpleStringProperty(
            "XAF " + c.getValue().getAmountPaid().toPlainString()));
        TableColumn<Payment, String> methCol   = new TableColumn<>("Method");
        methCol.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        TableColumn<Payment, String> statCol   = new TableColumn<>("Status");
        statCol.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));
        TableColumn<Payment, String> paidAtCol = new TableColumn<>("Paid At");
        paidAtCol.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().getPaidAt() != null ?
            c.getValue().getPaidAt().toString().replace("T"," ") : "—"));
        payTable.getColumns().addAll(pidCol, amtPCol, methCol, statCol, paidAtCol);
        payTable.setPrefHeight(250);

        histBtn.setOnAction(e -> payTable.setItems(FXCollections.observableArrayList(
            billingService.getPaymentsByInvoice(histInvField.getText().trim().toUpperCase()))));
        histRow.getChildren().addAll(histInvField, histBtn);

        view.getChildren().addAll(title, form, payBtn, payMsg,
            new Separator(), histLabel, histRow, payTable);
        ScrollPane scroll = new ScrollPane(view);
        scroll.setFitToWidth(true);
        contentArea.getChildren().setAll(scroll);
    }

    // ── LOGOUT ────────────────────────────────────────────
    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/hms/fxml/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) contentArea.getScene().getWindow();
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(
                getClass().getResource("/com/hms/styles/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setMaximized(false);
            stage.setResizable(false);
            stage.setWidth(900);
            stage.setHeight(600);
            stage.setTitle("Hospital Management System");
            stage.show();
        } catch (Exception e) {
            System.err.println("Logout error: " + e.getMessage());
        }
    }

    // ── HELPERS ───────────────────────────────────────────

    private TableView<Invoice> buildInvoiceTable() {
        TableView<Invoice> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Invoice, String> idCol     = new TableColumn<>("Invoice ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("invoiceId"));
        TableColumn<Invoice, String> patCol    = new TableColumn<>("Patient ID");
        patCol.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        TableColumn<Invoice, String> totalCol  = new TableColumn<>("Total");
        totalCol.setCellValueFactory(c -> new SimpleStringProperty(
            "XAF " + c.getValue().getTotalAmount().toPlainString()));
        TableColumn<Invoice, String> dedCol    = new TableColumn<>("Deduction");
        dedCol.setCellValueFactory(c -> new SimpleStringProperty(
            "XAF " + c.getValue().getInsuranceDeduction().toPlainString()));
        TableColumn<Invoice, String> dueCol    = new TableColumn<>("Amount Due");
        dueCol.setCellValueFactory(c -> new SimpleStringProperty(
            "XAF " + c.getValue().getAmountDue().toPlainString()));
        TableColumn<Invoice, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().addAll(idCol, patCol, totalCol, dedCol, dueCol, statusCol);
        return table;
    }

    private VBox statCard(String label, String value, String color) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(180);
        card.setPrefHeight(100);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color:" + color + ";-fx-background-radius:10;");
        Label valLabel = new Label(value);
        valLabel.setStyle("-fx-font-size:28px;-fx-font-weight:bold;-fx-text-fill:white;");
        Label lblLabel = new Label(label);
        lblLabel.setStyle("-fx-font-size:13px;-fx-text-fill:rgba(255,255,255,0.85);");
        card.getChildren().addAll(valLabel, lblLabel);
        return card;
    }

    private Label sectionTitle(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size:20px;-fx-font-weight:bold;-fx-text-fill:#1e3a5f;");
        return l;
    }

    private Label boldLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-weight:bold;-fx-font-size:13px;");
        return l;
    }
}