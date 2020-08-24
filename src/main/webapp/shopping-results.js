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
 * Builds the Shopping Results Page UI by integrating product results from 
 * Google Shopping into the webpage. 
 */
async function buildShoppingResultsUI() {
  // Make a GET request to '/search-shopping-results' to scrape the Google Shopping 
  // query search results. The request returns the complete HTML of the SERP, stored
  // into {@code shoppingSearchResultsPage}.

  const response = await fetch('/search-shopping-results').catch(handleError);

  if (!response.ok) {
    return Promise.reject(response);
  }

  const shoppingSearchResultsPage = await response.text();

  // Get the array of product containers from SERP - each has the class "u30d4".
  const productElementsHTML = $('.u30d4', shoppingSearchResultsPage);

  for (let i = 0; i < productElementsHTML.length - 1; i++) {
    const currentProductHTML = productElementsHTML[i];

    // Get info about the current product by extracting from the HTML element.

    const productImageLink = $('.oR27Gd > img', currentProductHTML).attr('src');

    let productLink = $('.rgHvZc > a', currentProductHTML).attr('href');

    // Fix product link - if the URL starts with '/url?q=', the URL redirection will not work.
    const wrongStartOfLink = '/url?q=';
    // Therefore delete the start if this is the case, for the redirection to successfully work.
    if (productLink.substring(0, wrongStartOfLink.length) === wrongStartOfLink) {
      productLink = productLink.substring(wrongStartOfLink.length);
    }

    // Get the title as HTML instead of text, in order to keep the <b> tags.
    const productTitle = $('.rgHvZc > a', currentProductHTML).html();

    const productPrice = $('.dD8iuc > .HRLxBb', currentProductHTML).text();

    // As some products do not have rating, the classes order may differ, therefore
    // define productPriceAndSeller for both cases.
    let productPriceAndSeller;
    let productRatingInStars;
    // If the product does not have a rating container, 
    if ($('.dD8iuc:nth-of-type(3)', currentProductHTML).html() == undefined) {
      // only get the price and seller container.
      productPriceAndSeller = $('.dD8iuc:nth-of-type(2)', currentProductHTML).html();
    } else {
      // Else, assign both values.
      productRatingInStars = $('.dD8iuc:nth-of-type(2)', currentProductHTML).text();
      productPriceAndSeller = $('.dD8iuc:nth-of-type(3)', currentProductHTML).html();
    }

    const productShippingPrice = $('.dD8iuc:nth-of-type(1)', currentProductHTML).text();

    // Create an HTML node for the item container.
    let $productContainer = $('<div>', { class: 'col-md-4' });

    // Get the HTML for the container.
    const productElementHTML = getProductElementHTML(productTitle,
                                                     productImageLink,
                                                     productPriceAndSeller,
                                                     productLink,
                                                     productShippingPrice);
    // Load the content using jQuery's append.
    $productContainer.append(productElementHTML);

    // Add the container to the results page, into the corresponding product wrapper.
    $('#shopping-results-wrapper').append($productContainer);
  }
}

/**
 * Returns the HTML for a product container, based on the arguments.
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

/**
 * Handles error from asynchronous function that fetches '/search-shopping-results'.
 */
function handleError(error) {
  console.warn(error);
  return new Response(JSON.stringify({
    code: 400,
    message: 'Failed to fetch "/search-shopping-results"',
  }));
};

buildShoppingResultsUI();
