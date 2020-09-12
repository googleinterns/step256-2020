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
import com.google.cloud.vision.v1.AnnotateImageRequest.Builder;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.ColorInfo;
import com.google.cloud.vision.v1.DominantColorsAnnotation;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;

import com.google.common.collect.ImmutableList;

import com.google.protobuf.ByteString;

import com.google.sps.data.ProductDetectionData;

import java.awt.Color;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

/**
 * Uses Cloud Vision API to detect product photo content.
 */
public class ProductDetectionAPIImpl implements ProductDetectionAPI {

  public ProductDetectionData detectProductPhotoContent(byte[] imageBytes) 
      throws PhotoDetectionException {
    // Build Image object by converting bytes array.
    ByteString byteString = ByteString.copyFrom(imageBytes);
    Image image = Image.newBuilder().setContent(byteString).build();

    // Build the image annotation request.
    // {@code requestBuilder} holds the image and the features to detect from it.
    Builder requestBuilder = AnnotateImageRequest.newBuilder();
    
    requestBuilder.setImage(image);

    Feature labelDetectionFeature = 
        Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
    requestBuilder.addFeatures(labelDetectionFeature);

    Feature logoDetectionFeature =
        Feature.newBuilder().setType(Feature.Type.LOGO_DETECTION).build();
    requestBuilder.addFeatures(logoDetectionFeature);

    Feature imagePropertiesDetectionFeature =
        Feature.newBuilder().setType(Feature.Type.IMAGE_PROPERTIES).setMaxResults(5).build();
    requestBuilder.addFeatures(imagePropertiesDetectionFeature);

    List<AnnotateImageRequest> requests = new ArrayList<>();
    requests.add(requestBuilder.build());

    // Initialize client that will be used to send requests. This client only needs to be created
    // once, and can be reused for multiple requests.
    ImageAnnotatorClient imageAnnotatorClient;
    try {
      imageAnnotatorClient = ImageAnnotatorClient.create();
    } catch (IOException exception) {
      throw new PhotoDetectionException(
          "Failed to create ImageAnnotatorClient.\n" + exception.getMessage(), exception);
    }

    // Perform detection on the image file.
    BatchAnnotateImagesResponse response = imageAnnotatorClient.batchAnnotateImages(requests);
    
    // Get only the first response as there is only one request.
    AnnotateImageResponse annotateImageResponse = response.getResponsesList().get(0);

    if (annotateImageResponse.hasError()) {
      throw new PhotoDetectionException(annotateImageResponse.getError().getMessage());
    }

    ImmutableList<String> labels = getLabels(annotateImageResponse);
    ImmutableList<String> logos = getLogos(annotateImageResponse);
    ImmutableList<String> colors = getColors(annotateImageResponse);

    ProductDetectionData productDetectionData = ProductDetectionData.create(labels, logos, colors);

    // All requests have been completed, so call the "close" method on the client 
    // to safely clean up any remaining background resources.
    imageAnnotatorClient.close();

    return productDetectionData;
  }

  private ImmutableList<String> getLabels(AnnotateImageResponse annotateImageResponse) {
    return annotateImageResponse.getLabelAnnotationsList()
        .stream()
        .map(annotation -> annotation.getDescription())
        .collect(ImmutableList.toImmutableList());
  }

  private ImmutableList<String> getLogos(AnnotateImageResponse annotateImageResponse) {
    return annotateImageResponse.getLogoAnnotationsList()
        .stream()
        .map(annotation -> annotation.getDescription())
        .collect(ImmutableList.toImmutableList());
  }

  private ImmutableList<String> getColors(AnnotateImageResponse res) {
    List<String> colorNames = new ArrayList(); 
    
    DominantColorsAnnotation colors = res.getImagePropertiesAnnotation().getDominantColors();
    for (ColorInfo color : colors.getColorsList()) {
      String colorName = ColorUtils.getColorNameFromRGB(
          (int)color.getColor().getRed(),
          (int)color.getColor().getGreen(),
          (int)color.getColor().getBlue());
      
      colorNames.add(colorName);
    }
    
    return ImmutableList.copyOf(colorNames);
  }
}
