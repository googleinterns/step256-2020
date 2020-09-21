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
 * This class generates a shopping list based on image 
 * 1) It uses cloudVisionAPI to scan an image containing shopping
 * list items and detect text from it. 
 * 2) It then uses an algorithm to create shopping sentences (queries)
 * from the text and their position. 
 * 3) This list of queries is returned to the Servlet from the method 'extractShoppingList'.
 */
public class ImageTextDectector {

  TextDetectionAPI textDetectionAPI;

  public ImageTextDectector(TextDetectionAPI textDetectionAPI) {
    this.textDetectionAPI = textDetectionAPI;
  }

  public List<String> extractShoppingList(byte[] shoppingImageBytes)
      throws IOException, PhotoDetectionException {
    List<ShoppingListTextEntry> shoppingListText = textDetectionAPI.detect(shoppingImageBytes);

    return createShoppingListQuery(shoppingListText);
  }

  /** Creates query from the text detected by cloudVision API. */
  private List<String> createShoppingListQuery(List<ShoppingListTextEntry> shoppingListText)
      throws PhotoDetectionException {
    if (shoppingListText.isEmpty()) {
      throw new PhotoDetectionException("Shopping List doesn't contain any text");
    }

    List<String> shoppingQueries = algorithm(shoppingListText);
    return shoppingQueries;
    // return PhotoShoppingUtil.formatQuery(queryItem);
  }

  private List<String> algorithm(List<ShoppingListTextEntry> shoppingListText) {
    // Create query sentences by separating out text returned by cloudVisionAPI 
    // To group shoppping items based on their position (y axis).
    int xAxisCurrent, yAxisCurrentLower, yAxisCurrentUpper;
    String sentence = "";
    List<String> shoppingQueries = new ArrayList<>();
    int yAxisPrevUpper = shoppingListText.get(0).getUpperYBoundary();
    for (ShoppingListTextEntry singleWord : shoppingListText) {
      xAxisCurrent = singleWord.getLowerXBoundary();
      yAxisCurrentLower = singleWord.getLowerYBoundary();
      yAxisCurrentUpper = singleWord.getUpperYBoundary();

      if (checkForSameLineWord(xAxisCurrent, yAxisCurrentLower, yAxisPrevUpper)) {
        sentence += singleWord.getText() + " ";
      } else {
        sentence = PhotoShoppingUtil.formatQuery(sentence);
        if (!sentence.isEmpty()) {
          shoppingQueries.add(sentence);
        }
        sentence = singleWord.getText() + " ";
      }

      yAxisPrevUpper = yAxisCurrentUpper;
    }

    sentence = PhotoShoppingUtil.formatQuery(sentence);
    if (!sentence.isEmpty()) {
      shoppingQueries.add(sentence);
    }

    return shoppingQueries;
  }

  private boolean checkForSameLineWord(int lowerXCurrent, int lowerYCurrent, int prevUpperY) {
    if (lowerYCurrent > prevUpperY) {
      return false;
    }
    return true;
  }
}
