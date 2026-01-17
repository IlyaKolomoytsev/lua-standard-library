package com.luajvm;

import java.util.List;
import java.util.function.Function;

final public class LuaFunctions {
    static public List<LuaValue> toNumber(LuaValue value) {
        LuaValue.Type type = value.getType();
        switch (type) {
            case integer, real:
                return List.of(value);
            case string:
                String str = value.getStringValue();
                try {
                    if (str.contains(".")) {
                        double realNumber = Double.parseDouble(str);
                        return List.of(new LuaValue(realNumber));
                    } else {
                        long intNumber = Integer.parseInt(str);
                        return List.of(new LuaValue(intNumber));
                    }
                } catch (NumberFormatException ignored) {
                }
            default:
                return List.of(LuaValue.NIL_VALUE);
        }
    }

    static public List<LuaValue> toNumber(List<LuaValue> args) {
        LuaValue value = args.getFirst();
        return toNumber(value);
    }

    static public List<LuaValue> type(LuaValue value) {
        LuaValue.Type type = value.getType();
        return List.of(new LuaValue(value.getTypeString()));
    }


    static public List<LuaValue> type(List<LuaValue> args) {
        LuaValue value = args.getFirst();
        return type(value);
    }

    static public void forIterator(List<LuaValue> exprlist, Function<List<LuaValue>, List<LuaValue>> loopBlock) {
        final LuaValue f = new LuaValue();
        final LuaValue s = new LuaValue();
        final LuaValue var = new LuaValue();

        LuaValue.assignment(List.of(f, s, var), exprlist);

        while (true) {
            // function arguments
            List<LuaValue> functionArguments = new LuaList();
            functionArguments.add(s);
            functionArguments.add(var);

            // set values to local variables from function
            LuaList parameters = new LuaList();
            parameters.addAll(f.call(functionArguments));

            // check leave from loop condition
            LuaValue firstValue = parameters.getFirst();
            if (firstValue.isNil()) {
                return;
            }
            // setValue for var value
            LuaValue.assignment(List.of(var), List.of(firstValue));

            // execute loop block
            loopBlock.apply(parameters);
        }
    }
}
