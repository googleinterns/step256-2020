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
import com.google.cloud.vision.v1.ImageSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class generates a shopping list based on image 1) It uses cloudVisionAPI to scan an image
 * containing shopping list items and detect text from it. 2) It then uses an algorithm to create
 * shopping sentences (queries) from the text and their position. 3) This list of queries is
 * returned to the Servlet from the method 'imageToShoppingListExtractor'.
 */
public class DetectTextFromImage {

  public DetectTextFromImage() {}

  public String imageToShoppingListExtractor(String shoppingImageKey)
      throws IOException, PhotoShoppingException {

    ImageSource shoppingImageSource = shoppingImageInitializer(shoppingImageKey);

    List<AnnotateImageRequest> requests = shoppingImageRequestGenerator(shoppingImageSource);

    BatchAnnotateImagesResponse response = cloudVisionAPIQuerier(requests);

    List<EntityAnnotation> annotation = parseAnnotateImageResponse(response);

    return createShoppingListQuery(annotation);
  }

  public ImageSource shoppingImageInitializer(String shoppingImageKey)
      throws PhotoShoppingException {
    if (!PhotoShoppingUtil.isValidImageKey(shoppingImageKey)) {
      throw new PhotoShoppingException("Invalid blob key");
    }
    ImageSource shoppingImageSource =
        ImageSource.newBuilder().setImageUri(Constants.IMAGE_BASE_URI + shoppingImageKey).build();
    return shoppingImageSource;
  }

  /** Generates the request query to be sent to CloudVisionAPI client. */
  // Keeping it public so that it could be tested from the unit tests
  public List<AnnotateImageRequest> shoppingImageRequestGenerator(ImageSource shoppingImageSource) {
    List<AnnotateImageRequest> requests = new ArrayList<>();
    Image shoppingImage = Image.newBuilder().setSource(shoppingImageSource).build();

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
  private BatchAnnotateImagesResponse cloudVisionAPIQuerier(List<AnnotateImageRequest> requests)
      throws PhotoShoppingException {
    ImageAnnotatorClient cloudVisionClient;
    try {
      cloudVisionClient = ImageAnnotatorClient.create();
    } catch (IOException exception) {
      throw new PhotoShoppingException(
          "Failed to create cloudVisionClient\n" + exception.getMessage());
    }
    BatchAnnotateImagesResponse response = cloudVisionClient.batchAnnotateImages(requests);
    cloudVisionClient.close();
    return response;
  }

  /**
   * Takes cloudVisionAPI's response and generates shopping list as Text, Position. ToDo : This
   * position will be used in sentence formation algorithm to separate individual queries from the
   * shopping list.
   */
  private List<EntityAnnotation> parseAnnotateImageResponse(BatchAnnotateImagesResponse response)
      throws PhotoShoppingException {
    List<String> shoppingList = new ArrayList<>();
    List<AnnotateImageResponse> responses = response.getResponsesList();
    List<EntityAnnotation> annotation = new ArrayList<>();
    for (AnnotateImageResponse identifiedText : responses) {
      if (identifiedText.hasError()) {
        throw new PhotoShoppingException(
            "An error occurred while identifying the text from the image\n"
                + identifiedText.getError().getMessage());
      }
      annotation = identifiedText.getTextAnnotationsList();
    }
    return annotation;
  }

  /** Creates query from the text detected by cloudVision API. */
  // Keeping it public so that it could be tested from the unit tests
  public String createShoppingListQuery(List<EntityAnnotation> annotation)
      throws PhotoShoppingException {
    // ToDo: Make an algorithm to create query sentences by separating out text returned by
    // cloudVisionAPI
    // to group shoppping items based on their position (y axis).
    if (annotation.size() < 1) {
      throw new PhotoShoppingException("Shopping List doesn't contain any text");
    }
    // split only first :. Ignore other : values.
    String queryItem = annotation.get(0).getDescription();
    return PhotoShoppingUtil.formatQuery(queryItem);
  }
}
