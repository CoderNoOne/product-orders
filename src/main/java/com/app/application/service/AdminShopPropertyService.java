package com.app.application.service;

import com.app.application.validators.impl.CreateAdminShopPropertyDtoValidator;
import com.app.application.validators.impl.UpdateAdminShopPropertyDtoValidator;
import com.app.domain.entity.AdminShopProperty;
import com.app.domain.enums.AdminShopPropertyName;
import com.app.domain.repository.AdminShopPropertyRepository;
import com.app.infrastructure.dto.AdminShopPropertyDto;
import com.app.infrastructure.dto.CreateAdminShopPropertyDto;
import com.app.infrastructure.dto.UpdateAdminShopPropertyDto;
import com.app.infrastructure.exception.NotFoundException;
import com.app.infrastructure.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminShopPropertyService {

    private final AdminShopPropertyRepository adminShopPropertyRepository;
    private final CreateAdminShopPropertyDtoValidator createAdminShopPropertyDtoValidator;
    private final UpdateAdminShopPropertyDtoValidator updateAdminShopPropertyDtoValidator;

    public Long addProperty(CreateAdminShopPropertyDto createAdminShopPropertyDto) {

        var errors = createAdminShopPropertyDtoValidator.validate(createAdminShopPropertyDto);
        if (createAdminShopPropertyDtoValidator.hasErrors()) {
            throw new ValidationException(Validations.createErrorMessage(errors));
        }


        var propertyIdWrapper = new AtomicLong();
        adminShopPropertyRepository.findByProperty(AdminShopPropertyName.valueOf(createAdminShopPropertyDto.getProperty()))
                .ifPresentOrElse(adminShopProperty ->
                        {
                            adminShopProperty.setValue(createAdminShopPropertyDto.getValue());
                            propertyIdWrapper.set(adminShopProperty.getId());
                        },
                        () -> {
                            AdminShopProperty saved = adminShopPropertyRepository.save(AdminShopProperty.builder()
                                    .property(AdminShopPropertyName.valueOf(createAdminShopPropertyDto.getProperty()))
                                    .value(createAdminShopPropertyDto.getValue())
                                    .build());
                            propertyIdWrapper.set(saved.getId());
                        }
                );

        return propertyIdWrapper.get();
    }


    public List<AdminShopPropertyDto> getAllProperties() {
        return adminShopPropertyRepository.findAll()
                .stream()
                .map(AdminShopProperty::toDto)
                .collect(Collectors.toList());
    }

    public BigDecimal getPropertyValueByName(String propertyName) {

        if (Objects.isNull(propertyName)) {
            throw new ValidationException("Property name is null");
        } else if (Arrays.stream(AdminShopPropertyName.values())
                .map(AdminShopPropertyName::name)
                .noneMatch(adminShopPropertyName -> Objects.equals(adminShopPropertyName, propertyName))) {
            throw new ValidationException("Not valid property name");
        }

        var valueWrapper = new AtomicReference<BigDecimal>();
        adminShopPropertyRepository.findByProperty(AdminShopPropertyName.valueOf(propertyName))
                .ifPresentOrElse(property -> valueWrapper.set(property.getValue()),
                        () -> {
                            throw new NotFoundException("No value for propery: " + propertyName + " found");
                        });

        return valueWrapper.get();
    }

    public Long updatePropertyValue(UpdateAdminShopPropertyDto updateAdminShopPropertyDto) {

        var errors = updateAdminShopPropertyDtoValidator.validate(updateAdminShopPropertyDto);
        if (updateAdminShopPropertyDtoValidator.hasErrors()) {
            throw new ValidationException(Validations.createErrorMessage(errors));
        }

        var idWrapper = new AtomicLong();

        adminShopPropertyRepository.findByProperty(AdminShopPropertyName.valueOf(updateAdminShopPropertyDto.getPropertyName()))
                .ifPresentOrElse(
                        adminShopProperty -> {
                            adminShopProperty.setValue(updateAdminShopPropertyDto.getValue());
                            idWrapper.set(adminShopProperty.getId());
                        }, () -> {
                            throw new NotFoundException("Property: " + updateAdminShopPropertyDto.getPropertyName() + " not found");
                        }

                );

        return idWrapper.get();
    }

    public void deletePropertyValue(String propertyName) {

        if (Objects.isNull(propertyName)) {
            throw new ValidationException(Validations.createErrorMessage(Map.of("Property name", "is null")));
        } else if (Arrays.stream(AdminShopPropertyName.values()).map(AdminShopPropertyName::name).noneMatch(adminProperty -> Objects.equals(adminProperty, propertyName))) {
            throw new ValidationException(Validations.createErrorMessage(Map.of("Property name", "is not valid")));
        }

        adminShopPropertyRepository.findByProperty(AdminShopPropertyName.valueOf(propertyName))
                .ifPresentOrElse(
                        adminShopPropertyRepository::delete
                        , () -> {
                            throw new NotFoundException("No value for property: " + propertyName + " found");
                        }
                );
    }
}
