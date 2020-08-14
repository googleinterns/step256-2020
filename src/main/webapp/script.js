/* event processing for upload button */
function uploadFile() {
    document.querySelector('.bg-model').style.display='flex';
}
    
/*event processing for close button */
function closeDiv() {
    document.querySelector('.bg-model').style.display='none';
    console.log("working");
}

/** Makes the form visible and adds the 'action' to it by fetching
the url(that the form needs to post to) from the servlet */
function fetchBlobstoreUrlAndShowForm() {
    fetch('/blobstore-upload-url')
        .then((response) => response.text())
        .then((imageUploadUrl) => {
          const messageForm = document.getElementById('upload-barcode');
          messageForm.action = imageUploadUrl;
          messageForm.classList.remove('hidden');
        });
  }
    