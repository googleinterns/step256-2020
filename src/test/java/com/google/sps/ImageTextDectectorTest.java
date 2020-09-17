// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import com.google.sps.data.ShoppingListTextEntry;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Test for ImageTextDectector class. */
@RunWith(JUnit4.class)
public final class ImageTextDectectorTest {

  private static final byte[] NULL_IMAGE_BYTES = new byte[0];
  private static final byte[] IMAGE_BYTES = new byte[1];

  private FakeTextDetectionAPIImpl fakeTextDetectionAPIImpl;

  private ImageTextDectector imageTextDectector;

  @Before
  public void setUp() {
    fakeTextDetectionAPIImpl = new FakeTextDetectionAPIImpl();
  }

  /**
   * Set text detection result/exception, mocking Cloud Vision API, and initialize
   * ImageTextDectector's object.
   */
  private void initImageTextDectector(List<ShoppingListTextEntry> shoppingListTextEntries) {
    fakeTextDetectionAPIImpl.setReturnValue(shoppingListTextEntries);
    imageTextDectector = new ImageTextDectector(fakeTextDetectionAPIImpl);
  }

  /** Negative test for empty image */
  @Test
  public void noBytesImage() throws Exception {
    String exceptionMessage = "byte array is empty";
    fakeTextDetectionAPIImpl.setException(new PhotoDetectionException(exceptionMessage));
    imageTextDectector = new ImageTextDectector(fakeTextDetectionAPIImpl);

    Exception exception =
        Assertions.assertThrows(
            PhotoDetectionException.class,
            () -> {
              imageTextDectector.extractShoppingList(NULL_IMAGE_BYTES);
            });

    String expectedMessage = exceptionMessage;
    String actualMessage = exception.getMessage();
    Assertions.assertTrue(actualMessage.equals(expectedMessage));
  }

  /** Negative test for no text */
  @Test
  public void noText() throws Exception {
    List<ShoppingListTextEntry> shoppingListTextEntries = new ArrayList<>();

    initImageTextDectector(shoppingListTextEntries);

    Exception exception =
        Assertions.assertThrows(
            PhotoDetectionException.class,
            () -> {
              imageTextDectector.extractShoppingList(IMAGE_BYTES);
            });

    String expectedMessage = "Shopping List doesn't contain any text";
    String actualMessage = exception.getMessage();
    Assertions.assertTrue(actualMessage.equals(expectedMessage));
  }

  @Test
  public void singleWordImage() throws Exception {
    List<ShoppingListTextEntry> shoppingListTextEntries = new ArrayList<>();
    shoppingListTextEntries.add(ShoppingListTextEntry.create("Bag", 10, 10, 13));

    initImageTextDectector(shoppingListTextEntries);

    List<String> expectedShoppingQuery = new ArrayList<>();
    expectedShoppingQuery.add("Bag");
    List<String> actualShoppingQuery = imageTextDectector.extractShoppingList(IMAGE_BYTES);
    Assert.assertEquals(expectedShoppingQuery, actualShoppingQuery);
  }

  @Test
  public void multiWordsInSingleLineImage() throws Exception {
    List<ShoppingListTextEntry> shoppingListTextEntries = new ArrayList<>();
    shoppingListTextEntries.add(ShoppingListTextEntry.create("Blue", 10, 10, 13));
    shoppingListTextEntries.add(ShoppingListTextEntry.create("Shoes", 15, 11, 14));
    shoppingListTextEntries.add(ShoppingListTextEntry.create("For", 20, 9, 12));
    shoppingListTextEntries.add(ShoppingListTextEntry.create("Boys", 28, 10, 13));

    initImageTextDectector(shoppingListTextEntries);

    List<String> expectedShoppingQuery = new ArrayList<>();
    expectedShoppingQuery.add("Blue Shoes For Boys");
    List<String> actualShoppingQuery = imageTextDectector.extractShoppingList(IMAGE_BYTES);
    Assert.assertEquals(expectedShoppingQuery, actualShoppingQuery);
  }

  @Test
  public void multiWordsWithSpecialCharsImage() throws Exception {
    List<ShoppingListTextEntry> shoppingListTextEntries = new ArrayList<>();
    shoppingListTextEntries.add(ShoppingListTextEntry.create("Blue", 10, 10, 13));
    shoppingListTextEntries.add(ShoppingListTextEntry.create("Shoes", 15, 11, 13));
    shoppingListTextEntries.add(ShoppingListTextEntry.create("^+-", 22, 8, 13));
    shoppingListTextEntries.add(ShoppingListTextEntry.create("\n", 29, 14, 13));

    initImageTextDectector(shoppingListTextEntries);

    List<String> expectedShoppingQuery = new ArrayList<>();
    expectedShoppingQuery.add("Blue Shoes");
    List<String> actualShoppingQuery = imageTextDectector.extractShoppingList(IMAGE_BYTES);
    Assert.assertEquals(expectedShoppingQuery, actualShoppingQuery);
  }

  @Test
  public void multiWordsInMultiLinesImage() throws Exception {
    List<ShoppingListTextEntry> shoppingListTextEntries = new ArrayList<>();
    shoppingListTextEntries.add(ShoppingListTextEntry.create("Canon", 134, 63, 71));
    shoppingListTextEntries.add(ShoppingListTextEntry.create("Camera", 237, 63, 71));
    shoppingListTextEntries.add(ShoppingListTextEntry.create("Pink", 134, 78, 86));
    shoppingListTextEntries.add(ShoppingListTextEntry.create("shoes", 160, 78, 86));

    initImageTextDectector(shoppingListTextEntries);

    List<String> expectedShoppingQuery = new ArrayList<>();
    expectedShoppingQuery.add("Canon Camera");
    expectedShoppingQuery.add("Pink shoes");
    List<String> actualShoppingQuery = imageTextDectector.extractShoppingList(IMAGE_BYTES);
    Assert.assertEquals(expectedShoppingQuery, actualShoppingQuery);
  }

  @Test
  public void singleWordsInMultiLinesImage() throws Exception {
    List<ShoppingListTextEntry> shoppingListTextEntries = new ArrayList<>();
    shoppingListTextEntries.add(ShoppingListTextEntry.create("NoteBook", 10, 10, 15));
    shoppingListTextEntries.add(ShoppingListTextEntry.create("Tea", 10, 20, 26));

    initImageTextDectector(shoppingListTextEntries);

    List<String> expectedShoppingQuery = new ArrayList<>();
    expectedShoppingQuery.add("NoteBook");
    expectedShoppingQuery.add("Tea");
    List<String> actualShoppingQuery = imageTextDectector.extractShoppingList(IMAGE_BYTES);
    Assert.assertEquals(expectedShoppingQuery, actualShoppingQuery);
  }
}
