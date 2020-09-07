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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.jupiter.api.Assertions;

/** Test for DetectText class. */
@RunWith(JUnit4.class)
public final class DetectTextTest {

  private String nullBlobKey = "";
  private String invalidBlobKey = "abcd";
  private String validBlobKey = "AMIfv943BzZrlhyLIN_el9l15Zz2LUp4H3bRNwHj2yvPaDNuv0uZjsp8vSySw0u5pEp-sspSVv4U5fkCT6SY1vbTeObItcslXvPswVieK3rInnCc0nBkTDlfIgWqxlvYjFVPS1QQqCjiY7NlRNSHA3gRXtsv6hrj7_J3_c_DaCP4jhpbZid2bkKvOA1XD0geHOXbCjKHf0ZIbEU4wKqQgGcCxT0X4Ddi4o6Nk8rhN7CNbN27GD-iBfrIG2RwNXr24xLLlFg8Bw9xCUxrbaXO6tkJNHndSghb0x4KAZ-IEPYKHfuWtLfXSmgMV_D1hHZdqShTaUZIyJu-";
  private DetectText detectText;

  @Before
  public void setUp() {
    detectText = new DetectText();
  }

// ToDO: After creating a service account 

//   @Test
//   public void optionsForNoAttendees() {
// System.out.println("Test");
//     Assertions.assertThrows(PhotoShoppingException.class, ()-> {detectText.imageToShoppingListExtractor(nullBlobKey);});  
//   }
//     @Test
//   public void optionsForNoAttendees() {
// //System.out.println("Test");
//     Assertions.assertThrows(PhotoShoppingException.class, ()-> {detectText.imageToShoppingListExtractor(invalidBlobKey);});  
//   //Assert.assertEquals(PhotoShoppingException.class, ()-> {detectText.imageToShoppingListExtractor(invalidBlobKey);}); 
//   }
//       @Test
//   public void optionsForNoAttendees() {
// //System.out.println("Test");
//     Assertions.assertThrows(PhotoShoppingException.class, ()-> {detectText.imageToShoppingListExtractor(validBlobKey);});  
//   //Assert.assertEquals(PhotoShoppingException.class, ()-> {detectText.imageToShoppingListExtractor(invalidBlobKey);}); 
//   }
}
