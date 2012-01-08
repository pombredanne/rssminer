(ns rssminer.handlers.feeds
  (:use (rssminer [util :only [session-get to-int assoc-if]]
                  [config :only [rssminer-conf]]
                  [search :only [update-index]]
                  [http :only [client parse-response extract-host]])
        [clojure.tools.logging :only [debug error]])
  (:require [rssminer.db.feed :as db]
            [rssminer.db.user-feed :as uf])
  (:import rssminer.Utils
           rssminer.async.ProxyFuture
           org.jboss.netty.handler.codec.http.HttpResponse
           java.net.URI))

(defn user-vote [req]
  (let [fid (-> req :params :feed-id to-int)
        vote (-> req :body :vote to-int)
        user (session-get req :user)]
    (uf/insert-user-vote (:id user) fid vote)
    (if (-> user :conf :updated)
      {:status 204 :body nil}
      {:status 204 :body nil
       :session {:user (assoc user :conf
                              (assoc (:conf user) :updated true))}})))

(defn mark-as-read [req]
  (let [fid (-> req :params :feed-id to-int)
        user-id (:id (session-get req :user))]
    (uf/mark-as-read user-id fid)))

(defn get-by-subscription [req]
  (let [{:keys [rss-id limit offset] :or {limit 40 offset 0}} (:params req)
        uid (:id (session-get req :user))]
    (db/fetch-by-rssid uid (to-int rss-id) (to-int limit)
                       (to-int offset))))

(defn get-by-id [req]
  (let [feed-id (-> req :params :feed-id)]
    (db/fetch-by-id feed-id)))

(defn- proxy? [link]
  (let [^String host (extract-host link)]
    (or (= -1 (.indexOf host "blogspot"))
        (= -1 (.indexOf host "wordpress")))))

(defn- rewrite-html [original link proxy]
  (if (or proxy (proxy? link))
    (Utils/rewrite original link (str
                                  (:proxy-server @rssminer-conf)  "/p?u="))
    (Utils/rewrite original link)))

(defn- fetch-and-store-orginal [id link proxy]
  {:status 200
   :body (ProxyFuture. client link {} (:proxy @rssminer-conf)
                       (fn [{:keys [resp final-link]}]
                         (let [resp (parse-response resp)]
                           (if (= 200 (:status resp))
                             (let [body (:body resp)]
                               (update-index id body)
                               ;; save final_link if different
                               (db/update-feed id (if (not= final-link link)
                                                    {:original body
                                                     :final_link final-link}
                                                    {:original body}))
                               {:status 200
                                :headers {"Content-Type"
                                          "text/html; charset=utf-8"}
                                :body (rewrite-html body final-link proxy)})
                             (do
                               (debug link resp)
                               {:status 404})))))})

(defn get-orginal [req]
  (let [{:keys [id p]} (-> req :params)
        {:keys [original link final_link]} (db/fetch-orginal id)] ; proxy
    (if original
      (rewrite-html original (or final_link link) p)
      (fetch-and-store-orginal id link p))))

