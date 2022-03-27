package ks.model;

public class L1Command {
    private final String _name;

    private final int _level;

    private final String _executorClassName;

    public L1Command(String name, int level, String executorClassName) {
        _name = name;
        _level = level;
        _executorClassName = executorClassName;
    }

    public String getName() {
        return _name;
    }

    public int getLevel() {
        return _level;
    }

    public String getExecutorClassName() {
        return _executorClassName;
    }
}
