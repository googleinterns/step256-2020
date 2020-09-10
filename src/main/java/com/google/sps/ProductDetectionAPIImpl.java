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
import com.google.cloud.vision.v1.ImageSource;

import com.google.protobuf.ByteString;

import com.google.sps.data.ProductDetectionData;

import java.awt.Color;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

/**
 *
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

    Feature labelDetectionFeature = 
        Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
    requestBuilder.addFeatures(labelDetectionFeature);

    Feature logoDetectionFeature =
        Feature.newBuilder().setType(Feature.Type.LOGO_DETECTION).build();
    requestBuilder.addFeatures(logoDetectionFeature);

    Feature imagePropertiesDetectionFeature =
        Feature.newBuilder().setType(Feature.Type.IMAGE_PROPERTIES).setMaxResults(5).build();
    requestBuilder.addFeatures(imagePropertiesDetectionFeature);

    requestBuilder.setImage(image);

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
    
    List<AnnotateImageResponse> responses = response.getResponsesList();

    List<String> labels = new ArrayList();
    List<String> logos = new ArrayList();
    List<String> colors = new ArrayList();

    for (AnnotateImageResponse res : responses) {
      if (res.hasError()) {
        throw new PhotoDetectionException(res.getError().getMessage());
      }

      labels.addAll(getLabels(res));
      logos.addAll(getLogos(res));
      colors.addAll(getColors(res));
    }

    ProductDetectionData productDetectionData = new ProductDetectionData(labels, logos, colors);

    // All requests have been completed, so call the "close" method on the client 
    // to safely clean up any remaining background resources.
    imageAnnotatorClient.close();

    return productDetectionData;
  }

  private List<String> getLabels(AnnotateImageResponse res) {
    List<String> labels = new ArrayList(); 
        
    for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
      System.out.println(annotation.getDescription());
      labels.add(annotation.getDescription());
    }
    return labels;
  }

  private List<String> getLogos(AnnotateImageResponse res) {
    List<String> logos = new ArrayList(); 

    for (EntityAnnotation annotation : res.getLogoAnnotationsList()) {
      System.out.println(annotation.getDescription());
      logos.add(annotation.getDescription());
    }

    return logos;
  }

  private List<String> getColors(AnnotateImageResponse res) {
    List<String> colorNames = new ArrayList(); 
    
    DominantColorsAnnotation colors = res.getImagePropertiesAnnotation().getDominantColors();
    for (ColorInfo color : colors.getColorsList()) {
      System.out.format(
          "fraction: %f%nr: %f, g: %f, b: %f%n",
          color.getPixelFraction(),
          color.getColor().getRed(),
          color.getColor().getGreen(),
          color.getColor().getBlue());
      
      ColorUtils colorUtils = new ColorUtils();
      String mainColorName = colorUtils.getColorNameFromRgb((int)color.getColor().getRed(),
          (int)color.getColor().getGreen(),
          (int)color.getColor().getBlue());
      System.out.println(mainColorName);
      colorNames.add(mainColorName);
    }
    return colorNames;
  }
}
