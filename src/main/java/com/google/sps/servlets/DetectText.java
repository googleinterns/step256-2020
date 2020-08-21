/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.servlet;

import java.net.URL;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageSource;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

@WebServlet("/detect-text")
public class DetectText extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
System.out.println("start");
    List<AnnotateImageRequest> reqs = new ArrayList<>();

    List<String> text = new ArrayList<>();

    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get("barcode");

    // The form only contains a single file input, so get the first index.
    BlobKey blobKey = blobKeys.get(0);
    String blobKeyString = blobKey.getKeyString();

    ImageSource imgSource = ImageSource.newBuilder().setImageUri("https://shop-by-photos-step-2020.ey.r.appspot.com/get-image-url?blob-key=" + blobKeyString).build();
    Image img = Image.newBuilder().setSource(imgSource).build();
System.out.println("after img");
    // // ByteString imgBytes = ByteString.readFrom("/get-image-url?blob-key=" + blobKeyString);
    // // Image img = Image.newBuilder().setContent(imgBytes).build();

    // // URL url = new URL("/get-image-url?blob-key=" + blobKeyString);
    // // Image img = ImageIO.read(url);

    Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
    AnnotateImageRequest req =
        AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
    reqs.add(req);
System.out.println("reqs:"+reqs);
System.out.println("after reqs");
    // Initialize client that will be used to send requests. This client only needs to be created
    // once, and can be reused for multiple requests. After completing all of your requests, call
    // the "close" method on the client to safely clean up any remaining background resources.
    try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
      BatchAnnotateImagesResponse resp = client.batchAnnotateImages(reqs);
System.out.println("resp:"+resp);
      List<AnnotateImageResponse> resps = resp.getResponsesList();
System.out.println("resps:"+resps);
      for (AnnotateImageResponse res : resps) {
        // if (res.hasError()) {
        //   text.add(System.out.format("Error: %s%n", res.getError().getMessage()));
        //   return;
        // }

        // For full list of available annotations, see http://g.co/cloud/vision/docs
        for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
System.out.println("in for");
          text.add("Text: " + annotation.getDescription());
          text.add("<br>");
System.out.println("text:"+text);
          text.add("Position : " + annotation.getBoundingPoly());
System.out.println("text:"+text);
          text.add("<br>");
        }
      }
    }
    response.setContentType("text/html");
    response.getWriter().println(text);
System.out.println("end");
  } 
}
