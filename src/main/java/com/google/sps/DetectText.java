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
import com.google.sps.PhotoShoppingException;

/**
 * Class that detects text and sends it as a url query to be handled by the shopping results script
 * and search shopping results servlet.
 */
public class DetectText {

  public List<String> productDetection(String blobKeyString) throws IOException, PhotoShoppingException {
    List<AnnotateImageRequest> requests = new ArrayList<>();

    ImageSource imgSource =
        ImageSource.newBuilder()
            .setImageUri(
                "https://shop-by-photos-step-2020.ey.r.appspot.com/get-image-url?blob-key="
                    + blobKeyString)
            .build();
    Image img = Image.newBuilder().setSource(imgSource).build();
    Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
    AnnotateImageRequest request =
        AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
    requests.add(request);

    List<String> text = new ArrayList<>();

    // Initialize client that will be used to send requests. This client only needs to be created
    // once, and can be reused for multiple requests. After completing all of your requests, call
    // the "close" method on the client to safely clean up any remaining background resources.
    try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
      BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
      List<AnnotateImageResponse> responses = response.getResponsesList();
      for (AnnotateImageResponse res : responses) {
        if (res.hasError()) {
         throw new PhotoShoppingException (res.getError().getMessage());
        }

        // For full list of available annotations, see http://g.co/cloud/vision/docs
        for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
          text.add("Text:" + annotation.getDescription());
          text.add("Position :" + annotation.getBoundingPoly());
        }
      }
    }

    List<String> queryItem = new ArrayList<>();
    //check length
    // ToDo: Make an algorithm to get query sentences.
    if (text.size()>1) {
        queryItem.add(text.get(0).split(":", 2)[1]); // split with limit 2

        for (int i = 0; i < queryItem.size(); i++) {
        queryItem.set(i, queryItem.get(i).replaceAll("\\s+", " ").trim());
        }
    }
    return queryItem;
  }

  public boolean isInSameLine() {
      
  }

}
