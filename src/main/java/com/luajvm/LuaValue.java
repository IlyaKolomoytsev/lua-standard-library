package com.luajvm;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;


public class LuaValue {
    private static final String NIL = "nil";
    private static final String BOOL = "bool";
    private static final String NUMBER = "number";
    private static final String STRING = "string";
    private static final String FUNCTION = "function";
    private static final String TABLE = "table";

    public static final String ADD = "__add";
    public static final String SUB = "__sub";
    public static final String MUL = "__mul";
    public static final String DIV = "__div";
    public static final String MOD = "__mod";
    public static final String POW = "__pow";
    public static final String UNM = "__unm";
    public static final String IDIV = "__idiv";
    public static final String CONCAT = "__concat";
    public static final String LEN = "__len";
    public static final String EQ = "__eq";
    public static final String LT = "__lt";
    public static final String LE = "__le";
    public static final String INDEX = "__index";
    public static final String NEW_INDEX = "__newindex";
    public static final String CALL = "__call";

    public static final LuaValue ADD_VAlUE = new LuaValue(ADD);
    public static final LuaValue SUB_VAlUE = new LuaValue(SUB);
    public static final LuaValue MUL_VAlUE = new LuaValue(MUL);
    public static final LuaValue DIV_VAlUE = new LuaValue(DIV);
    public static final LuaValue MOD_VAlUE = new LuaValue(MOD);
    public static final LuaValue POW_VAlUE = new LuaValue(POW);
    public static final LuaValue UNM_VAlUE = new LuaValue(UNM);
    public static final LuaValue IDIV_VAlUE = new LuaValue(IDIV);
    public static final LuaValue CONCAT_VALUE = new LuaValue(CONCAT);
    public static final LuaValue LEN_VAlUE = new LuaValue(LEN);
    public static final LuaValue EQ_VAlUE = new LuaValue(EQ);
    public static final LuaValue LT_VAlUE = new LuaValue(LT);
    public static final LuaValue LE_VAlUE = new LuaValue(LE);
    public static final LuaValue INDEX_VALUE = new LuaValue(INDEX);
    public static final LuaValue NEW_INDEX_VALUE = new LuaValue(NEW_INDEX);
    public static final LuaValue CALL_VAlUE = new LuaValue(CALL);

    public static final LuaValue NIL_VALUE = new LuaValue();

