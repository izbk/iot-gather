package net.cdsunrise.ztyg.acquisition.common.exception;

import java.util.function.Supplier;

public class Asserts {
    public static void assertTrue(boolean b, Supplier<? extends RuntimeException> supplier) {
        if (!b) {
            throw supplier.get();
        }
    }

    public static void assertFalse(boolean b, Supplier<? extends RuntimeException> supplier) {
        if (b) {
            throw supplier.get();
        }
    }

    public static void assertNotNull(Object o, Supplier<? extends RuntimeException> supplier) {
        if (o == null) {
            throw supplier.get();
        }
    }

    public static void assertStringNotNull(String s, Supplier<? extends RuntimeException> supplier) {
        if (s == null || s.length() == 0) {
            throw supplier.get();
        }
    }

    public static void assertNull(Object o, Supplier<? extends RuntimeException> supplier) {
        if (o != null) {
            throw supplier.get();
        }
    }

    public static void assertEquals(Object lhs, Object rhs,
                                    Supplier<? extends RuntimeException> supplier) {
        assertNotNull(lhs, supplier);
        assertNotNull(rhs, supplier);
        assertTrue(lhs.equals(rhs), supplier);
    }
}
