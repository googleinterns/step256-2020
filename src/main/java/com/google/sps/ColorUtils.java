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

import java.awt.Color;
import java.util.ArrayList;

/**
 * Utility class for converting RGB color values to color name.
 */
public class ColorUtils {

  /**
   * Compiles a list of known colors.
   */
  private ArrayList<ColorName> initColorList() {
    ArrayList<ColorName> colorList = new ArrayList<ColorName>();
    colorList.add(ColorName.create("Beige", 0xF5, 0xF5, 0xDC));
    colorList.add(ColorName.create("Black", 0x00, 0x00, 0x00));
    colorList.add(ColorName.create("Blue", 0x00, 0x00, 0xFF));
    colorList.add(ColorName.create("Coral", 0xFF, 0x7F, 0x50));
    colorList.add(ColorName.create("Cyan", 0x00, 0xFF, 0xFF));
    colorList.add(ColorName.create("Gray", 0xA9, 0xA9, 0xA9));
    colorList.add(ColorName.create("Dark Green", 0x00, 0x64, 0x00));
    colorList.add(ColorName.create("Khaki", 0xBD, 0xB7, 0x6B));      // Dark Khaki
    colorList.add(ColorName.create("Orange", 0xFF, 0x8C, 0x00));     // Dark Orange
    colorList.add(ColorName.create("Dark Red", 0x8B, 0x00, 0x00));
    colorList.add(ColorName.create("Turquoise", 0x00, 0xCE, 0xD1));  // Dark Turquoise
    colorList.add(ColorName.create("Violet", 0x94, 0x00, 0xD3));     // Dark Violet
    colorList.add(ColorName.create("Pink", 0xFF, 0x14, 0x93));       // DeepPink
    colorList.add(ColorName.create("Blue", 0x00, 0xBF, 0xFF));       // DeepSkyBlue
    colorList.add(ColorName.create("Green", 0x22, 0x8B, 0x22));      // ForestGreen
    colorList.add(ColorName.create("Fuchsia", 0xFF, 0x00, 0xFF));
    colorList.add(ColorName.create("Yellow", 0xFF, 0xD7, 0x00));     // Gold
    colorList.add(ColorName.create("Gray", 0x80, 0x80, 0x80));
    colorList.add(ColorName.create("Green", 0x00, 0x80, 0x00));
    colorList.add(ColorName.create("Pink", 0xFF, 0x69, 0xB4));       // HotPink
    colorList.add(ColorName.create("Indigo", 0x4B, 0x00, 0x82));
    colorList.add(ColorName.create("Khaki", 0xF0, 0xE6, 0x8C));
    colorList.add(ColorName.create("Lavender", 0xE6, 0xE6, 0xFA));
    colorList.add(ColorName.create("Light Blue", 0xAD, 0xD8, 0xE6));
    colorList.add(ColorName.create("Light Cyan", 0xE0, 0xFF, 0xFF));
    colorList.add(ColorName.create("Light Gray", 0xD3, 0xD3, 0xD3));
    colorList.add(ColorName.create("Light Green", 0x90, 0xEE, 0x90));
    colorList.add(ColorName.create("Light Pink", 0xFF, 0xB6, 0xC1));
    colorList.add(ColorName.create("LightYellow", 0xFF, 0xFF, 0xE0));
    colorList.add(ColorName.create("Lime", 0x00, 0xFF, 0x00));
    colorList.add(ColorName.create("Magenta", 0xFF, 0x00, 0xFF));
    colorList.add(ColorName.create("Maroon", 0x80, 0x00, 0x00));
    colorList.add(ColorName.create("Blue", 0x00, 0x00, 0xCD));       // MediumBlue
    colorList.add(ColorName.create("Purple", 0x93, 0x70, 0xDB));     // MediumPurple
    colorList.add(ColorName.create("Green", 0x00, 0xFA, 0x9A));      // MediumSpringGreen
    colorList.add(ColorName.create("Turquoise", 0x48, 0xD1, 0xCC));  // MediumTurquoise
    colorList.add(ColorName.create("Navy", 0x00, 0x00, 0x80));
    colorList.add(ColorName.create("Orange", 0xFF, 0xA5, 0x00));
    colorList.add(ColorName.create("Orange", 0xFF, 0x45, 0x00));     // OrangeRed
    colorList.add(ColorName.create("Pink", 0xFF, 0xC0, 0xCB));
    colorList.add(ColorName.create("Purple", 0x80, 0x00, 0x80));
    colorList.add(ColorName.create("Red", 0xFF, 0x00, 0x00));
    colorList.add(ColorName.create("Blue", 0x41, 0x69, 0xE1));       // RoyalBlue
    colorList.add(ColorName.create("Brown", 0x8B, 0x45, 0x13));      // SaddleBrown
    colorList.add(ColorName.create("Silver", 0xC0, 0xC0, 0xC0));
    colorList.add(ColorName.create("Blue", 0x87, 0xCE, 0xEB));       // SkyBlue
    colorList.add(ColorName.create("Turquoise", 0x40, 0xE0, 0xD0));
    colorList.add(ColorName.create("Violet", 0xEE, 0x82, 0xEE));
    colorList.add(ColorName.create("White", 0xFF, 0xFF, 0xFF));
    colorList.add(ColorName.create("Yellow", 0xFF, 0xFF, 0x00));
    return colorList;
  }
  
  /**
   * Returns the color name of the closest color (to the query color provided as RGB).
   * To find the closest color, gets the color list, and then computes the distance between 
   * the query color and every color in the color list, based on their RGB values.
   */
  public String getColorNameFromRGB(int r, int g, int b) throws IllegalArgumentException {
    if (!(0 < r && r < 255) || !(0 < g && g < 255) || !(0 < b && b < 255)) {
      throw new IllegalArgumentException("RGB values outside [0, 255] range.");
    } 

    ArrayList<ColorName> colorList = initColorList();
    
    // Get the color name of the color with minimum RGB distance to the query color.
    ColorName closestColorMatch = null;

    int minRGBDistance = Integer.MAX_VALUE;
    for (ColorName color : colorList) {
      int currentRGBDistance = color.computeRGBDistance(r, g, b);

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
    public int computeRGBDistance(int queryR, int queryG, int queryB) {
      return (int) (Math.sqrt((queryR - getR()) * (queryR - getR()) + (queryG - getG()) * (queryG - getG()) + 
          (queryB - getB()) * (queryB - getB())));
    }
  }
}
