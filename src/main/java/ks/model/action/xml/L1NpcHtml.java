package ks.model.action.xml;

public class L1NpcHtml {
    public static final L1NpcHtml HTML_CLOSE = new L1NpcHtml("");
    private final String name;
    private final String[] args;

    public L1NpcHtml(String name) {
        this(name, new String[]{});
    }

    public L1NpcHtml(String name, String... args) {
        if (name == null || args == null) {
            throw new NullPointerException();
        }
        this.name = name;
        this.args = args;
    }

    public String getName() {
        return name;
    }

    public String[] getArgs() {
        return args;
    }
}
