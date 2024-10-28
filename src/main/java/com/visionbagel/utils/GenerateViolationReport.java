package com.visionbagel.utils;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ViolationReport;

import java.util.ArrayList;

@ApplicationScoped
public class GenerateViolationReport {
    private final ArrayList<ResteasyConstraintViolation> violations = new ArrayList<>();
    private String exception;

    public GenerateViolationReport add(ResteasyConstraintViolation resteasyConstraintViolation) {
        violations.add(resteasyConstraintViolation);
        return this;
    }

    public GenerateViolationReport exception(String exception) {
        this.exception = exception;
        return this;
    }

    public ViolationReport build() {
        ViolationReport violationReport = new ViolationReport();
        violationReport.setParameterViolations(this.violations);
        violationReport.setException(this.exception);

        return violationReport;
    }
}
