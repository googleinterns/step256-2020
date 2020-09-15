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
import com.google.common.collect.ImmutableList;

import java.awt.Color;

/**
 * Utility class for converting RGB color values to color name.
 */
public class ColorUtils {
  // Prevent creating an instance of this class.
  private ColorUtils() {} 
  
  // Compile a list of known colors.
  private static final ImmutableList<ColorName> COLOR_LIST =  
      ImmutableList.of(
          ColorName.create("Beige", 0xF5, 0xF5, 0xDC),
          ColorName.create("Black", 0x00, 0x00, 0x00),
          ColorName.create("Blue", 0x00, 0x00, 0xFF),
          ColorName.create("Coral", 0xFF, 0x7F, 0x50),
          ColorName.create("Cyan", 0x00, 0xFF, 0xFF),
          ColorName.create("Gray", 0xA9, 0xA9, 0xA9),
          ColorName.create("Dark Green", 0x00, 0x64, 0x00),
          ColorName.create("Khaki", 0xBD, 0xB7, 0x6B),      // Dark Khaki
          ColorName.create("Orange", 0xFF, 0x8C, 0x00),     // Dark Orange
          ColorName.create("Dark Red", 0x8B, 0x00, 0x00),
          ColorName.create("Turquoise", 0x00, 0xCE, 0xD1),  // Dark Turquoise
          ColorName.create("Violet", 0x94, 0x00, 0xD3),     // Dark Violet
          ColorName.create("Pink", 0xFF, 0x14, 0x93),       // DeepPink
          ColorName.create("Blue", 0x00, 0xBF, 0xFF),       // DeepSkyBlue
          ColorName.create("Green", 0x22, 0x8B, 0x22),      // ForestGreen
          ColorName.create("Fuchsia", 0xFF, 0x00, 0xFF),
          ColorName.create("Yellow", 0xFF, 0xD7, 0x00),     // Gold
          ColorName.create("Gray", 0x80, 0x80, 0x80),
          ColorName.create("Green", 0x00, 0x80, 0x00),
          ColorName.create("Pink", 0xFF, 0x69, 0xB4),       // HotPink
          ColorName.create("Indigo", 0x4B, 0x00, 0x82),
          ColorName.create("Khaki", 0xF0, 0xE6, 0x8C),
          ColorName.create("Lavender", 0xE6, 0xE6, 0xFA),
          ColorName.create("Light Blue", 0xAD, 0xD8, 0xE6),
          ColorName.create("Light Cyan", 0xE0, 0xFF, 0xFF),
          ColorName.create("Light Gray", 0xD3, 0xD3, 0xD3),
          ColorName.create("Light Green", 0x90, 0xEE, 0x90),
          ColorName.create("Light Pink", 0xFF, 0xB6, 0xC1),
          ColorName.create("LightYellow", 0xFF, 0xFF, 0xE0),
          ColorName.create("Lime", 0x00, 0xFF, 0x00),
          ColorName.create("Magenta", 0xFF, 0x00, 0xFF),
          ColorName.create("Maroon", 0x80, 0x00, 0x00),
          ColorName.create("Blue", 0x00, 0x00, 0xCD),       // MediumBlue
          ColorName.create("Purple", 0x93, 0x70, 0xDB),     // MediumPurple
          ColorName.create("Green", 0x00, 0xFA, 0x9A),      // MediumSpringGreen
          ColorName.create("Turquoise", 0x48, 0xD1, 0xCC),  // MediumTurquoise
          ColorName.create("Navy", 0x00, 0x00, 0x80),
          ColorName.create("Orange", 0xFF, 0xA5, 0x00),
          ColorName.create("Orange", 0xFF, 0x45, 0x00),     // OrangeRed
          ColorName.create("Pink", 0xFF, 0xC0, 0xCB),
          ColorName.create("Purple", 0x80, 0x00, 0x80),
          ColorName.create("Red", 0xFF, 0x00, 0x00),
          ColorName.create("Blue", 0x41, 0x69, 0xE1),       // RoyalBlue
          ColorName.create("Brown", 0x8B, 0x45, 0x13),      // SaddleBrown
          ColorName.create("Silver", 0xC0, 0xC0, 0xC0),
          ColorName.create("Blue", 0x87, 0xCE, 0xEB),       // SkyBlue
          ColorName.create("Turquoise", 0x40, 0xE0, 0xD0),
          ColorName.create("Violet", 0xEE, 0x82, 0xEE),
          ColorName.create("White", 0xFF, 0xFF, 0xFF),
          ColorName.create("Yellow", 0xFF, 0xFF, 0x00));

  /**
   * Returns the color name of the closest color (to the query color provided as RGB).
   * To find the closest color, gets the color list, and then computes the distance between 
   * the query color and every color in the color list, based on their RGB values.
   */
  public static String getColorNameFromRGB(int r, int g, int b) throws IllegalArgumentException {
    if (!(0 < r && r < 255) || !(0 < g && g < 255) || !(0 < b && b < 255)) {
      throw new IllegalArgumentException("RGB values outside [0, 255] range.");
    }
    
    // Get the color name of the color with minimum RGB distance to the query color.
    ColorName closestColorMatch = null;

    double minRGBDistance = Double.MAX_VALUE;
    for (ColorName color : COLOR_LIST) {
      double currentRGBDistance = color.computeRGBDistance(r, g, b);

      if (currentRGBDistance < minRGBDistance) {
        minRGBDistance = currentRGBDistance;
        closestColorMatch = color;
      }
    }
    
    return closestColorMatch.getName();
  }
  
  /**
   * Inner class defining a known color with a color name and an RGB value, and handling the 
   * computation of the distance to a query color.
   */
  @AutoValue
  public static abstract class ColorName {

    public static ColorName create(String name, int r, int g, int b) {
      return new AutoValue_ColorUtils_ColorName(name, r, g, b);
    }

    public abstract String getName();
    public abstract int getR();
    public abstract int getG();
    public abstract int getB();

    /** 
     * Returns the distance between two RGB color values, based on the three-dimensional distance formula.
     */
    public double computeRGBDistance(int queryR, int queryG, int queryB) {
      return Math.sqrt(
          Math.pow(queryR - getR(), 2) + Math.pow(queryG - getG(), 2) + Math.pow(queryB - getB(), 2));
    }
  }
}
