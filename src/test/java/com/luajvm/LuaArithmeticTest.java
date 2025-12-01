package com.luajvm;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static java.lang.Math.round;
import static org.junit.jupiter.api.Assertions.*;

public class LuaArithmeticTest {
    public <T1, T2, E> void arithmeticTest(T1 val1, T2 val2, LuaValue.Type type, E expected, BiFunction<LuaValue, LuaValue, List<LuaValue>> action) {
        LuaValue arg1 = LuaValue.create(val1);
        LuaValue arg2 = LuaValue.create(val2);
        LuaValue sum = action.apply(arg1, arg2).getFirst();
        assertEquals(type, sum.getType());
        switch (type) {
            case bool -> {
                assertEquals(expected, sum.getBoolValue());
            }
            case integer -> {
                assertEquals(((Number) expected).longValue(), sum.getIntegerValue());
            }
            case real -> {
                assertEquals(expected, round(sum.getRealValue() * 1000000) / 1000000d);
            }
            case string -> {
                assertEquals(expected, sum.getStringValue());
            }
            case function -> {
                assertEquals(expected, sum.getFunctionValue());
            }
            case table -> {
                assertEquals(expected, sum.getTableValue());
            }
        }
    }

    enum Arg {
        none,
        first,
        second,
    }

    static <T1, T2> void arithmeticExceptionTest(T1 val1, T2 val2, Arg exceptionArg1, Arg exceptionArg2, BiFunction<LuaValue, LuaValue, List<LuaValue>> action) {
        LuaValue arg1 = LuaValue.create(val1);
        LuaValue arg2 = LuaValue.create(val2);
        LuaRuntimeException exception = assertThrows(LuaRuntimeException.class, () -> action.apply(arg1, arg2));
        switch (exceptionArg1) {
            case first -> {
                assertEquals(arg1, exception.getArg1());
            }
            case second -> {
                assertEquals(arg2, exception.getArg1());
            }
        }
        switch (exceptionArg2) {
            case first -> {
                assertEquals(arg1, exception.getArg2());
            }
            case second -> {
                assertEquals(arg2, exception.getArg2());
            }
        }
    }

    @ParameterizedTest
    @MethodSource("addArguments")
    public <T1, T2, E> void addTest(T1 val1, T2 val2, LuaValue.Type type, E expected) {
        arithmeticTest(val1, val2, type, expected, LuaValue::add);
    }

    @ParameterizedTest
    @MethodSource("subArguments")
    public <T1, T2, E> void subTest(T1 val1, T2 val2, LuaValue.Type type, E expected) {
        arithmeticTest(val1, val2, type, expected, LuaValue::sub);
    }

    @ParameterizedTest
    @MethodSource("mulArguments")
    public <T1, T2, E> void mulTest(T1 val1, T2 val2, LuaValue.Type type, E expected) {
        arithmeticTest(val1, val2, type, expected, LuaValue::mul);
    }

    @ParameterizedTest
    @MethodSource("divArguments")
    public <T1, T2, E> void divTest(T1 val1, T2 val2, LuaValue.Type type, E expected) {
        arithmeticTest(val1, val2, type, expected, LuaValue::div);
    }

    @ParameterizedTest
    @MethodSource("modArguments")
    public <T1, T2, E> void modTest(T1 val1, T2 val2, LuaValue.Type type, E expected) {
        arithmeticTest(val1, val2, type, expected, LuaValue::mod);
    }

    @ParameterizedTest
    @MethodSource("exceptionArguments")
    public <T1, T2> void addExceptionTest(T1 val1, T2 val2, Arg arg) {
        arithmeticExceptionTest(val1, val2, arg, Arg.none, LuaValue::add);
    }

    @ParameterizedTest
    @MethodSource("exceptionArguments")
    public <T1, T2> void subExceptionTest(T1 val1, T2 val2, Arg arg) {
        arithmeticExceptionTest(val1, val2, arg, Arg.none, LuaValue::sub);
    }

    @ParameterizedTest
    @MethodSource("exceptionArguments")
    public <T1, T2> void mulExceptionTest(T1 val1, T2 val2, Arg arg) {
        arithmeticExceptionTest(val1, val2, arg, Arg.none, LuaValue::mul);
    }

    @ParameterizedTest
    @MethodSource("exceptionArguments")
    public <T1, T2> void divExceptionTest(T1 val1, T2 val2, Arg arg) {
        arithmeticExceptionTest(val1, val2, arg, Arg.none, LuaValue::div);
    }

    @ParameterizedTest
    @MethodSource("exceptionArguments")
    public <T1, T2> void modExceptionTest(T1 val1, T2 val2, Arg arg) {
        arithmeticExceptionTest(val1, val2, arg, Arg.none, LuaValue::mod);
    }

