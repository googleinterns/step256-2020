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

import com.google.sps.data.Product;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**  
 * Tests {@link ProductListExtractor#extract(Document)}, based on mock shopping results files,
 * checking that products data is successfully extracted.
 */
@RunWith(JUnit4.class)
public final class ProductListExtractorTest {
  private ProductListExtractor productListExtractor;

  @Before
  public void setUp() {
    productListExtractor = new ProductListExtractor();
  }

  @Test
  public void CompleteProductData() throws Exception {
    List<Product> expectedProducts = new ArrayList<>();
    expectedProducts.add(Product.create("TWSBI Eco <b>Fountain Pen</b> - Clear - Extra-Fine", 
        "https://image-link-1.com", 
        "<span class=\"HRLxBb\">$30.99</span> from Goldspot", 
        "https://product-link-1.com", 
        "+$4.95 shipping"));
    expectedProducts.add(Product.create("Faber-Castell Fountain Pen", 
        "https://image-link-2.com", 
        "<span class=\"HRLxBb\">$20.00</span> from Faber-Castell", 
        "https://product-link-2.com", 
        "Free shipping"));
    
    Document mockResultsDoc = 
        Jsoup.parse(new File("./src/main/webapp/mock-shopping-results/complete-data.html"), "UTF-8");

    List<Product> actualProducts = productListExtractor.extract(mockResultsDoc);

    Assert.assertEquals(expectedProducts, actualProducts);
  }

  @Test
  public void noProductRatingProvided() throws Exception {
    // Have a product container missing rating data, which should not alternate the extraction.
    List<Product> expectedProducts = new ArrayList<>();
    expectedProducts.add(Product.create("TWSBI Eco <b>Fountain Pen</b> - Clear - Extra-Fine", 
        "https://image-link-1.com", 
        "<span class=\"HRLxBb\">$30.99</span> from Goldspot", 
        "https://product-link-1.com", 
        "+$4.95 shipping"));
    
    Document mockResultsDoc = 
        Jsoup.parse(
            new File("./src/main/webapp/mock-shopping-results/product-rating-missing.html"), "UTF-8");

    List<Product> actualProducts = productListExtractor.extract(mockResultsDoc);

    Assert.assertEquals(expectedProducts, actualProducts);
  }

  @Test
  public void emptyProductTitle() throws Exception {
    // Add two product containers - one missing the product title, which should be ignored.
    List<Product> expectedProducts = new ArrayList<>();
    expectedProducts.add(Product.create("Faber-Castell Fountain Pen", 
        "https://image-link-2.com", 
        "<span class=\"HRLxBb\">$20.00</span> from Faber-Castell", 
        "https://product-link-2.com", 
        "Free shipping"));
    
    Document mockResultsDoc = 
        Jsoup.parse(
            new File("./src/main/webapp/mock-shopping-results/empty-product-title.html"), "UTF-8");

    List<Product> actualProducts = productListExtractor.extract(mockResultsDoc);

    Assert.assertEquals(expectedProducts, actualProducts);
  }

  @Test
  public void NoProductData() throws Exception {
    // Have an empty doc with no data, therefore no product containers. The extractor should 
    // return an empty array.
    List<Product> expectedProducts = new ArrayList<>();
    
    Document mockResultsDoc = 
        Jsoup.parse(new File("./src/main/webapp/mock-shopping-results/no-data.html"), "UTF-8");

    List<Product> actualProducts = productListExtractor.extract(mockResultsDoc);

    Assert.assertEquals(expectedProducts, actualProducts);
  }
}