    public static void assignment(LuaList left, LuaList right) {
        int leftSize = left.size();
        int rightSize = right.size();

        LuaValue[] temp = new LuaValue[leftSize];
        for (int i = 0; i < leftSize; i++) {
            if (i < rightSize) {
                temp[i] = new LuaValue(right.get(i));
            } else {
                temp[i] = new LuaValue();
            }
        }

        for (int i = 0; i < leftSize; i++) {
            LuaValue target = left.get(i);
            target.setValue(temp[i]);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LuaValue)) {
            return false;
        }
        LuaValue value = (LuaValue) obj;
        if (value.type != type && !(this.isNumber() && value.isNumber())) {
            return false;
        } else {
            boolean result = false;
            switch (type) {
                case nil -> result = true;
                case bool -> result = value.getBoolValue() == this.getBoolValue();
                case integer, real -> result = value.getRealValue() == this.getRealValue();
                case string -> result = value.getStringValue().equals(this.getStringValue());
                case function -> result = value.getFunctionValue() == this.getFunctionValue(); // compare pointers
                case table -> {
                    Function<LuaList, LuaList> metamethod = getFunctionFromMetatable(this, value, EQ_VAlUE);
                    if (metamethod == null) {
                        result = value.getTableValue() == this.getTableValue(); // compare pointers
                    } else {
                        LuaValue metamethodResult = metamethod.apply(new LuaList(List.of(this, value))).getFirst();
                        result = metamethodResult.getBoolValue(true);
                    }
                }
            }
            return result;
        }
    }


    @Override
    public String toString() {
        switch (type) {
            case nil -> {
                return "nil";
            }
            case bool -> {
                return boolValue ? "true" : "false";
            }
            case integer -> {
                return String.valueOf(integerValue);
            }
            case real -> {
                return String.valueOf(realValue);
            }
            case string -> {
                return stringValue;
            }
            case function -> {
                return "function: " + Integer.toHexString(System.identityHashCode(functionValue));
            }
            case table -> {
                return "table: " + Integer.toHexString(System.identityHashCode(tableValue));
            }
            default -> throw new IllegalArgumentException("Unknown type: " + type);
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

    private static LuaValue arithmeticOperation(LuaValue left, LuaValue right, LuaValue metamethod, BiFunction<LuaValue, LuaValue, LuaValue> operationForNumbers) {
        // try to reduce values to numbers
        LuaValue leftNumber = LuaFunctions.toNumber(left).getFirst();
        LuaValue rightNumber = LuaFunctions.toNumber(right).getFirst();
        // use operation for numbers
        if (leftNumber.isNumber() && rightNumber.isNumber()) {
            return operationForNumbers.apply(leftNumber, rightNumber);
        }
        // use metatables functions
        else {
            // get function from metatables
            Function<LuaList, LuaList> function = getFunctionFromMetatable(left, right, metamethod);
            // throw exception if function does not exist
            if (function == null) {
                LuaValue unsupportedAddValue = leftNumber.isNumber() ? right : left;
                throw new LuaRuntimeException("perform arithmetic on", unsupportedAddValue);
            }
            // return result of function call
            return function.apply(new LuaList(List.of(left, right))).getFirst();
        }
    }

    private static Function<LuaList, LuaList> getFunctionFromMetatable(LuaValue left, LuaValue right, LuaValue metamethodKey) {
        // get function from metatables
        LuaValue leftMetatable = left.getMetatable();
        LuaValue rightMetatable = right.getMetatable();
        LuaValue metamethod;
        boolean addExistInLeft = leftMetatable.isTableValue() && leftMetatable.getTableValue().containsKey(metamethodKey);
        boolean addExistInRight = rightMetatable.isTableValue() && rightMetatable.getTableValue().containsKey(metamethodKey);
        // use function from left metatable
        if (addExistInLeft) {
            metamethod = leftMetatable.getTableValue().get(metamethodKey);
        }
        // use function from right metatable
        else if (addExistInRight) {
            metamethod = rightMetatable.getTableValue().get(metamethodKey);
        }
        // return null if metamethod not found
        else {
            return null;
        }

        // return function if metamethod is function value
        if (metamethod.isFunctionValue()) {
            return metamethod.getFunctionValue();
        }
        // otherwise throw exception
        else {
            throw new LuaRuntimeException("call", metamethod);
        }
    }

    public static LuaValue add(LuaValue left, LuaValue right) {
        BiFunction<LuaValue, LuaValue, LuaValue> addNumbersFunction = (leftNumber, rightNumber) -> {
            // sum integer values
            if (leftNumber.isIntegerValue() && rightNumber.isIntegerValue()) {
                long result = leftNumber.getIntegerValue() + rightNumber.getIntegerValue();
                return new LuaValue(result);
            }
            // sum real values
            else {
                double result = leftNumber.getRealValue() + rightNumber.getRealValue();
                return new LuaValue(result);
            }
        };
        return arithmeticOperation(left, right, ADD_VAlUE, addNumbersFunction);
    }

    public static LuaValue sub(LuaValue left, LuaValue right) {
        BiFunction<LuaValue, LuaValue, LuaValue> div = (leftNumber, rightNumber) -> {
            // difference integer values
            if (leftNumber.isIntegerValue() && rightNumber.isIntegerValue()) {
                long result = leftNumber.getIntegerValue() - rightNumber.getIntegerValue();
                return new LuaValue(result);
            }
            // difference real values
            else {
                double result = leftNumber.getRealValue() - rightNumber.getRealValue();
                return new LuaValue(result);
            }
        };
        return arithmeticOperation(left, right, SUB_VAlUE, div);
    }

    public static LuaValue mul(LuaValue left, LuaValue right) {
        BiFunction<LuaValue, LuaValue, LuaValue> mulNumbersFunction = (leftNumber, rightNumber) -> {
            // product of integer values
            if (leftNumber.isIntegerValue() && rightNumber.isIntegerValue()) {
                long result = leftNumber.getIntegerValue() * rightNumber.getIntegerValue();
                return new LuaValue(result);
            }
            // product of real values
            else {
                double result = leftNumber.getRealValue() * rightNumber.getRealValue();
                return new LuaValue(result);
            }
        };
        return arithmeticOperation(left, right, MUL_VAlUE, mulNumbersFunction);
    }

    public static LuaValue div(LuaValue left, LuaValue right) {
        BiFunction<LuaValue, LuaValue, LuaValue> divNumbersFunction = (leftNumber, rightNumber) -> {
            double result = leftNumber.getRealValue() / rightNumber.getRealValue();
            return new LuaValue(result);
        };
        return arithmeticOperation(left, right, DIV_VAlUE, divNumbersFunction);
    }

    public static LuaValue mod(LuaValue left, LuaValue right) {
        BiFunction<LuaValue, LuaValue, LuaValue> modNumbersFunction = (leftNumber, rightNumber) -> {
            // for integer values
            if (leftNumber.isIntegerValue() && rightNumber.isIntegerValue()) {
                long result = leftNumber.getIntegerValue() % rightNumber.getIntegerValue();
                return new LuaValue(result);
            }
            // for real values
            else {
                double result = leftNumber.getRealValue() % rightNumber.getRealValue();
                return new LuaValue(result);
            }
        };
        return arithmeticOperation(left, right, MOD_VAlUE, modNumbersFunction);
    }

    public static LuaValue pow(LuaValue left, LuaValue right) {
        BiFunction<LuaValue, LuaValue, LuaValue> powNumbersFunction = (leftNumber, rightNumber) -> {
            double result = Math.pow(leftNumber.getRealValue(), rightNumber.getRealValue());
            return new LuaValue(result);
        };
        return arithmeticOperation(left, right, POW_VAlUE, powNumbersFunction);
    }

    public static LuaValue unm(LuaValue left, LuaValue right) {
        return unm(left);
    }

    public static LuaValue unm(LuaValue value) {
        if (value.isTableValue()) {
            LuaValue metatable = value.getMetatable();
            if (metatable != null && metatable.isTableValue() && metatable.getTableValue().containsKey(UNM_VAlUE)) {
                LuaValue metamethodValue = metatable.getTableValue().get(UNM_VAlUE);
                if (metamethodValue.isFunctionValue()) {
                    return metamethodValue.getFunctionValue().apply(new LuaList(List.of(value))).getFirst();
                } else {
                    throw new LuaRuntimeException("call", metamethodValue);
                }
            }
        } else {
            // Convert value to number
            LuaValue number = LuaFunctions.toNumber(value).getFirst();

            // string number case
            if (value.type == Type.string && number.isNumber()) {
                return new LuaValue(-number.getRealValue());
            }
            // integer case
            if (number.isIntegerValue()) {
                return new LuaValue(-number.getIntegerValue());
            }
            // real case
            else if (number.isRealValue()) {
                return new LuaValue(-number.getRealValue());
            }
        }
        // exception
        throw new LuaRuntimeException("perform arithmetic on", value);
    }

    public static LuaValue idiv(LuaValue left, LuaValue right) {
        BiFunction<LuaValue, LuaValue, LuaValue> powNumbersFunction = (leftNumber, rightNumber) -> {
            if (leftNumber.isIntegerValue() && rightNumber.isIntegerValue()) {
                return new LuaValue(leftNumber.getIntegerValue() / rightNumber.getIntegerValue());
            } else {
                return new LuaValue((long) (leftNumber.getRealValue() / rightNumber.getRealValue()));
            }
        };
        return arithmeticOperation(left, right, IDIV_VAlUE, powNumbersFunction);
    }

    public static LuaValue concat(LuaValue left, LuaValue right) {
        // get left string
        String leftString = null;
        try {
            leftString = left.getStringValue();
        } catch (IllegalStateException ignored) {
        }

        // get right string
        String rightString = null;
        try {
            rightString = right.getStringValue();
        } catch (IllegalStateException ignored) {
        }

        // concatenate strings
        if (leftString != null && rightString != null) {
            return new LuaValue(leftString + rightString);
        }
        // use metamethod
        else {
            Function<LuaList, LuaList> function = getFunctionFromMetatable(left, right, CONCAT_VALUE);
            // throw exception if function does not exist
            if (function == null) {
                LuaValue unsupportedAddValue = leftString == null ? left : right;
                throw new LuaRuntimeException("concatenate", unsupportedAddValue);
            }
            // return result of function call
            return function.apply(new LuaList(List.of(left, right))).getFirst();
        }
    }

    public static LuaValue len(LuaValue left, LuaValue right) {
        return len(left);
    }

    public static LuaValue len(LuaValue value) {
        switch (value.getType()) {
            case string -> {
                return new LuaValue(value.getStringValue().length());
            }
            case table -> {
                LuaValue metatable = value.getMetatable();
                // try return metamethod result
                if (metatable != null && metatable.isTableValue() && metatable.getTableValue().containsKey(LEN_VAlUE)) {
                    LuaValue metamethodValue = metatable.getTableValue().get(LEN_VAlUE);
                    if (metamethodValue.isFunctionValue()) {
                        return metamethodValue.getFunctionValue().apply(new LuaList(List.of(value))).getFirst();
                    } else {
                        throw new LuaRuntimeException("call", metamethodValue);
                    }

                }
                // return table size
                return new LuaValue(value.getTableValue().size());

            }
            default -> throw new LuaRuntimeException("get length of", value);
        }
    }

    public static LuaValue eq(LuaValue left, LuaValue right) {
        return new LuaValue(left.equals(right));
    }

    public static LuaValue lt(LuaValue left, LuaValue right) {
        if (left.isTableValue() && right.isTableValue()) {
            Function<LuaList, LuaList> metamethod = getFunctionFromMetatable(left, right, LT_VAlUE);
            if (metamethod != null)
                return new LuaValue(metamethod.apply(new LuaList(List.of(left, right))).getFirst().getBoolValue(true));
        } else if (left.isNumber() && right.isNumber()) {
            return new LuaValue(left.getRealValue() < right.getRealValue());
        } else if (left.isStringValue() && right.isStringValue()) {
            return new LuaValue(left.getStringValue().compareTo(right.getStringValue()) < 0);
        }
        throw new LuaRuntimeException("compare", left, right);
    }

    public static LuaValue le(LuaValue left, LuaValue right) {
        if (left.isTableValue() && right.isTableValue()) {
            // use le metamethod
            Function<LuaList, LuaList> leMetamethod = getFunctionFromMetatable(left, right, LE_VAlUE);
            if (leMetamethod != null)
                return new LuaValue(leMetamethod.apply(new LuaList(List.of(left, right))).getFirst().getBoolValue(true));

            // use lt metamethod
            Function<LuaList, LuaList> ltMetamethod = getFunctionFromMetatable(left, right, LT_VAlUE);
            if (ltMetamethod != null) {
                return new LuaValue(!ltMetamethod.apply(new LuaList(List.of(right, left))).getFirst().getBoolValue(true));
            }
        } else if (left.isNumber() && right.isNumber()) {
            return new LuaValue(left.getRealValue() <= right.getRealValue());
        } else if (left.isStringValue() && right.isStringValue()) {
            return new LuaValue(left.getStringValue().compareTo(right.getStringValue()) <= 0);
        }
        throw new LuaRuntimeException("compare", left, right);
    }

    public static LuaValue not(LuaValue value) {
        if (value.type == Type.nil || (value.isBoolValue() && !value.getBoolValue())) {
            return new LuaValue(true);
        } else {
            return new LuaValue(false);
        }
    }

    public LuaValue index(LuaValue indexValue) {
        if (isTableValue()) {
            if (tableValue.table.containsKey(indexValue)) {
                return tableValue.table.get(indexValue);
            } else {
                if (tableValue.metatable == null) {
                    return new LuaValue(); // nil
                }

                if (tableValue.metatable.table.containsKey(INDEX_VALUE)) {
                    LuaValue indexMetamethod = tableValue.metatable.table.get(INDEX_VALUE);
                    switch (indexMetamethod.type) {
                        case table -> {
                            return indexMetamethod.index(indexValue);
                        }
                        case function -> {
                            return indexMetamethod.functionValue.apply(new LuaList(List.of(this, indexValue))).getFirst();
                        }
                        default -> throw new LuaRuntimeException("index", indexMetamethod);
                    }
                } else {
                    return new LuaValue();
                }
            }
        } else {
            throw new LuaRuntimeException("index", this);
        }
    }

    public void newIndex(LuaValue key, LuaValue value) {
        if (isTableValue()) {
            if (!tableValue.table.containsKey(key) && tableValue.metatable != null && tableValue.metatable.table.containsKey(NEW_INDEX_VALUE)) {
                LuaValue metamethodValue = tableValue.metatable.table.get(NEW_INDEX_VALUE);
                if (metamethodValue.isFunctionValue()) {
                    metamethodValue.getFunctionValue().apply(new LuaList(List.of(this, key, value)));
                    return;
                } else if (metamethodValue.isTableValue()) {
                    metamethodValue.newIndex(key, value);
                    return;
                } else {
                    throw new LuaRuntimeException("index", metamethodValue);
                }
            }
            tableValue.table.put(key, value);
        } else {
            throw new LuaRuntimeException("index", this);
        }
    }

    public LuaList call(LuaList list) {
        if (type == Type.function) {
            return functionValue.apply(list);
        } else if (Objects.requireNonNull(type) == Type.table) {
            if (tableValue.metatable != null && tableValue.metatable.table.containsKey(CALL_VAlUE)) {
                LuaValue metamethodValue = tableValue.metatable.table.get(CALL_VAlUE);
                if (metamethodValue.isFunctionValue()) {
                    list.addFirst(this);
                    return metamethodValue.getFunctionValue().apply(list);
                } else {
                    throw new LuaRuntimeException("call", metamethodValue);
                }
            }
        }
        throw new LuaRuntimeException("call", this);
    }

    static <T> LuaValue create(T value) {
        if (value == null) {
            return new LuaValue();
        } else if (value instanceof Boolean) {
            return new LuaValue((boolean) value);
        } else if (value instanceof Integer || value instanceof Long) {
            return new LuaValue(((Number) value).longValue());
        } else if (value instanceof Float || value instanceof Double) {
            return new LuaValue((double) value);
        } else if (value instanceof String) {
            return new LuaValue((String) value);
        } else if (value instanceof Function) {
            return new LuaValue((Function<LuaList, LuaList>) value);
        } else if (value instanceof Map) {
            return new LuaValue((Map<LuaValue, LuaValue>) value);
        } else if (value instanceof LuaValue) {
            return (LuaValue) value;
        } else {
            throw new ClassCastException("LuaValue does not support type: " + value.getClass().getName());
        }
    }

    public LuaValue() {
        type = Type.nil;
    }

    public LuaValue(LuaValue value) {
        setValue(value);
    }

    public LuaValue(boolean value) {
        setValue(value);
    }

    public LuaValue(int value) {
        setValue(value);
    }

    public LuaValue(long value) {
        setValue(value);
    }

    public LuaValue(float value) {
        setValue(value);
    }

    public LuaValue(double value) {
        setValue(value);
    }

    public LuaValue(String value) {
        setValue(value);
    }

    public LuaValue(Function<LuaList, LuaList> value) {
        setValue(value);
    }

    public LuaValue(Map<LuaValue, LuaValue> value) {
        setValue(value);
    }

    public LuaValue(LuaTable value) {
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

    public void setValue(Function<LuaList, LuaList> value) {
        type = Type.function;
        functionValue = value;
    }

    public void setValue(Map<LuaValue, LuaValue> value) {
        type = Type.table;
        if (tableValue==null) {
            tableValue = new LuaTable();
        }
        tableValue.table = value;
    }

    public void setValue(LuaTable value) {
        type = Type.table;
        tableValue = value;
    }

    public void setMetatable(LuaValue value) {
        if (value.isTableValue()) {
            this.tableValue.metatable = value.tableValue;
        } else {
            this.tableValue.metatable = null;
        }
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

    public boolean getBoolValue(boolean save) {
        if (save) {
            return getBoolValueSave();
        } else {
            return getBoolValue();
        }
    }

    public boolean getBoolValueSave() {
        return switch (type) {
            case nil -> false;
            case bool -> boolValue;
            default -> true;
        };
    }

    public boolean getBoolValue() {
        if (!isBoolValue()) {
            throw getCantGetPrimitiveValueException(BOOL);
        }
        return boolValue;
    }

    public long getIntegerValue() {
        if (!isIntegerValue()) {
            throw getCantGetPrimitiveValueException(NUMBER);
        }
        return integerValue;
    }

    public double getRealValue() {
        if (!isNumber()) {
            throw getCantGetPrimitiveValueException(NUMBER);
        }
        if (type == Type.integer) {
            return integerValue;
        } else {
            return realValue;
        }
    }

    public String getStringValue() {
        if (isStringValue()) {
            return stringValue;
        } else if (isIntegerValue()) {
            return String.valueOf(integerValue);
        } else if (isRealValue()) {
            return String.valueOf(realValue);
        } else {
            throw getCantGetPrimitiveValueException(STRING);
        }
    }

    public Function<LuaList, LuaList> getFunctionValue() {
        if (!isFunctionValue()) {
            throw getCantGetPrimitiveValueException(FUNCTION);
        }
        return functionValue;
    }

    public Map<LuaValue, LuaValue> getTableValue() {
        if (!isTableValue()) {
            throw getCantGetPrimitiveValueException(TABLE);
        }
        return tableValue.table;
    }

    public LuaValue getMetatable() {
        if (type != Type.table || tableValue.metatable == null) {
            return new LuaValue();
        } else {
            return new LuaValue(tableValue.metatable);
        }
    }

    private IllegalStateException getCantGetPrimitiveValueException(String primitive) {
        return new IllegalStateException("Can't return " + primitive + "value for'" + getTypeString() + "'.");
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
    private Function<LuaList, LuaList> functionValue = null;
    private LuaTable tableValue =null;

    public static class LuaTable {
        public LuaTable() {
        }

        public LuaTable(Map<LuaValue, LuaValue> table, LuaTable metatable) {
            this.table = table;
            this.metatable = metatable;
        }

        public Map<LuaValue, LuaValue> table = null;
        public LuaTable metatable = null;
    }
}