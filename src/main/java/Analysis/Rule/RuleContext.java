package Analysis.Rule;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

//规则上下文
public class RuleContext {
    private Map<String, Object> facts = new HashMap<>();
    //设置事实数据
    public void setFact(String key, Object value) {
        facts.put(key, value);
    }

    //获取事实数据
    public Object getFact(String key) {
        return facts.get(key);
    }

    //获取特定类型的事实数据
    @SuppressWarnings("unchecked")
    public <T> T getFact(String key, Class<T> clazz) {
        Object value = facts.get(key);
        if (value == null) {
            return null;
        }

        if (clazz.isAssignableFrom(value.getClass())) {
            return (T) value;
        }

        throw new ClassCastException("Cannot cast " + value.getClass() + " to " + clazz);
    }

    // 检查是否包含指定的事实
    public boolean hasFact(String key) {
        return facts.containsKey(key);
    }

    //获取所有事实数据
    public Map<String, Object> getAllFacts() {
        return Collections.unmodifiableMap(facts);
    }
}
