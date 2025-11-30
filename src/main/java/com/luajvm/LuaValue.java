package com.luajvm;

import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LuaValue)) {
            return false;
        }
        LuaValue value = (LuaValue) obj;
        if (value.type != type) {
            return false;
        } else {
            return switch (type) {
                case nil -> true;
                case bool -> value.getBoolValue() == this.getBoolValue();
                case integer, real -> value.getRealValue() == this.getRealValue();
                case string -> value.getStringValue().equals(this.getStringValue());
                case function -> value.getFunctionValue() == this.getFunctionValue(); // compare pointers
                case table -> value.getTableValue() == this.getTableValue(); // compare pointers
            };
        }
    }

    @Override
    public int hashCode() {
        return switch (type) {
            case nil -> Objects.hash(type);
            case bool -> Objects.hash(type, boolValue);
            case integer, real -> Objects.hash(type, getRealValue());
            case string -> Objects.hash(type, stringValue);
            case function -> Objects.hash(type, functionValue);
            case table -> Objects.hash(type, tableValue);
        };
    }

    public static List<LuaValue> add(LuaValue left, LuaValue right) {
        // try to reduce values to numbers
        LuaValue leftNumber = LuaFunctions.toNumber(left).getFirst();
        LuaValue rightNumber = LuaFunctions.toNumber(right).getFirst();
        // use add operation for numbers
        if (leftNumber.isNumber() && rightNumber.isNumber()) {
            LuaValue resultValue; // value of arguments sum
            // sum integer values
            if (leftNumber.isIntegerValue() && rightNumber.isIntegerValue()) {
                long result = leftNumber.getIntegerValue() + rightNumber.getIntegerValue();
                resultValue = new LuaValue(result);
            }
            // sum real values
            else {
                double result = leftNumber.getRealValue() + rightNumber.getRealValue();
                resultValue = new LuaValue(result);
            }
            //return result
            return List.of(resultValue);

        }
        // use metatables functions
        else {
            // get add function from metatables
            LuaValue leftMetatable = left.getMetatable();
            LuaValue rightMetatable = right.getMetatable();
            Function<List<LuaValue>, List<LuaValue>> addFunction = null;
            // use function from left metatable
            boolean addExistInLeft = leftMetatable.isTableValue() && leftMetatable.getTableValue().containsKey(LuaMetatable.ADD_VAlUE);
            boolean addExistInRight = rightMetatable.isTableValue() && rightMetatable.getTableValue().containsKey(LuaMetatable.ADD_VAlUE);
            if (addExistInLeft) {
                addFunction = leftMetatable.getTableValue().get(LuaMetatable.ADD_VAlUE).getFunctionValue();
            }
            // use function from right metatable
            else if (addExistInRight) {
                addFunction = rightMetatable.getTableValue().get(LuaMetatable.ADD_VAlUE).getFunctionValue();
            }
            // throw exception if the "add" function is not found
            else {
                LuaValue unsupportedAddValue = leftNumber.isNumber() ? right : left;
                throw new LuaRuntimeException("perform arithmetic on", unsupportedAddValue);
            }
            // return result of function call
            return addFunction.apply(List.of(left, right));
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
        if (!isFunctionValue()) {
            throwCantGetPrimitiveValue(FUNCTION);
        }
        return functionValue;
    }

    public Map<LuaValue, LuaValue> getTableValue() {
        if (!isTableValue()) {
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