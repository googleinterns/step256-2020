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

import java.io.IOException;

import java.util.List;
import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/** Get and upload the HttpSession session variable with the image info */
@WebServlet("/get-image-info")
public class UploadedImageInfoServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the session, which contains user-specific data
    HttpSession session = request.getSession();

    // Get the BlobKey that points to the image uploaded by the user.
    BlobKey blobKey = getBlobKey(request, "photo");

    // Send an error if the user did not upload a file.
    if (blobKey == null) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Client must upload an image file.");
    }

    String blobKeyString = blobKey.getKeyString();

    if (request.getParameter("photo-category").isEmpty()) {
      response.sendError(
          HttpServletResponse.SC_BAD_REQUEST, "Client must select a photo category when submitting the form.");
    }
    String photoCategory = request.getParameter("photo-category");

    // Store the item in the session
    session.setAttribute("blobKeyString", blobKeyString);
    session.setAttribute("photoCategory", photoCategory);

    response.sendRedirect("/");
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
}