    private static Stream<Arguments> addArguments() {
        return Stream.of(
                // Integer + Integer
                Arguments.of(0, 0, LuaValue.Type.integer, 0),
                Arguments.of(0, 1, LuaValue.Type.integer, 1),
                Arguments.of(1, 0, LuaValue.Type.integer, 1),
                Arguments.of(1, 2, LuaValue.Type.integer, 3),
                Arguments.of(Long.MAX_VALUE / 2, Long.MAX_VALUE / 2, LuaValue.Type.integer, Long.MAX_VALUE - 1),
                Arguments.of(0, -1, LuaValue.Type.integer, -1),
                Arguments.of(-1, 0, LuaValue.Type.integer, -1),
                Arguments.of(-1, -4, LuaValue.Type.integer, -5),
                Arguments.of(-10, 10, LuaValue.Type.integer, 0),
                Arguments.of(Long.MAX_VALUE, Long.MIN_VALUE, LuaValue.Type.integer, round((Long.MAX_VALUE + Long.MIN_VALUE) * 1000000) / 1000000d),
                // Real + Real
                Arguments.of(0.0d, 0.0d, LuaValue.Type.real, 0.0d),
                Arguments.of(0d, 1d, LuaValue.Type.real, 1d),
                Arguments.of(1d, 0d, LuaValue.Type.real, 1d),
                Arguments.of(1d, 2d, LuaValue.Type.real, 3d),
                Arguments.of(Double.MAX_VALUE / 2, Double.MAX_VALUE / 2, LuaValue.Type.real, round((Double.MAX_VALUE) * 1000000) / 1000000d),
                Arguments.of(0d, -1d, LuaValue.Type.real, -1d),
                Arguments.of(-1d, 0d, LuaValue.Type.real, -1d),
                Arguments.of(-1d, -4d, LuaValue.Type.real, -5d),
                Arguments.of(-10d, 10d, LuaValue.Type.real, 0d),
                Arguments.of(Double.MAX_VALUE, Double.MIN_VALUE, LuaValue.Type.real, round((Double.MAX_VALUE + Double.MIN_VALUE) * 1000000) / 1000000d),
                Arguments.of(1.25d, 2.26d, LuaValue.Type.real, 3.51d),
                // Real + Integer and Integer + Real
                Arguments.of(1, 2.26d, LuaValue.Type.real, 3.26d),
                Arguments.of(2.26d, 1, LuaValue.Type.real, 3.26d),
                // String(Integer) + Integer
                Arguments.of("1", 1, LuaValue.Type.integer, 2),
                Arguments.of(9, "2", LuaValue.Type.integer, 11),
                Arguments.of("-1", 1, LuaValue.Type.integer, 0),
                Arguments.of(9, "-2", LuaValue.Type.integer, 7),
                // String(Integer) + Real
                Arguments.of("1", 1d, LuaValue.Type.real, 2d),
                Arguments.of(9d, "2", LuaValue.Type.real, 11d),
                Arguments.of("-1", 1d, LuaValue.Type.real, 0d),
                Arguments.of(9d, "-2", LuaValue.Type.real, 7d),
                // String(Real) + Integer
                Arguments.of("1.2", 1, LuaValue.Type.real, 2.2d),
                Arguments.of(9, "2.1", LuaValue.Type.real, 11.1d),
                Arguments.of("-1.3", 1, LuaValue.Type.real, -0.3d),
                Arguments.of(9, "-2.2", LuaValue.Type.real, 6.8d),
                // String(Real) + Real
                Arguments.of("1.2", 1d, LuaValue.Type.real, 2.2d),
                Arguments.of(9d, "2.1", LuaValue.Type.real, 11.1d),
                Arguments.of("-1.3", 1d, LuaValue.Type.real, -0.3d),
                Arguments.of(9d, "-2.2", LuaValue.Type.real, 6.8d),
                // String + String
                Arguments.of("9", "2", LuaValue.Type.integer, 11),
                Arguments.of("9", "-2", LuaValue.Type.integer, 7),
                Arguments.of("-9", "2", LuaValue.Type.integer, -7),
                Arguments.of("9.1", "2", LuaValue.Type.real, 11.1),
                Arguments.of("9", "2.1", LuaValue.Type.real, 11.1),
                Arguments.of("9.1", "2.1", LuaValue.Type.real, 11.2),
                // Table with metatable operation + Number
                Arguments.of(createTableWithAddMetatableAction(new LuaValue(5)), 1, LuaValue.Type.integer, 6),
                Arguments.of(createTableWithAddMetatableAction(new LuaValue(5)), 1d, LuaValue.Type.real, 6d),
                Arguments.of(createTableWithAddMetatableAction(new LuaValue(5d)), 1, LuaValue.Type.real, 6d),
                Arguments.of(createTableWithAddMetatableAction(new LuaValue(5d)), 1d, LuaValue.Type.real, 6d),
                Arguments.of(1, createTableWithAddMetatableAction(new LuaValue(5)), LuaValue.Type.integer, 6),
                Arguments.of(1d, createTableWithAddMetatableAction(new LuaValue(5)), LuaValue.Type.real, 6d),
                Arguments.of(1, createTableWithAddMetatableAction(new LuaValue(5d)), LuaValue.Type.real, 6d),
                Arguments.of(1d, createTableWithAddMetatableAction(new LuaValue(5d)), LuaValue.Type.real, 6d),
                Arguments.of(createTableWithAddMetatableAction(new LuaValue(5)), "1", LuaValue.Type.integer, 6),
                Arguments.of(createTableWithAddMetatableAction(new LuaValue(5)), "1.1", LuaValue.Type.real, 6.1d),
                Arguments.of(createTableWithAddMetatableAction(new LuaValue(5d)), "1", LuaValue.Type.real, 6d),
                Arguments.of(createTableWithAddMetatableAction(new LuaValue(5d)), "1.1", LuaValue.Type.real, 6.1d),
                Arguments.of("1", createTableWithAddMetatableAction(new LuaValue(5)), LuaValue.Type.integer, 6),
                Arguments.of("1.1", createTableWithAddMetatableAction(new LuaValue(5)), LuaValue.Type.real, 6.1d),
                Arguments.of(1, createTableWithAddMetatableAction(new LuaValue(5d)), LuaValue.Type.real, 6d),
                Arguments.of("1.1", createTableWithAddMetatableAction(new LuaValue(5d)), LuaValue.Type.real, 6.1d)
        );
    }

