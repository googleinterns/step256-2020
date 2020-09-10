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

import com.google.sps.data.ProductDetectionData;

/**
 * Builds query to search on Google Shopping, using the data from the image detection. 
 */
public class ProductPhotoDetector {
  ProductDetectionAPI productDetectionAPI;

  public ProductPhotoDetector(ProductDetectionAPI productDetectionAPI) {
    this.productDetectionAPI = productDetectionAPI;
  }

  public String buildShoppingQuery(byte[] imageBytes) 
      throws PhotoDetectionException {
    ProductDetectionData results = productDetectionAPI.detectProductPhotoContent(imageBytes);

    // If labels list is empty, there is no specific product query to search on Google Shopping.
    if (results.getLabels().isEmpty()) {
      throw new PhotoDetectionException("Missing labels for image detection.");
    }

    // TODO: Update label to search with, by handling the case when the chosen label does not define a product
    // - e.g. "furniture" instead of "sofa".

    String firstLabelDetected = results.getLabels().get(0);
    String firstLogoDetected = !results.getLogos().isEmpty() ? results.getLogos().get(0) + " " : "";
    String dominantColorDetected = !results.getColors().isEmpty() ? results.getColors().get(0) + " " : "";

    return dominantColorDetected + firstLogoDetected + firstLabelDetected;
  }
}
