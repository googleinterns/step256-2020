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

import com.google.cloud.vision.v1.Image;
import com.google.protobuf.ByteString;

public class PhotoShoppingUtil {
  public static String formatQuery(String query) {
    query =
        query
            .replaceAll("\\s+", " ") // Remove duplicate spaces
            .trim() // Remove spaces from the beginning and end of string
            .replaceAll("[-+=,\n._^\";:~#></|!*]", ""); // Remove special characters
    return query;
  }

  public static Image getImageFromBytes(byte[] shoppingImageBytes) throws PhotoDetectionException{
    if (shoppingImageBytes.length == 0) {
      throw new PhotoDetectionException("Bytes array is empty.");
    }
    ByteString imageByteString = ByteString.copyFrom(shoppingImageBytes);
    Image shoppingImage = Image.newBuilder().setContent(imageByteString).build();
    return shoppingImage;
  }
}