    private static Stream<Arguments> subArguments() {
        return Stream.of(
                // Integer + Integer
                Arguments.of(0, 0, LuaValue.Type.integer, 0),
                Arguments.of(0, 1, LuaValue.Type.integer, -1),
                Arguments.of(1, 0, LuaValue.Type.integer, 1),
                Arguments.of(1, 2, LuaValue.Type.integer, -1),
                Arguments.of(Long.MAX_VALUE / 2, Long.MAX_VALUE / 2, LuaValue.Type.integer, 0d),
                Arguments.of(0, -1, LuaValue.Type.integer, 1),
                Arguments.of(-1, 0, LuaValue.Type.integer, -1),
                Arguments.of(-1, -4, LuaValue.Type.integer, 3),
                Arguments.of(-10, 10, LuaValue.Type.integer, -20),
                // Real + Real
                Arguments.of(0.0d, 0.0d, LuaValue.Type.real, 0.0d),
                Arguments.of(0d, 1d, LuaValue.Type.real, -1d),
                Arguments.of(1d, 0d, LuaValue.Type.real, 1d),
                Arguments.of(1d, 2d, LuaValue.Type.real, -1d),
                Arguments.of(Double.MAX_VALUE / 2, Double.MAX_VALUE / 2, LuaValue.Type.real, 0d),
                Arguments.of(0d, -1d, LuaValue.Type.real, 1d),
                Arguments.of(-1d, 0d, LuaValue.Type.real, -1d),
                Arguments.of(-1d, -4d, LuaValue.Type.real, 3d),
                Arguments.of(-10d, 10d, LuaValue.Type.real, -20d),
                Arguments.of(1.25d, 2.26d, LuaValue.Type.real, -1.01d),
                // Real + Integer and Integer + Real
                Arguments.of(1, 2.26d, LuaValue.Type.real, -1.26d),
                Arguments.of(2.26d, 1, LuaValue.Type.real, 1.26d),
                // String(Integer) + Integer
                Arguments.of("1", 1, LuaValue.Type.integer, 0),
                Arguments.of(9, "2", LuaValue.Type.integer, 7),
                Arguments.of("-1", 1, LuaValue.Type.integer, -2),
                Arguments.of(9, "-2", LuaValue.Type.integer, 11),
                // String(Integer) + Real
                Arguments.of("1", 1d, LuaValue.Type.real, 0d),
                Arguments.of(9d, "2", LuaValue.Type.real, 7d),
                Arguments.of("-1", 1d, LuaValue.Type.real, -2d),
                Arguments.of(9d, "-2", LuaValue.Type.real, 11d),
                // String(Real) + Integer
                Arguments.of("1.2", 1, LuaValue.Type.real, 0.2d),
                Arguments.of(9, "2.1", LuaValue.Type.real, 6.9d),
                Arguments.of("-1.3", 1, LuaValue.Type.real, -2.3d),
                Arguments.of(9, "-2.2", LuaValue.Type.real, 11.2d),
                // String(Real) + Real
                Arguments.of("1.2", 1d, LuaValue.Type.real, 0.2d),
                Arguments.of(9d, "2.1", LuaValue.Type.real, 6.9d),
                Arguments.of("-1.3", 1d, LuaValue.Type.real, -2.3d),
                Arguments.of(9d, "-2.2", LuaValue.Type.real, 11.2d),
                // String + String
                Arguments.of("9", "2", LuaValue.Type.integer, 7),
                Arguments.of("9", "-2", LuaValue.Type.integer, 11),
                Arguments.of("-9", "2", LuaValue.Type.integer, -11),
                Arguments.of("9.1", "2", LuaValue.Type.real, 7.1),
                Arguments.of("9", "2.1", LuaValue.Type.real, 6.9),
                Arguments.of("9.1", "2.1", LuaValue.Type.real, 7.0),
                // Table with metatable operation + Number
                Arguments.of(createTableWithSubMetatableAction(new LuaValue(5)), 1, LuaValue.Type.integer, 4),
                Arguments.of(createTableWithSubMetatableAction(new LuaValue(5)), 1d, LuaValue.Type.real, 4d),
                Arguments.of(createTableWithSubMetatableAction(new LuaValue(5d)), 1, LuaValue.Type.real, 4d),
                Arguments.of(createTableWithSubMetatableAction(new LuaValue(5d)), 1d, LuaValue.Type.real, 4d),
                Arguments.of(1, createTableWithSubMetatableAction(new LuaValue(5)), LuaValue.Type.integer, -4),
                Arguments.of(1d, createTableWithSubMetatableAction(new LuaValue(5)), LuaValue.Type.real, -4d),
                Arguments.of(1, createTableWithSubMetatableAction(new LuaValue(5d)), LuaValue.Type.real, -4d),
                Arguments.of(1d, createTableWithSubMetatableAction(new LuaValue(5d)), LuaValue.Type.real, -4d),
                Arguments.of(createTableWithSubMetatableAction(new LuaValue(5)), "1", LuaValue.Type.integer, 4),
                Arguments.of(createTableWithSubMetatableAction(new LuaValue(5)), "1.1", LuaValue.Type.real, 3.9d),
                Arguments.of(createTableWithSubMetatableAction(new LuaValue(5d)), "1", LuaValue.Type.real, 4d),
                Arguments.of(createTableWithSubMetatableAction(new LuaValue(5d)), "1.1", LuaValue.Type.real, 3.9d),
                Arguments.of("1", createTableWithSubMetatableAction(new LuaValue(5)), LuaValue.Type.integer, -4),
                Arguments.of("1.1", createTableWithSubMetatableAction(new LuaValue(5)), LuaValue.Type.real, -3.9d),
                Arguments.of(1, createTableWithSubMetatableAction(new LuaValue(5d)), LuaValue.Type.real, -4d),
                Arguments.of("1.1", createTableWithSubMetatableAction(new LuaValue(5d)), LuaValue.Type.real, -3.9d)
        );
    }

