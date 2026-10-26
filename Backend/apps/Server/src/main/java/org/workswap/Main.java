package org.workswap;

import org.workswap.server.ServerApplication;

import io.github.kirya343.terminal.runtime.TerminalRuntime;

public class Main {
    
    public static void main(String[] args) {
        TerminalRuntime.launch(ServerApplication.class, args);
    }
}
