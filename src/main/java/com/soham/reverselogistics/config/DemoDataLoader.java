package com.soham.reverselogistics.config;

import com.soham.reverselogistics.model.ReturnCategory;
import com.soham.reverselogistics.model.ReturnRequest;
import com.soham.reverselogistics.model.ReturnStatus;
import com.soham.reverselogistics.model.Role;
import com.soham.reverselogistics.model.StatusHistory;
import com.soham.reverselogistics.repository.ReturnRequestRepository;
import com.soham.reverselogistics.repository.StatusHistoryRepository;
import com.soham.reverselogistics.security.DemoUsers;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Fills an empty database with realistic sample returns, so the portal
 * looks alive the first time it starts. Switch off with app.seed-demo-data=false.
 */
@Component
public class DemoDataLoader implements CommandLineRunner {

    private static final String[] PRODUCT_NAMES = {
        "Wireless Mouse", "Bluetooth Headphones", "Running Shoes", "Cotton T-Shirt", "Smart Watch",
        "Laptop Backpack", "Mixer Grinder", "Yoga Mat", "Table Lamp", "Water Bottle", "Phone Case", "Desk Chair Cushion"
    };

    private static final int[] PRODUCT_PRICES = {899, 2499, 3299, 599, 4999, 1599, 3899, 799, 1199, 499, 399, 1299};

    private static final String[] ADDRESSES = {
        "Flat 402, Lake View Apartments, Hiranandani Estate, Thane West, Maharashtra 400607",
        "12, Shivaji Nagar, Near City Mall, Pune, Maharashtra 411005",
        "B-17, Sector 21, Nerul, Navi Mumbai, Maharashtra 400706",
        "204, Green Park Society, Andheri East, Mumbai, Maharashtra 400069",
        "Plot 9, Gandhi Road, Nashik, Maharashtra 422001"
    };

    private static final String[] REASONS = {
        "The product stopped working after two days of normal use.",
        "I received a different model from the one I ordered.",
        "The package arrived with a broken seal and visible damage.",
        "The colour and material look different from the product photos.",
        "The size is too small and does not fit as expected.",
        "I no longer need the item and would like to return it.",
        "Other issue, please see the product for details."
    };

    private static final ReturnStatus[] TARGETS = {
        ReturnStatus.REFUNDED, ReturnStatus.RESTOCKED, ReturnStatus.REFUNDED, ReturnStatus.DISPOSED,
        ReturnStatus.REJECTED, ReturnStatus.REFUNDED, ReturnStatus.RESTOCKED, ReturnStatus.REFUNDED,
        ReturnStatus.REJECTED, ReturnStatus.REFUNDED, ReturnStatus.RESTOCKED, ReturnStatus.INSPECTED,
        ReturnStatus.INSPECTED, ReturnStatus.RECEIVED, ReturnStatus.RECEIVED, ReturnStatus.PICKUP_SCHEDULED,
        ReturnStatus.PICKUP_SCHEDULED, ReturnStatus.PICKUP_SCHEDULED, ReturnStatus.APPROVED, ReturnStatus.APPROVED,
        ReturnStatus.REQUESTED, ReturnStatus.REQUESTED, ReturnStatus.REQUESTED, ReturnStatus.REQUESTED
    };

    private static final ReturnStatus[] MAIN_PATH = {
        ReturnStatus.REQUESTED, ReturnStatus.APPROVED, ReturnStatus.PICKUP_SCHEDULED,
        ReturnStatus.RECEIVED, ReturnStatus.INSPECTED
    };

    private final ReturnRequestRepository requests;
    private final StatusHistoryRepository histories;
    private final boolean enabled;

