package com.soham.reverselogistics;

import com.soham.reverselogistics.dto.ActionForm;
import com.soham.reverselogistics.dto.DashboardStats;
import com.soham.reverselogistics.dto.ReturnRequestForm;
import com.soham.reverselogistics.model.ReturnAction;
import com.soham.reverselogistics.model.ReturnCategory;
import com.soham.reverselogistics.model.ReturnRequest;
import com.soham.reverselogistics.model.ReturnStatus;
import com.soham.reverselogistics.model.Role;
import com.soham.reverselogistics.security.CurrentUser;
import com.soham.reverselogistics.service.ReturnRequestService;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ReturnRequestWorkflowTests {

    @Autowired
    private ReturnRequestService service;

    private final CurrentUser customer = new CurrentUser("customer1", Role.CUSTOMER, "Aarav Mehta");
    private final CurrentUser manager = new CurrentUser("manager1", Role.MANAGER, "Neha Kulkarni");
    private final CurrentUser warehouse = new CurrentUser("warehouse1", Role.WAREHOUSE, "Rahul Deshmukh");

    @Test
    void testEndToEndReturnWorkflow() {
        // 1. Customer creates return request
        ReturnRequestForm form = new ReturnRequestForm();
        form.setOrderNumber("ORD-9999");
        form.setCustomerName("Aarav Mehta");
        form.setProductName("Test Mechanical Keyboard");
        form.setQuantity(1);
        form.setCategory(ReturnCategory.DEFECTIVE);
        form.setReason("Keys are not registering");
        form.setPickupAddress("Flat 101, Test Residency, Mumbai");

        ReturnRequest created = service.create(form, customer);
        Assertions.assertNotNull(created.getId());
        Assertions.assertEquals(ReturnStatus.REQUESTED, created.getStatus());

        // 2. Manager approves
        ActionForm approveForm = new ActionForm();
        ReturnRequest approved = service.applyAction(created.getId(), ReturnAction.APPROVE, approveForm, manager);
        Assertions.assertEquals(ReturnStatus.APPROVED, approved.getStatus());

        // 3. Manager schedules pickup
        ActionForm scheduleForm = new ActionForm();
        scheduleForm.setPickupDate(LocalDate.now().plusDays(2));
        ReturnRequest scheduled = service.applyAction(approved.getId(), ReturnAction.SCHEDULE, scheduleForm, manager);
        Assertions.assertEquals(ReturnStatus.PICKUP_SCHEDULED, scheduled.getStatus());

        // 4. Warehouse receives item
        ActionForm receiveForm = new ActionForm();
        ReturnRequest received = service.applyAction(scheduled.getId(), ReturnAction.RECEIVE, receiveForm, warehouse);
        Assertions.assertEquals(ReturnStatus.RECEIVED, received.getStatus());

        // 5. Warehouse inspects item
        ActionForm inspectForm = new ActionForm();
        inspectForm.setRemarks("Hardware PCB issue verified. Approved for refund.");
        ReturnRequest inspected = service.applyAction(received.getId(), ReturnAction.INSPECT, inspectForm, warehouse);
        Assertions.assertEquals(ReturnStatus.INSPECTED, inspected.getStatus());

        // 6. Manager issues refund
        ActionForm refundForm = new ActionForm();
        refundForm.setRefundAmount(BigDecimal.valueOf(2499.00));
        ReturnRequest refunded = service.applyAction(inspected.getId(), ReturnAction.REFUND, refundForm, manager);
        Assertions.assertEquals(ReturnStatus.REFUNDED, refunded.getStatus());
        Assertions.assertEquals(BigDecimal.valueOf(2499.00), refunded.getRefundAmount());

        // 7. Verify dashboard statistics calculation
        DashboardStats stats = service.getDashboardStats(manager);
        Assertions.assertTrue(stats.getTotalReturns() > 0);
        Assertions.assertTrue(stats.getRefundedCount() > 0);
        Assertions.assertNotNull(stats.getTotalRefunded());
        Assertions.assertFalse(stats.getRecentReturns().isEmpty());
    }
}
