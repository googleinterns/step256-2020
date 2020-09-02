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

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;

// Import the Google Cloud client library
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageSource;
import com.google.protobuf.ByteString;

import com.google.sps.data.Product;
import com.google.sps.data.ShoppingQueryInput;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

public class ProductPhotoShoppingImpl {
  public List<Product> shopWithPhoto(String blobKey) throws IOException, ShoppingQuerierConnectionException  {
    // Get the labels of the image that the user uploaded.
    String firstLabel = detectLabels(blobKey);

    ShoppingQueryInput input = 
        new ShoppingQueryInput.Builder(firstLabel).language("en").maxResultsNumber(20).build();

    // Initialize the Google Shopping querier.
    GoogleShoppingQuerier querier = new GoogleShoppingQuerier();
    
    List<Product> shoppingQuerierResults = new ArrayList<>();
    try {
      shoppingQuerierResults = querier.query(input);
    } catch(IllegalArgumentException | ShoppingQuerierConnectionException | IOException exception) {
      // Re-throw exception.
      throw exception;
    }

    return shoppingQuerierResults;
  }

  public static String detectLabels(String blobKey) throws IOException {
    // Builds the image annotation request
    List<AnnotateImageRequest> requests = new ArrayList<>();

    ImageSource imgSource = 
        ImageSource.newBuilder()
            .setImageUri("https://shop-by-photos-step-2020.appspot.com/get-image-url?blob-key=" + blobKey)
            .build();
    Image img = Image.newBuilder().setSource(imgSource).build();

    Feature feat = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
    AnnotateImageRequest request =
        AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
    requests.add(request);

    // Initialize client that will be used to send requests. This client only needs to be created
    // once, and can be reused for multiple requests.
    try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
      // Perform label detection on the image file
      BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
      // All requests have been completed, so call the "close" method on the client 
      // to safely clean up any remaining background resources.
      client.close();
      
      List<AnnotateImageResponse> responses = response.getResponsesList();

      for (AnnotateImageResponse res : responses) {
        if (res.hasError()) {
          System.out.format("Error: %s%n", res.getError().getMessage());
          return "shoe";
        }
        
        for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
          return annotation.getDescription(); // get first label
        }

        // for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
        //   annotation
        //       .getAllFields()
        //       .forEach((k, v) -> System.out.format("%s : %s%n", k, v.toString()));
        // }
      }
    }
    return "pen";
  }
}
