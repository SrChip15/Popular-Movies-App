Popular Movies - Stage One    <img src=https://www.themoviedb.org/static_cache/v4/logos/408x161-powered-by-rectangle-green-bb4301c10ddc749b4e79463811a68afebeae66ef43d17bcfd8ff0e60ded7ce99.png width="150"/>
==========================

The app will:

* Present the user with a grid arrangement of movie posters upon launch.
* Allow your user to change sort order via a setting:
    * The sort order can be by most popular or by highest-rated
* Allow the user to tap on a movie poster and transition to a details screen with additional information such as:
    * original title
    * movie poster image thumbnail
    * A plot synopsis (called overview in the api)
    * user rating (called vote_average in the api)
    * release date
    
**This product uses the TMDb API but is not endorsed or certified by TMDb.**

Tools/Libraries
----------------

* Gradle v4.4
* Android Plugin v3.1.2
* Android API v27
* Android Build Tools v27
* Android Support Library v27
* [Butter Knife](https://github.com/JakeWharton/butterknife)
* [Retrofit](https://github.com/square/retrofit)
* [Glide](https://github.com/bumptech/glide)


Note
----
Please substitute your API key for PrivateApiKey.YOUR_API_KEY in the MoviesListFragment.java file (callMoviesApi() method).


License
----

```

Copyright 2018 Srinath Chintapalli

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

```
