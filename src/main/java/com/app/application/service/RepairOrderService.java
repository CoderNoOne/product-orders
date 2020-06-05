//package com.app.application.service;
//
//import com.app.application.validators.impl.CreateRepairOrderDtoValidator;
//import com.app.domain.enums.RepairOrderStatus;
//import com.app.domain.repository.ComplaintRepository;
//import com.app.domain.entity.ProductFailureReport;
//import com.app.domain.repository.CustomerRepository;
//import com.app.domain.repository.RepairOrderRepository;
//import com.app.domain.enums.ComplaintStatus;
//import com.app.infrastructure.dto.CreateRepairOrderDto;
//import com.app.infrastructure.dto.RepairOrderDto;
//import com.app.infrastructure.exception.NotFoundException;
//import com.app.infrastructure.exception.ValidationException;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.concurrent.atomic.AtomicLong;
//import java.util.concurrent.atomic.AtomicReference;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class RepairOrderService {
//
//    private final RepairOrderRepository repairOrderRepository;
//    private final ComplaintRepository complaintRepository;
//    private final CreateRepairOrderDtoValidator createRepairOrderDtoValidator;
//    private final CustomerRepository customerRepository;
//
//    public Long addRepairOrder(String username, CreateRepairOrderDto createRepairOrderDto) {
//
//        var errors = createRepairOrderDtoValidator.validate(createRepairOrderDto);
//
//        if (createRepairOrderDtoValidator.hasErrors()) {
//            throw new ValidationException(Validations.createErrorMessage(errors));
//        }
//
//        var repairOrderId = new AtomicLong();
//        complaintRepository.findByIdAndManagerUsername(createRepairOrderDto.getComplaintId(), username)
//                .ifPresentOrElse(
//                        complaint -> {
//
//                            if (!complaint.getStatus().equals(ComplaintStatus.AWAITING)) {
//                                throw new ValidationException("Complaint status should be: AWAITING");
//                            }
//
//                            repairOrderId.set(repairOrderRepository.save(ProductFailureReport.builder()
//                                    .completionDate(createRepairOrderDto.getCompletionDate())
//                                    // TODO: 04.06.2020
////                                    .repairCosts(createRepairOrderDto.getRepairCosts())
//                                    .status(RepairOrderStatus.PROPOSED)
//                                    .productOrder(complaint.getProductOrder())
//                                    .build()).getId());
//
//                            complaint.setStatus(ComplaintStatus.CONFIRMED);
//                        },
//                        () -> {
//                            throw new NotFoundException("No complaint with id: " + createRepairOrderDto.getComplaintId());
//                        }
//                );
//
//        return repairOrderId.get();
//    }
//
//    public List<RepairOrderDto> getAll(String username) {
//
//        var result = new AtomicReference<List<RepairOrderDto>>(Collections.emptyList());
//
//        customerRepository.findByUsername(username)
//                .ifPresentOrElse(customer ->
//                                result.set(repairOrderRepository.findAllByCustomerUsername(customer.getUsername())
//                                        .stream()
//                                        .map(ProductFailureReport::toDto)
//                                        .collect(Collectors.toList())),
//
//                        () -> result.set(repairOrderRepository.findAllByManagerUsername(username)
//                                .stream()
//                                .map(ProductFailureReport::toDto)
//                                .collect(Collectors.toList()))
//                );
//
//        return result.get();
//    }
//
//    public RepairOrderDto getOne(Long id, String username) {
//
//        var result = new AtomicReference<RepairOrderDto>();
//
//        customerRepository.findByUsername(username)
//                .ifPresentOrElse(customer ->
//                                result.set(repairOrderRepository.findByIdAndCustomerUsername(id, customer.getUsername())
//                                        .map(ProductFailureReport::toDto)
//                                        .orElseThrow(() -> new NotFoundException("No repair order with id: " + id +
//                                                " and customer username: " + username))
//                                ),
//
//                        () -> result.set(repairOrderRepository.findByIdAndManagerUsername(id, username)
//                                .map(ProductFailureReport::toDto)
//                                .orElseThrow(() -> new NotFoundException("No repair order with id: " + id +
//                                        " and manager username: " + username))
//                        ));
//
//        return result.get();
//    }
//}
