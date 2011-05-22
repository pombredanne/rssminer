(ns feng.rss.db.feed
  (:use [feng.rss.db.util :only [exec-query select-sql-params
                                 insert-sql-params]]))

(defn inser-feedsource [feedsource]
  (first
   (exec-query (insert-sql-params :feedsources feedsource))))

(defn insert-user-feedsource [item]
  (first
   (exec-query (insert-sql-params :user_feedsource item))))

(defn insert-feed [feed]
  (first
   (exec-query (insert-sql-params :feeds feed))))

(defn insert-user-feed [item]
  (first
   (exec-query (insert-sql-params :user_feed item))))

(defn fetch-feedsource [map]
  (first
   (exec-query (select-sql-params :feedsources map))))

(defn fetch-user-feedsource [map]
  (first
   (exec-query (select-sql-params :user_feedsource map))))


(defn fetch-feed
  ([fs-id] (exec-query ["select * from feeds"]))
  ([user-id fs-id limit offset]
     (exec-query ["select feeds.* from feeds join user_feed on feeds.id =
                     user_feed.feed_id where user_feed.user_id = ? and
                     feeds.feedsource_id = ? limit ? offset ?"
                  user-id fs-id limit offset])))
