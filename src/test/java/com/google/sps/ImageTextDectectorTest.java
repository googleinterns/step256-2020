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
import com.google.cloud.vision.v1.ImageSource;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Test for ImageTextDectector class. */
@RunWith(JUnit4.class)
public final class ImageTextDectectorTest {

  private ImageTextDectector detectText;

  @Before
  public void setUp() {
    detectText = new ImageTextDectector();
  }

  /**
   * ***************************************
   * **** TESTS FOR shoppingImageInitializer
   * ***************************************
   */
  @Test
  public void shoppingImageInitializer_forNullBytesArray() {
    byte[] nullBytesArray = new byte[0];
    Assertions.assertThrows(
        PhotoShoppingException.class,
        () -> {
          detectText.shoppingImageInitializer(nullBytesArray);
        });
  }

//   @Test
//   public void shoppingImageInitializer_forInvalidImageKey() {
//     String invalidImageKey = "abc £$%  d\n\n\n";
//     Assertions.assertThrows(
//         PhotoShoppingException.class,
//         () -> {
//           detectText.shoppingImageInitializer(invalidImageKey);
//         });
//   }

//   /**
//    * ********************************************
//    * **** TESTS FOR shoppingImageRequestGenerator
//    * ********************************************
//    */
//   // Testing the shoppingImageRequestGenerator routine with valid Image source
//   @Test
//   public void shoppingImageRequestGenerator_forValidImageKey() throws Exception {
//     // This key is copied from Cloud Datastore. Make sure it exists there before running the tests.
//     String validImageKey =
//         "AMIfv943BzZrlhyLIN_el9l15Zz2LUp4H3bRNwHj2yvPaDNuv0uZjsp8vSySw0u5pEp-sspSVv4U5fkCT6SY1vbTeObItcslXvPswVieK3rInnCc0nBkTDlfIgWqxlvYjFVPS1QQqCjiY7NlRNSHA3gRXtsv6hrj7_J3_c_DaCP4jhpbZid2bkKvOA1XD0geHOXbCjKHf0ZIbEU4wKqQgGcCxT0X4Ddi4o6Nk8rhN7CNbN27GD-iBfrIG2RwNXr24xLLlFg8Bw9xCUxrbaXO6tkJNHndSghb0x4KAZ-IEPYKHfuWtLfXSmgMV_D1hHZdqShTaUZIyJu-";
//     String expectedUri = Constants.IMAGE_BASE_URI + validImageKey;
//     ImageSource shoppingImageSource = detectText.shoppingImageInitializer(validImageKey);
//     List<AnnotateImageRequest> actual_requests =
//         detectText.shoppingImageRequestGenerator(shoppingImageSource);
//     Assert.assertEquals(expectedUri, actual_requests.get(0).getImage().getSource().getImageUri());
//   }
}
