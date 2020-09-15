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

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

/**
 * Contains detection results for a product image.
 */
@AutoValue
public abstract class ProductDetectionData {

  public static ProductDetectionData create(
      ImmutableList<String> labels, ImmutableList<String> logos, ImmutableList<String> colors) {
    return new AutoValue_ProductDetectionData(labels, logos, colors);
  }

  public abstract ImmutableList<String> getLabels();
  public abstract ImmutableList<String> getLogos();
  public abstract ImmutableList<String> getColors();
}
