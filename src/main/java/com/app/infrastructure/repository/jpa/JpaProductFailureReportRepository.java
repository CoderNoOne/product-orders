package com.app.infrastructure.repository.jpa;

import com.app.domain.entity.ProductFailureOnGuaranteeReport;
import com.app.domain.entity.ProductFailureReport;
import com.app.domain.entity.ProductFailureWithGuaranteeExpiredReport;
import com.app.domain.enums.ProductFailureReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public interface JpaProductFailureReportRepository extends JpaRepository<ProductFailureReport, Long> {
    List<ProductFailureReport> findAllByProductOrderCustomerUsername(String username);

    default boolean isAnyConfirmedComplaintInProgressForProductOrderById(Long productOrderId, Date todayDate) {

        Optional<ProductFailureReport> optional = findById(productOrderId);

        AtomicBoolean returnValue = new AtomicBoolean(false);
        optional
                .ifPresent(
                        productFailureReport -> {
                            if (productFailureReport instanceof ProductFailureOnGuaranteeReport productFailureOnGuaranteeReport) {
                                returnValue.set(productFailureOnGuaranteeReport.getCompletionDate().compareTo(LocalDate.now()) >= 0);
                            } else if (productFailureReport instanceof ProductFailureWithGuaranteeExpiredReport productFailureWithGuaranteeExpiredReport) {
                                returnValue.set(Objects.equals(productFailureWithGuaranteeExpiredReport.getStatus(), ProductFailureReportStatus.ACCEPTED) &&
                                        productFailureWithGuaranteeExpiredReport.getCompletionDate().compareTo(LocalDate.now()) >= 0);
                            }
                        }
                );

        return returnValue.get();
    }

    @Query(value = "select p from ProductFailureReport p where p.productOrder.customer.manager.username = :username")
    List<ProductFailureReport> findAllByManagerUsername(String username);

}
