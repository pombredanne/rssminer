(ns rssminer.views.layouts
  (:require net.cgrand.enlive-html
            rssminer.config
            [clojure.string :as str]))

(def ^{:private true} profile-specific
  '([(net.cgrand.enlive-html/attr= :data-profile "dev")]
      (if (rssminer.config/in-dev?) identity
          (net.cgrand.enlive-html/substitute ""))
      [(net.cgrand.enlive-html/attr= :data-profile "prod")]
      (if (rssminer.config/in-prod?) identity
          (net.cgrand.enlive-html/substitute ""))))

(defmacro snippet [source selector args & forms]
  (let [with-profile (concat profile-specific forms)]
    `(net.cgrand.enlive-html/snippet ~source ~selector ~args ~@with-profile)))

(defmacro template [source args & forms]
  (let [with-profile (concat profile-specific forms)]
    `(comp (fn [b#]
             {:body (filter (complement str/blank?) b#)
              :headers {"Content-Type" "text/html; charset=utf-8"}})
           (net.cgrand.enlive-html/template
            ~source ~args ~@with-profile))))

(defmacro deftemplate [name source args & forms]
  `(def ~name (template ~source ~args ~@forms)))

(defmacro defsnippet [name source selector args & forms]
  `(def ~name (snippet ~source ~selector ~args ~@forms)))

(deftemplate layout "templates/layout.html" [body]
  [:#main] (net.cgrand.enlive-html/substitute body))
