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

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import java.io.IOException;
import java.lang.NullPointerException;
import java.util.List;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Get and save uploaded image info and return it when requested from front-end. */
@WebServlet("/get-image-info")
public class UploadedImageInfoServlet extends HttpServlet {
  private final BlobstoreService blobstoreService;
  private String blobKeyString;
  private String photoCategory;

  private UploadedImageInfoServlet() {
    blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    try {
      Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
      List<BlobKey> blobKeys = blobs.get("photo");
      // The form only contains a single file input, so get the first index.
      BlobKey blobKey = blobKeys.get(0);
      blobKeyString = blobKey.getKeyString();
    } catch (NullPointerException exception) {
      response.sendError(
          HttpServletResponse.SC_UNAUTHORIZED,
          "Getting BlobKey Failed: No uploaded image found. \n" + exception.getMessage());
    }

    if (request.getParameter("image-options").isEmpty()) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No 'Photo Category' Found.");
    }
    photoCategory = request.getParameter("image-options");

    response.sendRedirect("/");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");
    response.getWriter().println(photoCategory);
    response.getWriter().println(blobKeyString);
  }
}
