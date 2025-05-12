package Analysis.Rule;

import Meta.LoginEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// 暴力破解检测规则
public class BruteForceDetectionRule extends AbstractRule{
    private static final int LOGIN_FAILURE_THRESHOLD = 5; // 失败次数阈值
    private static final long TIME_WINDOW = 300000; // 时间窗口(5分钟)

    private Map<String, LoginAttemptTracker> loginTrackers = new ConcurrentHashMap<>();
    private ScheduledExecutorService cleanupService;

    public BruteForceDetectionRule() {
        super("RULE_BRUTE_FORCE", "Brute Force Login Detection",
                "Detects brute force login attempts based on failed logins", 300);

        // 启动清理任务，移除过期的跟踪器
        cleanupService = Executors.newSingleThreadScheduledExecutor();
        cleanupService.scheduleAtFixedRate(this::cleanupExpiredTrackers, 60, 60, TimeUnit.SECONDS);
    }

    @Override
    public RuleResult evaluate(RuleContext context) {
        LoginEvent loginEvent = context.getFact("login_event", LoginEvent.class);
        if (loginEvent == null || loginEvent.isSuccess()) {
            return RuleResult.notTriggered(getId(), getName());
        }

        String username = loginEvent.getUsername();
        String clientIp = loginEvent.getClientIp();

        // 获取或创建登录尝试跟踪器
        String trackerKey = username + "@" + clientIp;
        LoginAttemptTracker tracker = loginTrackers.computeIfAbsent(
                trackerKey, k -> new LoginAttemptTracker(username, clientIp)
        );

        // 记录失败尝试
        boolean bruteForceDetected = tracker.recordFailure();

        if (bruteForceDetected) {
            String message = String.format(
                    "Brute force attempt detected from %s for user %s: %d failures in %d minutes",
                    clientIp, username, tracker.getFailureCount(), TIME_WINDOW / 60000);

            RuleResult result = RuleResult.triggered(getId(), getName(), message, 4);
            result.addAdditionalInfo("clientIp", clientIp);
            result.addAdditionalInfo("username", username);
            result.addAdditionalInfo("failureCount", tracker.getFailureCount());
            result.addAdditionalInfo("lastAttemptTime", tracker.getLastAttemptTime());

            return result;
        }

        return RuleResult.notTriggered(getId(), getName());
    }

    private void cleanupExpiredTrackers() {
        long currentTime = System.currentTimeMillis();
        loginTrackers.entrySet().removeIf(entry ->
                currentTime - entry.getValue().getLastAttemptTime() > TIME_WINDOW);
    }

    // 登录尝试跟踪器
    private static class LoginAttemptTracker {
        private String username;
        private String clientIp;
        private int failureCount;
        private long firstFailureTime;
        private long lastAttemptTime;

        public LoginAttemptTracker(String username, String clientIp) {
            this.username = username;
            this.clientIp = clientIp;
            this.firstFailureTime = System.currentTimeMillis();
            this.lastAttemptTime = firstFailureTime;
        }

        public synchronized boolean recordFailure() {
            lastAttemptTime = System.currentTimeMillis();
            failureCount++;

            // 重置时间窗口
            if (lastAttemptTime - firstFailureTime > TIME_WINDOW) {
                failureCount = 1;
                firstFailureTime = lastAttemptTime;
            }

            return failureCount >= LOGIN_FAILURE_THRESHOLD;
        }

        public int getFailureCount() {
            return failureCount;
        }

        public long getLastAttemptTime() {
            return lastAttemptTime;
        }
    }
}