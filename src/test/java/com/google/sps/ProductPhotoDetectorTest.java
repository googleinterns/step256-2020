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

import com.google.common.collect.ImmutableList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** 
 * Tests the behaviour of {@link ProductPhotoDetector#buildShoppingQuery(byte[])}.
 */
@RunWith(JUnit4.class)
public final class ProductPhotoDetectorTest {

  private static final byte[] NULL_IMAGE_BYTES = new byte[0];
  private static final byte[] IMAGE_BYTES = new byte[1];

  private FakeProductDetectionAPIImpl fakeProductDetection;

  @Rule
  public ExpectedException exceptionRule = ExpectedException.none();

  @Before
  public void setUp() {
    fakeProductDetection = new FakeProductDetectionAPIImpl();
  }

  /** Sets product detection results and initializes ProductPhotoDetector instance. */
  private ProductPhotoDetector initProductPhotoDetector(ImmutableList<String> labels, 
      ImmutableList<String> logos, ImmutableList<String> colors) {
    ProductDetectionData productDetectionData = ProductDetectionData.create(labels, logos, colors);
    fakeProductDetection.setReturnValue(productDetectionData);

    ProductPhotoDetector productPhotoDetector = new ProductPhotoDetector(fakeProductDetection);
    return productPhotoDetector;
  }

  @Test
  public void completeDetectionData() throws Exception {
    ImmutableList<String> labels = ImmutableList.of("Shoe", "Sneaker");
    ImmutableList<String> logos = ImmutableList.of("Nike", "Brand");
    ImmutableList<String> colors = ImmutableList.of("Black", "Grey");

    ProductPhotoDetector productPhotoDetector = initProductPhotoDetector(labels, logos, colors);

    String expectedShoppingQuery = "Black Nike Shoe";
    String actualShoppingQuery = 
        productPhotoDetector.buildShoppingQuery(IMAGE_BYTES);

    Assert.assertEquals(expectedShoppingQuery, actualShoppingQuery);
  }

  @Test
  public void emptyDetectionData() throws Exception {
    ImmutableList<String> labels = ImmutableList.of();
    ImmutableList<String> logos = ImmutableList.of();
    ImmutableList<String> colors = ImmutableList.of();

    ProductPhotoDetector productPhotoDetector = initProductPhotoDetector(labels, logos, colors);

    Assertions.assertThrows(PhotoDetectionException.class, () -> {
        productPhotoDetector.buildShoppingQuery(IMAGE_BYTES);
    });
  }

  @Test
  public void emptyLogosList() throws Exception {
    ImmutableList<String> labels = ImmutableList.of("Shoe", "Sneaker");
    ImmutableList<String> logos = ImmutableList.of();
    ImmutableList<String> colors = ImmutableList.of("Black", "Grey");

    ProductPhotoDetector productPhotoDetector = initProductPhotoDetector(labels, logos, colors);

    String expectedShoppingQuery = "Black Shoe";
    String actualShoppingQuery = 
        productPhotoDetector.buildShoppingQuery(IMAGE_BYTES);

    Assert.assertEquals(expectedShoppingQuery, actualShoppingQuery);
  }

  @Test
  public void emptyLabelsList() throws Exception {
    ImmutableList<String> labels = ImmutableList.of();
    ImmutableList<String> logos = ImmutableList.of("Nike", "Brand");
    ImmutableList<String> colors = ImmutableList.of("Black", "Grey");

    ProductPhotoDetector productPhotoDetector = initProductPhotoDetector(labels, logos, colors);

    exceptionRule.expect(PhotoDetectionException.class);
    exceptionRule.expectMessage("Missing labels for image detection.");
    productPhotoDetector.buildShoppingQuery(IMAGE_BYTES);
  }

  @Test
  public void emptyColorsList() throws Exception {
    ImmutableList<String> labels = ImmutableList.of("Shoe", "Sneaker");
    ImmutableList<String> logos = ImmutableList.of("Nike", "Brand");
    ImmutableList<String> colors = ImmutableList.of();

    ProductPhotoDetector productPhotoDetector = initProductPhotoDetector(labels, logos, colors);

    String expectedShoppingQuery = "Nike Shoe";
    String actualShoppingQuery = 
        productPhotoDetector.buildShoppingQuery(IMAGE_BYTES);

    Assert.assertEquals(expectedShoppingQuery, actualShoppingQuery);
  }

  @Test
  public void photoDetectionExceptionThrown() throws Exception {
    fakeProductDetection.setException(new PhotoDetectionException("Fake error."));
    ProductPhotoDetector productPhotoDetector = new ProductPhotoDetector(fakeProductDetection);

    // The exception should be re-thrown.
    Assertions.assertThrows(PhotoDetectionException.class, () -> {
        productPhotoDetector.buildShoppingQuery(IMAGE_BYTES);
    });
  }

  @Test
  public void noImageBytesProvided() throws Exception {
    ProductPhotoDetector productPhotoDetector = new ProductPhotoDetector(fakeProductDetection);
  
    exceptionRule.expect(PhotoDetectionException.class);
    exceptionRule.expectMessage("Empty byte array.");
    productPhotoDetector.buildShoppingQuery(NULL_IMAGE_BYTES);
  }
}
