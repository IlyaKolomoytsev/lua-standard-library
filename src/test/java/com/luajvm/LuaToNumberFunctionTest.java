package com.luajvm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LuaToNumberFunctionTest {
    @Test
    public void integerToNumber() {
        long number = 10;
        LuaValue val = new LuaValue(number);
        LuaValue result = LuaFunctions.toNumber(val).getFirst();
        assertEquals(LuaValue.Type.integer, result.getType());
        assertEquals(number, result.getIntegerValue());
    }

    @Test
    public void realToNumber() {
        double number = 100.0;
        LuaValue val = new LuaValue(number);
        LuaValue result = LuaFunctions.toNumber(val).getFirst();
        assertEquals(LuaValue.Type.real, result.getType());
        assertEquals(number, result.getRealValue());
    }

    @Test
    public void integerStringToNumber() {
        long number = 100;
        LuaValue val = new LuaValue(String.valueOf(number));
        LuaValue result = LuaFunctions.toNumber(val).getFirst();
        assertEquals(LuaValue.Type.integer, result.getType());
        assertEquals(number, result.getIntegerValue());
    }

    @Test
    public void realStringToNumber() {
        double number = 100.5;
        LuaValue val = new LuaValue(String.valueOf(number));
        LuaValue result = LuaFunctions.toNumber(val).getFirst();
        assertEquals(LuaValue.Type.real, result.getType());
        assertEquals(number, result.getRealValue());
    }

    @Test
    public void incorrectStringToNumber() {
        String str = "abc";
        LuaValue val = new LuaValue(str);
        LuaValue result = LuaFunctions.toNumber(val).getFirst();
        assertEquals(LuaValue.Type.nil, result.getType());
    }
}
