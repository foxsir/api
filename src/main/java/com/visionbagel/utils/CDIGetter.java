package com.visionbagel.utils;

import com.visionbagel.repositorys.UserRepository;
import jakarta.enterprise.inject.spi.CDI;

public class CDIGetter {
    public static UserRepository getUserRepository() {
        return CDI.current().select(UserRepository.class).get();
    }
}
