package com.twogather.twogatherwebbackend.utils;

import java.io.IOException;

public final class ProcessUtils {

    private ProcessUtils() {
    }

    public static boolean isRunningPort(int port) {
        return availablePort(port);
    }

    public static int findAvailableRandomPort() {
        for (int port = 10000; port <= 65535; port++) {
            if (availablePort(port)) {
                return port;
            }
        }
        throw new IllegalArgumentException("사용가능한 포트를 찾을 수 없습니다. (10000 ~ 65535)");
    }

    private static boolean availablePort(int port) {
        try {
            new java.net.Socket("127.0.0.1", port).close();
            return false;
        } catch (IOException e) {
            return true;
        }
    }
}