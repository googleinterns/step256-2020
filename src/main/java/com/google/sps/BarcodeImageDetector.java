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
 *
 */
public class BarcodeImageDetector {
  
  /** */
  public String detect(InputStream inputStream) throws PhotoDetectionException {
    
    BufferedImage bufferedImage;
    try {
      bufferedImage = ImageIO.read(inputStream);
    } catch (IOException e) {
      throw new PhotoDetectionException("Failed to convert InputStream to BufferedImage.", e);
    }

    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(
        new BufferedImageLuminanceSource(bufferedImage)));
    

    if (bitmap.getWidth() < bitmap.getHeight()) {
      if (bitmap.isRotateSupported()) {
        bitmap = bitmap.rotateCounterClockwise();
      }
    }
    return decode(bitmap).getText();
  }
  
  /** */
  private BarcodeInfo decode(BinaryBitmap bitmap) throws PhotoDetectionException {
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
      
    return BarcodeInfo.create(result.getText(), result.getBarcodeFormat().toString());
  }
  
  /**
   * Nested class
   */
  @AutoValue
  public static abstract class BarcodeInfo {

    public static BarcodeInfo create(String text, String format) {
      return new AutoValue_BarcodeImageDetector_BarcodeInfo(text, format);
    }

    public abstract String getText();
    public abstract String getFormat();
  }
}
