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
import com.google.cloud.vision.v1.BoundingPoly;
import com.google.cloud.vision.v1.Vertex;
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

  public List<String> imageToShoppingListExtractor(byte[] shoppingImageBytes)
       throws IOException, PhotoDetectionException {
    Image shoppingImage = PhotoShoppingUtil.getImageFromBytes(shoppingImageBytes);
 
    List<AnnotateImageRequest> requests = shoppingImageRequestGenerator(shoppingImage);
 
    BatchAnnotateImagesResponse response = detectTextFromImage(requests);
 
    List<EntityAnnotation> annotation = parseAnnotateImageResponse(response);
 
    return createShoppingListQuery(annotation);
  }

  /** Generates the request query to be sent to CloudVisionAPI client. */
  private List<AnnotateImageRequest> shoppingImageRequestGenerator(Image shoppingImage) {
    List<AnnotateImageRequest> requests = new ArrayList<>();

    AnnotateImageRequest request =
        AnnotateImageRequest.newBuilder()
            .addFeatures(Constants.TEXT_DETECTION_FEATURE)
            .setImage(shoppingImage)
            .build();

    requests.add(request);
    return requests;
  }

  /**
   * Sends request to cloudVisionAPI. The Cloud Vision API scans the image and returns back the
   * text, its position and properties as the response.
   */
  private BatchAnnotateImagesResponse detectTextFromImage(List<AnnotateImageRequest> requests)
      throws PhotoDetectionException {
    ImageAnnotatorClient cloudVisionClient;
    try {
      cloudVisionClient = ImageAnnotatorClient.create();
    } catch (IOException exception) {
      throw new PhotoDetectionException(
          "Failed to create cloudVisionClient\n" + exception.getMessage(), exception);
    }
    BatchAnnotateImagesResponse response = cloudVisionClient.batchAnnotateImages(requests);
    cloudVisionClient.close();
    return response;
  }

  /**
   * Takes cloudVisionAPI's response and returns a list of annotations. ToDo : The
   * positions from annotation will be used in sentence formation algorithm to separate
   * individual queries from the shopping list.
   */
  private List<EntityAnnotation> parseAnnotateImageResponse(BatchAnnotateImagesResponse response)
      throws PhotoDetectionException {
    List<String> shoppingList = new ArrayList<>();
    List<AnnotateImageResponse> responses = response.getResponsesList();
    List<EntityAnnotation> annotations = new ArrayList<>();
    for (AnnotateImageResponse identifiedText : responses) {
      if (identifiedText.hasError()) {
        throw new PhotoDetectionException(
            "An error occurred while identifying the text from the image\n"
                + identifiedText.getError().getMessage());
      }
      annotations = identifiedText.getTextAnnotationsList();
    }
    return annotations;
  }

  /** Creates query from the text detected by cloudVision API. */
  private List<String> createShoppingListQuery(List<EntityAnnotation> annotations)
      throws PhotoDetectionException {
    if (annotations.size() < 1) {
      throw new PhotoDetectionException("Shopping List doesn't contain any text");
    }
    // Create query sentences by separating out text returned by
    // cloudVisionAPI; to group shoppping items based on their position (y axis).
    int yAxisRef = 0;
    String sentence = "";
    List<String> shoppingQueries = new ArrayList<>();
    for(EntityAnnotation annotation : annotations) {

    }
    return shoppingQueries;
  }
}
