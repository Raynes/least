(ns me.raynes.least.webauth
  (:use [me.raynes.least.authorize :only [get-session]])
  (:require [me.raynes.least :as least]))

(defn get-token
  "Takes an API key and returns a map with a URL to send the user to
in to authenticate. You can optionally specify a callback URL
that is different to your API Account callback url. The next step is
to call get-session with the token supplied to the callback URL as a
GET parameter after the user has authenticated your application."
  ([key]
     {:url (format "http://www.last.fm/api/auth/?api_key=%s" key)})
  ([key callback]
     {:url (format "http://www.last.fm/api/auth/?api_key=%s&cb=%s"
                   key
                   callback)}))