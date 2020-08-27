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

package com.google.sps.data;

import java.net.URL;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageSource;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;

/**
 * Class that detects text and sends it as a url query to be handled by the shopping results script
 * and search shopping results servlet.
 */

public class DetectText {

  public static List<String> detectText(String blobKeyString) throws IOException {

    List<AnnotateImageRequest> requests = new ArrayList<>();

    List<String> text = new ArrayList<>();

    ImageSource imgSource = ImageSource.newBuilder().setImageUri("https://shop-by-photos-step-2020.ey.r.appspot.com/get-image-url?blob-key=" + blobKeyString).build();
    Image img = Image.newBuilder().setSource(imgSource).build();

    Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();

    AnnotateImageRequest request =
        AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
    requests.add(request);

    // Initialize client that will be used to send requests. This client only needs to be created
    // once, and can be reused for multiple requests. After completing all of your requests, call
    // the "close" method on the client to safely clean up any remaining background resources.
    try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
      BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
      List<AnnotateImageResponse> responses = response.getResponsesList();
      for (AnnotateImageResponse res : responses) {
        // if (res.hasError()) {
        //   text.add(System.out.format("Error: %s%n", res.getError().getMessage()));
        //   return;
        // }

        int prePosition = 0;

        // For full list of available annotations, see http://g.co/cloud/vision/docs
        for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
          int position;
          text.add("Text:" + annotation.getDescription());
          text.add("<br>");
          text.add("Position :" + annotation.getBoundingPoly());
          text.add("<br>");
        }
      }
    }

    int startIndexToFetchText = 4;
    List<String> queryItem = new ArrayList<>();
    queryItem.add(text.get(startIndexToFetchText).split(":")[1]);
    startIndexToFetchText += 4;
    while(startIndexToFetchText < text.size()) {
        queryItem.add(text.get(startIndexToFetchText).split(":")[1]);
        startIndexToFetchText += 4;
    }
    return queryItem;
  } 
}