    private static Stream<Arguments> mulArguments() {
        return Stream.of(
                // Integer + Integer
                Arguments.of(0, 0, LuaValue.Type.integer, 0),
                Arguments.of(0, 1, LuaValue.Type.integer, 0),
                Arguments.of(1, 0, LuaValue.Type.integer, 0),
                Arguments.of(1, 2, LuaValue.Type.integer, 2),
                Arguments.of(0, -1, LuaValue.Type.integer, 0),
                Arguments.of(-1, 0, LuaValue.Type.integer, 0),
                Arguments.of(-1, -4, LuaValue.Type.integer, 4),
                Arguments.of(-10, 10, LuaValue.Type.integer, -100),
                // Real + Real
                Arguments.of(0.0d, 0.0d, LuaValue.Type.real, 0.0d),
                Arguments.of(0d, 1d, LuaValue.Type.real, 0d),
                Arguments.of(1d, 0d, LuaValue.Type.real, 0d),
                Arguments.of(1d, 2d, LuaValue.Type.real, 2d),
                Arguments.of(0d, -1d, LuaValue.Type.real, 0d),
                Arguments.of(-1d, 0d, LuaValue.Type.real, 0d),
                Arguments.of(-1d, -4d, LuaValue.Type.real, 4d),
                Arguments.of(-10d, 10d, LuaValue.Type.real, -100d),
                Arguments.of(1.25d, 2.26d, LuaValue.Type.real, 2.825d),
                // Real + Integer and Integer + Real
                Arguments.of(1, 2.26d, LuaValue.Type.real, 2.26d),
                Arguments.of(2.26d, 1, LuaValue.Type.real, 2.26d),
                // String(Integer) + Integer
                Arguments.of("1", 1, LuaValue.Type.integer, 1),
                Arguments.of(9, "2", LuaValue.Type.integer, 18),
                Arguments.of("-1", 1, LuaValue.Type.integer, -1),
                Arguments.of(9, "-2", LuaValue.Type.integer, -18),
                // String(Integer) + Real
                Arguments.of("1", 1d, LuaValue.Type.real, 1d),
                Arguments.of(9d, "2", LuaValue.Type.real, 18d),
                Arguments.of("-1", 1d, LuaValue.Type.real, -1d),
                Arguments.of(9d, "-2", LuaValue.Type.real, -18d),
                // String(Real) + Integer
                Arguments.of("1.2", 1, LuaValue.Type.real, 1.2d),
                Arguments.of(9, "2.1", LuaValue.Type.real, 18.9d),
                Arguments.of("-1.3", 1, LuaValue.Type.real, -1.3d),
                Arguments.of(9, "-2.2", LuaValue.Type.real, -19.8d),
                // String(Real) + Real
                Arguments.of("1.2", 1d, LuaValue.Type.real, 1.2d),
                Arguments.of(9d, "2.1", LuaValue.Type.real, 18.9d),
                Arguments.of("-1.3", 1d, LuaValue.Type.real, -1.3d),
                Arguments.of(9d, "-2.2", LuaValue.Type.real, -19.8d),
                // String + String
                Arguments.of("9", "2", LuaValue.Type.integer, 18),
                Arguments.of("9", "-2", LuaValue.Type.integer, -18),
                Arguments.of("-9", "2", LuaValue.Type.integer, -18),
                Arguments.of("9.1", "2", LuaValue.Type.real, 18.2),
                Arguments.of("9", "2.1", LuaValue.Type.real, 18.9),
                Arguments.of("9.1", "2.1", LuaValue.Type.real, 19.11),
                // Table with metatable operation + Number
                Arguments.of(createTableWithMulMetatableAction(new LuaValue(5)), 1, LuaValue.Type.integer, 5),
                Arguments.of(createTableWithMulMetatableAction(new LuaValue(5)), 1d, LuaValue.Type.real, 5d),
                Arguments.of(createTableWithMulMetatableAction(new LuaValue(5d)), 1, LuaValue.Type.real, 5d),
                Arguments.of(createTableWithMulMetatableAction(new LuaValue(5d)), 1d, LuaValue.Type.real, 5d),
                Arguments.of(1, createTableWithMulMetatableAction(new LuaValue(5)), LuaValue.Type.integer, 5),
                Arguments.of(1d, createTableWithMulMetatableAction(new LuaValue(5)), LuaValue.Type.real, 5d),
                Arguments.of(1, createTableWithMulMetatableAction(new LuaValue(5d)), LuaValue.Type.real, 5d),
                Arguments.of(1d, createTableWithMulMetatableAction(new LuaValue(5d)), LuaValue.Type.real, 5d),
                Arguments.of(createTableWithMulMetatableAction(new LuaValue(5)), "1", LuaValue.Type.integer, 5),
                Arguments.of(createTableWithMulMetatableAction(new LuaValue(5)), "1.1", LuaValue.Type.real, 5.5d),
                Arguments.of(createTableWithMulMetatableAction(new LuaValue(5d)), "1", LuaValue.Type.real, 5d),
                Arguments.of(createTableWithMulMetatableAction(new LuaValue(5d)), "1.1", LuaValue.Type.real, 5.5d),
                Arguments.of("1", createTableWithMulMetatableAction(new LuaValue(5)), LuaValue.Type.integer, 5),
                Arguments.of("1.1", createTableWithMulMetatableAction(new LuaValue(5)), LuaValue.Type.real, 5.5d),
                Arguments.of(1, createTableWithMulMetatableAction(new LuaValue(5d)), LuaValue.Type.real, 5d),
                Arguments.of("1.1", createTableWithMulMetatableAction(new LuaValue(5d)), LuaValue.Type.real, 5.5d)
        );
    }

