package Analysis.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

public class RuleEngine {
    private List<Rule> rules = new ArrayList<>();
    private static final Logger logger = LoggerFactory.getLogger(RuleEngine.class);

    //添加规则
    public void addRule(Rule rule) {
        rules.add(rule);
        // 按优先级排序规则
        Collections.sort(rules, Comparator.comparingInt(Rule::getPriority));
    }

    //添加多个规则
    public void addRules(List<Rule> rulesToAdd) {
        rules.addAll(rulesToAdd);
        // 按优先级排序规则
        Collections.sort(rules, Comparator.comparingInt(Rule::getPriority));
    }

    //移除规则
    public boolean removeRule(String ruleId) {
        return rules.removeIf(rule -> rule.getId().equals(ruleId));
    }

    //清空所有规则
    public void clearRules() {
        rules.clear();
    }

    // 获取所有规则
    public List<Rule> getAllRules() {
        return new ArrayList<>(rules);
    }

    //运行所有规则
    public List<RuleResult> runRules(RuleContext context) {
        List<RuleResult> results = new ArrayList<>();

        for (Rule rule : rules) {
            try {
                long startTime = System.currentTimeMillis();
                RuleResult result = rule.evaluate(context);
                long duration = System.currentTimeMillis() - startTime;

                logger.debug("Rule '{}' execution took {}ms", rule.getId(), duration);

                if (result.isTriggered()) {
                    results.add(result);
                    logger.info("Rule triggered: {} - {}", rule.getId(), result.getMessage());
                }
            } catch (Exception e) {
                logger.error("Error executing rule '{}': {}", rule.getId(), e.getMessage(), e);
            }
        }

        return results;
    }
}
