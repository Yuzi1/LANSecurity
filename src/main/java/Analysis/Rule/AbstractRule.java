package Analysis.Rule;

public abstract class AbstractRule implements Rule{
    private String id;
    private String name;
    private String description;
    private int priority;

    public AbstractRule(String id, String name, String description, int priority) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.priority = priority;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public int getPriority() {
        return priority;
    }
}
