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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.jupiter.api.Assertions;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.Feature;

/** Test for DetectText class. */
@RunWith(JUnit4.class)
public final class DetectTextTest {

  private String nullImageKey = "";
  private String invalidImageKey = "abcd";
  private String validImageKey = "AMIfv943BzZrlhyLIN_el9l15Zz2LUp4H3bRNwHj2yvPaDNuv0uZjsp8vSySw0u5pEp-sspSVv4U5fkCT6SY1vbTeObItcslXvPswVieK3rInnCc0nBkTDlfIgWqxlvYjFVPS1QQqCjiY7NlRNSHA3gRXtsv6hrj7_J3_c_DaCP4jhpbZid2bkKvOA1XD0geHOXbCjKHf0ZIbEU4wKqQgGcCxT0X4Ddi4o6Nk8rhN7CNbN27GD-iBfrIG2RwNXr24xLLlFg8Bw9xCUxrbaXO6tkJNHndSghb0x4KAZ-IEPYKHfuWtLfXSmgMV_D1hHZdqShTaUZIyJu-";

  private List<String> nullShoppingList = new ArrayList<>(); 
  private List<String> validShoppingList = new ArrayList<>(); 


  private List<String> invalidShoppingList = new ArrayList<>(); 
  private DetectText detectText;

  @Before
  public void setUp() {
    detectText = new DetectText();

    validShoppingList.add("Text:Bag ");
    validShoppingList.add("Position : vertices { x: 151 y: 94 } vertices { x: 187 y: 94 } vertices { x: 187 y: 115 } vertices { x: 151 y: 115 }");
    validShoppingList.add("Text:Bag");
    validShoppingList.add("Position :vertices { x: 152 y: 94 } vertices { x: 187 y: 96 } vertices { x: 186 y: 114 } vertices { x: 151 y: 112 }");
  
    invalidShoppingList.add("!%^&*+");
  }

      @Test
  public void imageToShoppingListExtractor_forNullImageKey() {
    Assertions.assertThrows(PhotoShoppingException.class, ()-> {detectText.imageToShoppingListExtractor(nullImageKey);});  
  }

//     @Test
//   public void shoppingImageRequestGenerator_ForNullImageKey() {
//     List<AnnotateImageRequest> actual = detectText.shoppingImageRequestGenerator(nullImageKey);
//     //AnnotateImageRequest request = 
//     List<AnnotateImageRequest> expected = new ArrayList<>();
//     //expected.add(request);


// //     image {
// //   source {
// //     image_uri: "https://shop-by-photos-step-2020.ey.r.appspot.com/get-image-url?blob-key="
// //   }
// // }
// // features {
// //   type: TEXT_DETECTION
// // }


//     Assert.assertEquals(expected, actual);
//   }

  @Test   // Negative test with Null list string
  public void createShoppingListQuery_forNullList() {
    Assertions.assertThrows(PhotoShoppingException.class, ()-> {detectText.createShoppingListQuery(nullShoppingList);});
  }

    @Test   // Negative test with invalid list string
  public void createShoppingListQuery_forInvalidList() {
      List<String> actual = new ArrayList<>();
    try {
        actual = detectText.createShoppingListQuery(invalidShoppingList);
    } catch (PhotoShoppingException e) {
        Assert.fail("Threw PhotoShoppingException");
    }
    List<String> expected = new ArrayList<>();
    Assert.assertEquals(expected, actual);
  }

    @Test   // Negative test with valid list string
  public void createShoppingListQuery_forValidList() {
    List<String> actual = new ArrayList<>();
    try {
        actual = detectText.createShoppingListQuery(validShoppingList);
    } catch (PhotoShoppingException e) {
        Assert.fail("Threw PhotoShoppingException");
    }
    List<String> expected = new ArrayList<>();
    expected.add("Bag");

    Assert.assertEquals(expected, actual);
  }

  @Test
  public void shoppingImageInitializer_forNullImageKey() {
    Assertions.assertThrows(PhotoShoppingException.class, ()-> {detectText.shoppingImageInitializer(nullImageKey);});
  }

}
