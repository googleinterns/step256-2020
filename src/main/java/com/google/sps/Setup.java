package com.google.sps;

/**
 * Class responsible for testing the repository setup.
 */
public class Setup {
  public static void main(String[] args) {
    Setup setup = new Setup();
    System.out.println(setup.echo());
  }  
  
  public static String echo() {
    return "Hello World!";
  }
}