    private static Stream<Arguments> divArguments() {
        return Stream.of(
                // Integer + Integer
                Arguments.of(0, 1, LuaValue.Type.real, 0d),
                Arguments.of(1, 2, LuaValue.Type.real, 0.5d),
                Arguments.of(0, -1, LuaValue.Type.real, 0d),
                Arguments.of(-1, -4, LuaValue.Type.real, 0.25d),
                Arguments.of(-10, 10, LuaValue.Type.real, -1d),
                // Real + Real
                Arguments.of(0d, 1d, LuaValue.Type.real, 0d),
                Arguments.of(1d, 2d, LuaValue.Type.real, 0.5d),
                Arguments.of(0d, -1d, LuaValue.Type.real, 0d),
                Arguments.of(-1d, -4d, LuaValue.Type.real, 0.25d),
                Arguments.of(-10d, 10d, LuaValue.Type.real, -1d),
                // Real + Integer and Integer + Real
                Arguments.of(1, 2.26d, LuaValue.Type.real, 0.442478d),
                Arguments.of(2.26d, 1, LuaValue.Type.real, 2.26d),
                // String(Integer) + Integer
                Arguments.of("1", 1, LuaValue.Type.real, 1d),
                Arguments.of(9, "2", LuaValue.Type.real, 4.5d),
                Arguments.of("-1", 1, LuaValue.Type.real, -1d),
                Arguments.of(9, "-2", LuaValue.Type.real, -4.5d),
                // String(Integer) + Real
                Arguments.of("1", 1d, LuaValue.Type.real, 1d),
                Arguments.of(9d, "2", LuaValue.Type.real, 4.5d),
                Arguments.of("-1", 1d, LuaValue.Type.real, -1d),
                Arguments.of(9d, "-2", LuaValue.Type.real, -4.5d),
                // String(Real) + Integer
                Arguments.of("1.2", 1, LuaValue.Type.real, 1.2d),
                Arguments.of(9, "2.1", LuaValue.Type.real, 4.285714d),
                Arguments.of("-1.3", 1, LuaValue.Type.real, -1.3d),
                Arguments.of(9, "-2.2", LuaValue.Type.real, -4.090909d),
                // String(Real) + Real
                Arguments.of("1.2", 1d, LuaValue.Type.real, 1.2d),
                Arguments.of(9d, "2.1", LuaValue.Type.real, 4.285714d),
                Arguments.of("-1.3", 1d, LuaValue.Type.real, -1.3d),
                Arguments.of(9d, "-2.2", LuaValue.Type.real, -4.090909d),
                // String + String
                Arguments.of("9", "2", LuaValue.Type.real, 4.5d),
                Arguments.of("9", "-2", LuaValue.Type.real, -4.5d),
                Arguments.of("-9", "2", LuaValue.Type.real, -4.5d),
                Arguments.of("9.1", "2", LuaValue.Type.real, 4.55d),
                Arguments.of("9", "2.1", LuaValue.Type.real, 4.285714d),
                Arguments.of("9.1", "2.1", LuaValue.Type.real, 4.333333d),
                // Table with metatable operation + Number
                Arguments.of(createTableWithDivMetatableAction(new LuaValue(5)), 1, LuaValue.Type.real, 5d),
                Arguments.of(createTableWithDivMetatableAction(new LuaValue(5)), 1d, LuaValue.Type.real, 5d),
                Arguments.of(createTableWithDivMetatableAction(new LuaValue(5d)), 1, LuaValue.Type.real, 5d),
                Arguments.of(createTableWithDivMetatableAction(new LuaValue(5d)), 1d, LuaValue.Type.real, 5d),
                Arguments.of(1, createTableWithDivMetatableAction(new LuaValue(5)), LuaValue.Type.real, 0.2d),
                Arguments.of(1d, createTableWithDivMetatableAction(new LuaValue(5)), LuaValue.Type.real, 0.2d),
                Arguments.of(1, createTableWithDivMetatableAction(new LuaValue(5d)), LuaValue.Type.real, 0.2d),
                Arguments.of(1d, createTableWithDivMetatableAction(new LuaValue(5d)), LuaValue.Type.real, 0.2d),
                Arguments.of(createTableWithDivMetatableAction(new LuaValue(5)), "1", LuaValue.Type.real, 5d),
                Arguments.of(createTableWithDivMetatableAction(new LuaValue(5)), "1.1", LuaValue.Type.real, 4.545455d),
                Arguments.of(createTableWithDivMetatableAction(new LuaValue(5d)), "1", LuaValue.Type.real, 5d),
                Arguments.of(createTableWithDivMetatableAction(new LuaValue(5d)), "1.1", LuaValue.Type.real, 4.545455d),
                Arguments.of("1", createTableWithDivMetatableAction(new LuaValue(5)), LuaValue.Type.real, 0.2d),
                Arguments.of("1.1", createTableWithDivMetatableAction(new LuaValue(5)), LuaValue.Type.real, 0.22d),
                Arguments.of(1, createTableWithDivMetatableAction(new LuaValue(5d)), LuaValue.Type.real, 0.2d),
                Arguments.of("1.1", createTableWithDivMetatableAction(new LuaValue(5d)), LuaValue.Type.real, 0.22d)
        );
    }