    public DemoDataLoader(ReturnRequestRepository requests, StatusHistoryRepository histories,
                          @Value("${app.seed-demo-data:true}") boolean enabled) {
        this.requests = requests;
        this.histories = histories;
        this.enabled = enabled;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!enabled || requests.count() > 0) {
            return;
        }
        Random random = new Random(42);
        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < TARGETS.length; i++) {
            ReturnStatus target = TARGETS[i];
            int daysAgo = Math.max(0, 22 - i);
            LocalDateTime created = now.minusDays(daysAgo).minusHours(random.nextInt(8)).withNano(0);
            if (i >= TARGETS.length - 4) {
                created = now.minusHours(2L + random.nextInt(40)).withNano(0);
            }

            int product = random.nextInt(PRODUCT_NAMES.length);
            int quantity = 1 + random.nextInt(2);
            ReturnCategory category = ReturnCategory.values()[random.nextInt(ReturnCategory.values().length)];
            boolean first = i % 2 == 0;
            String username = first ? "customer1" : "customer2";

            List<ReturnStatus> path = pathTo(target);
            LocalDateTime lastChange = created.plusHours(5L * (path.size() - 1));
            if (lastChange.isAfter(now)) {
                lastChange = now.minusMinutes(5);
            }

            ReturnRequest r = new ReturnRequest();
            r.setOrderNumber("ORD-" + (24100 + i * 7));
            r.setCustomerName(DemoUsers.displayName(username));
            r.setProductName(PRODUCT_NAMES[product]);
            r.setQuantity(quantity);
            r.setCategory(category);
            r.setReason(REASONS[category.ordinal()]);
            r.setPickupAddress(ADDRESSES[i % ADDRESSES.length]);
            r.setCreatedBy(username);
            r.setCreatedAt(created);
            r.setUpdatedAt(lastChange);
            r.setStatus(target);

            if (target == ReturnStatus.REJECTED) {
                r.setRemarks("Return window has passed or the item is not eligible for return.");
            }
            if (path.contains(ReturnStatus.PICKUP_SCHEDULED)) {
                r.setPickupDate(created.toLocalDate().plusDays(2));
            }
            if (path.contains(ReturnStatus.INSPECTED)) {
                r.setInspectionRemarks(inspectionText(target));
            }
            if (target == ReturnStatus.REFUNDED) {
                r.setRefundAmount(BigDecimal.valueOf((long) PRODUCT_PRICES[product] * quantity));
            }

            ReturnRequest saved = requests.save(r);

            LocalDateTime when = created;
            ReturnStatus previous = null;
            for (ReturnStatus step : path) {
                String actor = actorFor(step, username);
                Role role = roleFor(step);
                histories.save(new StatusHistory(saved.getId(), previous, step, actor, role,
                        noteFor(step, saved), when));
                previous = step;
                when = when.plusHours(5);
            }
        }
    }

    private List<ReturnStatus> pathTo(ReturnStatus target) {
        List<ReturnStatus> path = new ArrayList<>();
        if (target == ReturnStatus.REJECTED) {
            path.add(ReturnStatus.REQUESTED);
            path.add(ReturnStatus.REJECTED);
            return path;
        }
        for (ReturnStatus step : MAIN_PATH) {
            path.add(step);
            if (step == target) {
                return path;
            }
        }
        path.add(target);
        return path;
    }

    private String actorFor(ReturnStatus step, String customer) {
        if (step == ReturnStatus.REQUESTED) {
            return customer;
        }
        Role role = roleFor(step);
        return role == Role.WAREHOUSE ? "warehouse1" : "manager1";
    }

    private Role roleFor(ReturnStatus step) {
        if (step == ReturnStatus.REQUESTED) {
            return Role.CUSTOMER;
        }
        if (step == ReturnStatus.RECEIVED || step == ReturnStatus.INSPECTED
                || step == ReturnStatus.RESTOCKED || step == ReturnStatus.DISPOSED) {
            return Role.WAREHOUSE;
        }
        return Role.MANAGER;
    }

    private String noteFor(ReturnStatus step, ReturnRequest r) {
        if (step == ReturnStatus.REQUESTED) {
            return "Return request created";
        }
        if (step == ReturnStatus.APPROVED) {
            return "Approved. The item is eligible for return.";
        }
        if (step == ReturnStatus.REJECTED) {
            return r.getRemarks();
        }
        if (step == ReturnStatus.PICKUP_SCHEDULED) {
            LocalDate date = r.getPickupDate();
            return "Pickup scheduled for " + date;
        }
        if (step == ReturnStatus.RECEIVED) {
            return "Item received at the warehouse";
        }
        if (step == ReturnStatus.INSPECTED) {
            return r.getInspectionRemarks();
        }
        if (step == ReturnStatus.REFUNDED) {
            return "Refund of " + r.getRefundAmount() + " issued";
        }
        if (step == ReturnStatus.RESTOCKED) {
            return "Item is in good condition and was returned to stock";
        }
        return "Item could not be resold and was disposed of";
    }

    private String inspectionText(ReturnStatus target) {
        if (target == ReturnStatus.DISPOSED) {
            return "Item is damaged beyond repair and cannot be resold.";
        }
        if (target == ReturnStatus.RESTOCKED) {
            return "Item is in good condition with original packaging.";
        }
        return "Inspection completed. Defect confirmed as reported by the customer.";
    }
}
