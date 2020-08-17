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
 * Makes the form for uploading an image visible and adds the 'action' to it by fetching
 * the url(that the form needs to post to) from the servlet
 */
function fetchBlobstoreUrlAndShowForm() {
  fetch('/blobstore-upload-url')
      .then((response) => response.text())
      .then((imageUploadUrl) => {
        const uploadForm = document.getElementById('upload-barcode-form');
        uploadForm.action = imageUploadUrl;
        uploadForm.classList.remove('hidden');
      });
}

/**
 * Fetches all image keys stored in the database and
 * displays them on the web page along with date, time and message
 * using get-image-url servlet
 */
function getAndShowImages() {
  const imageListElement = document.getElementById('history-images');

  fetch('/handle-image', {
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    },
  })
      .then((response) => response.json())
      .then((imageDetails) => {
        if (imageDetails.length === 0) {
          imageListElement.innerText = 'No history';
        } else {
          imageDetails.forEach((imageDetail) => {
            imageListElement.appendChild(createImageElement(imageDetail));
          });
        }
      });
}

/**
 * Creates an image element when imageDetail is passed to it
 * from the getAndShowImages function
 * @return {Object} an imageElement i.e. the image and the
 * date/time when it was first posted.
 * @param {Object} imageDetail contains details of the images i.e.
 * image key and date for each image key in datastore.
 */
function createImageElement(imageDetail) {
  const imageElement = document.createElement('li');
  imageElement.className = 'image-element';

  const blobstoreKey = imageDetail.blobKey;
  const imageUrl = `/get-image-url?blob-key=${blobstoreKey}`;

  const image = document.createElement('IMG');
  image.className = 'image';
  image.setAttribute('src', imageUrl);
  image.setAttribute('width', 'auto');
  image.setAttribute('height', '100');
  image.setAttribute('alt', imageUrl);

  const expandedImg = document.createElement('A');
  expandedImg.setAttribute('href', imageUrl);
  expandedImg.appendChild(image);

  const timeElement = document.createElement('p');
  timeElement.className = 'date';
  const date = new Date(imageDetail.timestamp).toLocaleDateString('en-US');
  const time = new Date(imageDetail.timestamp).toLocaleTimeString('en-US');
  timeElement.innerHTML = `🕘${date}&nbsp;${time}`;

  imageElement.appendChild(timeElement);
  imageElement.appendChild(expandedImg);
  return imageElement;
}