    private static Stream<Arguments> modArguments() {
        return Stream.of(
                // Integer + Integer
                Arguments.of(0, 1, LuaValue.Type.integer, 0),
                Arguments.of(1, 2, LuaValue.Type.integer, 1),
                Arguments.of(0, -1, LuaValue.Type.integer, 0),
                Arguments.of(-1, -4, LuaValue.Type.integer, -1),
                Arguments.of(-10, 10, LuaValue.Type.integer, 0),
                // Real + Real
                Arguments.of(0d, 1d, LuaValue.Type.real, 0d),
                Arguments.of(1d, 2d, LuaValue.Type.real, 1d),
                Arguments.of(0d, -1d, LuaValue.Type.real, 0d),
                Arguments.of(-1d, -4d, LuaValue.Type.real, -1d),
                Arguments.of(-10d, 10d, LuaValue.Type.real, 0d),
                // Real + Integer and Integer + Real
                Arguments.of(1, 2.26d, LuaValue.Type.real, 1d),
                Arguments.of(2.26d, 1, LuaValue.Type.real, 0.26d),
                // String(Integer) + Integer
                Arguments.of("1", 1, LuaValue.Type.integer, 0d),
                Arguments.of(9, "2", LuaValue.Type.integer, 1d),
                Arguments.of("-1", 1, LuaValue.Type.integer, 0d),
                Arguments.of(9, "-2", LuaValue.Type.integer, 1),
                // String(Integer) + Real
                Arguments.of("1", 1d, LuaValue.Type.real, 0d),
                Arguments.of(9d, "2", LuaValue.Type.real, 1d),
                Arguments.of("-1", 1d, LuaValue.Type.real, 0d),
                Arguments.of(9d, "-2", LuaValue.Type.real, 1d),
                // String(Real) + Integer
                Arguments.of("1.2", 1, LuaValue.Type.real, 0.2d),
                Arguments.of(9, "2.1", LuaValue.Type.real, 0.6d),
                Arguments.of("-1.3", 1, LuaValue.Type.real, -0.3d),
                Arguments.of(9, "-2.2", LuaValue.Type.real, 0.2d),
                // String(Real) + Real
                Arguments.of("1.2", 1d, LuaValue.Type.real, 0.2d),
                Arguments.of(9d, "2.1", LuaValue.Type.real, 0.6d),
                Arguments.of("-1.3", 1d, LuaValue.Type.real, -0.3d),
                Arguments.of(9d, "-2.2", LuaValue.Type.real, 0.2d),
                // String + String
                Arguments.of("9", "2", LuaValue.Type.integer, 1),
                Arguments.of("9", "-2", LuaValue.Type.integer, 1),
                Arguments.of("-9", "2", LuaValue.Type.integer, -1d),
                Arguments.of("9.1", "2", LuaValue.Type.real, 1.1d),
                Arguments.of("9", "2.1", LuaValue.Type.real, 0.6d),
                Arguments.of("9.1", "2.1", LuaValue.Type.real, 0.7d),
                // Table with metatable operation + Number
                Arguments.of(createTableWithModMetatableAction(new LuaValue(5)), 1, LuaValue.Type.integer, 0),
                Arguments.of(createTableWithModMetatableAction(new LuaValue(5)), 1d, LuaValue.Type.real, 0d),
                Arguments.of(createTableWithModMetatableAction(new LuaValue(5d)), 1, LuaValue.Type.real, 0d),
                Arguments.of(createTableWithModMetatableAction(new LuaValue(5d)), 1d, LuaValue.Type.real, 0d),
                Arguments.of(1, createTableWithModMetatableAction(new LuaValue(5)), LuaValue.Type.integer, 1),
                Arguments.of(1d, createTableWithModMetatableAction(new LuaValue(5)), LuaValue.Type.real, 1d),
                Arguments.of(1, createTableWithModMetatableAction(new LuaValue(5d)), LuaValue.Type.real, 1d),
                Arguments.of(1d, createTableWithModMetatableAction(new LuaValue(5d)), LuaValue.Type.real, 1d),
                Arguments.of(createTableWithModMetatableAction(new LuaValue(5)), "1", LuaValue.Type.integer, 0),
                Arguments.of(createTableWithModMetatableAction(new LuaValue(5)), "1.1", LuaValue.Type.real, 0.6d),
                Arguments.of(createTableWithModMetatableAction(new LuaValue(5d)), "1", LuaValue.Type.real, 0d),
                Arguments.of(createTableWithModMetatableAction(new LuaValue(5d)), "1.1", LuaValue.Type.real, 0.6d),
                Arguments.of("1", createTableWithModMetatableAction(new LuaValue(5)), LuaValue.Type.integer, 1),
                Arguments.of("1.1", createTableWithModMetatableAction(new LuaValue(5)), LuaValue.Type.real, 1.1d),
                Arguments.of(1, createTableWithModMetatableAction(new LuaValue(5d)), LuaValue.Type.real, 1d),
                Arguments.of("1.1", createTableWithModMetatableAction(new LuaValue(5d)), LuaValue.Type.real, 1.1d)
        );
    }

