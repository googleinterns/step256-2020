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

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** 
 * Tests the behaviour of ProductPhotoDetector, namely the shopping query returned and the 
 * exceptions thrown.
 */
@RunWith(JUnit4.class)
public final class ProductPhotoDetectorTest {

  private static final byte[] IMAGE_BYTES = "Fake image".getBytes();

  private FakeProductDetectionAPIImpl fakeProductDetection;

  @Before
  public void setUp() {
    fakeProductDetection = new FakeProductDetectionAPIImpl();
  }

  @Test
  public void completeDetectionData() throws Exception {
    List<String> labels = new ArrayList();
    labels.add("Shoe");
    labels.add("Sneaker");
    List<String> logos = new ArrayList();
    logos.add("Nike");
    List<String> colors = new ArrayList();
    colors.add("Black");

    ProductDetectionData productDetectionData = new ProductDetectionData(labels, logos, colors);
    fakeProductDetection.setReturnValue(productDetectionData);
    ProductPhotoDetector productPhotoDetector = new ProductPhotoDetector(fakeProductDetection);

    String expectedShoppingQuery = "Black Nike Shoe";
    String actualShoppingQuery = 
        productPhotoDetector.buildShoppingQuery(IMAGE_BYTES);

    Assert.assertEquals(expectedShoppingQuery, actualShoppingQuery);
  }

  @Test
  public void emptyDetectionData() throws Exception {
    ProductDetectionData productDetectionData = 
        new ProductDetectionData(new ArrayList(), new ArrayList(), new ArrayList());
    fakeProductDetection.setReturnValue(productDetectionData);
    ProductPhotoDetector productPhotoDetector = new ProductPhotoDetector(fakeProductDetection);

    Assertions.assertThrows(PhotoDetectionException.class, () -> {
        productPhotoDetector.buildShoppingQuery(IMAGE_BYTES);
    });
  }

  @Test
  public void emptyLogosList() throws Exception {
    List<String> labels = new ArrayList();
    labels.add("Shoe");
    labels.add("Sneaker");
    List<String> colors = new ArrayList();
    colors.add("Black");

    ProductDetectionData productDetectionData = new ProductDetectionData(labels, new ArrayList(), colors);
    fakeProductDetection.setReturnValue(productDetectionData);
    ProductPhotoDetector productPhotoDetector = new ProductPhotoDetector(fakeProductDetection);

    String expectedShoppingQuery = "Black Shoe";
    String actualShoppingQuery = 
        productPhotoDetector.buildShoppingQuery(IMAGE_BYTES);

    Assert.assertEquals(expectedShoppingQuery, actualShoppingQuery);
  }

  @Test
  public void emptyLabelsList() throws Exception {
    List<String> logos = new ArrayList();
    logos.add("Nike");
    List<String> colors = new ArrayList();
    colors.add("Black");

    ProductDetectionData productDetectionData = new ProductDetectionData(new ArrayList(), logos, colors);
    fakeProductDetection.setReturnValue(productDetectionData);
    ProductPhotoDetector productPhotoDetector = new ProductPhotoDetector(fakeProductDetection);

    Assertions.assertThrows(PhotoDetectionException.class, () -> {
        productPhotoDetector.buildShoppingQuery(IMAGE_BYTES);
    });
  }

  @Test
  public void emptyColorsList() throws Exception {
    List<String> labels = new ArrayList();
    labels.add("Shoe");
    labels.add("Sneaker");
    List<String> logos = new ArrayList();
    logos.add("Nike");

    ProductDetectionData productDetectionData = new ProductDetectionData(labels, logos, new ArrayList());
    fakeProductDetection.setReturnValue(productDetectionData);
    ProductPhotoDetector productPhotoDetector = new ProductPhotoDetector(fakeProductDetection);

    String expectedShoppingQuery = "Nike Shoe";
    String actualShoppingQuery = 
        productPhotoDetector.buildShoppingQuery(IMAGE_BYTES);

    Assert.assertEquals(expectedShoppingQuery, actualShoppingQuery);
  }

  @Test
  public void photoDetectionExceptionThrown() throws Exception {
    fakeProductDetection.setException(new PhotoDetectionException("error"));
    ProductPhotoDetector productPhotoDetector = new ProductPhotoDetector(fakeProductDetection);

    // The exception should be re-thrown.
    Assertions.assertThrows(PhotoDetectionException.class, () -> {
        productPhotoDetector.buildShoppingQuery(IMAGE_BYTES);
    });
  }

  // TODO: Add tests when first label detected does not define a product - e.g. "furniture" instead
  // of "sofa" (after adding the solution for this problem in ProductPhotoDetector's buildShoppingQuery).
}
