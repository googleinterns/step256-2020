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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class generates a shopping list based on image provided in {@link #extractShoppingList(byte[])}
 * 1) It uses cloudVisionAPI to scan the image containing shopping
 * list items and detect text from it. 
 * 2) It then uses an algorithm to extract shopping sentences (queries)
 * from the text based on their position (y coordinates). 
 * 3) This list of queries is returned to the Servlet from the {@link #extractShoppingList(byte[])}.
 */
public class ImageTextDectector {

  // This variable is used to invoke interface's implimentation for real CloudVisionAPI calls and fake calls from tests.
  private TextDetectionAPI textDetectionAPI;

  public ImageTextDectector(TextDetectionAPI textDetectionAPI) {
    this.textDetectionAPI = textDetectionAPI;
  }

  public List<String> extractShoppingList(byte[] shoppingImageBytes)
      throws IOException, PhotoDetectionException {
    List<ShoppingListTextEntry> shoppingListText = textDetectionAPI.detect(shoppingImageBytes);

    return createShoppingListQueries(shoppingListText);
  }

  /** Creates query from the text detected by Cloud Vision API. */
  private List<String> createShoppingListQueries(List<ShoppingListTextEntry> shoppingListText)
      throws PhotoDetectionException {
    if (shoppingListText.isEmpty()) {
      throw new PhotoDetectionException("Shopping List doesn't contain any text");
    }

    // Remove the first element of this list because cloud vision API returns all the 
    // text detetcted as the first element of the list followed by list of single 
    // words and their properties. Examples of text returned from API can be seen in the test file.
    shoppingListText.remove(0);

    if (shoppingListText.isEmpty()) {
      throw new PhotoDetectionException("Shopping List is empty");
    }

    // To group shoppping items based on their position.
    String sentence = "";
    List<String> shoppingQueries = new ArrayList<>();

    // Initialize yAxisPrevLower with the current lower y value.
    int yAxisPrevLower = shoppingListText.get(0).getLowerYBoundary();

    for (ShoppingListTextEntry detectedWord : shoppingListText) {
      int yAxisCurrentLower = detectedWord.getLowerYBoundary();
      int yAxisCurrentUpper = detectedWord.getUpperYBoundary();
      
      // ToDo: Determine sentence's height by subtracting lower boundary (lower y-axis
      // position) and upper boundary (upper y-axis position) to help in handwritten recognition
      // when sentences height ration will vary

      if (isInSameLine(yAxisCurrentUpper, yAxisPrevLower)) {
        sentence += detectedWord.getText() + " ";
      } else {
        shoppingQueries = formatAndAddQuery(sentence, shoppingQueries);
        sentence = detectedWord.getText() + " ";
      }

      // Assign current word's lower y value to yAxisPrevLower for comparisions in the next iteration. 
      yAxisPrevLower = yAxisCurrentLower;
    }

    shoppingQueries = formatAndAddQuery(sentence, shoppingQueries);
    return shoppingQueries;
  }

  private boolean isInSameLine(int currentUpperY, int prevLowerY) {
    // Compare the upper boundary of the current word box to the lower boundary of the previous word box.
    // The Cloud Vision API returns words from left to right and from top to bottom, the origin being
    // the top-left corner.
    // LOGIC :
    // (1) Upper y axis boundary is always less than lower y axis boundary for word boxes on the same sentence. 
    // (2) Once the sentence changes (ie goes to the next row), the upper boundary of the current word box 
    // becomes greater than the lower boundary of the previous.

    // SAMPLE ITERATION OF CLOUD VISION API OUTPUT
    // 2nd iteration
    //                         v current upper y axis
    // Sentence 1   // Box1.1  Box1.2
    //                 ^ previous lower y axis
    // Sentence 2   // Box2

    // 3rd iteration
    // Sentence 1       // Box1.1  Box1.2
    //                             ^ previous lower y axis
    //                     v current upper y axis
    // Sentence 2       // Box2

    if (currentUpperY > prevLowerY) {
      return false; // Next sentence
    }

    return true; // Same sentence
  }

  private List<String> formatAndAddQuery(String sentence, List<String> shoppingQueries) {
    sentence = PhotoShoppingUtil.formatQuery(sentence);

    // Add the formatted sentence only if it is not empty. 
    if (!sentence.isEmpty()) {
      shoppingQueries.add(sentence);
    }

    return shoppingQueries;
  }
}
