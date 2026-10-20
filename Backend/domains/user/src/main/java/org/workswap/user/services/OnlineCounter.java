package org.workswap.user.services;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class OnlineCounter {

    private final Map<String, AtomicInteger> activeSessions = new ConcurrentHashMap<>();

    public void userConnected(String sub) {
        activeSessions
            .computeIfAbsent(sub, k -> new AtomicInteger(0))
            .incrementAndGet();
    }

    public void userDisconnected(String sub) {
        activeSessions.computeIfPresent(sub, (k, counter) -> {
            int now = counter.decrementAndGet();
            return now <= 0 ? null : counter; // удалить запись, если больше нет соединений
        });
    }

    public boolean isOnline(String sub) {
        return activeSessions.containsKey(sub);
    }

    public int getConnectionsCount(String sub) {
        return activeSessions.getOrDefault(sub, new AtomicInteger(0)).get();
    }

    public Set<String> getOnlineUsers() {
        return activeSessions.entrySet().stream()
                .filter(e -> e.getValue().get() > 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }
}