package org.workswap.datasource.testers;

import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class QueryCounter implements StatementInspector {

    private static final Logger logger = LoggerFactory.getLogger(QueryCounter.class);

    private final ThreadLocal<Integer> count = ThreadLocal.withInitial(() -> 0);

    @Override
    public String inspect(String sql) {
        int currentCount = count.get() + 1;
        count.set(currentCount);

        logger.debug("Query #{}", currentCount);

        return sql;
    }

    public int getCount() {
        return count.get();
    }

    public void reset() {
        count.set(0);
    }

    public void clear() {
        count.remove();
    }
}