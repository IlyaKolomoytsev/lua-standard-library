package com.luajvm;

import java.util.List;
import java.util.Map;

public class LuaMetatable {
    public static final String ADD = "add";
    public static final String SUB = "sub";
    public static final String MUL = "mul";
    public static final String DIV = "div";
    public static final String MOD = "mod";
    public static final String POW = "pow";
    public static final String UNM = "unm";
    public static final String IDIV = "idiv";
    public static final String CONCAT = "concat";
    public static final String LEN = "len";
    public static final String EQ = "eq";
    public static final String LT = "lt";
    public static final String LE = "le";
    public static final String INDEX = "index";
    public static final String NEW_INDEX = "newindex";
    public static final String CALL = "call";

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

    private static RuntimeException getException(String operationName, LuaValue arg) {
        return new RuntimeException("attempt to " + operationName + " a " + arg.getTypeString() + " value");
    }

    private static RuntimeException getException(String operationName, LuaValue arg1, LuaValue arg2) {
        return new RuntimeException("attempt to " + operationName + " a " + arg1.getTypeString() + " with a " + arg2.getTypeString());
    }

    private static List<LuaValue> convertArgumentsToNumbers(List<LuaValue> args, String operationName) {
        LuaValue arg1 = args.get(0);
        LuaValue arg2 = args.get(1);
        LuaValue val1 = LuaFunctions.toNumber(arg1).getFirst();
        LuaValue val2 = LuaFunctions.toNumber(arg2).getFirst();

        if (val1.isNil() || val2.isNil()) {
            throw getException(operationName, arg1, arg2);
        }

        return List.of(val1, val2);
    }

    private static String getStringForConcatenation(LuaValue value) {
        return switch (value.getType()) {
            case integer -> String.valueOf(value.getIntegerValue());
            case real -> String.valueOf(value.getRealValue());
            case string -> value.getStringValue();
            default -> throw getException("concatenate", value);
        };
    }

    private static List<LuaValue> addFunctionForNumberAndString(List<LuaValue> args) {
        List<LuaValue> values = convertArgumentsToNumbers(args, ADD);
        LuaValue val1 = values.get(0);
        LuaValue val2 = values.get(1);

        if (val1.isIntegerValue() && val2.isIntegerValue()) {
            long integer1 = val1.getIntegerValue();
            long integer2 = val2.getIntegerValue();
            long result = integer1 + integer2;
            return List.of(new LuaValue(result));
        } else {
            double real1 = val1.getRealValue();
            double real2 = val2.getRealValue();
            double result = real1 + real2;
            return List.of(new LuaValue(result));
        }
    }

    private static List<LuaValue> subFunctionForNumberAndString(List<LuaValue> args) {
        List<LuaValue> values = convertArgumentsToNumbers(args, SUB);
        LuaValue val1 = values.get(0);
        LuaValue val2 = values.get(1);

        if (val1.isIntegerValue() && val2.isIntegerValue()) {
            long integer1 = val1.getIntegerValue();
            long integer2 = val2.getIntegerValue();
            long result = integer1 - integer2;
            return List.of(new LuaValue(result));
        } else {
            double real1 = val1.getRealValue();
            double real2 = val2.getRealValue();
            double result = real1 - real2;
            return List.of(new LuaValue(result));
        }
    }

    private static List<LuaValue> mulFunctionForNumberAndString(List<LuaValue> args) {
        List<LuaValue> values = convertArgumentsToNumbers(args, MUL);
        LuaValue val1 = values.get(0);
        LuaValue val2 = values.get(1);

        if (val1.isIntegerValue() && val2.isIntegerValue()) {
            long integer1 = val1.getIntegerValue();
            long integer2 = val2.getIntegerValue();
            long result = integer1 * integer2;
            return List.of(new LuaValue(result));
        } else {
            double real1 = val1.getRealValue();
            double real2 = val2.getRealValue();
            double result = real1 * real2;
            return List.of(new LuaValue(result));
        }
    }

    private static List<LuaValue> divFunctionForNumberAndString(List<LuaValue> args) {
        List<LuaValue> values = convertArgumentsToNumbers(args, DIV);
        LuaValue val1 = values.get(0);
        LuaValue val2 = values.get(1);

        double real1 = val1.getRealValue();
        double real2 = val2.getRealValue();
        double result = real1 / real2;
        return List.of(new LuaValue(result));
    }

    private static List<LuaValue> modFunctionForNumberAndString(List<LuaValue> args) {
        List<LuaValue> values = convertArgumentsToNumbers(args, MOD);
        LuaValue val1 = values.get(0);
        LuaValue val2 = values.get(1);

        if (val1.isIntegerValue() && val2.isIntegerValue()) {
            long integer1 = val1.getIntegerValue();
            long integer2 = val2.getIntegerValue();
            long result = integer1 % integer2;
            return List.of(new LuaValue(result));
        } else {
            double real1 = val1.getRealValue();
            double real2 = val2.getRealValue();
            double result = real1 % real2;
            return List.of(new LuaValue(result));
        }
    }

