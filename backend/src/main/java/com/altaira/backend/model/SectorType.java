package com.altaira.backend.model;

import java.util.Arrays;
import java.util.Locale;

/** Allowed sector type values understood by the backend. */
public enum SectorType {
    /** Represents clinics with serialized value {@code clinics}. */
    CLINICS("clinics"),
    /** Represents restaurants with serialized value {@code restaurants}. */
    RESTAURANTS("restaurants"),
    /** Represents car dealers with serialized value {@code car_dealers}. */
    CAR_DEALERS("car_dealers"),
    /** Represents custom with serialized value {@code custom}. */
    CUSTOM("custom");

    /** Stored or supplied representation. */
    private final String value;

    /**
     * Constructs {@code SectorType} using the supplied value.
     *
     * @param value stored or supplied representation
     */
    SectorType(String value) {
        this.value = value;
    }

    /**
     * Returns the stored or supplied representation for this sector type.
     *
     * @return stored or supplied representation
     */
    public String value() {
        return value;
    }

    /**
     * Resolves the supplied text to a sector type value using this enum’s accepted spellings.
     *
     * @param rawValue value as supplied, before normalization
     * @return the sector type
     */
    public static SectorType parse(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return CUSTOM;
        }

        String normalized = rawValue.trim().toLowerCase(Locale.ROOT)
                .replace("-", "_")
                .replace(" ", "_");

        if ("clinic".equals(normalized) || "clinica".equals(normalized) || "clinicas".equals(normalized)) {
            return CLINICS;
        }

        if ("restaurant".equals(normalized) || "restaurante".equals(normalized) || "restaurantes".equals(normalized)) {
            return RESTAURANTS;
        }

        if ("car_dealer".equals(normalized) || "cardealer".equals(normalized) || "concesionario".equals(normalized) || "concesionarios".equals(normalized)) {
            return CAR_DEALERS;
        }

        if ("a_medida".equals(normalized) || "custom_business".equals(normalized) || "specialty".equals(normalized)) {
            return CUSTOM;
        }

        return Arrays.stream(values())
                .filter(sectorType -> sectorType.value.equals(normalized))
                .findFirst()
                .orElse(CUSTOM);
    }
}
