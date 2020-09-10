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

import java.awt.Color;
import java.util.ArrayList;

/**
 * Gets color name from RGB color.
 */
public class ColorUtils {
  /**
   * Initialize the color list.
   */
  private ArrayList<ColorName> initColorList() {
    ArrayList<ColorName> colorList = new ArrayList<ColorName>();
		colorList.add(new ColorName("Beige", 0xF5, 0xF5, 0xDC));
		colorList.add(new ColorName("Black", 0x00, 0x00, 0x00));
		colorList.add(new ColorName("Blue", 0x00, 0x00, 0xFF));
		colorList.add(new ColorName("Coral", 0xFF, 0x7F, 0x50));
		colorList.add(new ColorName("Cyan", 0x00, 0xFF, 0xFF));
		// colorList.add(new ColorName("Dark Blue", 0x00, 0x00, 0x8B));
		colorList.add(new ColorName("Gray", 0xA9, 0xA9, 0xA9));
		colorList.add(new ColorName("Dark Green", 0x00, 0x64, 0x00));
		colorList.add(new ColorName("Khaki", 0xBD, 0xB7, 0x6B)); // Dark Khaki
		colorList.add(new ColorName("Orange", 0xFF, 0x8C, 0x00));  // Dark Orange
		colorList.add(new ColorName("Dark Red", 0x8B, 0x00, 0x00));
		colorList.add(new ColorName("Turquoise", 0x00, 0xCE, 0xD1));  // Dark Turquoise
		colorList.add(new ColorName("Violet", 0x94, 0x00, 0xD3));  // Dark Violet
		colorList.add(new ColorName("Pink", 0xFF, 0x14, 0x93)); // DeepPink
		colorList.add(new ColorName("Blue", 0x00, 0xBF, 0xFF));  // DeepSkyBlue
		colorList.add(new ColorName("Green", 0x22, 0x8B, 0x22));  // ForestGreen
		colorList.add(new ColorName("Fuchsia", 0xFF, 0x00, 0xFF));
		colorList.add(new ColorName("Yellow", 0xFF, 0xD7, 0x00)); // Gold
		colorList.add(new ColorName("Gray", 0x80, 0x80, 0x80));
		colorList.add(new ColorName("Green", 0x00, 0x80, 0x00));
		colorList.add(new ColorName("Pink", 0xFF, 0x69, 0xB4)); // HotPink
		colorList.add(new ColorName("Indigo", 0x4B, 0x00, 0x82));
		colorList.add(new ColorName("Khaki", 0xF0, 0xE6, 0x8C));
		colorList.add(new ColorName("Lavender", 0xE6, 0xE6, 0xFA));
		colorList.add(new ColorName("Light Blue", 0xAD, 0xD8, 0xE6));
		colorList.add(new ColorName("Light Cyan", 0xE0, 0xFF, 0xFF));
		colorList.add(new ColorName("Light Gray", 0xD3, 0xD3, 0xD3));
		colorList.add(new ColorName("Light Green", 0x90, 0xEE, 0x90));
		colorList.add(new ColorName("Light Pink", 0xFF, 0xB6, 0xC1));
		colorList.add(new ColorName("LightYellow", 0xFF, 0xFF, 0xE0));
		colorList.add(new ColorName("Lime", 0x00, 0xFF, 0x00));
		colorList.add(new ColorName("Magenta", 0xFF, 0x00, 0xFF));
		colorList.add(new ColorName("Maroon", 0x80, 0x00, 0x00));
		colorList.add(new ColorName("Blue", 0x00, 0x00, 0xCD));  // MediumBlue
		colorList.add(new ColorName("Purple", 0x93, 0x70, 0xDB)); // MediumPurple
		colorList.add(new ColorName("Green", 0x00, 0xFA, 0x9A));  // MediumSpringGreen
		colorList.add(new ColorName("Turquoise", 0x48, 0xD1, 0xCC));  // MediumTurquoise
		colorList.add(new ColorName("Navy", 0x00, 0x00, 0x80));
		colorList.add(new ColorName("Olive", 0x80, 0x80, 0x00));
		colorList.add(new ColorName("Orange", 0xFF, 0xA5, 0x00));
		colorList.add(new ColorName("Orange", 0xFF, 0x45, 0x00)); // OrangeRed
		colorList.add(new ColorName("Pink", 0xFF, 0xC0, 0xCB));
		colorList.add(new ColorName("Plum", 0xDD, 0xA0, 0xDD));
		colorList.add(new ColorName("Purple", 0x80, 0x00, 0x80));
		colorList.add(new ColorName("Red", 0xFF, 0x00, 0x00));
		colorList.add(new ColorName("Blue", 0x41, 0x69, 0xE1));  // RoyalBlue
    colorList.add(new ColorName("Brown", 0x8B, 0x45, 0x13));  // SaddleBrown	139, 69, 19	0x8B, 0x45, 0x13
		colorList.add(new ColorName("Silver", 0xC0, 0xC0, 0xC0));
		colorList.add(new ColorName("Blue", 0x87, 0xCE, 0xEB)); // SkyBlue
		colorList.add(new ColorName("Snow", 0xFF, 0xFA, 0xFA));
		colorList.add(new ColorName("Tan", 0xD2, 0xB4, 0x8C));
		colorList.add(new ColorName("Turquoise", 0x40, 0xE0, 0xD0));
		colorList.add(new ColorName("Violet", 0xEE, 0x82, 0xEE));
		colorList.add(new ColorName("White", 0xFF, 0xFF, 0xFF));
		colorList.add(new ColorName("Yellow", 0xFF, 0xFF, 0x00));
		return colorList;
	}

	/**
	 * Get the closest color name from colorList.
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @return
	 */
	public String getColorNameFromRgb(int r, int g, int b) {
		ArrayList<ColorName> colorList = initColorList();
		ColorName closestMatch = null;
		int minMSE = Integer.MAX_VALUE;
		int mse;
		for (ColorName c : colorList) {
			mse = c.computeMSE(r, g, b);
			if (mse < minMSE) {
				minMSE = mse;
				closestMatch = c;
			}
		}

		if (closestMatch != null) {
			return closestMatch.getName();
		} else {
			return "No matched color name.";
		}
	}

	public String getColorNameFromColor(Color color) {
		return getColorNameFromRgb(color.getRed(), color.getGreen(), color.getBlue());
	}

	/**
	 * SubClass of ColorUtils. In order to lookup color name
	 */
	public class ColorName {
		public int r, g, b;
		public String name;

		public ColorName(String name, int r, int g, int b) {
			this.r = r;
			this.g = g;
			this.b = b;
			this.name = name;
		}

		public int computeMSE(int pixR, int pixG, int pixB) {
			return (int) (((pixR - r) * (pixR - r) + (pixG - g) * (pixG - g) + (pixB - b)
					* (pixB - b)) / 3);
		}
    
		public String getName() {
			return name;
		}
	}
}
