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

import java.time.LocalDate;
import java.util.List;

public class DoctorDashboardController implements DashboardController {

    @FXML private Label loggedInLabel;
    @FXML private Label userInfoLabel;
    @FXML private StackPane contentArea;

    private User currentUser;

    private final AppointmentService    appointmentService    = new AppointmentService();
    private final MedicalRecordService  medicalRecordService  = new MedicalRecordService();
    private final LabService            labService            = new LabService();
    private final PrescriptionService   prescriptionService   = new PrescriptionService();
    private final ReferralService       referralService       = new ReferralService();
    private final ScheduleService       scheduleService       = new ScheduleService();
    private final PatientService        patientService        = new PatientService();

    @Override
    public void initUser(User user) {
        this.currentUser = user;
        loggedInLabel.setText("Dr. " + user.getFirstName() + " " + user.getLastName());
        userInfoLabel.setText("👤 Dr. " + user.getFirstName() + " " + user.getLastName()
            + "\nID: " + user.getUserId()
            + "\nRole: Doctor");
        showDashboard();
    }

    // ── DASHBOARD ─────────────────────────────────────────
    @FXML
    private void showDashboard() {
        VBox view = new VBox(24);
        view.setPadding(new Insets(32));

        Label title = sectionTitle("Doctor Dashboard");

        List<Appointment> todayAppts = appointmentService.getTodaysAppointments()
            .stream().filter(a -> a.getDoctorId().equals(currentUser.getUserId())).toList();
        List<LabOrder> pendingLabs = labService.getPendingLabOrders()
            .stream().filter(l -> l.getDoctorId().equals(currentUser.getUserId())).toList();
        List<Prescription> pendingRx = prescriptionService.getPendingPrescriptions()
            .stream().filter(p -> p.getDoctorId().equals(currentUser.getUserId())).toList();

        HBox cards = new HBox(16);
        cards.getChildren().addAll(
            statCard("Today's Appointments", String.valueOf(todayAppts.size()), "#2563eb"),
            statCard("Pending Lab Orders",   String.valueOf(pendingLabs.size()), "#0891b2"),
            statCard("Pending Prescriptions",String.valueOf(pendingRx.size()),   "#d97706")
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
                Label l = new Label("• " + a.getDateTime().toLocalTime() + " — " + name
                    + " [" + a.getStatus() + "]");
                l.setStyle("-fx-font-size: 13px;");
                apptList.getChildren().add(l);
            }
        }

