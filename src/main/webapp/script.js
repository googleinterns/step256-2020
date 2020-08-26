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

/**
 * Event processing for upload button
 */
function openFileUploadDialog() {
  document.querySelector('.bg-model').style.display = 'flex';
}

/**
 * Event processing for close button
 */
function closeFileUploadDialog() {
  document.querySelector('.bg-model').style.display = 'none';
}

/**
 * Makes the form for uploading an image visible and
 * adds the 'action' to it by fetching
 * the url(that the form needs to post to) from the servlet
 */
async function fetchBlobstoreUrlAndShowForm() {
  const response = await fetch('/blobstore-upload-url');
  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }
  
  let imageUploadUrl = await response.text();
  const uploadForm = document.getElementById('upload-image-form');
  uploadForm.action = imageUploadUrl;
  uploadForm.classList.remove('hidden');
}

let photoCategory;
let blobKeyString;

async function fetchBlobKeyString() {
  const response = await fetch('/get-image-blobkey');
  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }
  
  // Get and store the photo category (i.e. product, list or barcode) and the keystring 
  // of the blobkey.
  let uploadedPhotoInformation = await response.text();

  uploadedPhotoInformation = uploadedPhotoInformation.split('\n');

  photoCategory = uploadedPhotoInformation[0];
  blobKeyString = uploadedPhotoInformation[1];
}
