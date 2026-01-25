package com.luajvm;

import java.util.List;
import java.util.function.Function;
import java.util.Scanner;

final public class LuaFunctions {

    private static final Scanner SCANNER = new Scanner(System.in);

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

    static public LuaList read(LuaList args) {
        if (SCANNER.hasNextLine()) {
            return new LuaList(List.of(new LuaValue(SCANNER.nextLine())));
        }
        return new LuaList(List.of(LuaValue.NIL_VALUE));
    }

    static public LuaList setMetatable(LuaList args) {
        LuaValue t = args.get(0);
        LuaValue mt = args.get(1);

        if (!t.isTableValue()) {
            throw new LuaRuntimeException("setmetatable", t);
        }

        t.setMetatable(mt);

        return new LuaList(List.of(t));
    }

    static public LuaList getMetatable(LuaList args) {
        LuaValue t = args.getFirst();
        return new LuaList(List.of(t.getMetatable()));
    }

    static public LuaList error(LuaList args) {
        LuaValue v = args.isEmpty() ? LuaValue.NIL_VALUE : args.getFirst();
        throw new LuaRuntimeException(v);
    }

    static LuaList pcall(LuaList args) {
        if (args == null || args.size() == 0) {
            return new LuaList(List.of(new LuaValue(false), new LuaValue("pcall: function expected")));
        }

        try {
            LuaValue func = args.getFirst();

            LuaList rest = new LuaList();
            if (args.size() > 1) {
                rest.addAll(args.subList(1, args.size()));
            }

            LuaList result = func.call(rest);

            LuaList out = new LuaList();
            out.add(new LuaValue(true));
            if (result != null && result.size() > 0) {
                out.addAll(result);
            }
            return out;

        } catch (LuaRuntimeException e) {
            return new LuaList(List.of(new LuaValue(false), new LuaValue(e.getMessage())));
        }
    }

    static public LuaList format(LuaList args) {
        if (args.isEmpty()) {
            return new LuaList(List.of(LuaValue.NIL_VALUE));
        }

        String format = args.get(0).getStringValue();
        StringBuilder result = new StringBuilder();

        int argIndex = 1;

        for (int i = 0; i < format.length(); i++) {
            char c = format.charAt(i);

            if (c == '%' && i + 1 < format.length()) {
                char spec = format.charAt(++i);

                if (spec == '%') {
                    result.append('%');
                    continue;
                }

                if (argIndex >= args.size()) {
                    return new LuaList(List.of(LuaValue.NIL_VALUE));
                }

                LuaValue value = args.get(argIndex++);

                switch (spec) {
                    case 's' -> result.append(value.getStringValue());
                    case 'd' -> result.append(value.getIntegerValue());
                    case 'f' -> result.append(value.getRealValue());
                    case 'c' -> result.append((char) value.getIntegerValue());
                    default -> result.append('%').append(spec);
                }
            } else {
                result.append(c);
            }
        }

        return new LuaList(List.of(new LuaValue(result.toString())));
    }

}
