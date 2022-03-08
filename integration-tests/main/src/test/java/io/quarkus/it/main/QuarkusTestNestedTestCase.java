package io.quarkus.it.main;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.junit.QuarkusTest;

/**
 * Tests {@link Nested} support of {@link QuarkusTest}. Notes:
 * <ul>
 * <li>to avoid unexpected execution order, don't use surefire's {@code -Dtest=...}, use {@code -Dgroups=nested} instead</li>
 * <li>order of nested test classes is reversed by JUnit (and there's no way to enforce a specific order)</li>
 * </ul>
 */
@QuarkusTest
@Tag("nested")
public class QuarkusTestNestedTestCase {

    private static final AtomicInteger COUNT_BEFORE_ALL = new AtomicInteger(0);
    private static final AtomicInteger COUNT_BEFORE_EACH = new AtomicInteger(0);
    private static final AtomicInteger COUNT_TEST = new AtomicInteger(0);
    private static final AtomicInteger COUNT_AFTER_EACH = new AtomicInteger(0);
    private static final AtomicInteger COUNT_AFTER_ALL = new AtomicInteger(0);
    private static final String EXPECTED_OUTER_VALUE = "set from outer";
    private static final String EXPECTED_INNER_VALUE = "set from inner";

    String outerValue;

    @BeforeAll
    static void beforeAll() {
        COUNT_BEFORE_ALL.incrementAndGet();
    }

    @BeforeEach
    void beforeEach() {
        COUNT_BEFORE_EACH.incrementAndGet();
        outerValue = EXPECTED_OUTER_VALUE;
    }

    @Test
    void test() {
        assertEquals(1, COUNT_BEFORE_ALL.get(), "COUNT_BEFORE_ALL");
        assertEquals(1, COUNT_BEFORE_EACH.get(), "COUNT_BEFORE_EACH");
        assertEquals(0, COUNT_TEST.getAndIncrement(), "COUNT_TEST");
        assertEquals(0, COUNT_AFTER_EACH.get(), "COUNT_AFTER_EACH");
        assertEquals(0, COUNT_AFTER_ALL.get(), "COUNT_AFTER_ALL");
    }

    @Nested
    @TestMethodOrder(OrderAnnotation.class)
    class FirstNested {

        String innerValue;

        @BeforeEach
        void beforeEach() {
            COUNT_BEFORE_EACH.incrementAndGet();
            innerValue = EXPECTED_INNER_VALUE;
        }

        @Test
        @Order(1)
        void testOne() {
            assertEquals(1, COUNT_BEFORE_ALL.get(), "COUNT_BEFORE_ALL");
            assertEquals(5, COUNT_BEFORE_EACH.get(), "COUNT_BEFORE_EACH");
            assertEquals(2, COUNT_TEST.getAndIncrement(), "COUNT_TEST");
            assertEquals(3, COUNT_AFTER_EACH.get(), "COUNT_AFTER_EACH");
            assertEquals(0, COUNT_AFTER_ALL.get(), "COUNT_AFTER_ALL");
        }

        @Test
        @Order(2)
        void testTwo() {
            assertEquals(1, COUNT_BEFORE_ALL.get(), "COUNT_BEFORE_ALL");
            assertEquals(7, COUNT_BEFORE_EACH.get(), "COUNT_BEFORE_EACH");
            assertEquals(3, COUNT_TEST.getAndIncrement(), "COUNT_TEST");
            assertEquals(5, COUNT_AFTER_EACH.get(), "COUNT_AFTER_EACH");
            assertEquals(0, COUNT_AFTER_ALL.get(), "COUNT_AFTER_ALL");
        }

        @Test
        void testInnerAndOuterValues() {
            assertEquals(EXPECTED_INNER_VALUE, innerValue);
            assertEquals(EXPECTED_OUTER_VALUE, outerValue);
        }

        @AfterEach
        void afterEach() {
            COUNT_AFTER_EACH.incrementAndGet();
        }
    }

    @Nested
    class SecondNested {

        @BeforeEach
        void beforeEach() {
            COUNT_BEFORE_EACH.incrementAndGet();
        }

        @Test
        void testOne() {
            assertEquals(1, COUNT_BEFORE_ALL.get(), "COUNT_BEFORE_ALL");
            assertEquals(3, COUNT_BEFORE_EACH.get(), "COUNT_BEFORE_EACH");
            assertEquals(1, COUNT_TEST.getAndIncrement(), "COUNT_TEST");
            assertEquals(1, COUNT_AFTER_EACH.get(), "COUNT_AFTER_EACH");
            assertEquals(0, COUNT_AFTER_ALL.get(), "COUNT_AFTER_ALL");
        }

        @AfterEach
        void afterEach() {
            COUNT_AFTER_EACH.incrementAndGet();
        }
    }

    @AfterEach
    void afterEach() {
        COUNT_AFTER_EACH.incrementAndGet();
    }

    @AfterAll
    static void afterAll() {
        assertEquals(1, COUNT_BEFORE_ALL.get(), "COUNT_BEFORE_ALL");
        assertEquals(9, COUNT_BEFORE_EACH.get(), "COUNT_BEFORE_EACH");
        assertEquals(4, COUNT_TEST.get(), "COUNT_TEST");
        assertEquals(9, COUNT_AFTER_EACH.get(), "COUNT_AFTER_EACH");
        assertEquals(0, COUNT_AFTER_ALL.getAndIncrement(), "COUNT_AFTER_ALL");
    }
}
