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

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

import com.google.sps.GoogleShoppingQuerier;
import com.google.sps.ProductPhotoShoppingException;
import com.google.sps.ProductPhotoShoppingImpl;
import com.google.sps.ShoppingQuerierConnectionException;
import com.google.sps.data.Product;
import com.google.sps.data.ShoppingQueryInput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

import org.jsoup.nodes.Document;

/**
 * Get shopping results based on the image uploaded by the user.
 */
@WebServlet("/handle-photo")
public class PhotoHandlerServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the BlobKey that points to the image uploaded by the user.
    BlobKey blobKey = getBlobKey(request, "photo");

    // Send an error if the user did not upload a file.
    if (blobKey == null) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Client must upload an image file.");
    }

    if (request.getParameter("photo-category").isEmpty()) {
      response.sendError(
          HttpServletResponse.SC_BAD_REQUEST, "Client must select a photo category when submitting the form.");
    }
    String photoCategory = request.getParameter("photo-category");

    // Get the image the user uploaded as bytes.
    byte[] imageBytes = getBlobBytes(blobKey);

    response.setContentType("text/html");
    Document shoppingQuerierResults;

    if (photoCategory.equals("product")) {
      // Initialize the ProductPhotoShoppingImpl object.
      ProductPhotoShoppingImpl productPhotoShoppingImpl = new ProductPhotoShoppingImpl();

      try {
        shoppingQuerierResults = productPhotoShoppingImpl.shopWithPhoto(imageBytes);
        response.getWriter().println(shoppingQuerierResults);
      } catch(IllegalArgumentException | 
              ShoppingQuerierConnectionException | 
              ProductPhotoShoppingException | 
              IOException exception) {
        response.sendError(SC_INTERNAL_SERVER_ERROR, exception.getMessage());
      }
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
    if (blobKeys == null || blobKeys.isEmpty()) {
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
      // End index is inclusive, subtract 1 to get fetchSize bytes.
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
