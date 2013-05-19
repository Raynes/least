# least

This is a very simple and concise last.fm API client. It has a very
straightforward interface to interacting with the last.fm APIs, including some
simple helper functions for the desktop oauth flow.

## Usage

Add a dependency for least:

```clojure
:dependencies [[me.raynes/least "0.1.3"]]
```

Maps have been pretty printed for readability.

First of all, `require` it!

```clojure
user> (require '[me.raynes.least :as least])
nil
```

Now let's make a simple request. Let's see what I've been listening to.

```clojure
user> (least/read "user.getRecentTracks" "REDACTED" {:limit 2 :user "RaynesFM"})
{:recenttracks
 {:track
  [{:artist
    {:text "Fall Out Boy",
     :mbid "516cef4d-0718-4007-9939-f9b38af3f784"},
    :name "The Phoenix",
    :streamable "0",
    :mbid "36b02c04-bf13-4d65-ba48-e00dab4a7453",
    :album
    {:text "Save Rock And Roll",
     :mbid "56227792-5344-41a0-ab74-ba7608b505eb"},
    :url "http://www.last.fm/music/Fall+Out+Boy/_/The+Phoenix",
    :image
    [{:text "http://userserve-ak.last.fm/serve/34s/87829405.png",
      :size "small"}
     {:text "http://userserve-ak.last.fm/serve/64s/87829405.png",
      :size "medium"}
     {:text "http://userserve-ak.last.fm/serve/126/87829405.png",
      :size "large"}
     {:text "http://userserve-ak.last.fm/serve/300x300/87829405.png",
      :size "extralarge"}],
    :attr {:nowplaying "true"}}
   {:artist
    {:text "Neon Trees",
     :mbid "16243662-8538-4746-a0fb-0d15b5828b8e"},
    :name "Hooray for Hollywood",
    :streamable "1",
    :mbid "367ad558-478c-43dc-a175-aae826edad51",
    :album
    {:text "Picture Show",
     :mbid
 "b660188a-4cef-46fa-80f4-58b8bdb4bfc9"},
    :url "http://www.last.fm/music/Neon+Trees/_/Hooray+for+Hollywood",
    :image
    [{:text "http://userserve-ak.last.fm/serve/34s/76690558.png",
      :size "small"}
     {:text "http://userserve-ak.last.fm/serve/64s/76690558.png",
      :size "medium"}
     {:text "http://userserve-ak.last.fm/serve/126/76690558.png",
      :size "large"}
     {:text "http://userserve-ak.last.fm/serve/300x300/76690558.png",
      :size "extralarge"}],
    :date {:text "18 Apr 2013, 06:02", :uts "1366264961"}}
   {:artist
    {:text "Neon Trees",
     :mbid "16243662-8538-4746-a0fb-0d15b5828b8e"},
    :name "Close to You",
    :streamable "1",
    :mbid "e043a3d8-ce1e-4f7f-ab0e-9d99f231e93c",
    :album
    {:text "Picture Show",
     :mbid "b660188a-4cef-46fa-80f4-58b8bdb4bfc9"},
    :url "http://www.last.fm/music/Neon+Trees/_/Close+to+You",
    :image
    [{:text "http://userserve-ak.last.fm/serve/34s/76690558.png",
      :size "small"}
     {:text "http://userserve-ak.la
st.fm/serve/64s/76690558.png",
      :size "medium"}
     {:text "http://userserve-ak.last.fm/serve/126/76690558.png",
      :size "large"}
     {:text "http://userserve-ak.last.fm/serve/300x300/76690558.png",
      :size "extralarge"}],
    :date {:text "18 Apr 2013, 05:57", :uts "1366264654"}}],
  :attr
  {:user "RaynesFM",
   :page "1",
   :perPage "2",
   :totalPages "7275",
   :total "14549"}}}
```

Correct! I'm listening to The Phoenix by Fall Out Boy. Right before that I was
listening to a Neon Trees album.

All of the API calls are executed with either `read` or `write`. The former is
for GET requests and the latter for POSTs. The 'foo.bar' string is the method
we're calling. All of the API methods are listed on the
[last.fm API site](http://www.last.fm/api/intro).

Most things don't require authentication on last.fm, but some things do. I've
written two simple functions in `me.raynes.least.authorize` to help you
authenticate users in a desktop application. Here is how that works:

```clojure
user> (require '[me.raynes.least.authorize :as auth])
nil
user> (def token (auth/get-token "REDACTED API KEY" "REDACTED SECRET"))
#'user/token
user> token
{:url "http://www.last.fm/api/auth/?api_key=REDACTED&token=REDACTED", :token "REDACTED AUTH TOKEN"}
```

At this point, your user should go to this URL where he will authenticate the
application. You should make him give you some indication that he has
finished. After that, do this:

```clojure
user> (def session (auth/get-session "REDACTED API KEY" "REDACTED SECRET" (:token token)))
#'user/session
user> session
{:name "RaynesFM", :key "REDACTED SESSION KEY", :subscriber "0"}
```

Boom, there you have it. Authenticated. Now you're gonna want to save that API
key. You can pass it to API calls requiring authentication along with your
secret. Your secret is not necessary for
API calls that do not require authentication, but it is required for ones that
do. Here is an example. I *really* like The Phoenix, the
song by Fall Out Boy. In fact, you could say I **love** it! Let's tell last.fm
about this discovery:

```clojure
user> (least/write "track.love" "REDACTED" {:secret "REDACTED" :sk (:key session) :track "The Phoenix" :artist "Fall Out Boy"})
{:status "ok"}
```

Now last.fm knows how much I love this particular Fall Out Boy song. Pretty
great, huh?

For full last.fm API documentation, take a look at their [API documentation site](http://www.last.fm/api/intro).

## License

Copyright © 2013 Anthony Grimes

Distributed under the Eclipse Public License, the same as Clojure.
