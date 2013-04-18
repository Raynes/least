(ns me.raynes.least.authorize
  (:require [me.raynes.least :as least]))

(defn get-token
  "Takes an API key and secret and returns a map with an authentication token and
   a URL to send the user to in order to authenticate. The next step is
   to call get-session with the token returned here after the user has
   authenticated your application."
  [key secret]
  (when-let [token (:token (least/read "auth.getToken" key {:secret secret}))]
    {:url (format "http://www.last.fm/api/auth/?api_key=%s&token=%s" key token)
     :token token}))

(defn get-session
  "Takes an API key, secret, and either the output of get-token or the
   token string itself. Returns a map containing :name and :key keys. To
   authenticate requests, just pass an :sk key in params with either the
   session key or the map returned by this function."
  [key secret token]
  (:session (least/read "auth.getSession" key {:token (if (map? token)
                                                        (:token token)
                                                        token)
                                               :secret secret})))