    private static Stream<Arguments> exceptionArguments() {
        List<LuaValue> incorrectValues = List.of(
                new LuaValue(),
                new LuaValue(true),
                new LuaValue(false),
                new LuaValue("abc"),
                new LuaValue((list) -> List.of()),
                new LuaValue(Map.of())
        );

        List<Arguments> argList = new ArrayList<>();
        argList.add(Arguments.of("a", "1", Arg.first));
        argList.add(Arguments.of("1", "a", Arg.second));
        for (LuaValue val1 : incorrectValues) {
            for (LuaValue val2 : incorrectValues) {
                argList.add(Arguments.of(val1, val2, Arg.first, Arg.second));
            }
        }
        return argList.stream();
    }

    private static LuaValue createTableWithAddMetatableAction(LuaValue value) {
        Map<LuaValue, LuaValue> tableContent = new HashMap<>();
        tableContent.put(new LuaValue("value"), value);
        LuaValue table = new LuaValue(tableContent);

        Map<LuaValue, LuaValue> metatableContent = new HashMap<>();
        metatableContent.put(LuaMetatable.ADD_VAlUE, new LuaValue((args) -> {
            LuaValue arg1 = args.get(0);
            LuaValue arg2 = args.get(1);

            if (arg1.getType() == LuaValue.Type.table) {
                LuaValue val = arg1.getTableValue().get(new LuaValue("value"));
                return LuaValue.add(val, arg2);
            } else if (arg2.getType() == LuaValue.Type.table) {
                LuaValue val = arg2.getTableValue().get(new LuaValue("value"));
                return LuaValue.add(val, arg1);
            } else {
                return List.of(new LuaValue());
            }
        }));
        LuaValue metatable = new LuaValue(metatableContent);

        table.setMetatable(metatable);

        return table;
    }

