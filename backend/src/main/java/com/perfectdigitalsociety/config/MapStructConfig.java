package com.perfectdigitalsociety.config;

import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

@MapperConfig(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    typeConversionPolicy = ReportingPolicy.ERROR
)
public interface MapStructConfig {
    // Global MapStruct configuration
}