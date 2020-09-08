/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.sps;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class generates a shopping list based on image
 * 1) It uses cloudVisionAPI to scan an image containing shopping list items and detect text from it. 
 * 2) It then uses an algorithm to create shopping sentences (queries) from the text and their position.
 * 3) This list of queries is returned to the Servlet from {@code imageToShoppingListExtractor()}.
 */
public class DetectTextFromImage {
  private final String IMAGE_BASE_URI =
      "https://shop-by-photos-step-2020.ey.r.appspot.com/get-image-url?blob-key=";
  private final Feature TEXT_DETECTION_FEATURE = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
  private ImageSource shoppingImageSource;

  /*
  * This constructor creates object only if a valid ImageKey is provided. 
  * It then initializes shoppingImageSource to be used by the Generator function.
  * If the ImageKey is invalid/mull, it throws an exception and the object never gets created.
  */
  public DetectTextFromImage(String shoppingImageKey) throws PhotoShoppingException {
    if (!isValidImageKey(shoppingImageKey)) {
      throw new PhotoShoppingException("ERROR : Invalid blob key");
    }
    shoppingImageSource = ImageSource.newBuilder().setImageUri(IMAGE_BASE_URI + shoppingImageKey).build();
  }

  /** {@code shoppingImageRequestGenerator()} generates the request query to be sent to CloudVisionAPI client. */
  // Keeping it public so that it could be tested from the unit tests
  public List<AnnotateImageRequest> shoppingImageRequestGenerator() {
    List<AnnotateImageRequest> requests = new ArrayList<>();
    Image shoppingImage = Image.newBuilder().setSource(shoppingImageSource).build();

    AnnotateImageRequest request =
        AnnotateImageRequest.newBuilder()
            .addFeatures(TEXT_DETECTION_FEATURE)
            .setImage(shoppingImage)
            .build();

    requests.add(request);
    return requests;
  }

/** 
 * This function is used to send request to cloudVisionAPI 
 *  The cloudVisionAPI scans the image and returns back the text, its position and properties as the response.
 */
  private BatchAnnotateImagesResponse cloudVisionAPIQuerier(List<AnnotateImageRequest> requests) 
    throws PhotoShoppingException {
    ImageAnnotatorClient cloudVisionClient;
    try {
         cloudVisionClient = ImageAnnotatorClient.create();
    } catch (IOException exception) {
        throw new PhotoShoppingException ("ERROR : Failed to create cloudVisionClient\n" + exception.getMessage());
    }
    BatchAnnotateImagesResponse response = cloudVisionClient.batchAnnotateImages(requests);
    cloudVisionClient.close();
    return response;
  }

/** 
 * {@code cloudVisionResponseParser} takes cloudVisionAPI's response and generates shopping list as Text, Position.
 * ToDo : This position will be used in sentence formation algorithm to separate individual queries from the shopping list.
 */
  private List<String> cloudVisionResponseParser(BatchAnnotateImagesResponse response)
      throws PhotoShoppingException {
    List<String> shoppingList = new ArrayList<>();
    List<AnnotateImageResponse> responses = response.getResponsesList();
    for (AnnotateImageResponse identifiedText : responses) {
      if (identifiedText.hasError()) {
        throw new PhotoShoppingException("ERROR : An error occurred while identifying the text from the image\n" 
            + identifiedText.getError().getMessage());
      }
      for (EntityAnnotation annotation : identifiedText.getTextAnnotationsList()) {
        shoppingList.add("Text:" + annotation.getDescription());
        shoppingList.add("Position :" + annotation.getBoundingPoly());
      }
    }
    return shoppingList;
  }

  /** createShoppingListQuery function creates query from the text detected by cloudVision API. */
  // Keeping it public so that it could be tested from the unit tests
  public String createShoppingListQuery(List<String> shoppingList) throws PhotoShoppingException {
    // ToDo: Make an algorithm to create query sentences by separating out text returned by cloudVisionAPI 
    // to group shoppping items based on their position (y axis).
    if (shoppingList.size() < 1) {
      throw new PhotoShoppingException("Shopping List doesn't contain any text");
    }
    if (!shoppingList.get(0).contains(":")) {
      throw new PhotoShoppingException("Invalid Shopping List");
    }
    // split only first :. Ignore other : values.
    String queryItem = shoppingList.get(0).split(":", 2)[1];
    return formatQueryItem(queryItem);
  }

  private String formatQueryItem(String queryItem) {
    queryItem =
        queryItem
            .replaceAll("\\s+", " ") // Remove duplicate spaces
            .trim() // Remove spaces from the beginning and end of string
            .replaceAll("[-+=,\n._^\";:~#></|!*]", ""); // Remove special characters
    return queryItem;
  }

  private boolean isValidImageKey(String shoppingImageKey) {
    // If the key contains space or escape charaters or if null then it is invalid.
    if (shoppingImageKey.contains("[\n]")
        | shoppingImageKey.equals("")
        | shoppingImageKey.isEmpty()
        | shoppingImageKey.contains(" ")) {
      return false;
    }
    return true;
  }

  public String imageToShoppingListExtractor()
      throws IOException, PhotoShoppingException {
    // ImageSource shoppingImageSource = shoppingImageInitializer(shoppingImageKey);

    List<AnnotateImageRequest> requests = shoppingImageRequestGenerator();

    BatchAnnotateImagesResponse response = cloudVisionAPIQuerier(requests);

    List<String> shoppingList = cloudVisionResponseParser(response);

    return createShoppingListQuery(shoppingList);
  }
}