    private static LuaValue createTableWithSubMetatableAction(LuaValue value) {
        Map<LuaValue, LuaValue> tableContent = new HashMap<>();
        tableContent.put(new LuaValue("value"), value);
        LuaValue table = new LuaValue(tableContent);

        Map<LuaValue, LuaValue> metatableContent = new HashMap<>();
        metatableContent.put(LuaMetatable.SUB_VAlUE, new LuaValue((args) -> {
            LuaValue arg1 = args.get(0);
            LuaValue arg2 = args.get(1);

            if (arg1.getType() == LuaValue.Type.table) {
                LuaValue val = arg1.getTableValue().get(new LuaValue("value"));
                return LuaValue.sub(val, arg2);
            } else if (arg2.getType() == LuaValue.Type.table) {
                LuaValue val = arg2.getTableValue().get(new LuaValue("value"));
                return LuaValue.sub(arg1, val);
            } else {
                return List.of(new LuaValue());
            }
        }));
        LuaValue metatable = new LuaValue(metatableContent);

        table.setMetatable(metatable);

        return table;
    }

    private static LuaValue createTableWithMulMetatableAction(LuaValue value) {
        Map<LuaValue, LuaValue> tableContent = new HashMap<>();
        tableContent.put(new LuaValue("value"), value);
        LuaValue table = new LuaValue(tableContent);

        Map<LuaValue, LuaValue> metatableContent = new HashMap<>();
        metatableContent.put(LuaMetatable.MUL_VAlUE, new LuaValue((args) -> {
            LuaValue arg1 = args.get(0);
            LuaValue arg2 = args.get(1);

            if (arg1.getType() == LuaValue.Type.table) {
                LuaValue val = arg1.getTableValue().get(new LuaValue("value"));
                return LuaValue.mul(val, arg2);
            } else if (arg2.getType() == LuaValue.Type.table) {
                LuaValue val = arg2.getTableValue().get(new LuaValue("value"));
                return LuaValue.mul(arg1, val);
            } else {
                return List.of(new LuaValue());
            }
        }));
        LuaValue metatable = new LuaValue(metatableContent);

        table.setMetatable(metatable);

        return table;
    }

    private static LuaValue createTableWithDivMetatableAction(LuaValue value) {
        Map<LuaValue, LuaValue> tableContent = new HashMap<>();
        tableContent.put(new LuaValue("value"), value);
        LuaValue table = new LuaValue(tableContent);

        Map<LuaValue, LuaValue> metatableContent = new HashMap<>();
        metatableContent.put(LuaMetatable.DIV_VAlUE, new LuaValue((args) -> {
            LuaValue arg1 = args.get(0);
            LuaValue arg2 = args.get(1);

            if (arg1.getType() == LuaValue.Type.table) {
                LuaValue val = arg1.getTableValue().get(new LuaValue("value"));
                return LuaValue.div(val, arg2);
            } else if (arg2.getType() == LuaValue.Type.table) {
                LuaValue val = arg2.getTableValue().get(new LuaValue("value"));
                return LuaValue.div(arg1, val);
            } else {
                return List.of(new LuaValue());
            }
        }));
        LuaValue metatable = new LuaValue(metatableContent);

        table.setMetatable(metatable);

        return table;
    }

    private static LuaValue createTableWithModMetatableAction(LuaValue value) {
        Map<LuaValue, LuaValue> tableContent = new HashMap<>();
        tableContent.put(new LuaValue("value"), value);
        LuaValue table = new LuaValue(tableContent);

        Map<LuaValue, LuaValue> metatableContent = new HashMap<>();
        metatableContent.put(LuaMetatable.MOD_VAlUE, new LuaValue((args) -> {
            LuaValue arg1 = args.get(0);
            LuaValue arg2 = args.get(1);

            if (arg1.getType() == LuaValue.Type.table) {
                LuaValue val = arg1.getTableValue().get(new LuaValue("value"));
                return LuaValue.mod(val, arg2);
            } else if (arg2.getType() == LuaValue.Type.table) {
                LuaValue val = arg2.getTableValue().get(new LuaValue("value"));
                return LuaValue.mod(arg1, val);
            } else {
                return List.of(new LuaValue());
            }
        }));
        LuaValue metatable = new LuaValue(metatableContent);

        table.setMetatable(metatable);

        return table;
    }
}
