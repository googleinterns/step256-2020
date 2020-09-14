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

import com.google.auto.value.AutoValue;

import com.google.common.io.ByteSource;

// Import the ZXing barcode image processing library.
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;

import java.io.IOException;
import java.io.InputStream;

/**
 * For a barcode image, detects the code, using the Java Apache Camel 
 * Barcode API based on the ZXing library, and returns it.
 */
public class BarcodeImageDetector {
  
  public String detect(byte[] imageBytes) throws PhotoDetectionException {
    // Convert byte array to InputStream by wrapping the byte array into the Guava ByteSource,
    // which then allows getting the stream.
    InputStream imageInputStream;
    try {
      imageInputStream = ByteSource.wrap(imageBytes).openStream();
    } catch (IOException exception) {
      throw new PhotoDetectionException("Failed to convert byte array to InputStream.", exception);
    }
    
    // Get the BufferedImage as the result of decoding the InputStream.
    BufferedImage bufferedImage;
    try {
      bufferedImage = ImageIO.read(imageInputStream);
    } catch (IOException e) {
      throw new PhotoDetectionException("Failed to convert InputStream to BufferedImage.", e);
    }

    // ZXing Reader objects accept a BinaryBitmap and attempt to decode it, therefore
    // construct the BinaryBitmap object based on the {@code bufferedImage}.
    // The ZXing HybridBinarizer class is designed for high frequency images of barcodes 
    // with black data on white backgrounds.
    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(
        new BufferedImageLuminanceSource(bufferedImage)));
    
    // If needed, rotate the image.
    if (bitmap.getWidth() < bitmap.getHeight()) {
      if (bitmap.isRotateSupported()) {
        bitmap = bitmap.rotateCounterClockwise();
      }
    }

    return decode(bitmap);
  }
  
  private String decode(BinaryBitmap bitmap) throws PhotoDetectionException {
    Reader reader = new MultiFormatReader();
    Result result;
    try {
      result = reader.decode(bitmap);
    } catch (NotFoundException e) {
      // No potential barcode is found.
      throw new PhotoDetectionException("No potential barcode found.", e);
    } catch (ChecksumException e) {
      // A potential barcode is found but does not pass its checksum.
      throw new PhotoDetectionException("Potential barcode does not pass its checksum.", e);
    } catch (FormatException e) {
      // A potential barcode is found but format is invalid.
      throw new PhotoDetectionException("Invalid format for potential barcode.", e);
    }
      
    return result.getText();
  }
}
