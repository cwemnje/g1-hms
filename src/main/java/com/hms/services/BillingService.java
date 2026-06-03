package com.hms.services;

import com.hms.database.DatabaseConnection;
import com.hms.models.Invoice;
import com.hms.models.InvoiceItem;
import com.hms.models.Payment;
import com.hms.models.Receipt;
import com.hms.utils.IDGenerator;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillingService {

    private Connection connection;

    public BillingService() {
        this.connection = DatabaseConnection.getConnection();
    }

    // Generate a new invoice for a patient
    public boolean generateInvoice(Invoice invoice) {
        String invoiceId = IDGenerator.generateGenericId("INVS", "invoice", connection);
        if (invoiceId == null) {
            System.err.println("Invoice failed: could not generate invoice ID.");
            return false;
        }

        invoice.setInvoiceId(invoiceId);

        String query = "INSERT INTO invoice (invoice_id, patient_id, receptionist_id, " +
                       "total_amount, insurance_deduction, amount_due, status) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, invoice.getInvoiceId());
            stmt.setString(2, invoice.getPatientId());
            stmt.setString(3, invoice.getReceptionistId());
            stmt.setBigDecimal(4, invoice.getTotalAmount() != null ?
                    invoice.getTotalAmount() : BigDecimal.ZERO);
            stmt.setBigDecimal(5, invoice.getInsuranceDeduction() != null ?
                    invoice.getInsuranceDeduction() : BigDecimal.ZERO);
            stmt.setBigDecimal(6, invoice.getAmountDue() != null ?
                    invoice.getAmountDue() : BigDecimal.ZERO);
            stmt.setString(7, "generated");
            stmt.executeUpdate();

            System.out.println("Invoice generated: " + invoiceId);
            return true;

        } catch (SQLException e) {
            System.err.println("BillingService generateInvoice error: " + e.getMessage());
            return false;
        }
    }

    // Add an item to an invoice
    public boolean addInvoiceItem(InvoiceItem item) {
        String query = "INSERT INTO invoice_item (invoice_id, description, charge_type, amount) " +
                       "VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, item.getInvoiceId());
            stmt.setString(2, item.getDescription());
            stmt.setString(3, item.getChargeType());
            stmt.setBigDecimal(4, item.getAmount());
            stmt.executeUpdate();

            // Recalculate total after adding item
            recalculateInvoiceTotal(item.getInvoiceId());

            System.out.println("Invoice item added to: " + item.getInvoiceId());
            return true;

        } catch (SQLException e) {
            System.err.println("BillingService addInvoiceItem error: " + e.getMessage());
            return false;
        }
    }

    // Get invoice by ID
    public Invoice getInvoiceById(String invoiceId) {
        String query = "SELECT * FROM invoice WHERE invoice_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, invoiceId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToInvoice(rs);
            }
        } catch (SQLException e) {
            System.err.println("BillingService getInvoiceById error: " + e.getMessage());
        }

        return null;
    }

    // Get all invoices for a patient
    public List<Invoice> getInvoicesByPatient(String patientId) {
        List<Invoice> invoices = new ArrayList<>();
        String query = "SELECT * FROM invoice WHERE patient_id = ? ORDER BY created_at DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                invoices.add(mapResultSetToInvoice(rs));
            }
        } catch (SQLException e) {
            System.err.println("BillingService getInvoicesByPatient error: " + e.getMessage());
        }

        return invoices;
    }

    // Get all invoice items for an invoice
    public List<InvoiceItem> getInvoiceItems(String invoiceId) {
        List<InvoiceItem> items = new ArrayList<>();
        String query = "SELECT * FROM invoice_item WHERE invoice_id = ? ORDER BY created_at ASC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, invoiceId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                items.add(mapResultSetToInvoiceItem(rs));
            }
        } catch (SQLException e) {
            System.err.println("BillingService getInvoiceItems error: " + e.getMessage());
        }

        return items;
    }

    // Apply insurance deduction to invoice
    public boolean applyInsuranceDeduction(String invoiceId, BigDecimal deduction) {
        String query = "UPDATE invoice SET insurance_deduction = ?, " +
                       "amount_due = total_amount - ? WHERE invoice_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setBigDecimal(1, deduction);
            stmt.setBigDecimal(2, deduction);
            stmt.setString(3, invoiceId);
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("BillingService applyInsuranceDeduction error: " + e.getMessage());
            return false;
        }
    }

    // Process a payment
    public boolean processPayment(Payment payment) {
        Invoice invoice = getInvoiceById(payment.getInvoiceId());
        if (invoice == null) {
            System.err.println("Payment failed: invoice not found.");
            return false;
        }

        if (invoice.getStatus().equals("settled")) {
            System.err.println("Payment failed: invoice already settled.");
            return false;
        }

        String query = "INSERT INTO payment (invoice_id, amount_paid, payment_method, " +
                       "payment_status, processed_by) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, payment.getInvoiceId());
            stmt.setBigDecimal(2, payment.getAmountPaid());
            stmt.setString(3, payment.getPaymentMethod());
            stmt.setString(4, "successful");
            stmt.setString(5, payment.getProcessedBy());
            stmt.executeUpdate();

            // Update invoice status
            updateInvoiceStatus(payment.getInvoiceId(), payment.getAmountPaid(), invoice.getAmountDue());

            // Generate receipt
            generateReceipt(payment.getInvoiceId(), payment.getAmountPaid());

            System.out.println("Payment processed for invoice: " + payment.getInvoiceId());
            return true;

        } catch (SQLException e) {
            System.err.println("BillingService processPayment error: " + e.getMessage());
            return false;
        }
    }

    // Get all payments for an invoice
    public List<Payment> getPaymentsByInvoice(String invoiceId) {
        List<Payment> payments = new ArrayList<>();
        String query = "SELECT * FROM payment WHERE invoice_id = ? ORDER BY paid_at DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, invoiceId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                payments.add(mapResultSetToPayment(rs));
            }
        } catch (SQLException e) {
            System.err.println("BillingService getPaymentsByInvoice error: " + e.getMessage());
        }

        return payments;
    }

    // Get all unpaid invoices
    public List<Invoice> getUnpaidInvoices() {
        List<Invoice> invoices = new ArrayList<>();
        String query = "SELECT * FROM invoice WHERE status IN ('generated', 'partially_paid') " +
                       "ORDER BY created_at ASC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                invoices.add(mapResultSetToInvoice(rs));
            }
        } catch (SQLException e) {
            System.err.println("BillingService getUnpaidInvoices error: " + e.getMessage());
        }

        return invoices;
    }

    // Update outstanding bill on patient record
    public boolean updatePatientOutstandingBill(String patientId, BigDecimal amount) {
        String query = "UPDATE patient SET outstanding_bill_amount = ? WHERE patient_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setBigDecimal(1, amount);
            stmt.setString(2, patientId);
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("BillingService updatePatientOutstandingBill error: " + e.getMessage());
            return false;
        }
    }

    // Recalculate invoice total after adding items — private helper
    private void recalculateInvoiceTotal(String invoiceId) {
        String query = "UPDATE invoice SET total_amount = (" +
                       "SELECT COALESCE(SUM(amount), 0) FROM invoice_item WHERE invoice_id = ?), " +
                       "amount_due = (" +
                       "SELECT COALESCE(SUM(amount), 0) FROM invoice_item WHERE invoice_id = ?) " +
                       "- insurance_deduction WHERE invoice_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, invoiceId);
            stmt.setString(2, invoiceId);
            stmt.setString(3, invoiceId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("BillingService recalculateInvoiceTotal error: " + e.getMessage());
        }
    }

    // Update invoice status based on payment — private helper
    private void updateInvoiceStatus(String invoiceId, BigDecimal amountPaid, BigDecimal amountDue) {
        String status = amountPaid.compareTo(amountDue) >= 0 ? "settled" : "partially_paid";
        String query = "UPDATE invoice SET status = ? WHERE invoice_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setString(2, invoiceId);
            stmt.executeUpdate();
            System.out.println("Invoice " + invoiceId + " status → " + status);
        } catch (SQLException e) {
            System.err.println("BillingService updateInvoiceStatus error: " + e.getMessage());
        }
    }

    // Generate receipt after payment — private helper
    private void generateReceipt(String invoiceId, BigDecimal amountPaid) {
        String getPaymentQuery = "SELECT payment_id FROM payment WHERE invoice_id = ? " +
                                 "ORDER BY paid_at DESC LIMIT 1";

        try (PreparedStatement stmt = connection.prepareStatement(getPaymentQuery)) {
            stmt.setString(1, invoiceId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int paymentId = rs.getInt("payment_id");
                String receiptQuery = "INSERT INTO receipt (payment_id, invoice_id, amount_paid) " +
                                      "VALUES (?, ?, ?)";
                try (PreparedStatement receiptStmt = connection.prepareStatement(receiptQuery)) {
                    receiptStmt.setInt(1, paymentId);
                    receiptStmt.setString(2, invoiceId);
                    receiptStmt.setBigDecimal(3, amountPaid);
                    receiptStmt.executeUpdate();
                    System.out.println("Receipt generated for invoice: " + invoiceId);
                }
            }
        } catch (SQLException e) {
            System.err.println("BillingService generateReceipt error: " + e.getMessage());
        }
    }

    // Map ResultSet to Invoice
    private Invoice mapResultSetToInvoice(ResultSet rs) throws SQLException {
        Invoice invoice = new Invoice();
        invoice.setInvoiceId(rs.getString("invoice_id"));
        invoice.setPatientId(rs.getString("patient_id"));
        invoice.setReceptionistId(rs.getString("receptionist_id"));
        invoice.setTotalAmount(rs.getBigDecimal("total_amount"));
        invoice.setInsuranceDeduction(rs.getBigDecimal("insurance_deduction"));
        invoice.setAmountDue(rs.getBigDecimal("amount_due"));
        invoice.setStatus(rs.getString("status"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) invoice.setCreatedAt(createdAt.toLocalDateTime());

        return invoice;
    }

    // Map ResultSet to InvoiceItem
    private InvoiceItem mapResultSetToInvoiceItem(ResultSet rs) throws SQLException {
        InvoiceItem item = new InvoiceItem();
        item.setItemId(rs.getInt("item_id"));
        item.setInvoiceId(rs.getString("invoice_id"));
        item.setDescription(rs.getString("description"));
        item.setChargeType(rs.getString("charge_type"));
        item.setAmount(rs.getBigDecimal("amount"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) item.setCreatedAt(createdAt.toLocalDateTime());

        return item;
    }

    // Map ResultSet to Payment
    private Payment mapResultSetToPayment(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setPaymentId(rs.getInt("payment_id"));
        payment.setInvoiceId(rs.getString("invoice_id"));
        payment.setAmountPaid(rs.getBigDecimal("amount_paid"));
        payment.setPaymentMethod(rs.getString("payment_method"));
        payment.setPaymentStatus(rs.getString("payment_status"));
        payment.setProcessedBy(rs.getString("processed_by"));

        Timestamp paidAt = rs.getTimestamp("paid_at");
        if (paidAt != null) payment.setPaidAt(paidAt.toLocalDateTime());

        return payment;
    }
}