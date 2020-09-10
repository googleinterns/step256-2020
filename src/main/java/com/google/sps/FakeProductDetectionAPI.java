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
 * Mocks ProductDetectionAPIImpl by setting return values and exceptions.
 */
public class FakeProductDetectionAPI implements ProductDetectionAPI {
  private ProductDetectionData productDetectionData;
  private PhotoDetectionException photoDetectionException;

  /** Sets image detection data to be returned. */
  public void setReturnValue(ProductDetectionData productDetectionData) {
    this.productDetectionData = productDetectionData;
  }

  /** Sets exception to be thrown. */
  public void setException(PhotoDetectionException e) {
    this.photoDetectionException = e;
  }

  public ProductDetectionData detectProductPhotoContent(byte[] imageBytes) 
      throws PhotoDetectionException {
    if (photoDetectionException != null) {
      throw this.photoDetectionException;
    }
    return this.productDetectionData;
  }
}
