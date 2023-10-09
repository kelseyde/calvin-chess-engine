package com.kelseyde.calvin.api.uci;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
record ApplicationShutdownManager(ApplicationContext appContext) {

    public void initiateShutdown(int returnCode) {
        System.exit(SpringApplication.exit(appContext, () -> returnCode));
    }

}
