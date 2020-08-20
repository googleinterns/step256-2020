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

function buildUI() {
  // Scrape Shopping Search Results & get SERP
  fetch('/search-shopping-results').then(response => response.text()).then((shoppingSearchResultsPage) => {

    // Get product elements from the HTML returned
    let productElementsHTML = $('.u30d4', shoppingSearchResultsPage);

    for (let i = 0 ; i < productElementsHTML.length - 1; i++) {
      let currentProductHTML = productElementsHTML[i];
      
      // Get the info about the product by extracting from the HTML element
      
      let productImageLink = $('.oR27Gd > img', currentProductHTML).attr('src');

      let productLink = $('.rgHvZc > a', currentProductHTML).attr('href');

      // Fix product link - if the URL starts with '/url?q=', the URL redirection will not work.
      let wrongStartOfLink = '/url?q=';
      // Therefore delete the start if this is the case, for the redirection to successfully work.
      if (productLink.substring(0, wrongStartOfLink.length) == wrongStartOfLink) {
        productLink = productLink.substring(wrongStartOfLink.length);
      }
      
      // Get the title as html instead of text in order to keep the <b> tags
      let productTitle = $('.rgHvZc > a', currentProductHTML).html();

      let productPrice = $('.dD8iuc > .HRLxBb', currentProductHTML).text()

      // Some products do not have rating - define productPriceAndSeller for both cases
      let productPriceAndSeller;
      let productRatingInStars;
      if ($('.dD8iuc:nth-of-type(3)', currentProductHTML).html() == undefined) {
        productPriceAndSeller = $('.dD8iuc:nth-of-type(2)', currentProductHTML).html();
      } else {
        productRatingInStars = $('.dD8iuc:nth-of-type(2)', currentProductHTML).text();
        productPriceAndSeller = $('.dD8iuc:nth-of-type(3)', currentProductHTML).html();
      }

      let productShippingPrice = $('.dD8iuc:nth-of-type(1)', currentProductHTML).text();
      
      // Create the wrapper node for the product - append it to the results HTML page,
      // then load the content using jQuery's append.
      let $itemContainer = $('<div>', {class: 'col-md-4'});     
      
      let productElementHTML = 
          getProductElementHTML(productTitle, productImageLink, productPriceAndSeller, productLink, productShippingPrice);
      $itemContainer.append(productElementHTML);
      
      $('#shopping-results-wrapper').append($itemContainer);
    }
  });
}

function getProductElementHTML(productTitle, productImageLink, productPriceAndSeller, productLink, productShippingPrice) {
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

buildUI();
