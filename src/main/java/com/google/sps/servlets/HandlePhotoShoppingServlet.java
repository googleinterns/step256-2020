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

package com.google.sps.servlets;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.gson.Gson;
import com.google.sps.BarcodeImageDetector;
import com.google.sps.GoogleShoppingQuerier;
import com.google.sps.ImageTextDectector;
import com.google.sps.PhotoDetectionException;
import com.google.sps.ProductDetectionAPI;
import com.google.sps.ProductDetectionAPIImpl;
import com.google.sps.ProductPhotoDetector;
import com.google.sps.ShoppingQuerierConnectionException;
import com.google.sps.TextDetectionAPIImpl;
import com.google.sps.data.Product;
import com.google.sps.data.ShoppingQueryInput;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * When the user submits the form for image uploading, Blobstore processes the file upload and
 * forwards the request to this servlet, which returns the product shopping results for the
 * respective photo, in JSON format, along with the shopping query used to search.
 */
@WebServlet("/handle-photo-shopping")
public class HandlePhotoShoppingServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the BlobKey that points to the image uploaded by the user.
    BlobKey uploadedImageBlobKey = getBlobKey(request, "photo");

    // Send an error if the user did not upload a file.
    if (uploadedImageBlobKey == null) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing input image file.");
      return;
    }

    // Get the photo category (i.e. product, shopping-list or barcode) entered by the user.
    String photoCategory = request.getParameter("photo-category");
    if (photoCategory.isEmpty()) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing photo category.");
      return;
    }

    // Get the image the user uploaded as bytes.
    byte[] uploadedImageBytes = getBlobBytes(uploadedImageBlobKey);

    // Call GoogleShoppingQuerier to return the extracted products data.
    // First, build the shopping query input.
    List<String> shoppingQueries = new ArrayList<>();
    try {
      shoppingQueries = getQuery(request.getParameter("photo-category"), uploadedImageBytes);
    } catch (IllegalArgumentException | PhotoDetectionException exception) {
      response.sendError(SC_INTERNAL_SERVER_ERROR, exception.getMessage());
      return;
    }

    int maxResultsNumber = 24;
    // For shopping-list, where the number of queries exceeds 1, 
    // limit the number of products displayed for each item.
    if (shoppingQueries.size() > 1) {
      maxResultsNumber = 15;
    }
    List<ShoppingQueryInput> shoppingQueryInputs = new ArrayList<>();
    for (String shoppingQuery : shoppingQueries) {
      shoppingQueryInputs.add(
          new ShoppingQueryInput.Builder(shoppingQuery)
              .language("en")
              .maxResultsNumber(maxResultsNumber)
              .build());
    }

    // Initialize the Google Shopping querier.
    GoogleShoppingQuerier querier = new GoogleShoppingQuerier();

    List<List<Product>> shoppingQuerierResults = new ArrayList<>();
    for (ShoppingQueryInput shoppingQueryInput : shoppingQueryInputs) {
      try {
        shoppingQuerierResults.add(querier.query(shoppingQueryInput));
      } catch (IllegalArgumentException
          | ShoppingQuerierConnectionException
          | IOException exception) {
        response.sendError(SC_INTERNAL_SERVER_ERROR, exception.getMessage());
        return;
      }
    }

    // Convert {@code shoppingQuery} and products List - {@code shoppingQuerierResults} - into JSON
    // strings
    // using Gson library and send a JSON array with both of the JSON strings as response.
    Gson gson = new Gson();

    String shoppingQueryJSON = gson.toJson(shoppingQueries);
    String shoppingQuerierResultsJSON = gson.toJson(shoppingQuerierResults);
    response.setContentType("application/json;");
    response.getWriter().write("[" + shoppingQueryJSON + "," + shoppingQuerierResultsJSON + "]");
  }

  /**
   * Returns the shopping query by calling methods from the photo content detection classes, based
   * on the {@code photoCategory}, passing {@code uploadedImageBytes} as argument.
   */
  private List<String> getQuery(String photoCategory, byte[] uploadedImageBytes)
      throws IllegalArgumentException, PhotoDetectionException {
    List<String> queryResults = new ArrayList<>();
    switch (photoCategory) {
      case "product":
        ProductDetectionAPI productDetectionAPI = new ProductDetectionAPIImpl();
        ProductPhotoDetector productPhotoDetector = new ProductPhotoDetector(productDetectionAPI);

        String productShoppingQuery;
        try {
          queryResults.add(productPhotoDetector.buildShoppingQuery(uploadedImageBytes));
        } catch (PhotoDetectionException exception) {
          throw exception;
        }
        return queryResults;
      case "shopping-list":
        TextDetectionAPIImpl textDetectionAPI = new TextDetectionAPIImpl();
        ImageTextDectector imageTextDectector = new ImageTextDectector(textDetectionAPI);

        try {
          queryResults = imageTextDectector.extractShoppingList(uploadedImageBytes);
        } catch (PhotoDetectionException exception) {
          throw exception;
        } catch (IOException e) {
          throw new PhotoDetectionException("Error while getting shopping query.", e);
        }
        return queryResults;
      case "barcode":
        BarcodeImageDetector barcodeImageDetector = new BarcodeImageDetector();
        queryResults.add(barcodeImageDetector.detect(uploadedImageBytes));
        return queryResults;
      default:
        throw new IllegalArgumentException(
            "Photo category has to be either product, shopping-list or barcode.");
    }
  }

  /**
   * Returns the BlobKey corresponding to the file uploaded by the user, or null if the user did not
   * upload a file.
   */
  private BlobKey getBlobKey(HttpServletRequest request, String formFileInputElementName) {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get(formFileInputElementName);

    // User submitted form without selecting a file. (dev server)
    if (blobKeys.equals(Optional.empty()) || blobKeys.isEmpty()) {
      return null;
    }

    // The form only contains a single file input, so get the first index.
    BlobKey blobKey = blobKeys.get(0);

    // User submitted form without selecting a file, so the BlobKey is empty. (live server)
    BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
    if (blobInfo.getSize() == 0) {
      blobstoreService.delete(blobKey);
      return null;
    }

    return blobKey;
  }

  /**
   * Blobstore stores files as binary data. This function retrieves the image represented by the
   * binary data stored at the BlobKey parameter.
   */
  private byte[] getBlobBytes(BlobKey blobKey) throws IOException {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();

    int fetchSize = BlobstoreService.MAX_BLOB_FETCH_SIZE;
    long currentByteIndex = 0;
    boolean continueReading = true;
    while (continueReading) {
      // End index is inclusive, therefore subtract 1 to get fetchSize bytes.
      byte[] b =
          blobstoreService.fetchData(blobKey, currentByteIndex, currentByteIndex + fetchSize - 1);
      outputBytes.write(b);

      // If fewer bytes than requested have been read, the end is reached.
      if (b.length < fetchSize) {
        continueReading = false;
      }

      currentByteIndex += fetchSize;
    }

    return outputBytes.toByteArray();
  }
}
