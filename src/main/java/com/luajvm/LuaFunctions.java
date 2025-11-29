package com.luajvm;

import java.util.List;

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
                return List.of(new LuaValue());
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
}
