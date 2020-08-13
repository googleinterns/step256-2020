package com.google.sps;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Class responsible for testing repository setup.
 */
public final class SetupTest {

  @Test
  public void setupTest() {
    Setup setupTester = new Setup();
    assertEquals("Hello World!", setupTester.echo());
  }
}
