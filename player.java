 MediaRouteButton customMediaButton = moviePlayerView.findViewById(R.id.full_cast_totv);
        casty = Casty.create(this).withMiniController(); // initlize mini player. (Customize if you like)
        casty.setUpMediaRouteButton(customMediaButton);
        casty.setOnConnectChangeListener(new Casty.OnConnectChangeListener() {
            @Override
            public void onConnected() {
                Log.d("ConnectionStatus", "Connection Status is true");
                if (movie_Stream != null)
                {
                    try {
                        {
                            initCastPlayer(movie_Stream);
                        }

                    }
                    catch (Exception e)
                    {
                        Toast.makeText(context, "Cast Not supported; Returning to player. ", Toast.LENGTH_SHORT).show();
                        FetchTMDBID(movie_Stream,contentID,source);// if dooo  app use prepare(cpurl) method
                        LoadMovieDetails(contentID);// if dooo  app use prepare(cpurl) method

                    }
                }
                else
                {
                    Toast.makeText(context, "Url is null", Toast.LENGTH_SHORT).show();
                    FetchTMDBID(movie_Stream,contentID,source);// if dooo  app use prepare(cpurl) method
                    LoadMovieDetails(contentID);// if dooo  app use prepare(cpurl) method
                }
            }

            @Override
            public void onDisconnected() {
                Log.d("ConnectionStatus", "Connection Status is false");
                Toast.makeText(context, "Url is null", Toast.LENGTH_SHORT).show();
                FetchTMDBID(movie_Stream,contentID,source);
                LoadMovieDetails(contentID); // if dooo  app use prepare(cpurl) method
            }
        });


// outside oncreate we have methods
   private void initCastPlayer(String movie_link)
    {
        Intent castIntent = getIntent();
        // movie image we gonna get
        String name = Objects.requireNonNull(castIntent.getExtras()).getString("name"); // name of the film
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.GET, AppConfig.url + "getMovieDetails/" + contentID, response -> {

            JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
            String banner = jsonObject.get("poster").getAsString(); // image of the film
            casty.getPlayer().loadMediaAndPlay(createSampleMediaData(movie_link, name, banner)); // casty data poass
        }, error -> {
            // Do nothing
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-api-key", AppConfig.apiKey);
                return params;
            }
        };
        queue.add(sr);
    }

    private MediaData createSampleMediaData(String videoUrl, String name, String banner)
    {
        String contentType = getType(videoUrl);  // identify video format types./
        return new MediaData.Builder(videoUrl).setStreamType(MediaData.STREAM_TYPE_BUFFERED).setContentType(contentType).setMediaType(MediaData.MEDIA_TYPE_MOVIE).setTitle(name).setSubtitle(getString(R.string.app_name)).addPhotoUrl(banner).build();

    }

    private String getType(String videoUrl)
    {
        if (videoUrl.endsWith(".mp4")) {
            return "videos/mp4";
        } else if (videoUrl.endsWith(".mkv")) {
            return "video/x-matroska";
        } else if (videoUrl.endsWith(".m3u8")) {
            return "application/x-mpegurl";
        } else if (videoUrl.startsWith("https://stream")) // custom link for telegram bots or cloudflare worker.
        {
            return "video/x-matroska";
        } else {
            return "application/x-mpegurl";
        }
    }
