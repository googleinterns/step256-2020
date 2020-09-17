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
import com.google.cloud.vision.v1.BoundingPoly;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.Vertex;
import com.google.sps.data.ShoppingListTextEntry;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TextDetectionAPIImpl implements TextDetectionAPI {
  public List<ShoppingListTextEntry> detect(byte[] imageBytes) throws PhotoDetectionException {
    Image shoppingImage = PhotoShoppingUtil.getImageFromBytes(imageBytes);

    AnnotateImageRequest request = generateShoppingImageRequest(shoppingImage);

    BatchAnnotateImagesResponse response = detectTextFromImage(request);

    return parseAnnotateImageResponse(response);
  }

  /** Generates the request query to be sent to CloudVisionAPI client. */
  private AnnotateImageRequest generateShoppingImageRequest(Image shoppingImage) {
    AnnotateImageRequest request =
        AnnotateImageRequest.newBuilder()
            .addFeatures(Constants.TEXT_DETECTION_FEATURE)
            .setImage(shoppingImage)
            .build();

    return request;
  }

  /**
   * Sends request to cloudVisionAPI. The Cloud Vision API scans the image and returns back the
   * text, its position and properties as the response.
   */
  private BatchAnnotateImagesResponse detectTextFromImage(AnnotateImageRequest request)
      throws PhotoDetectionException {
    ImageAnnotatorClient cloudVisionClient;
    try {
      cloudVisionClient = ImageAnnotatorClient.create();
    } catch (IOException exception) {
      throw new PhotoDetectionException(
          "Failed to create cloudVisionClient\n" + exception.getMessage(), exception);
    }
    List<AnnotateImageRequest> requests = new ArrayList<>();
    requests.add(request);
    BatchAnnotateImagesResponse response = cloudVisionClient.batchAnnotateImages(requests);
    cloudVisionClient.close();
    return response;
  }

  /**
   * Takes cloudVisionAPI's response and returns a list of text and their y-axis position.
   * ToDo : The positions from annotation will be used in sentence formation algorithm to separate
   * individual queries from the shopping list.
   */
  private List<ShoppingListTextEntry> parseAnnotateImageResponse(
      BatchAnnotateImagesResponse response) throws PhotoDetectionException {
    List<AnnotateImageResponse> responses = response.getResponsesList();
    List<ShoppingListTextEntry> shoppingListText = new ArrayList<>();
    for (AnnotateImageResponse identifiedText : responses) {
      if (identifiedText.hasError()) {
        throw new PhotoDetectionException(
            "An error occurred while identifying the text from the image\n"
                + identifiedText.getError().getMessage());
      }
      // Future Scope: Determine sentence's height by subtracting lower boundary (lower y-axis position) and 
      // upper boundary (upper y-axis position) to help in handwritten recognition when sentences height ration will vary
      for (EntityAnnotation annotation : identifiedText.getTextAnnotationsList()) {
        shoppingListText.add(
            ShoppingListTextEntry.create(
                annotation.getDescription(), annotation.getBoundingPoly().getVertices(0).getX(), annotation.getBoundingPoly().getVertices(0).getY(), annotation.getBoundingPoly().getVertices(3).getY()));
      }
    }
    return shoppingListText;
  }
}
