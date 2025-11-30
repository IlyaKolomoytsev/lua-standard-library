package com.luajvm;

import java.util.List;
import java.util.Map;
import java.util.function.Function;


class LuaValue {
    private static final String NIL = "nil";
    private static final String BOOL = "bool";
    private static final String NUMBER = "number";
    private static final String STRING = "string";
    private static final String FUNCTION = "function";
    private static final String TABLE = "table";

    public static void assignment(List<LuaValue> left, List<LuaValue> right) {
        int leftSize = left.size();
        int rightSize = right.size();
        int size = Math.max(leftSize, rightSize);
        for (int i = 0; i < size; i++) {
            left.get(i).setValue(right.get(i));
        }
    }

    LuaValue() {
        type = Type.nil;
    }

    LuaValue(LuaValue value) {
        setValue(value);
    }

    LuaValue(boolean value) {
        setValue(value);
    }

    LuaValue(int value) {
        setValue(value);
    }

    LuaValue(long value) {
        setValue(value);
    }

    LuaValue(float value) {
        setValue(value);
    }

    LuaValue(double value) {
        setValue(value);
    }

    LuaValue(String value) {
        setValue(value);
    }

    LuaValue(Function<List<LuaValue>, List<LuaValue>> value) {
        setValue(value);
    }

    LuaValue(Map<LuaValue, LuaValue> value) {
        setValue(value);
    }

    public boolean isNil() {
        return type == Type.nil;
    }

    public boolean isBoolValue() {
        return type == Type.bool;
    }

    public boolean isIntegerValue() {
        return type == Type.integer;
    }

    public boolean isRealValue() {
        return type == Type.real;
    }

    public boolean isNumber() {
        return isIntegerValue() || isRealValue();
    }

    public boolean isStringValue() {
        return type == Type.string;
    }

    public boolean isFunctionValue() {
        return type == Type.function;
    }

    public boolean isTableValue() {
        return type == Type.table;
    }

    public void setValue(LuaValue value) {
        type = value.type;
        switch (value.type) {
            case nil:
                break;
            case bool:
                boolValue = value.boolValue;
                break;
            case integer:
                integerValue = value.integerValue;
                break;
            case real:
                realValue = value.realValue;
                break;
            case string:
                stringValue = value.stringValue;
                break;
            case function:
                functionValue = value.functionValue;
                break;
            case table:
                tableValue = value.tableValue;
                break;
        }
    }

    public void setValue(boolean value) {
        type = Type.bool;
        boolValue = value;
    }

    public void setValue(long value) {
        type = Type.integer;
        integerValue = value;
    }

    public void setValue(double value) {
        type = Type.real;
        realValue = value;
    }

    public void setValue(String value) {
        type = Type.string;
        stringValue = value;
    }

    public void setValue(Function<List<LuaValue>, List<LuaValue>> value) {
        type = Type.function;
        functionValue = value;
    }

    public void setValue(Map<LuaValue, LuaValue> value) {
        type = Type.table;
        tableValue = value;
    }

    public void setMetatable(LuaValue metatable) {
        this.metatable = metatable;
    }

    public Type getType() {
        return type;
    }

    String getTypeString() {
        return switch (type) {
            case nil -> NIL;
            case bool -> BOOL;
            case integer, real -> NUMBER;
            case string -> STRING;
            case function -> FUNCTION;
            case table -> TABLE;
        };
    }

    public boolean getBoolValue() {
        if (!isBoolValue()) {
            throwCantGetPrimitiveValue(BOOL);
        }
        return boolValue;
    }

    public long getIntegerValue() {
        if (!isIntegerValue()) {
            throwCantGetPrimitiveValue(NUMBER);
        }
        return integerValue;
    }

    public double getRealValue() {
        if (!isNumber()) {
            throwCantGetPrimitiveValue(NUMBER);
        }
        if (type == Type.integer) {
            return integerValue;
        } else {
            return realValue;
        }
    }

    public String getStringValue() {
        if (!isStringValue()) {
            throwCantGetPrimitiveValue(STRING);
        }
        return stringValue;
    }

    public Function<List<LuaValue>, List<LuaValue>> getFunctionValue() {
        if (isFunctionValue()) {
            throwCantGetPrimitiveValue(FUNCTION);
        }
        return functionValue;
    }

    public Map<LuaValue, LuaValue> getTableValue() {
        if (isTableValue()) {
            throwCantGetPrimitiveValue(TABLE);
        }
        return tableValue;
    }

    public LuaValue getMetatable() {
        if (metatable == null || type != Type.table) {
            return new LuaValue();
        } else {
            return metatable;
        }
    }

    private void throwCantGetPrimitiveValue(String primitive) {
        throw new IllegalStateException("Can't return " + primitive + "value for'" + getTypeString() + "'.");
    }

    enum Type {
        nil,
        bool,
        integer,
        real,
        string,
        function,
        table,
    }

    private Type type = Type.nil;
    private boolean boolValue = false;
    private long integerValue = 0;
    private double realValue = 0;
    private String stringValue = null;
    private Function<List<LuaValue>, List<LuaValue>> functionValue = null;
    private Map<LuaValue, LuaValue> tableValue = null;
    private LuaValue metatable = null;
}