package com.luajvm;

import java.util.List;
import java.util.function.Function;

public abstract class LuaFunction implements Function<LuaList, LuaList> {
    public LuaFunction(LuaContext luaContext) {
        this.context = new LuaContext(luaContext);
    }

    protected final LuaContext context;
}
