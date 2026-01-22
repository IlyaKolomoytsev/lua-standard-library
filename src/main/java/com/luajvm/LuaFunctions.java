package com.luajvm;

import java.util.List;
import java.util.function.Function;

final public class LuaFunctions {
    static public LuaList toNumber(LuaValue value) {
        LuaValue.Type type = value.getType();
        switch (type) {
            case integer, real:
                return new LuaList(List.of(value));
            case string:
                String str = value.getStringValue();
                try {
                    if (str.contains(".")) {
                        double realNumber = Double.parseDouble(str);
                        return new LuaList(List.of(new LuaValue(realNumber)));
                    } else {
                        long intNumber = Integer.parseInt(str);
                        return new LuaList(List.of(new LuaValue(intNumber)));
                    }
                } catch (NumberFormatException ignored) {
                }
            default:
                return new LuaList(List.of(LuaValue.NIL_VALUE));
        }
    }

    static public LuaList toNumber(LuaList args) {
        LuaValue value = args.getFirst();
        return toNumber(value);
    }

    static public LuaList type(LuaValue value) {
        LuaValue.Type type = value.getType();
        return new LuaList(List.of(new LuaValue(value.getTypeString())));
    }


    static public LuaList type(LuaList args) {
        LuaValue value = args.getFirst();
        return type(value);
    }

    static public void forIterator(LuaList exprlist, Function<LuaList, LuaList> loopBlock) {
        final LuaValue f = new LuaValue();
        final LuaValue s = new LuaValue();
        final LuaValue var = new LuaValue();

        LuaValue.assignment(new LuaList(List.of(f, s, var)), exprlist);

        while (true) {
            // function arguments
            LuaList functionArguments = new LuaList();
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
            LuaValue.assignment(new LuaList(List.of(var)), new LuaList(List.of(firstValue)));

            // execute loop block
            loopBlock.apply(parameters);
        }
    }

    static LuaList print(LuaList args) {
        for (int i = 0; i < args.size() - 1; i++) {
            System.out.print(args.get(i) + "\t");
        }
        System.out.println(args.getLast());
        return new LuaList();
    }
}
