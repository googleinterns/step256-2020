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

package com.google.sps.data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ProductDetectionData {
  private List<String> labels;
  private List<String> logos;
  private List<String> colors;

  public ProductDetectionData(List<String> labels, List<String> logos, List<String> colors) {
    // shallow copy
    this.labels = new ArrayList<String>(labels);
    this.logos = new ArrayList<String>(logos);
    this.colors = new ArrayList<String>(colors);
  }

  public List<String> getLabels() {
    return labels;
  }

  public List<String> getLogos() {
    return logos;
  }

  public List<String> getColors() {
    return colors;
  }
}