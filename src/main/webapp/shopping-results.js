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
 * Builds the Shopping Results Page results from 
 * Google Shopping into the webpage. 
 */
async function buildShoppingResultsUI() {
  // Make a GET request to '/photo-shopping-request' to scrape the Google Shopping 
  // search query results. The request returns a JSON with data about each product
  // from the Google Shopping results page.

  // Build the URL to be fetched - add parameters to identify the uploaded photo.
  const fetchURL = 
      `/photo-shopping-request`;

  const response = await fetch(fetchURL)
      .catch((error) => {
        console.warn(error);
        return new Response(JSON.stringify({
          code: error.response.status,
          message: `Failed to fetch ${fetchURL}`,
        }));
      });

  if (!response.ok) {
    return Promise.reject(response);
  }

  const products = await response.json();

  // Integrate the products into the webpage.
  products.forEach(product => {
    // Create an HTML node for the item container.
    let $productContainer = $('<div>', {class: 'col-md-4'});

    // Get the HTML content for the container.
    const productElementHTML = getProductElementHTML(product.title,
                                                     product.imageLink,
                                                     product.priceAndSeller,
                                                     product.link,
                                                     product.shippingPrice);

    // Load the content using jQuery's append.
    $productContainer.append(productElementHTML);

    // Add the container to the results page, into the corresponding product wrapper.
    $('#shopping-results-wrapper').append($productContainer);
  });
}

/**
 * Returns the HTML content for a product container, based on the arguments.
 */
function getProductElementHTML(productTitle,
                               productImageLink,
                               productPriceAndSeller,
                               productLink,
                               productShippingPrice) {
  return `<div class="card mb-4 shadow-sm">
            <div class="col-4">
              <img src="${productImageLink}" class="mx-auto d-block">
            </div>
            <div class="card-body">
              <p class="card-text">${productTitle}</p>
              <p class="card-text">${productPriceAndSeller}</p>
              <div class="d-flex justify-content-between align-items-center">
                <div class="btn-group">
                  <button type="button" 
                          class="btn btn-sm btn-outline-secondary" 
                          onclick="window.location.href='${productLink}'">
                    View
                  </button>
                </div>
                <small class="text-muted">${productShippingPrice}</small>
              </div>
            </div>
          </div>`;
}

buildShoppingResultsUI();