    private static List<LuaValue> powFunctionForNumberAndString(List<LuaValue> args) {
        List<LuaValue> values = convertArgumentsToNumbers(args, POW);
        LuaValue val1 = values.get(0);
        LuaValue val2 = values.get(1);

        if (val1.isIntegerValue() && val2.isIntegerValue()) {
            long integer1 = val1.getIntegerValue();
            long integer2 = val2.getIntegerValue();
            long result = (long) Math.pow(integer1, integer2);
            return List.of(new LuaValue(result));
        } else {
            double real1 = val1.getRealValue();
            double real2 = val2.getRealValue();
            double result = Math.pow(real1, real2);
            return List.of(new LuaValue(result));
        }
    }

    private static List<LuaValue> unmFunctionForNumberAndString(List<LuaValue> args) {
        LuaValue arg = args.getFirst();
        LuaValue val = LuaFunctions.toNumber(arg).getFirst();
        if (val.isNil()) {
            throw getException(UNM, arg, arg);
        }

        if (val.isIntegerValue()) {
            long result = -val.getIntegerValue();
            return List.of(new LuaValue(result));
        } else {
            double result = -val.getRealValue();
            return List.of(new LuaValue(result));
        }
    }

    private static List<LuaValue> idivFunctionForNumberAndString(List<LuaValue> args) {
        List<LuaValue> values = convertArgumentsToNumbers(args, IDIV);
        LuaValue val1 = values.get(0);
        LuaValue val2 = values.get(1);

        long result;
        if (val1.isIntegerValue() && val2.isIntegerValue()) {
            long integer1 = val1.getIntegerValue();
            long integer2 = val2.getIntegerValue();
            result = integer1 / integer2;
        } else {
            double real1 = val1.getRealValue();
            double real2 = val2.getRealValue();
            result = (long) (real1 / real2);
        }
        return List.of(new LuaValue(result));
    }

    private static List<LuaValue> concatFunctionForNumberAndString(List<LuaValue> args) {
        LuaValue arg1 = args.get(0);
        LuaValue arg2 = args.get(1);
        String val1 = getStringForConcatenation(arg1);
        String val2 = getStringForConcatenation(arg2);
        String result = val1 + val2;
        return List.of(new LuaValue(result));
    }

    private static List<LuaValue> lenFunctionForStringAndTable(List<LuaValue> args) {
        LuaValue arg = args.getFirst();
        LuaValue result;
        switch (arg.getType()) {
            case string -> result = new LuaValue(arg.getStringValue().length());
            case table -> result = new LuaValue(arg.getTableValue().size());
            default -> throw getException("get length of", arg);
        }
        return List.of(new LuaValue(result));
    }

    private static List<LuaValue> eqFunction(List<LuaValue> args) {
        LuaValue arg1 = args.get(0);
        LuaValue arg2 = args.get(1);
        LuaValue.Type type1 = arg1.getType();
        LuaValue.Type type2 = arg2.getType();
        LuaValue result;
        if (type1 != type2) {
            result = new LuaValue(false);
        } else {
            boolean boolResult = switch (type1) {
                case nil -> arg1.isNil() == arg2.isNil();
                case bool -> arg1.getBoolValue() == arg2.getBoolValue();
                case integer, real -> arg1.getRealValue() == arg2.getRealValue();
                case string -> arg1.getStringValue().equals(arg2.getStringValue());
                case function -> arg1.getFunctionValue() == arg2.getFunctionValue(); // compare pointers
                case table -> arg1.getTableValue() == arg2.getTableValue(); // compare pointers
            };
            result = new LuaValue(boolResult);
        }
        return List.of(result);
    }

    private static List<LuaValue> ltFunctionForStringAndTable(List<LuaValue> args) {
        LuaValue arg1 = args.get(0);
        LuaValue arg2 = args.get(1);
        boolean result;
        if (arg1.isNumber() && arg2.isNumber()) {
            result = arg1.getRealValue() < arg2.getRealValue();
        } else if (arg1.isStringValue() && arg2.isStringValue()) {
            result = arg1.getStringValue().compareTo(arg2.getStringValue()) < 0;
        } else {
            throw getException("compare", arg1, arg2);
        }
        return List.of(new LuaValue(result));
    }

    public static final LuaValue ADD_FUNC_FOR_NUMBER_AND_STRING_VALUE = new LuaValue(LuaMetatable::addFunctionForNumberAndString);

    public static final LuaValue SUB_FUNC_FOR_NUMBER_AND_STRING_VALUE = new LuaValue(LuaMetatable::subFunctionForNumberAndString);

    public static final LuaValue MUL_FUNC_FOR_NUMBER_AND_STRING_VALUE = new LuaValue(LuaMetatable::mulFunctionForNumberAndString);

    public static final LuaValue DIV_FUNC_FOR_NUMBER_AND_STRING_VALUE = new LuaValue(LuaMetatable::divFunctionForNumberAndString);

