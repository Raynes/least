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

(defn ^:private remove-stupid-characters [k]
  (-> (remove #{\@ \#} k)
      (join)
      (keyword)))

(defn ^:private req [method key http-method params]
  (let [params (assoc params
                 :format "json"
                 :method method
                 :api_key key)
        secret (:secret params)
        key-fn (:key-fn params remove-stupid-characters)
        params (dissoc params :secret :key-fn)
        params (assoc params :api_sig (sign params secret))]
    (-> (http/request {:url *api*
                       :method http-method
                       :query-params (when (= :get http-method) params)
                       :form-params (when (= :post http-method) params)})
        (:body)
        (json/decode key-fn))))

(defn read
  "Execute a read-only API request. Takes a method, API token, and some parameters.
   You can also pass in a :key-fn param that is passed to cheshire's decode function."
  [method key & [params]]
  (req method key :get params))

(defn write
  "Execute a writing API request. Takes a method, API token, and some parameters.
   You can also pass in a :key-fn param that is passed to cheshire's decode function."
  [method key & [params]]
  (req method key :post params))

