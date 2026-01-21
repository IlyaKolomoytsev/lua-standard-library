package com.luajvm;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public class LuaEqualsTest {
    @Test
    public void equalsNils() {
        LuaValue val1 = LuaValue.NIL_VALUE;
        LuaValue val2 = LuaValue.NIL_VALUE;
        assertEquals(val1, val2);
    }

    @Test
    public void notEqualsNil() {
        LuaValue val1 = LuaValue.NIL_VALUE;
        LuaValue[] values = new LuaValue[]{
                new LuaValue(false),
                new LuaValue(1),
                new LuaValue(1.2),
                new LuaValue("string"),
                new LuaValue((args) -> new LuaList()),
                new LuaValue(new HashMap<>())
        };
        for (LuaValue val2 : values) {
            assertNotEquals(val1, val2);
        }
    }

    @Test
    public void equalsBools() {
        LuaValue val1 = new LuaValue(true);
        LuaValue val2 = new LuaValue(true);
        assertEquals(val1, val2);
    }

    @Test
    public void notEqualsBool() {
        LuaValue val1 = new LuaValue(true);
        LuaValue[] values = new LuaValue[]{
                LuaValue.NIL_VALUE,
                new LuaValue(1),
                new LuaValue(1.2),
                new LuaValue("string"),
                new LuaValue((args) -> new LuaList()),
                new LuaValue(new HashMap<>())
        };
        for (LuaValue val2 : values) {
            assertNotEquals(val1, val2);
        }
    }

    @Test
    public void equalsIntegers() {
        LuaValue val1 = new LuaValue(14);
        LuaValue val2 = new LuaValue(14);
        assertEquals(val1, val2);
    }

    @Test
    public void notEqualsInteger() {
        LuaValue val1 = new LuaValue(1);
        LuaValue[] values = new LuaValue[]{
                LuaValue.NIL_VALUE,
                new LuaValue(true),
                new LuaValue(false),
                new LuaValue(2),
                new LuaValue(1.2),
                new LuaValue("string"),
                new LuaValue((args) -> new LuaList()),
                new LuaValue(new HashMap<>())
        };
        for (LuaValue val2 : values) {
            assertNotEquals(val1, val2);
        }
    }

    @Test
    public void equalsReals() {
        LuaValue val1 = new LuaValue(1.4);
        LuaValue val2 = new LuaValue(1.4);
        assertEquals(val1, val2);
    }

    @Test
    public void notEqualsReal() {
        LuaValue val1 = new LuaValue(1.1);
        LuaValue[] values = new LuaValue[]{
                LuaValue.NIL_VALUE,
                new LuaValue(true),
                new LuaValue(false),
                new LuaValue(1),
                new LuaValue(1.2),
                new LuaValue("string"),
                new LuaValue((args) -> new LuaList()),
                new LuaValue(new HashMap<>())
        };
        for (LuaValue val2 : values) {
            assertNotEquals(val1, val2);
        }
    }

    @Test
    public void equalsStrings() {
        LuaValue val1 = new LuaValue("string");
        LuaValue val2 = new LuaValue("string");
        assertEquals(val1, val2);
    }

    @Test
    public void notEqualsStrings() {
        LuaValue val1 = new LuaValue("str");
        LuaValue[] values = new LuaValue[]{
                LuaValue.NIL_VALUE,
                new LuaValue(true),
                new LuaValue(false),
                new LuaValue(1),
                new LuaValue(1.2),
                new LuaValue("string"),
                new LuaValue((args) -> new LuaList()),
                new LuaValue(new HashMap<>())
        };
        for (LuaValue val2 : values) {
            assertNotEquals(val1, val2);
        }
    }

    @Test
    public void equalsFunctions() {
        Function<LuaList, LuaList> f = (args) -> new LuaList();
        LuaValue val1 = new LuaValue(f);
        LuaValue val2 = new LuaValue(f);
        assertEquals(val1, val2);
    }

    @Test
    public void notEqualsFunctions() {
        LuaValue val1 = new LuaValue((args) -> new LuaList());
        LuaValue[] values = new LuaValue[]{
                LuaValue.NIL_VALUE,
                new LuaValue(true),
                new LuaValue(false),
                new LuaValue(1),
                new LuaValue(1.2),
                new LuaValue("string"),
                new LuaValue((args) -> new LuaList()),
                new LuaValue(new HashMap<>())
        };
        for (LuaValue val2 : values) {
            assertNotEquals(val1, val2);
        }
    }

    @Test
    public void equalsTables() {
        Map<LuaValue, LuaValue> map = new HashMap<>();
        LuaValue val1 = new LuaValue(map);
        LuaValue val2 = new LuaValue(map);
        assertEquals(val1, val2);
    }

    @Test
    public void notEqualsTables() {
        LuaValue val1 = new LuaValue(new HashMap<>());
        LuaValue[] values = new LuaValue[]{
                LuaValue.NIL_VALUE,
                new LuaValue(true),
                new LuaValue(false),
                new LuaValue(1),
                new LuaValue(1.2),
                new LuaValue("string"),
                new LuaValue((args) -> new LuaList()),
                new LuaValue(new HashMap<>())
        };
        for (LuaValue val2 : values) {
            assertNotEquals(val1, val2);
        }
    }

    @Test
    public void SearchAtTable() {
        LuaValue table = new LuaValue(
                Map.of(new LuaValue("val"), new LuaValue(1))
        );
        LuaValue value = table.getTableValue().get(new LuaValue("val"));
        assertEquals(new LuaValue(1), value);
    }
}
