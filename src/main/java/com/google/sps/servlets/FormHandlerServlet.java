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
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import com.google.sps.data.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/my-image-servlet")
public class FormHandlerServlet extends HttpServlet {
  private final BlobstoreService blobstoreService;

  public FormHandlerServlet() {
    blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get("barcode");

    // Our form only contains a single file input, so get the first index.
    BlobKey blobKey = blobKeys.get(0);
    String blobKey_String = blobKey.getKeyString();

    // Storing the image Keys in Datastore
    Entity imageEntity = new Entity("barcode");
    imageEntity.setProperty("blobKey", blobKey_String);
    imageEntity.setProperty("timestamp", System.currentTimeMillis());

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(imageEntity);
    response.sendRedirect("/index.html#history");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("barcode").addSort("timestamp", SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<Image> imageDetails = new ArrayList<>();

    for (Entity entity : results.asIterable()) {
      long id = entity.getKey().getId();
      String blobKey = entity.getProperty("blobKey").toString();
      long timestamp = (long) entity.getProperty("timestamp");
      Image image = Image.create(id, blobKey, timestamp);
      imageDetails.add(image);
    }

    Gson gson = new Gson();
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(imageDetails));
  }
}
