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

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class generates a shopping list based on image 
 * 1) It uses cloudVisionAPI to scan an image
 * containing shopping list items and detect text from it.
 * 2) It then uses an algorithm to create shopping sentences 
 * (queries) from the text and their position.
 * 3) This list of queries is returned to the Servlet from the
 * method 'imageToShoppingListExtractor'.
 */
public class ImageTextDectector {

  public ImageTextDectector() {}

  public String imageToShoppingListExtractor(byte[] shoppingImageBytes)
       throws IOException, PhotoDetectionException {
 
  
    List<EntityAnnotation> annotation = parseAnnotateImageResponse(response);
 
    return createShoppingListQuery(annotation);
  }



  

 

  /** Creates query from the text detected by cloudVision API. */
  private String createShoppingListQuery(List<EntityAnnotation> annotation)
      throws PhotoDetectionException {
    // ToDo: Make an algorithm to create query sentences by separating out text returned by
    // cloudVisionAPI; to group shoppping items based on their position (y axis).
    if (annotation.size() < 1) {
      throw new PhotoDetectionException("Shopping List doesn't contain any text");
    }
    // split only first :. Ignore other : values.
    String queryItem = annotation.get(0).getDescription();
    return PhotoShoppingUtil.formatQuery(queryItem);
  }
}
