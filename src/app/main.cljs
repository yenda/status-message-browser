(ns app.main
  (:require ["@erebos/swarm-browser" :as SwarmClient]
            [reagent.core :as reagent]))

(def bzz (SwarmClient/BzzAPI. #js {:url "http://localhost:8500"}))

(def content (reagent/atom "hello"))
(def contenthash (reagent/atom nil))

(defn download [file]
  (-> (.download bzz file)
      (.then (fn [resp]
               (-> (.text resp)
                   (.then (fn [resp-text]
                            (reset! content resp-text))))))))

(defn upload [content]
  (.then (.uploadFile bzz content #js {:contentType "text"})
         #(reset! contenthash %)))

(defn home-page
  [content]
  [:div @content])

(defn main! []
  (download "theswarm.eth")
  (reagent/render [home-page content]
                  (.getElementById js/document "app")))
