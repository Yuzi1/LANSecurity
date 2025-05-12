package Application;

import Analysis.Rule.RuleEngine;
import Analysis.Rule.TrafficAnomalyRule;
import Analysis.Service.AIService;

public class SecuritySystemMain {
    public static void main(String[] args) {
        // 初始化AI服务
        AIService aiService = new AIService();

        // 创建规则引擎并注入AI服务
        RuleEngine engine = new RuleEngine();
        engine.addRule(new TrafficAnomalyRule(baselineService, aiService));
        engine.addRule(new DNNAttackRule(aiService));
    }
    }
}