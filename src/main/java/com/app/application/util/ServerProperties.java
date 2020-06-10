//package com.app.application.util;
//
//import com.app.infrastructure.security.dto.AppError;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.core.env.Environment;
//import org.springframework.stereotype.Component;
//
//import java.net.InetAddress;
//import java.net.UnknownHostException;
//import java.util.Arrays;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class ServerProperties {
//
//    private final Environment environment;
//
//    public String getFullUrl(String requestMapping) {
//
//        return "https://" + getServerHost() + ":" + getServerPort() + "/" + requestMapping;
//    }
//
//    private String getServerPort() {
//        return environment.getProperty("server.port");
//    }
//
//    private String getServerHost() {
//        try {
//            return InetAddress.getLocalHost().getHostAddress();
//        } catch (UnknownHostException e) {
//            log.error(Arrays.toString(e.getStackTrace()));
//            throw new RuntimeException("Host is not known");
//            // TODO: 10.06.2020 exception handler
//        }
//
//    }
//}
