package data;

public class SameClassTarget {
    public static final InnerCls A = new InnerCls("A");

    private record InnerCls(String s) {}
}
