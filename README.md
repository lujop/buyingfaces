#BuyingFaces
Small task project I started for freelance project evaluation

The application follows MVVM architecture using bindings throught RxJava observables and caches data using RxCache.

<img src="https://github.com/lujop/buyingfaces/blob/master/screenshoots/screen1.png" width="256">
<img src="https://github.com/lujop/buyingfaces/blob/master/screenshoots/screen2.png" width="256"><br/>

##Requeriments for task project
There are some mockups included in the .zip (req.zip)
One shows the layout (search box, and result grid) and the other showing what each product in the result grid should look like. Your solution doesn't need to look exactly like this - treat them as a wireframe and adapt the design as best suits the device.  

You can get search results from the API with a request like this:
curl http://74.50.59.155:5000/api/search
The API also accepts some parameters:
GET /api/search Parameters
- limit (int)  Max number of search results to return
- skip (int)  Number of results to skip before sending back search results
- q (string) - Search query. Tags separated by spaces.
- onlyInStock (bool) - When flag is set, only return products that are currently in stock.

Response Type: NDJSON

The app should keep loading products from the API until it has enough to fill the screen, and then wait until the user has swiped to the bottom to load more. The app should cache API requests for 1 hour.
