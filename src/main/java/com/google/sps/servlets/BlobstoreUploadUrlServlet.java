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

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
<<<<<<< HEAD
 * Servlet that generates a Blobstore image upload URL, which links to the "/get-image-info"
 * servlet.
=======
 * Generates the destination (action) of the form for image uploading, by calling the Blobstore API.
 *
 * When the form is submitted, the user's browser uploads the file directly to the Blobstore 
 * via the generated URL. The Blobstore then passes the request to the "/handle-photo-shopping" path. 
 * This handler does additional processing based on the blob key.
>>>>>>> a5e1439194113079c8ebc7f1c1bac1e609d382b2
 */
@WebServlet("/blobstore-upload-url")
public class BlobstoreUploadUrlServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    
    // Create the URL that allows a user to upload a file to Blobstore.
    String formActionUrl = blobstoreService.createUploadUrl("/handle-photo-shopping");

    // Send the URL as the response.
    response.setContentType("text/html");
    response.getWriter().println(formActionUrl);
  }
}
