fetch('/test-search').then(response => response.text()).then((SERP) => {
  let items = $('.u30d4', SERP);
 
  for (let i = 0;  i < items.length - 1; i++) {
    document.getElementById('shopping-results-wrapper').appendChild( items[i] );
  }
});
