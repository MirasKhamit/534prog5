package edu.uw.bothell.css.dsl.mass.apps.RangeSearch;

import java.util.Arrays;

public class KDAgents {
    public static final int rangeSearch_ = 0;
    private final int operation;
    private final Object[] arguments;
    private final int param1;
    private final int param2;

    public KDAgents(int operation, Object[] arguments, int param1, int param2) {
        this.operation = operation;
        this.arguments = arguments;
        this.param1 = param1;
        this.param2 = param2;
    }

    public int getOperation() {
        return operation;
    }

    public Object[] getArguments() {
        return Arrays.copyOf(arguments, arguments.length);
    }

    public int getParam1() {
        return param1;
    }

    public int getParam2() {
        return param2;
    }

    @Override
    public String toString() {
        return "KDAgents{" +
                "operation=" + operation +
                ", arguments=" + Arrays.toString(arguments) +
                ", param1=" + param1 +
                ", param2=" + param2 +
                '}';
    }
}
