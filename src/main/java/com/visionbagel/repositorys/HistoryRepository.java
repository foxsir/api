package com.visionbagel.repositorys;


import com.visionbagel.entitys.History;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ViolationReport;

import java.util.ArrayList;

@ApplicationScoped
public class HistoryRepository extends RepositoryBase<History> {
    private ViolationReport violationReport;

    @Inject
    UserRepository userRepository;

    @Transactional
    public History create(History history) {
        history.user = userRepository.authUser();
        history.persistAndFlush();

        ViolationReport violationReport = new ViolationReport();
        ArrayList<ResteasyConstraintViolation> list = new ArrayList<>(){{
            add(new ResteasyConstraintViolation());
        }};
        violationReport.setPropertyViolations(list);

        return history;
    }
}
