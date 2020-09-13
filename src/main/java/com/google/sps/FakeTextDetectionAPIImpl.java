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
import com.google.sps.data.ShoppingListTextEntry;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FakeTextDetectionAPIImpl implements TextDetectionAPI {
    
  public List<ShoppingListTextEntry> detect(byte[] imageBytes) throws PhotoDetectionException {
      List<ShoppingListTextEntry> results = new ArrayList<>();
      if(imageBytes.length == 0) {
          throw new PhotoDetectionException("Invalid imageBytes");
      }
      else if(imageBytes.length < 10) {
          results.add(ShoppingListTextEntry.create("Grey boots for men", 100));
      }
      else if(imageBytes.length > 10) {
          results.add(ShoppingListTextEntry.create("Blue shirt for women", 98));
      }
      return results;
  }
}
