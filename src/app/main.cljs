(ns app.main
  (:require ["@erebos/swarm-browser" :as SwarmClient]
            [reagent.core :as reagent]
            [clojure.edn :as edn]))

(def bzz (SwarmClient/BzzAPI. #js {:url "http://localhost:8500"}))

(def content (reagent/atom nil))
(def contenthash (reagent/atom "ad449d6934bc7481533f456b4eb59522cd514e16d7fee1c0c07ec06452e46951"))

(def messages (reagent/atom []))

(defn download [file]
  (-> (.download bzz file)
      (.then (fn [resp]
               (-> (.text resp)
                   (.then (fn [resp-text]
                            (let [message (edn/read-string resp-text)]
                              (swap! messages conj message)
                              (doseq [ancestor (:ancestors message)]
                                (download ancestor))))))))))

(defn upload [content]
  (.then (.uploadFile bzz
                      (pr-str {:ancestors (if @contenthash
                                            [@contenthash]
                                            [])
                               :content content})
                      #js {:contentType "text"})
         #(do (reset! contenthash %)
              (when (< content 1000))(upload (inc content)))))

(defn message-view [content]
  [:div content])

(defn home-page
  []
  [:div
   (for [{:keys [content]} @messages]
     ^{:key content}[message-view content])])

(defn main! []
(reagent/render [home-page]
                (.getElementById js/document "app")))
