/**
 * Event processing for upload button
 */
function fileUploadDisplay() {
  document.querySelector('.bg-model').style.display = 'flex';
}

/**
 * Event processing for close button
 */
function closeDiv() {
  document.querySelector('.bg-model').style.display = 'none';
}

/**
 * Makes the form visible and adds the 'action' to it by fetching
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
 * Fetching all image keys stored in datastore and
 * presenting them on screen along with date/time and its message
 * using get-image-url servlet
 */
function getAndShowImages() {
  const imageListElement = document.getElementById('history-images');

  fetch('/my-image-servlet', {
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    },
  })
      .then((response) => response.json()).then((imageDetails) => {
        if (imageDetails.length == 0) {
          imageListElement.innerText = 'No history';
        } else {
          imageDetails.forEach((imageDetail) => {
            imageListElement.appendChild(createImageElement(imageDetail));
          });
        }
      });
}

/**
 * Creating an image element when imageDetail is passed to it
 * from the getAndShowImages function
 * @return {Object} an imageElement ie the image and the
 * date/time when it was first posted.
 * @param {Object} imageDetail contains details of the images ie
 * image key and date for each image key in datastore.
 */
function createImageElement(imageDetail) {
  const imageElement = document.createElement('li');
  imageElement.className = 'image-element';

  const blobstoreKey = imageDetail.blobKey;
  const imageUrl = '/get-image?blob-key='+blobstoreKey;

  const myImg = document.createElement('IMG');
  myImg.className = 'image';
  myImg.setAttribute('src', imageUrl);
  myImg.setAttribute('width', 'auto');
  myImg.setAttribute('height', '100');
  myImg.setAttribute('alt', imageUrl);

  const myExpandedImg = document.createElement('A');
  myExpandedImg.setAttribute('href', imageUrl);
  myExpandedImg.appendChild(myImg);


  const timeElement = document.createElement('p');
  timeElement.className = 'date';
  const date = new Date(imageDetail.timestamp).toLocaleDateString('en-US');
  const time = new Date(imageDetail.timestamp).toLocaleTimeString('en-US');
  timeElement.innerHTML = '🕘'+ date + '&nbsp;' + time;

  imageElement.appendChild(timeElement);
  imageElement.appendChild(myExpandedImg);
  return imageElement;
}
