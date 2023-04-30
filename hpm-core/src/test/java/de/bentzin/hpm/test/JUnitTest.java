package de.bentzin.hpm.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Ture Bentzin
 * 30.04.2023
 */
public class JUnitTest {

    @Test
    @DisplayName("JUnit functionality test")
    public void generalTest() {
        System.out.println("Test was executed!");
        assertTrue(true);
    }
}
