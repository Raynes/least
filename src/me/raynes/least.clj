(ns me.raynes.least
  (:refer-clojure :exclude [read])
  (:require [clj-http.client :as http]
            [cheshire.core :as json]
            [clojure.string :refer [join]])
  (:import java.security.MessageDigest))

(def ^:dynamic *api* "http://ws.audioscrobbler.com/2.0/")

(defn ^:private md5 [s]
  (join (map (partial format "%02x")
             (.digest
              (doto (MessageDigest/getInstance "MD5")
                (.reset)
                (.update (.getBytes s)))))))

(defn ^:private sign
  [params secret]
  (md5
   (str (->> (for [[k v] (dissoc params :format) 
                   :when v]
               [(name k) v])
             (sort-by first)
             (apply concat)
             (join))
        secret)))

(defn ^:private req [method key http-method params]
  (let [params (assoc params
                 :format "json"
                 :method method
                 :api_key key)
        secret (:secret params)
        string-keys? (:string-keys? params false)
        params (dissoc params :secret)
        params (assoc params :api_sig (sign params secret))]
    (-> (http/request {:url *api*
                       :method http-method
                       :query-params (when (= :get http-method) params)
                       :form-params (when (= :post http-method) params)})
        (:body)
        (json/decode (not string-keys?)))))

(defn read
  "Execute a read-only API request. Takes a method, API token, and some parameters.
   You can pass :string-keys? true in the param map to not convert keys to keywords.
   This can be useful for optimization purposes."
  [method key & [params]]
  (req method key :get params))

(defn write
  "Execute a writing API request. Takes a method, API token, and some parameters.
   You can pass :string-keys? true in the param map to not convert keys to keywords.
   This can be useful for optimization purposes."
  [method key & [params]]
  (req method key :post params))