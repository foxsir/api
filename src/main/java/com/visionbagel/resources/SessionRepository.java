package com.visionbagel.resources;


import com.opensponsor.entitys.CountryCodes;
import com.opensponsor.entitys.User;
import com.opensponsor.payload.LoginBody;
import com.opensponsor.payload.RegisterBody;
import com.opensponsor.utils.GenerateViolationReport;
import com.opensponsor.utils.SecurityTools;
import com.opensponsor.utils.TokenTools;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.resteasy.api.validation.ConstraintType;
import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ViolationReport;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class SessionRepository implements PanacheRepositoryBase<User, UUID> {
    private ViolationReport violationReport;

    @Inject
    SecurityTools securityTools;
    @Inject
    TokenTools tokenTools;

    @Transactional
    public User create(RegisterBody registerBody) {
        User user = new User();
        user.username = registerBody.username;
        user.legalName = registerBody.legalName;
        user.countryCode = CountryCodes.findById(registerBody.countryCode.id);
        user.sex = registerBody.sex;
        user.password = securityTools.generatePassword(registerBody.password);

        user.persistAndFlush();

        ViolationReport violationReport = new ViolationReport();
        ArrayList<ResteasyConstraintViolation> list = new ArrayList<>(){{
            add(new ResteasyConstraintViolation());
        }};
        violationReport.setPropertyViolations(list);

        return user;
    }

    public boolean validOfRegister(RegisterBody registerBody) {
        long count = User.find("username", registerBody.username).count();
        if(count == 0) {
            return true;
        } else {
            GenerateViolationReport generateViolationReport = new GenerateViolationReport();
            generateViolationReport.add(
                new ResteasyConstraintViolation(
                    ConstraintType.Type.PARAMETER, "username", "用户名已经存在", registerBody.username
                )
            );
            this.violationReport = generateViolationReport.build();
            return false;
        }
    }

    @Transactional
    public User login(LoginBody loginBody) {
        Optional<User> user = User.find("username", loginBody.account).firstResultOptional();
        if(user.isEmpty()) {
            user = User.find("email", loginBody.account).firstResultOptional();
        }
        if(user.isPresent()) {
            if(securityTools.matches(loginBody.password, user.get().password)) {
                User authUser = user.get();
                tokenTools.generate(authUser, List.of("User"));
                return authUser;
            }
        }
        return null;
    }

    public ViolationReport getViolationReport() {
        return violationReport;
    }
}