        view.getChildren().addAll(title, cards, apptTitle, apptList);
        contentArea.getChildren().setAll(view);
    }

    // ── APPOINTMENTS ──────────────────────────────────────
    @FXML
    private void showAppointments() {
        VBox view = new VBox(16);
        view.setPadding(new Insets(32));

        Label title = sectionTitle("My Appointments");

        TableView<Appointment> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Appointment, String> idCol     = new TableColumn<>("Appt ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));

        TableColumn<Appointment, String> patCol    = new TableColumn<>("Patient");
        patCol.setCellValueFactory(c -> {
            Patient p = patientService.searchPatientById(c.getValue().getPatientId());
            return new SimpleStringProperty(p != null ?
                p.getFirstName() + " " + p.getLastName() : c.getValue().getPatientId());
        });

        TableColumn<Appointment, String> dateCol   = new TableColumn<>("Date & Time");
        dateCol.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().getDateTime().toString().replace("T", " ")));

        TableColumn<Appointment, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<Appointment, String> notesCol  = new TableColumn<>("Notes");
        notesCol.setCellValueFactory(new PropertyValueFactory<>("notes"));

        TableColumn<Appointment, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> new TableCell<>() {
            final Button completeBtn  = new Button("Complete");
            final Button recordBtn    = new Button("View Record");
            {
                completeBtn.setStyle("-fx-background-color:#16a34a;-fx-text-fill:white;-fx-background-radius:4;");
                recordBtn.setStyle("-fx-background-color:#2563eb;-fx-text-fill:white;-fx-background-radius:4;");
                completeBtn.setOnAction(e -> {
                    appointmentService.completeAppointment(
                        getTableView().getItems().get(getIndex()).getAppointmentId());
                    showAppointments();
                });
                recordBtn.setOnAction(e -> {
                    String patId = getTableView().getItems().get(getIndex()).getPatientId();
                    showPatientRecord(patId);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                Appointment a = getTableView().getItems().get(getIndex());
                HBox box = new HBox(6);
                if (!a.getStatus().equals("completed") && !a.getStatus().equals("cancelled"))
                    box.getChildren().add(completeBtn);
                box.getChildren().add(recordBtn);
                setGraphic(box);
            }
        });

        table.getColumns().addAll(idCol, patCol, dateCol, statusCol, notesCol, actionCol);
        table.setItems(FXCollections.observableArrayList(
            appointmentService.getAppointmentsByDoctor(currentUser.getUserId())));
        table.setPrefHeight(500);

        view.getChildren().addAll(title, table);
        contentArea.getChildren().setAll(view);
    }

    // ── PATIENTS ──────────────────────────────────────────
    @FXML
    private void showPatients() {
        VBox view = new VBox(16);
        view.setPadding(new Insets(32));

        Label title = sectionTitle("Patient Search");

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

        TableColumn<Patient, String> idCol    = new TableColumn<>("Patient ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        TableColumn<Patient, String> nameCol  = new TableColumn<>("Name");
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().getFirstName() + " " + c.getValue().getLastName()));
        TableColumn<Patient, String> dobCol   = new TableColumn<>("DOB");
        dobCol.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().getDateOfBirth() != null ?
            c.getValue().getDateOfBirth().toString() : "—"));
        TableColumn<Patient, String> genderCol = new TableColumn<>("Gender");
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
        TableColumn<Patient, String> phoneCol  = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));

        TableColumn<Patient, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> new TableCell<>() {
            final Button viewBtn = new Button("View Record");
            {
                viewBtn.setStyle("-fx-background-color:#2563eb;-fx-text-fill:white;-fx-background-radius:4;");
                viewBtn.setOnAction(e -> showPatientRecord(
                    getTableView().getItems().get(getIndex()).getPatientId()));
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : viewBtn);
            }
        });

        table.getColumns().addAll(idCol, nameCol, dobCol, genderCol, phoneCol, actionCol);
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

    // ── PATIENT RECORD ────────────────────────────────────
    private void showPatientRecord(String patientId) {
        Patient patient = patientService.searchPatientById(patientId);
        if (patient == null) return;

        MedicalRecord record = medicalRecordService.getRecordByPatientId(patientId);

        VBox view = new VBox(16);
        view.setPadding(new Insets(32));

        // Header
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        Button backBtn = new Button("← Back");
        backBtn.setStyle("-fx-background-color:#64748b;-fx-text-fill:white;" +
            "-fx-background-radius:6;-fx-padding:6 16;");
        backBtn.setOnAction(e -> showPatients());
        Label title = sectionTitle("Medical Record — " +
            patient.getFirstName() + " " + patient.getLastName());
        header.getChildren().addAll(backBtn, title);

        // Patient info card
        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(20); infoGrid.setVgap(8);
        infoGrid.setPadding(new Insets(16));
        infoGrid.setStyle("-fx-background-color:white;-fx-background-radius:8;" +
            "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.08),10,0,0,2);");
        infoGrid.addRow(0, boldLabel("Patient ID:"),   new Label(patient.getPatientId()),
                           boldLabel("Gender:"),        new Label(patient.getGender() != null ? patient.getGender() : "—"));
        infoGrid.addRow(1, boldLabel("Date of Birth:"), new Label(patient.getDateOfBirth() != null ? patient.getDateOfBirth().toString() : "—"),
                           boldLabel("Phone:"),         new Label(patient.getPhone() != null ? patient.getPhone() : "—"));
        infoGrid.addRow(2, boldLabel("Insurance:"),    new Label(patient.getInsuranceDetails() != null ? patient.getInsuranceDetails() : "—"),
                           boldLabel("Address:"),       new Label(patient.getAddress() != null ? patient.getAddress() : "—"));

        // If no medical record exists, offer to create one
        if (record == null) {
            Label noRecord = new Label("No medical record found for this patient.");
            noRecord.setStyle("-fx-text-fill:#dc2626;-fx-font-size:13px;");
            Button createBtn = new Button("Create Medical Record");
            createBtn.setStyle("-fx-background-color:#2563eb;-fx-text-fill:white;" +
                "-fx-background-radius:6;-fx-padding:8 20;");
            createBtn.setOnAction(e -> {
                MedicalRecord newRecord = new MedicalRecord();
                newRecord.setPatientId(patientId);
                newRecord.setDiagnosis("");
                newRecord.setTreatmentHistory("");
                medicalRecordService.createMedicalRecord(newRecord);
                showPatientRecord(patientId);
            });
            view.getChildren().addAll(header, infoGrid, noRecord, createBtn);
            contentArea.getChildren().setAll(new ScrollPane(view) {{ setFitToWidth(true); }});
            return;
        }

        // Update diagnosis/treatment
        TitledPane diagPane = new TitledPane();
        diagPane.setText("Diagnosis & Treatment");
        diagPane.setExpanded(true);
        VBox diagBox = new VBox(10);
        diagBox.setPadding(new Insets(12));
        TextArea diagArea  = new TextArea(record.getDiagnosis() != null ? record.getDiagnosis() : "");
        diagArea.setPromptText("Diagnosis");
        diagArea.setPrefRowCount(3);
        TextArea treatArea = new TextArea(record.getTreatmentHistory() != null ? record.getTreatmentHistory() : "");
        treatArea.setPromptText("Treatment History");
        treatArea.setPrefRowCount(3);
        Label diagMsg = new Label();
        Button saveBtn = new Button("Save");
        saveBtn.setStyle("-fx-background-color:#16a34a;-fx-text-fill:white;" +
            "-fx-background-radius:6;-fx-padding:6 20;");
        saveBtn.setOnAction(e -> {
            record.setDiagnosis(diagArea.getText());
            record.setTreatmentHistory(treatArea.getText());
            boolean ok = medicalRecordService.updateMedicalRecord(record);
            diagMsg.setText(ok ? "Saved." : "Save failed.");
            diagMsg.setStyle(ok ? "-fx-text-fill:green;" : "-fx-text-fill:red;");
        });
        diagBox.getChildren().addAll(
            new Label("Diagnosis:"), diagArea,
            new Label("Treatment History:"), treatArea,
            saveBtn, diagMsg);
        diagPane.setContent(diagBox);

        // Consultation notes
        TitledPane notePane = new TitledPane();
        notePane.setText("Consultation Notes");
        notePane.setExpanded(false);
        VBox noteBox = new VBox(10);
        noteBox.setPadding(new Insets(12));
        TextArea noteArea = new TextArea();
        noteArea.setPromptText("Add consultation note...");
        noteArea.setPrefRowCount(3);
        Label noteMsg = new Label();
        Button addNoteBtn = new Button("Add Note");
        addNoteBtn.setStyle("-fx-background-color:#2563eb;-fx-text-fill:white;" +
            "-fx-background-radius:6;-fx-padding:6 20;");
        addNoteBtn.setOnAction(e -> {
            if (noteArea.getText().isEmpty()) return;
            ConsultationNote note = new ConsultationNote();
            note.setRecordId(record.getRecordId());
            note.setDoctorId(currentUser.getUserId());
            note.setNote(noteArea.getText());
            boolean ok = medicalRecordService.addConsultationNote(note);
            noteMsg.setText(ok ? "Note added." : "Failed.");
            noteMsg.setStyle(ok ? "-fx-text-fill:green;" : "-fx-text-fill:red;");
            if (ok) noteArea.clear();
        });

        // Existing notes list
        VBox notesList = new VBox(6);
        List<ConsultationNote> notes = medicalRecordService.getConsultationNotes(record.getRecordId());
        if (notes.isEmpty()) {
            notesList.getChildren().add(new Label("No consultation notes yet."));
        } else {
            for (ConsultationNote n : notes) {
                Label nl = new Label("• [" + n.getCreatedAt().toLocalDate() + "] " + n.getNote());
                nl.setWrapText(true);
                nl.setStyle("-fx-font-size:13px;");
                notesList.getChildren().add(nl);
            }
        }
        noteBox.getChildren().addAll(noteArea, addNoteBtn, noteMsg,
            new Separator(), new Label("Previous Notes:"), notesList);
        notePane.setContent(noteBox);

        // Order lab test
        TitledPane labPane = new TitledPane();
        labPane.setText("Order Lab Test");
        labPane.setExpanded(false);
        VBox labBox = new VBox(10);
        labBox.setPadding(new Insets(12));
        TextField testTypeField = new TextField(); testTypeField.setPromptText("Test Type");
        CheckBox urgentCheck = new CheckBox("Urgent");
        Label labMsg = new Label();
        Button orderLabBtn = new Button("Order Test");
        orderLabBtn.setStyle("-fx-background-color:#0891b2;-fx-text-fill:white;" +
            "-fx-background-radius:6;-fx-padding:6 20;");
        orderLabBtn.setOnAction(e -> {
            if (testTypeField.getText().isEmpty()) return;
            LabOrder order = new LabOrder();
            order.setPatientId(patientId);
            order.setDoctorId(currentUser.getUserId());
            order.setTestType(testTypeField.getText().trim());
            order.setUrgent(urgentCheck.isSelected());
            boolean ok = labService.orderLabTest(order);
            labMsg.setText(ok ? "Lab order created: " + order.getOrderId() : "Failed.");
            labMsg.setStyle(ok ? "-fx-text-fill:green;" : "-fx-text-fill:red;");
            if (ok) testTypeField.clear();
        });
        labBox.getChildren().addAll(
            new Label("Test Type:"), testTypeField, urgentCheck, orderLabBtn, labMsg);
        labPane.setContent(labBox);

        // Prescribe medication
        TitledPane rxPane = new TitledPane();
        rxPane.setText("Prescribe Medication");
        rxPane.setExpanded(false);
        VBox rxBox = new VBox(10);
        rxBox.setPadding(new Insets(12));

        List<Medication> meds = prescriptionService.getAllMedications();
        ComboBox<String> medBox = new ComboBox<>();
        meds.forEach(m -> medBox.getItems().add(m.getMedicationId() + " — " + m.getMedicationName()));
        medBox.setPromptText("Select Medication");
        TextField dosageField = new TextField(); dosageField.setPromptText("Dosage (e.g. 500mg twice daily)");
        TextField instrField  = new TextField(); instrField.setPromptText("Instructions");
        TextField qtyField    = new TextField(); qtyField.setPromptText("Quantity");
        CheckBox allergyCheck = new CheckBox("Allergy Checked");
        Label rxMsg = new Label();

        Button prescribeBtn = new Button("Create Prescription");
        prescribeBtn.setStyle("-fx-background-color:#7c3aed;-fx-text-fill:white;" +
            "-fx-background-radius:6;-fx-padding:6 20;");
        prescribeBtn.setOnAction(e -> {
            if (medBox.getValue() == null || dosageField.getText().isEmpty()
                || qtyField.getText().isEmpty()) {
                rxMsg.setText("Fill all fields."); rxMsg.setStyle("-fx-text-fill:red;"); return;
            }
            try {
                int medId = Integer.parseInt(medBox.getValue().split(" — ")[0]);
                int qty   = Integer.parseInt(qtyField.getText().trim());

                Prescription rx = new Prescription();
                rx.setPatientId(patientId);
                rx.setDoctorId(currentUser.getUserId());
                rx.setRecordId(record.getRecordId());
                rx.setAllergyChecked(allergyCheck.isSelected());
                boolean rxOk = prescriptionService.createPrescription(rx);

                if (rxOk) {
                    PrescriptionItem item = new PrescriptionItem();
                    item.setPrescriptionId(rx.getPrescriptionId());
                    item.setMedicationId(medId);
                    item.setDosage(dosageField.getText().trim());
                    item.setInstructions(instrField.getText().trim());
                    item.setQuantity(qty);
                    prescriptionService.addPrescriptionItem(item);
                    rxMsg.setText("Prescription created: " + rx.getPrescriptionId());
                    rxMsg.setStyle("-fx-text-fill:green;");
                    medBox.setValue(null); dosageField.clear();
                    instrField.clear(); qtyField.clear();
                }
            } catch (NumberFormatException ex) {
                rxMsg.setText("Invalid quantity."); rxMsg.setStyle("-fx-text-fill:red;");
            }
        });

        rxBox.getChildren().addAll(
            new Label("Medication:"), medBox,
            new Label("Dosage:"), dosageField,
            new Label("Instructions:"), instrField,
            new Label("Quantity:"), qtyField,
            allergyCheck, prescribeBtn, rxMsg);
        rxPane.setContent(rxBox);

        // Discharge summary
        TitledPane dischargePane = new TitledPane();
        dischargePane.setText("Discharge Summary");
        dischargePane.setExpanded(false);
        VBox dischargeBox = new VBox(10);
        dischargeBox.setPadding(new Insets(12));
        TextField invoiceIdField    = new TextField(); invoiceIdField.setPromptText("Invoice ID");
        TextArea diagSummArea       = new TextArea(); diagSummArea.setPromptText("Diagnosis Summary"); diagSummArea.setPrefRowCount(2);
        TextArea treatSummArea      = new TextArea(); treatSummArea.setPromptText("Treatment Summary"); treatSummArea.setPrefRowCount(2);
        TextArea medSummArea        = new TextArea(); medSummArea.setPromptText("Medications Summary"); medSummArea.setPrefRowCount(2);
        TextArea followupArea       = new TextArea(); followupArea.setPromptText("Follow-up Instructions"); followupArea.setPrefRowCount(2);
        TextField conditionField    = new TextField(); conditionField.setPromptText("Patient Condition (stable/recovering)");
        Label dischargeMsg = new Label();
        Button dischargeBtn = new Button("Create Discharge Summary");
        dischargeBtn.setStyle("-fx-background-color:#d97706;-fx-text-fill:white;" +
            "-fx-background-radius:6;-fx-padding:6 20;");
        dischargeBtn.setOnAction(e -> {
            DischargeService ds = new DischargeService();
            DischargeSummary summary = new DischargeSummary();
            summary.setPatientId(patientId);
            summary.setDoctorId(currentUser.getUserId());
            summary.setRecordId(record.getRecordId());
            summary.setInvoiceId(invoiceIdField.getText().trim().toUpperCase());
            summary.setDiagnosisSummary(diagSummArea.getText());
            summary.setTreatmentSummary(treatSummArea.getText());
            summary.setMedicationsSummary(medSummArea.getText());
            summary.setFollowupInstructions(followupArea.getText());
            summary.setPatientCondition(conditionField.getText().trim().toLowerCase());
            boolean ok = ds.createDischargeSummary(summary);
            dischargeMsg.setText(ok ? "Discharge summary created." : "Failed — check invoice status.");
            dischargeMsg.setStyle(ok ? "-fx-text-fill:green;" : "-fx-text-fill:red;");
        });
        dischargeBox.getChildren().addAll(
            new Label("Invoice ID:"), invoiceIdField,
            new Label("Diagnosis Summary:"), diagSummArea,
            new Label("Treatment Summary:"), treatSummArea,
            new Label("Medications Summary:"), medSummArea,
            new Label("Follow-up Instructions:"), followupArea,
            new Label("Patient Condition:"), conditionField,
            dischargeBtn, dischargeMsg);
        dischargePane.setContent(dischargeBox);

        ScrollPane scroll = new ScrollPane();
        VBox inner = new VBox(12, header, infoGrid, diagPane, notePane, labPane, rxPane, dischargePane);
        inner.setPadding(new Insets(32));
        scroll.setContent(inner);
        scroll.setFitToWidth(true);
        contentArea.getChildren().setAll(scroll);
    }

    // ── LAB ORDERS ────────────────────────────────────────
    @FXML
    private void showLabOrders() {
        VBox view = new VBox(16);
        view.setPadding(new Insets(32));

        Label title = sectionTitle("Lab Orders");

        TableView<LabOrder> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<LabOrder, String> idCol     = new TableColumn<>("Order ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        TableColumn<LabOrder, String> patCol    = new TableColumn<>("Patient ID");
        patCol.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        TableColumn<LabOrder, String> testCol   = new TableColumn<>("Test Type");
        testCol.setCellValueFactory(new PropertyValueFactory<>("testType"));
        TableColumn<LabOrder, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableColumn<LabOrder, String> urgentCol = new TableColumn<>("Urgent");
        urgentCol.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().isUrgent() ? "Yes" : "No"));
        TableColumn<LabOrder, String> dateCol   = new TableColumn<>("Ordered");
        dateCol.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().getCreatedAt() != null ?
            c.getValue().getCreatedAt().toLocalDate().toString() : "—"));

        // Results column
        TableColumn<LabOrder, Void> resultCol = new TableColumn<>("Results");
        resultCol.setCellFactory(col -> new TableCell<>() {
            final Button viewBtn = new Button("View Results");
            {
                viewBtn.setStyle("-fx-background-color:#0891b2;-fx-text-fill:white;-fx-background-radius:4;");
                viewBtn.setOnAction(e -> {
                    LabOrder order = getTableView().getItems().get(getIndex());
                    List<TestResult> results = labService.getTestResults(order.getOrderId());
                    showResultsDialog(order.getOrderId(), results);
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                LabOrder o = getTableView().getItems().get(getIndex());
                setGraphic(o.getStatus().equals("completed") ? viewBtn : null);
            }
        });

        table.getColumns().addAll(idCol, patCol, testCol, statusCol, urgentCol, dateCol, resultCol);

        // Show only this doctor's orders
        List<Appointment> myAppts = appointmentService.getAppointmentsByDoctor(currentUser.getUserId());
        List<LabOrder> myOrders = myAppts.stream()
            .flatMap(a -> labService.getLabOrdersByPatient(a.getPatientId()).stream())
            .filter(l -> l.getDoctorId().equals(currentUser.getUserId()))
            .distinct().toList();

        table.setItems(FXCollections.observableArrayList(myOrders));
        table.setPrefHeight(500);

        view.getChildren().addAll(title, table);
        contentArea.getChildren().setAll(view);
    }

    // ── PRESCRIPTIONS ─────────────────────────────────────
    @FXML
    private void showPrescriptions() {
        VBox view = new VBox(16);
        view.setPadding(new Insets(32));

        Label title = sectionTitle("My Prescriptions");

        TableView<Prescription> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Prescription, String> idCol     = new TableColumn<>("Prescription ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("prescriptionId"));
        TableColumn<Prescription, String> patCol    = new TableColumn<>("Patient");
        patCol.setCellValueFactory(c -> {
            Patient p = patientService.searchPatientById(c.getValue().getPatientId());
            return new SimpleStringProperty(p != null ?
                p.getFirstName() + " " + p.getLastName() : c.getValue().getPatientId());
        });
        TableColumn<Prescription, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableColumn<Prescription, String> dateCol   = new TableColumn<>("Date");
        dateCol.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().getCreatedAt() != null ?
            c.getValue().getCreatedAt().toLocalDate().toString() : "—"));
        TableColumn<Prescription, String> allergyCol = new TableColumn<>("Allergy Checked");
        allergyCol.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().isAllergyChecked() ? "Yes" : "No"));

        TableColumn<Prescription, Void> itemsCol = new TableColumn<>("Items");
        itemsCol.setCellFactory(col -> new TableCell<>() {
            final Button viewBtn = new Button("View Items");
            {
                viewBtn.setStyle("-fx-background-color:#2563eb;-fx-text-fill:white;-fx-background-radius:4;");
                viewBtn.setOnAction(e -> {
                    String rxId = getTableView().getItems().get(getIndex()).getPrescriptionId();
                    showPrescriptionItemsDialog(rxId);
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : viewBtn);
            }
        });

        table.getColumns().addAll(idCol, patCol, statusCol, dateCol, allergyCol, itemsCol);

        // Load all prescriptions by this doctor
        List<Appointment> myAppts = appointmentService.getAppointmentsByDoctor(currentUser.getUserId());
        List<Prescription> myRx = myAppts.stream()
            .flatMap(a -> prescriptionService.getPrescriptionsByPatient(a.getPatientId()).stream())
            .filter(p -> p.getDoctorId().equals(currentUser.getUserId()))
            .distinct().toList();

        table.setItems(FXCollections.observableArrayList(myRx));
        table.setPrefHeight(500);

        view.getChildren().addAll(title, table);
        contentArea.getChildren().setAll(view);
    }

    // ── REFERRALS ─────────────────────────────────────────
    @FXML
    private void showReferrals() {
        VBox view = new VBox(16);
        view.setPadding(new Insets(32));

        Label title = sectionTitle("Referrals");

        // Create referral form
        TitledPane formPane = new TitledPane("Create New Referral", null);
        formPane.setExpanded(false);
        GridPane form = new GridPane();
        form.setHgap(12); form.setVgap(12);
        form.setPadding(new Insets(16));

        TextField patIdField     = new TextField(); patIdField.setPromptText("Patient ID");
        List<ExternalFacility> facilities = referralService.getAllFacilities();
        ComboBox<String> facilityBox = new ComboBox<>();
        facilities.forEach(f -> facilityBox.getItems().add(f.getFacilityId() + " — " + f.getFacilityName()));
        facilityBox.setPromptText("Select Facility");
        TextArea detailsArea     = new TextArea(); detailsArea.setPromptText("Referral Details"); detailsArea.setPrefRowCount(2);
        TextArea justArea        = new TextArea(); justArea.setPromptText("Justification"); justArea.setPrefRowCount(2);
        Label refMsg = new Label();

        form.addRow(0, new Label("Patient ID:"), patIdField, new Label("Facility:"), facilityBox);
        form.addRow(1, new Label("Details:"), detailsArea);
        form.addRow(2, new Label("Justification:"), justArea);

        Button createBtn = new Button("Create Referral");
        createBtn.setStyle("-fx-background-color:#2563eb;-fx-text-fill:white;" +
            "-fx-background-radius:6;-fx-padding:8 20;");
        createBtn.setOnAction(e -> {
            if (patIdField.getText().isEmpty() || facilityBox.getValue() == null) {
                refMsg.setText("Fill required fields."); refMsg.setStyle("-fx-text-fill:red;"); return;
            }
            int facilityId = Integer.parseInt(facilityBox.getValue().split(" — ")[0]);
            Referral ref = new Referral();
            ref.setPatientId(patIdField.getText().trim().toUpperCase());
            ref.setDoctorId(currentUser.getUserId());
            ref.setFacilityId(facilityId);
            ref.setReferralDetails(detailsArea.getText());
            ref.setJustification(justArea.getText());
            boolean ok = referralService.createReferral(ref);
            refMsg.setText(ok ? "Referral created." : "Failed.");
            refMsg.setStyle(ok ? "-fx-text-fill:green;" : "-fx-text-fill:red;");
            if (ok) { patIdField.clear(); detailsArea.clear(); justArea.clear(); showReferrals(); }
        });

        VBox formBox = new VBox(12, form, createBtn, refMsg);
        formBox.setPadding(new Insets(8));
        formPane.setContent(formBox);

        // Referrals table
        TableView<Referral> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Referral, Integer> idCol    = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("referralId"));
        TableColumn<Referral, String> patCol    = new TableColumn<>("Patient ID");
        patCol.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        TableColumn<Referral, Integer> facCol   = new TableColumn<>("Facility ID");
        facCol.setCellValueFactory(new PropertyValueFactory<>("facilityId"));
        TableColumn<Referral, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableColumn<Referral, String> dateCol   = new TableColumn<>("Date");
        dateCol.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().getCreatedAt() != null ?
            c.getValue().getCreatedAt().toLocalDate().toString() : "—"));

        table.getColumns().addAll(idCol, patCol, facCol, statusCol, dateCol);
        table.setItems(FXCollections.observableArrayList(
            referralService.getReferralsByDoctor(currentUser.getUserId())));
        table.setPrefHeight(350);

        view.getChildren().addAll(title, formPane, table);
        contentArea.getChildren().setAll(view);
    }

    // ── MY SCHEDULE ───────────────────────────────────────
    @FXML
    private void showMySchedule() {
        VBox view = new VBox(16);
        view.setPadding(new Insets(32));

        Label title = sectionTitle("My Schedule");

        TableView<DoctorSchedule> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<DoctorSchedule, String> dateCol  = new TableColumn<>("Date");
        dateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getShiftDate().toString()));
        TableColumn<DoctorSchedule, String> startCol = new TableColumn<>("Start");
        startCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getShiftStart().toString()));
        TableColumn<DoctorSchedule, String> endCol   = new TableColumn<>("End");
        endCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getShiftEnd().toString()));
        TableColumn<DoctorSchedule, String> dutyCol  = new TableColumn<>("Duty Type");
        dutyCol.setCellValueFactory(new PropertyValueFactory<>("dutyType"));

        table.getColumns().addAll(dateCol, startCol, endCol, dutyCol);
        table.setItems(FXCollections.observableArrayList(
            scheduleService.getDoctorSchedules(currentUser.getUserId())));
        table.setPrefHeight(500);

        view.getChildren().addAll(title, table);
        contentArea.getChildren().setAll(view);
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

    // ── DIALOGS ───────────────────────────────────────────
    private void showResultsDialog(String orderId, List<TestResult> results) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Lab Results — " + orderId);
        alert.setHeaderText("Test Results");
        StringBuilder sb = new StringBuilder();
        if (results.isEmpty()) {
            sb.append("No results available.");
        } else {
            for (TestResult r : results) {
                sb.append("Result: ").append(r.getResultValue()).append("\n");
                if (r.getNotes() != null) sb.append("Notes: ").append(r.getNotes()).append("\n");
                sb.append("---\n");
            }
        }
        alert.setContentText(sb.toString());
        alert.showAndWait();
    }

    private void showPrescriptionItemsDialog(String prescriptionId) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Prescription Items — " + prescriptionId);
        alert.setHeaderText("Medications Prescribed");
        List<PrescriptionItem> items = prescriptionService.getPrescriptionItems(prescriptionId);
        StringBuilder sb = new StringBuilder();
        if (items.isEmpty()) {
            sb.append("No items.");
        } else {
            for (PrescriptionItem i : items) {
                Medication m = prescriptionService.getMedicationById(i.getMedicationId());
                sb.append("Medication: ").append(m != null ? m.getMedicationName() : i.getMedicationId()).append("\n");
                sb.append("Dosage: ").append(i.getDosage()).append("\n");
                sb.append("Instructions: ").append(i.getInstructions()).append("\n");
                sb.append("Quantity: ").append(i.getQuantity()).append("\n---\n");
            }
        }
        alert.setContentText(sb.toString());
        alert.showAndWait();
    }

    // ── HELPERS ───────────────────────────────────────────
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