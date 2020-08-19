function buildUI() {
  fetch('/test-search').then(response => response.text()).then((SERP) => {

    let items = $('.u30d4', SERP);

    for (let i = 0 ; i < items.length - 1; i++) {
      let productImageLink = $('.oR27Gd > img', items[i]).attr('src');

      let productLink = $('.rgHvZc > a', items[i]).attr('href');

      let wrongStartOfLink = '/url?q=';

      if (productLink.substring(0, wrongStartOfLink.length) == wrongStartOfLink) {
        productLink = productLink.substring(wrongStartOfLink.length);
      }
      
      let productTitle = $('.rgHvZc > a', items[i]).text();

      let productPrice = $('.dD8iuc > .HRLxBb', items[i]).text()

      let productPriceAndSeller = $('.dD8iuc:nth-of-type(3)', items[i]).text();

      let productRatingInStars = $('.dD8iuc:nth-of-type(2)', items[i]).text();

      let productShippingPrice = $('.dD8iuc:nth-of-type(1)', items[i]).text();

      let htmlItem = 
       `<div class="col-md-4">
          <div class="card mb-4 shadow-sm">
            <div class="col-4" style="width:75px;height:75px">
              <img src="${productImageLink}" class="mx-auto d-block">
            </div>
            <div class="card-body">
              <p class="card-text">${productTitle}</p>
              <p class="card-text">${productPriceAndSeller}</p>
              <div class="d-flex justify-content-between align-items-center">
                <div class="btn-group">
                  <button type="button" class="btn btn-sm btn-outline-secondary" onclick="window.location.href='${productLink}'">View</button>
                </div>
                <small class="text-muted">${productShippingPrice}</small>
              </div>
            </div>
          </div>
        </div>`;
      
      document.getElementById('shopping-results-wrapper').innerHTML += htmlItem;
    }
  });
}

buildUI();
