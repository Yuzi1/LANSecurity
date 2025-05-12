package Analysis.Rule;

// 规则接口
public interface Rule {
    RuleResult evaluate(RuleContext context);       //评估规则
    String getId();                                 //规则ID
    String getName();                               //规则名称
    String getDescription();                        //规则描述
    int getPriority();                              //规则优先级,数值越小优先级越高
}
