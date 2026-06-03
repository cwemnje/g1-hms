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
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminDashboardController implements DashboardController {

    @FXML private Label loggedInLabel;
    @FXML private Label userInfoLabel;
    @FXML private StackPane contentArea;

    private User currentUser;

    private final AdminService adminService       = new AdminService();
    private final AuthService authService         = new AuthService();
    private final ScheduleService scheduleService = new ScheduleService();
    private final ReportService reportService     = new ReportService();

    @Override
    public void initUser(User user) {
        this.currentUser = user;
        loggedInLabel.setText("Welcome, " + user.getFirstName() + " " + user.getLastName());
        userInfoLabel.setText("👤 " + user.getFirstName() + " " + user.getLastName()
            + "\nID: " + user.getUserId()
            + "\nRole: Admin");
        showDashboard();
    }

    // ── DASHBOARD OVERVIEW ────────────────────────────────
    @FXML
    private void showDashboard() {
        VBox view = new VBox(24);
        view.setPadding(new Insets(32));
        view.setStyle("-fx-background-color: #f0f4f8;");

        Label title = new Label("Dashboard Overview");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e3a5f;");

        // Stats cards
        List<User> users  = adminService.getAllUsers();
        List<Bed> beds    = adminService.getAllBeds();
        List<Bed> availBeds = adminService.getAvailableBeds();
        List<Theatre> theatres = adminService.getAllTheatres();
        List<ResourceAllocation> allocs = adminService.getActiveAllocations();

        HBox cards = new HBox(16);
        cards.getChildren().addAll(
            statCard("Total Staff",       String.valueOf(users.size()),       "#2563eb"),
            statCard("Total Beds",        String.valueOf(beds.size()),        "#0891b2"),
            statCard("Available Beds",    String.valueOf(availBeds.size()),   "#16a34a"),
            statCard("Active Allocations",String.valueOf(allocs.size()),      "#d97706")
        );

        // Revenue summary
        ReportService rs = new ReportService();
        LocalDate from = LocalDate.now().withDayOfMonth(1);
        LocalDate to   = LocalDate.now();
        Label revenueLabel = new Label("Month-to-date Revenue: XAF "
            + rs.getTotalRevenue(from, to).toPlainString());
        revenueLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: #374151;");

        Label outstandingLabel = new Label("Total Outstanding: XAF "
            + rs.getTotalOutstanding().toPlainString());
        outstandingLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: #dc2626;");

        view.getChildren().addAll(title, cards, revenueLabel, outstandingLabel);
        contentArea.getChildren().setAll(view);
    }

    // ── USERS ─────────────────────────────────────────────
    @FXML
    private void showUsers() {
        VBox view = new VBox(16);
        view.setPadding(new Insets(32));

        Label title = sectionTitle("User Management");

        // Register new user form
        TitledPane registerPane = new TitledPane();
        registerPane.setText("Register New Staff");
        registerPane.setExpanded(false);

        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(12);
        form.setPadding(new Insets(16));

        TextField firstNameField  = new TextField(); firstNameField.setPromptText("First Name");
        TextField lastNameField   = new TextField(); lastNameField.setPromptText("Last Name");
        TextField emailField      = new TextField(); emailField.setPromptText("Email");
        TextField phoneField      = new TextField(); phoneField.setPromptText("Phone");
        PasswordField passField   = new PasswordField(); passField.setPromptText("Password");
        ComboBox<String> roleBox  = new ComboBox<>();
        roleBox.getItems().addAll("admin","doctor","nurse","receptionist","pharmacist","lab_technician");
        roleBox.setPromptText("Select Role");

        form.addRow(0, new Label("First Name:"), firstNameField, new Label("Last Name:"), lastNameField);
        form.addRow(1, new Label("Email:"),      emailField,     new Label("Phone:"),     phoneField);
        form.addRow(2, new Label("Password:"),   passField,      new Label("Role:"),      roleBox);

        Label formMsg = new Label();
        Button registerBtn = new Button("Register Staff");
        registerBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; " +
            "-fx-background-radius: 6; -fx-padding: 8 20;");

        registerBtn.setOnAction(e -> {
            if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty()
                || emailField.getText().isEmpty() || passField.getText().isEmpty()
                || roleBox.getValue() == null) {
                formMsg.setText("Please fill all fields.");
                formMsg.setStyle("-fx-text-fill: red;");
                return;
            }

            String role   = roleBox.getValue();
            int roleId    = getRoleId(role);
            String userId = generateUserId(role);

            User newUser = new User();
            newUser.setUserId(userId);
            newUser.setFirstName(firstNameField.getText().trim());
            newUser.setLastName(lastNameField.getText().trim());
            newUser.setEmail(emailField.getText().trim());
            newUser.setPhone(phoneField.getText().trim());
            newUser.setRoleId(roleId);
            newUser.setAccessPermissions(true);
            newUser.setActive(true);

            boolean success = authService.registerUser(newUser, passField.getText());
            if (success) {
                formMsg.setText("Staff registered successfully. ID: " + userId);
                formMsg.setStyle("-fx-text-fill: green;");
                firstNameField.clear(); lastNameField.clear();
                emailField.clear(); phoneField.clear();
                passField.clear(); roleBox.setValue(null);
                showUsers(); // refresh
            } else {
                formMsg.setText("Registration failed. Email may already exist.");
                formMsg.setStyle("-fx-text-fill: red;");
            }
        });

        VBox formBox = new VBox(12, form, registerBtn, formMsg);
        formBox.setPadding(new Insets(8));
        registerPane.setContent(formBox);

        // Users table
        TableView<User> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<User, String> idCol    = new TableColumn<>("Staff ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("userId"));

        TableColumn<User, String> nameCol  = new TableColumn<>("Name");
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().getFirstName() + " " + c.getValue().getLastName()));

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<User, String> roleCol  = new TableColumn<>("Role ID");
        roleCol.setCellValueFactory(c -> new SimpleStringProperty(
            getRoleName(c.getValue().getRoleId())));

        TableColumn<User, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().isActive() ? "Active" : "Inactive"));

        TableColumn<User, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> new TableCell<>() {
            final Button deactivateBtn = new Button("Deactivate");
            final Button activateBtn   = new Button("Activate");
            {
                deactivateBtn.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-background-radius:4;");
                activateBtn.setStyle("-fx-background-color: #16a34a; -fx-text-fill: white; -fx-background-radius:4;");
                deactivateBtn.setOnAction(e -> {
                    User u = getTableView().getItems().get(getIndex());
                    authService.deactivateUser(u.getUserId());
                    showUsers();
                });
                activateBtn.setOnAction(e -> {
                    User u = getTableView().getItems().get(getIndex());
                    authService.reactivateUser(u.getUserId());
                    showUsers();
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                User u = getTableView().getItems().get(getIndex());
                HBox box = new HBox(6, u.isActive() ? deactivateBtn : activateBtn);
                setGraphic(box);
            }
        });

        table.getColumns().addAll(idCol, nameCol, emailCol, roleCol, statusCol, actionCol);
        table.setItems(FXCollections.observableArrayList(adminService.getAllUsers()));
        table.setPrefHeight(400);

        view.getChildren().addAll(title, registerPane, table);
        contentArea.getChildren().setAll(view);
    }

    // ── BEDS ──────────────────────────────────────────────
    @FXML
    private void showBeds() {
        VBox view = new VBox(16);
        view.setPadding(new Insets(32));

        Label title = sectionTitle("Bed Management");

        // Add bed form
        HBox addForm = new HBox(12);
        addForm.setAlignment(Pos.CENTER_LEFT);
        TextField wardField  = new TextField(); wardField.setPromptText("Ward Name");
        TextField bedNumField = new TextField(); bedNumField.setPromptText("Bed Number");
        Label bedMsg = new Label();
        Button addBedBtn = new Button("Add Bed");
        addBedBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; " +
            "-fx-background-radius: 6; -fx-padding: 8 20;");

        addBedBtn.setOnAction(e -> {
            if (wardField.getText().isEmpty() || bedNumField.getText().isEmpty()) {
                bedMsg.setText("Fill all fields."); bedMsg.setStyle("-fx-text-fill:red;"); return;
            }
            Bed bed = new Bed();
            bed.setWardName(wardField.getText().trim());
            bed.setBedNumber(bedNumField.getText().trim());
            boolean ok = adminService.addBed(bed);
            if (ok) {
                bedMsg.setText("Bed added."); bedMsg.setStyle("-fx-text-fill:green;");
                wardField.clear(); bedNumField.clear();
                showBeds();
            } else {
                bedMsg.setText("Failed — bed number may already exist.");
                bedMsg.setStyle("-fx-text-fill:red;");
            }
        });

        addForm.getChildren().addAll(
            new Label("Ward:"), wardField,
            new Label("Bed No:"), bedNumField,
            addBedBtn, bedMsg
        );

        // Beds table
        TableView<Bed> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Bed, Integer> idCol    = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("bedId"));

        TableColumn<Bed, String> wardCol   = new TableColumn<>("Ward");
        wardCol.setCellValueFactory(new PropertyValueFactory<>("wardName"));

        TableColumn<Bed, String> numCol    = new TableColumn<>("Bed Number");
        numCol.setCellValueFactory(new PropertyValueFactory<>("bedNumber"));

        TableColumn<Bed, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<Bed, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> new TableCell<>() {
            final Button occupyBtn    = new Button("Occupy");
            final Button availableBtn = new Button("Set Available");
            {
                occupyBtn.setStyle("-fx-background-color: #d97706; -fx-text-fill: white; -fx-background-radius:4;");
                availableBtn.setStyle("-fx-background-color: #16a34a; -fx-text-fill: white; -fx-background-radius:4;");
                occupyBtn.setOnAction(e -> {
                    adminService.updateBedStatus(getTableView().getItems().get(getIndex()).getBedId(), "occupied");
                    showBeds();
                });
                availableBtn.setOnAction(e -> {
                    adminService.updateBedStatus(getTableView().getItems().get(getIndex()).getBedId(), "available");
                    showBeds();
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                Bed b = getTableView().getItems().get(getIndex());
                HBox box = new HBox(6, b.getStatus().equals("available") ? occupyBtn : availableBtn);
                setGraphic(box);
            }
        });

        table.getColumns().addAll(idCol, wardCol, numCol, statusCol, actionCol);
        table.setItems(FXCollections.observableArrayList(adminService.getAllBeds()));
        table.setPrefHeight(400);

        view.getChildren().addAll(title, addForm, table);
        contentArea.getChildren().setAll(view);
    }

    // ── THEATRES ──────────────────────────────────────────
    @FXML
    private void showTheatres() {
        VBox view = new VBox(16);
        view.setPadding(new Insets(32));

        Label title = sectionTitle("Theatre Management");

        HBox addForm = new HBox(12);
        addForm.setAlignment(Pos.CENTER_LEFT);
        TextField nameField = new TextField(); nameField.setPromptText("Theatre Name");
        Label msg = new Label();
        Button addBtn = new Button("Add Theatre");
        addBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; " +
            "-fx-background-radius: 6; -fx-padding: 8 20;");

        addBtn.setOnAction(e -> {
            if (nameField.getText().isEmpty()) {
                msg.setText("Enter theatre name."); msg.setStyle("-fx-text-fill:red;"); return;
            }
            Theatre t = new Theatre();
            t.setTheatreName(nameField.getText().trim());
            boolean ok = adminService.addTheatre(t);
            if (ok) {
                msg.setText("Theatre added."); msg.setStyle("-fx-text-fill:green;");
                nameField.clear(); showTheatres();
            } else {
                msg.setText("Failed — name may already exist."); msg.setStyle("-fx-text-fill:red;");
            }
        });
        addForm.getChildren().addAll(new Label("Name:"), nameField, addBtn, msg);

        TableView<Theatre> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Theatre, Integer> idCol   = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("theatreId"));

        TableColumn<Theatre, String> nameCol  = new TableColumn<>("Theatre Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("theatreName"));

        TableColumn<Theatre, String> statCol  = new TableColumn<>("Status");
        statCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<Theatre, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> new TableCell<>() {
            final Button occupyBtn    = new Button("Occupy");
            final Button availableBtn = new Button("Set Available");
            {
                occupyBtn.setStyle("-fx-background-color: #d97706; -fx-text-fill: white; -fx-background-radius:4;");
                availableBtn.setStyle("-fx-background-color: #16a34a; -fx-text-fill: white; -fx-background-radius:4;");
                occupyBtn.setOnAction(e -> {
                    adminService.updateTheatreStatus(getTableView().getItems().get(getIndex()).getTheatreId(), "occupied");
                    showTheatres();
                });
                availableBtn.setOnAction(e -> {
                    adminService.updateTheatreStatus(getTableView().getItems().get(getIndex()).getTheatreId(), "available");
                    showTheatres();
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                Theatre t = getTableView().getItems().get(getIndex());
                HBox box = new HBox(6, t.getStatus().equals("available") ? occupyBtn : availableBtn);
                setGraphic(box);
            }
        });

        table.getColumns().addAll(idCol, nameCol, statCol, actionCol);
        table.setItems(FXCollections.observableArrayList(adminService.getAllTheatres()));
        table.setPrefHeight(400);

        view.getChildren().addAll(title, addForm, table);
        contentArea.getChildren().setAll(view);
    }

    // ── SCHEDULES ─────────────────────────────────────────
    @FXML
    private void showSchedules() {
        VBox view = new VBox(16);
        view.setPadding(new Insets(32));

        Label title = sectionTitle("Schedule Management");

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Doctor schedules tab
        Tab doctorTab = new Tab("Doctor Schedules");
        VBox doctorBox = new VBox(12);
        doctorBox.setPadding(new Insets(16));

        TextField docIdField   = new TextField(); docIdField.setPromptText("Doctor ID (e.g. HMSD001)");
        DatePicker datePicker  = new DatePicker(LocalDate.now().plusDays(1));
        TextField startField   = new TextField(); startField.setPromptText("Start (HH:mm:ss)");
        TextField endField     = new TextField(); endField.setPromptText("End (HH:mm:ss)");
        ComboBox<String> dutyBox = new ComboBox<>();
        dutyBox.getItems().addAll("regular", "on_call", "emergency");
        dutyBox.setPromptText("Duty Type");
        Label schedMsg = new Label();

        Button addDocSched = new Button("Add Doctor Schedule");
        addDocSched.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; " +
            "-fx-background-radius: 6; -fx-padding: 8 20;");
        addDocSched.setOnAction(e -> {
            try {
                DoctorSchedule ds = new DoctorSchedule();
                ds.setDoctorId(docIdField.getText().trim().toUpperCase());
                ds.setShiftDate(datePicker.getValue());
                ds.setShiftStart(java.time.LocalTime.parse(startField.getText().trim()));
                ds.setShiftEnd(java.time.LocalTime.parse(endField.getText().trim()));
                ds.setDutyType(dutyBox.getValue());
                boolean ok = scheduleService.createDoctorSchedule(ds);
                schedMsg.setText(ok ? "Schedule added." : "Failed — conflict or invalid data.");
                schedMsg.setStyle(ok ? "-fx-text-fill:green;" : "-fx-text-fill:red;");
                showSchedules();
            } catch (Exception ex) {
                schedMsg.setText("Invalid time format. Use HH:mm:ss");
                schedMsg.setStyle("-fx-text-fill:red;");
            }
        });

        HBox docForm = new HBox(10, new Label("Doctor ID:"), docIdField,
            new Label("Date:"), datePicker,
            new Label("Start:"), startField,
            new Label("End:"), endField,
            new Label("Duty:"), dutyBox,
            addDocSched);
        docForm.setAlignment(Pos.CENTER_LEFT);

        TableView<DoctorSchedule> docTable = new TableView<>();
        docTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<DoctorSchedule, Integer> dsIdCol  = new TableColumn<>("ID");
        dsIdCol.setCellValueFactory(new PropertyValueFactory<>("scheduleId"));
        TableColumn<DoctorSchedule, String> dsDoctCol = new TableColumn<>("Doctor ID");
        dsDoctCol.setCellValueFactory(new PropertyValueFactory<>("doctorId"));
        TableColumn<DoctorSchedule, String> dsDateCol = new TableColumn<>("Date");
        dsDateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getShiftDate().toString()));
        TableColumn<DoctorSchedule, String> dsStartCol = new TableColumn<>("Start");
        dsStartCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getShiftStart().toString()));
        TableColumn<DoctorSchedule, String> dsEndCol   = new TableColumn<>("End");
        dsEndCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getShiftEnd().toString()));
        TableColumn<DoctorSchedule, String> dsDutyCol  = new TableColumn<>("Duty");
        dsDutyCol.setCellValueFactory(new PropertyValueFactory<>("dutyType"));
        TableColumn<DoctorSchedule, Void> dsDelCol = new TableColumn<>("Action");
        dsDelCol.setCellFactory(col -> new TableCell<>() {
            final Button del = new Button("Delete");
            { del.setStyle("-fx-background-color:#dc2626;-fx-text-fill:white;-fx-background-radius:4;");
              del.setOnAction(e -> {
                  scheduleService.deleteDoctorSchedule(getTableView().getItems().get(getIndex()).getScheduleId());
                  showSchedules();
              }); }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : del);
            }
        });

        docTable.getColumns().addAll(dsIdCol, dsDoctCol, dsDateCol, dsStartCol, dsEndCol, dsDutyCol, dsDelCol);

        // Load all doctor schedules for today onwards
        List<DoctorSchedule> allDocScheds = scheduleService.getDoctorSchedulesByDate(LocalDate.now());
        docTable.setItems(FXCollections.observableArrayList(allDocScheds));
        docTable.setPrefHeight(300);

        doctorBox.getChildren().addAll(docForm, schedMsg, docTable);
        doctorTab.setContent(doctorBox);

        // Nurse schedules tab
        Tab nurseTab = new Tab("Nurse Schedules");
        VBox nurseBox = new VBox(12);
        nurseBox.setPadding(new Insets(16));

        TextField nurseIdField  = new TextField(); nurseIdField.setPromptText("Nurse ID (e.g. HMSN001)");
        DatePicker nurseDatePicker = new DatePicker(LocalDate.now().plusDays(1));
        TextField nurseStart    = new TextField(); nurseStart.setPromptText("Start (HH:mm:ss)");
        TextField nurseEnd      = new TextField(); nurseEnd.setPromptText("End (HH:mm:ss)");
        TextField nurseWard     = new TextField(); nurseWard.setPromptText("Ward");
        Label nurseSMsg = new Label();

        Button addNurseSched = new Button("Add Nurse Schedule");
        addNurseSched.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; " +
            "-fx-background-radius: 6; -fx-padding: 8 20;");
        addNurseSched.setOnAction(e -> {
            try {
                NurseSchedule ns = new NurseSchedule();
                ns.setNurseId(nurseIdField.getText().trim().toUpperCase());
                ns.setShiftDate(nurseDatePicker.getValue());
                ns.setShiftStart(java.time.LocalTime.parse(nurseStart.getText().trim()));
                ns.setShiftEnd(java.time.LocalTime.parse(nurseEnd.getText().trim()));
                ns.setWardAssigned(nurseWard.getText().trim());
                ns.setCreatedBy(currentUser.getUserId());
                boolean ok = scheduleService.createNurseSchedule(ns);
                nurseSMsg.setText(ok ? "Schedule added." : "Failed — conflict or invalid data.");
                nurseSMsg.setStyle(ok ? "-fx-text-fill:green;" : "-fx-text-fill:red;");
                showSchedules();
            } catch (Exception ex) {
                nurseSMsg.setText("Invalid time format. Use HH:mm:ss");
                nurseSMsg.setStyle("-fx-text-fill:red;");
            }
        });

        HBox nurseForm = new HBox(10, new Label("Nurse ID:"), nurseIdField,
            new Label("Date:"), nurseDatePicker,
            new Label("Start:"), nurseStart,
            new Label("End:"), nurseEnd,
            new Label("Ward:"), nurseWard,
            addNurseSched);
        nurseForm.setAlignment(Pos.CENTER_LEFT);

        TableView<NurseSchedule> nurseTable = new TableView<>();
        nurseTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<NurseSchedule, Integer> nsIdCol   = new TableColumn<>("ID");
        nsIdCol.setCellValueFactory(new PropertyValueFactory<>("scheduleId"));
        TableColumn<NurseSchedule, String> nsNurseCol = new TableColumn<>("Nurse ID");
        nsNurseCol.setCellValueFactory(new PropertyValueFactory<>("nurseId"));
        TableColumn<NurseSchedule, String> nsDateCol  = new TableColumn<>("Date");
        nsDateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getShiftDate().toString()));
        TableColumn<NurseSchedule, String> nsStartCol = new TableColumn<>("Start");
        nsStartCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getShiftStart().toString()));
        TableColumn<NurseSchedule, String> nsEndCol   = new TableColumn<>("End");
        nsEndCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getShiftEnd().toString()));
        TableColumn<NurseSchedule, String> nsWardCol  = new TableColumn<>("Ward");
        nsWardCol.setCellValueFactory(new PropertyValueFactory<>("wardAssigned"));
        TableColumn<NurseSchedule, Void> nsDelCol = new TableColumn<>("Action");
        nsDelCol.setCellFactory(col -> new TableCell<>() {
            final Button del = new Button("Delete");
            { del.setStyle("-fx-background-color:#dc2626;-fx-text-fill:white;-fx-background-radius:4;");
              del.setOnAction(e -> {
                  scheduleService.deleteNurseSchedule(getTableView().getItems().get(getIndex()).getScheduleId());
                  showSchedules();
              }); }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : del);
            }
        });

        nurseTable.getColumns().addAll(nsIdCol, nsNurseCol, nsDateCol, nsStartCol, nsEndCol, nsWardCol, nsDelCol);
        nurseTable.setItems(FXCollections.observableArrayList(
            scheduleService.getNurseSchedulesByDate(LocalDate.now())));
        nurseTable.setPrefHeight(300);

        nurseBox.getChildren().addAll(nurseForm, nurseSMsg, nurseTable);
        nurseTab.setContent(nurseBox);

        tabs.getTabs().addAll(doctorTab, nurseTab);
        view.getChildren().addAll(title, tabs);
        contentArea.getChildren().setAll(view);
    }

    // ── ALLOCATIONS ───────────────────────────────────────
    @FXML
    private void showAllocations() {
        VBox view = new VBox(16);
        view.setPadding(new Insets(32));

        Label title = sectionTitle("Resource Allocations");

        // Allocate form
        GridPane form = new GridPane();
        form.setHgap(12); form.setVgap(12);
        form.setPadding(new Insets(16));
        form.setStyle("-fx-background-color: white; -fx-background-radius: 8; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);");

        TextField patIdField  = new TextField(); patIdField.setPromptText("Patient ID");
        TextField docIdField  = new TextField(); docIdField.setPromptText("Doctor ID");
        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("bed", "theatre");
        typeBox.setPromptText("Type");
        ComboBox<String> resourceBox = new ComboBox<>();
        typeBox.setOnAction(e -> {
            resourceBox.getItems().clear();
            if ("bed".equals(typeBox.getValue())) {
                adminService.getAvailableBeds().forEach(b ->
                    resourceBox.getItems().add(b.getBedId() + " — " + b.getWardName() + " " + b.getBedNumber()));
            } else {
                adminService.getAvailableTheatres().forEach(t ->
                    resourceBox.getItems().add(t.getTheatreId() + " — " + t.getTheatreName()));
            }
        });
        resourceBox.setPromptText("Select Resource");
        CheckBox emergencyBox = new CheckBox("Emergency");
        Label allocMsg = new Label();

        form.addRow(0, new Label("Patient ID:"), patIdField,
                       new Label("Doctor ID:"), docIdField);
        form.addRow(1, new Label("Type:"), typeBox,
                       new Label("Resource:"), resourceBox);
        form.addRow(2, emergencyBox);

        Button allocBtn = new Button("Allocate Resource");
        allocBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; " +
            "-fx-background-radius: 6; -fx-padding: 8 20;");
        allocBtn.setOnAction(e -> {
            if (patIdField.getText().isEmpty() || docIdField.getText().isEmpty()
                || typeBox.getValue() == null || resourceBox.getValue() == null) {
                allocMsg.setText("Fill all fields."); allocMsg.setStyle("-fx-text-fill:red;"); return;
            }
            int resourceId = Integer.parseInt(resourceBox.getValue().split(" — ")[0]);
            ResourceAllocation alloc = new ResourceAllocation();
            alloc.setPatientId(patIdField.getText().trim().toUpperCase());
            alloc.setDoctorId(docIdField.getText().trim().toUpperCase());
            alloc.setApprovedBy(currentUser.getUserId());
            alloc.setAllocationType(typeBox.getValue());
            alloc.setEmergency(emergencyBox.isSelected());
            if ("bed".equals(typeBox.getValue())) alloc.setBedId(resourceId);
            else alloc.setTheatreId(resourceId);

            boolean ok = adminService.allocateResource(alloc);
            allocMsg.setText(ok ? "Resource allocated." : "Allocation failed.");
            allocMsg.setStyle(ok ? "-fx-text-fill:green;" : "-fx-text-fill:red;");
            showAllocations();
        });

        // Active allocations table
        TableView<ResourceAllocation> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<ResourceAllocation, Integer> idCol  = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("allocationId"));
        TableColumn<ResourceAllocation, String> patCol  = new TableColumn<>("Patient");
        patCol.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        TableColumn<ResourceAllocation, String> docCol  = new TableColumn<>("Doctor");
        docCol.setCellValueFactory(new PropertyValueFactory<>("doctorId"));
        TableColumn<ResourceAllocation, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("allocationType"));
        TableColumn<ResourceAllocation, String> emgCol  = new TableColumn<>("Emergency");
        emgCol.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().isEmergency() ? "Yes" : "No"));
        TableColumn<ResourceAllocation, Void> relCol = new TableColumn<>("Action");
        relCol.setCellFactory(col -> new TableCell<>() {
            final Button rel = new Button("Release");
            { rel.setStyle("-fx-background-color:#dc2626;-fx-text-fill:white;-fx-background-radius:4;");
              rel.setOnAction(e -> {
                  adminService.releaseResource(getTableView().getItems().get(getIndex()).getAllocationId());
                  showAllocations();
              }); }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : rel);
            }
        });

        table.getColumns().addAll(idCol, patCol, docCol, typeCol, emgCol, relCol);
        table.setItems(FXCollections.observableArrayList(adminService.getActiveAllocations()));
        table.setPrefHeight(300);

        view.getChildren().addAll(title, form, allocBtn, allocMsg, table);
        contentArea.getChildren().setAll(view);
    }

    // ── REPORTS ───────────────────────────────────────────
    @FXML
    private void showReports() {
        VBox view = new VBox(20);
        view.setPadding(new Insets(32));

        Label title = sectionTitle("Financial Reports");

        // Date range picker
        HBox dateRow = new HBox(12);
        dateRow.setAlignment(Pos.CENTER_LEFT);
        DatePicker fromPicker = new DatePicker(LocalDate.now().withDayOfMonth(1));
        DatePicker toPicker   = new DatePicker(LocalDate.now());
        Button generateBtn = new Button("Generate Report");
        generateBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; " +
            "-fx-background-radius: 6; -fx-padding: 8 20;");
        dateRow.getChildren().addAll(
            new Label("From:"), fromPicker,
            new Label("To:"), toPicker,
            generateBtn
        );

        VBox resultsBox = new VBox(12);

        generateBtn.setOnAction(e -> {
            resultsBox.getChildren().clear();
            LocalDate from = fromPicker.getValue();
            LocalDate to   = toPicker.getValue();

            GridPane grid = new GridPane();
            grid.setHgap(20); grid.setVgap(12);
            grid.setPadding(new Insets(20));
            grid.setStyle("-fx-background-color: white; -fx-background-radius: 8; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);");

            grid.addRow(0,
                boldLabel("Total Revenue:"),
                new Label("XAF " + reportService.getTotalRevenue(from, to).toPlainString()));
            grid.addRow(1,
                boldLabel("Total Outstanding:"),
                new Label("XAF " + reportService.getTotalOutstanding().toPlainString()));
            grid.addRow(2,
                boldLabel("Patients Registered:"),
                new Label(String.valueOf(reportService.getTotalPatientsRegistered(from, to))));
            grid.addRow(3,
                boldLabel("Appointments:"),
                new Label(String.valueOf(reportService.getTotalAppointments(from, to))));
            grid.addRow(4,
                boldLabel("Lab Orders:"),
                new Label(String.valueOf(reportService.getTotalLabOrders(from, to))));

            // Payment breakdown
            Label payTitle = boldLabel("Payments by Method:");
            VBox payBox = new VBox(6);
            reportService.getPaymentsByMethod(from, to).forEach((method, count) ->
                payBox.getChildren().add(new Label("  " + method + ": " + count + " transaction(s)")));

            // Invoice summary
            Label invTitle = boldLabel("Invoice Status Summary:");
            VBox invBox = new VBox(6);
            reportService.getInvoiceSummaryByStatus().forEach((status, count) ->
                invBox.getChildren().add(new Label("  " + status + ": " + count)));

            // Save report button
            Button saveBtn = new Button("Save Report to System");
            saveBtn.setStyle("-fx-background-color: #16a34a; -fx-text-fill: white; " +
                "-fx-background-radius: 6; -fx-padding: 8 20;");
            Label saveMsg = new Label();
            saveBtn.setOnAction(ev -> {
                FinancialReport fr = new FinancialReport();
                fr.setGeneratedBy(currentUser.getUserId());
                fr.setReportType("revenue");
                fr.setDateFrom(from);
                fr.setDateTo(to);
                fr.setDepartment("All");
                fr.setPaymentStatusFilter("settled");
                boolean ok = reportService.saveReport(fr);
                saveMsg.setText(ok ? "Report saved." : "Save failed.");
                saveMsg.setStyle(ok ? "-fx-text-fill:green;" : "-fx-text-fill:red;");
            });

            resultsBox.getChildren().addAll(grid, payTitle, payBox, invTitle, invBox, saveBtn, saveMsg);
        });

        view.getChildren().addAll(title, dateRow, resultsBox);
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

    private VBox statCard(String label, String value, String color) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(180);
        card.setPrefHeight(100);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 10;");

        Label valLabel = new Label(value);
        valLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label lblLabel = new Label(label);
        lblLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: rgba(255,255,255,0.85);");

        card.getChildren().addAll(valLabel, lblLabel);
        return card;
    }

    private Label sectionTitle(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1e3a5f;");
        return l;
    }

    private Label boldLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        return l;
    }

    private String getRoleName(int roleId) {
        return switch (roleId) {
            case 1 -> "Admin";
            case 2 -> "Doctor";
            case 3 -> "Nurse";
            case 4 -> "Receptionist";
            case 5 -> "Pharmacist";
            case 6 -> "Lab Technician";
            default -> "Unknown";
        };
    }

    private int getRoleId(String role) {
        return switch (role) {
            case "admin"          -> 1;
            case "doctor"         -> 2;
            case "nurse"          -> 3;
            case "receptionist"   -> 4;
            case "pharmacist"     -> 5;
            case "lab_technician" -> 6;
            default               -> 0;
        };
    }

    private String generateUserId(String role) {
        String prefix = switch (role) {
            case "admin"          -> "HMSA";
            case "doctor"         -> "HMSD";
            case "nurse"          -> "HMSN";
            case "receptionist"   -> "HMSR";
            case "pharmacist"     -> "HMSP";
            case "lab_technician" -> "HMSL";
            default               -> "HMSX";
        };
        List<User> roleUsers = adminService.getUsersByRole(getRoleId(role));
        return String.format("%s%03d", prefix, roleUsers.size() + 1);
    }
}