    public static final LuaValue MOD_FUNC_FOR_NUMBER_AND_STRING_VALUE = new LuaValue(LuaMetatable::modFunctionForNumberAndString);

    public static final LuaValue POW_FUNC_FOR_NUMBER_AND_STRING_VALUE = new LuaValue(LuaMetatable::powFunctionForNumberAndString);

    public static final LuaValue UNM_FUNC_FOR_NUMBER_AND_STRING_VALUE = new LuaValue(LuaMetatable::unmFunctionForNumberAndString);

    public static final LuaValue IDIV_FUNC_FOR_NUMBER_AND_STRING_VALUE = new LuaValue(LuaMetatable::idivFunctionForNumberAndString);

    public static final LuaValue CONCAT_FUNC_FOR_NUMBER_AND_STRING_VALUE = new LuaValue(LuaMetatable::concatFunctionForNumberAndString);

    public static final LuaValue LEN_FUNC_FOR_STRING_AND_TABLE_VALUE = new LuaValue(LuaMetatable::lenFunctionForStringAndTable);

    public static final LuaValue EQ_FUNC_VALUE = new LuaValue(LuaMetatable::eqFunction);

    public static final LuaValue LT_FUNC_FOR_NUMBER_AND_STRING_VALUE = new LuaValue(LuaMetatable::ltFunctionForStringAndTable);

    public static final LuaValue NilMetatable = new LuaValue(Map.ofEntries(
            Map.entry(EQ_VAlUE, EQ_FUNC_VALUE)
    ));

    public static final LuaValue BoolMetatable = new LuaValue(Map.ofEntries(
            Map.entry(EQ_VAlUE, EQ_FUNC_VALUE)
    ));

    public static final LuaValue NumberMetatable = new LuaValue(Map.ofEntries(
            Map.entry(ADD_VAlUE, ADD_FUNC_FOR_NUMBER_AND_STRING_VALUE),
            Map.entry(SUB_VAlUE, SUB_FUNC_FOR_NUMBER_AND_STRING_VALUE),
            Map.entry(MUL_VAlUE, MUL_FUNC_FOR_NUMBER_AND_STRING_VALUE),
            Map.entry(DIV_VAlUE, DIV_FUNC_FOR_NUMBER_AND_STRING_VALUE),
            Map.entry(MOD_VAlUE, MOD_FUNC_FOR_NUMBER_AND_STRING_VALUE),
            Map.entry(POW_VAlUE, POW_FUNC_FOR_NUMBER_AND_STRING_VALUE),
            Map.entry(UNM_VAlUE, UNM_FUNC_FOR_NUMBER_AND_STRING_VALUE),
            Map.entry(IDIV_VAlUE, IDIV_FUNC_FOR_NUMBER_AND_STRING_VALUE),
            Map.entry(CONCAT_VALUE, CONCAT_FUNC_FOR_NUMBER_AND_STRING_VALUE),
            Map.entry(EQ_VAlUE, EQ_FUNC_VALUE),
            Map.entry(LT_VAlUE, LT_FUNC_FOR_NUMBER_AND_STRING_VALUE)
    ));

    public static final LuaValue StringMetatable = new LuaValue(Map.ofEntries(
            Map.entry(ADD_VAlUE, ADD_FUNC_FOR_NUMBER_AND_STRING_VALUE),
            Map.entry(SUB_VAlUE, SUB_FUNC_FOR_NUMBER_AND_STRING_VALUE),
            Map.entry(MUL_VAlUE, MUL_FUNC_FOR_NUMBER_AND_STRING_VALUE),
            Map.entry(DIV_VAlUE, DIV_FUNC_FOR_NUMBER_AND_STRING_VALUE),
            Map.entry(MOD_VAlUE, MOD_FUNC_FOR_NUMBER_AND_STRING_VALUE),
            Map.entry(POW_VAlUE, POW_FUNC_FOR_NUMBER_AND_STRING_VALUE),
            Map.entry(UNM_VAlUE, UNM_FUNC_FOR_NUMBER_AND_STRING_VALUE),
            Map.entry(IDIV_VAlUE, IDIV_FUNC_FOR_NUMBER_AND_STRING_VALUE),
            Map.entry(CONCAT_VALUE, CONCAT_FUNC_FOR_NUMBER_AND_STRING_VALUE),
            Map.entry(LEN_VAlUE, LEN_FUNC_FOR_STRING_AND_TABLE_VALUE),
            Map.entry(EQ_VAlUE, EQ_FUNC_VALUE),
            Map.entry(LT_VAlUE, LT_FUNC_FOR_NUMBER_AND_STRING_VALUE)
    ));

    public static final LuaValue FunctionMetatable = new LuaValue(Map.ofEntries(
            Map.entry(EQ_VAlUE, EQ_FUNC_VALUE)
    ));

    public static final LuaValue TableMetatable = new LuaValue(Map.ofEntries(
            Map.entry(LEN_VAlUE, LEN_FUNC_FOR_STRING_AND_TABLE_VALUE),
            Map.entry(EQ_VAlUE, EQ_FUNC_VALUE)
    ));
}
