package com.luajvm;

import java.util.List;
import java.util.function.Function;

public abstract class LuaFunction implements Function<List<LuaValue>, List<LuaValue>> {
    public LuaFunction(LuaContext luaContext) {
        this.context = new LuaContext(luaContext);
    }

    protected final LuaContext context;
}
