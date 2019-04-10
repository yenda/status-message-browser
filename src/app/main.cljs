(ns app.main
  (:require ["@erebos/swarm-browser" :as SwarmClient]
            ["web3" :as Web3]
            ["@erebos/keccak256" :refer [pubKeyToAddress]]
            ["@erebos/secp256k1" :refer [createKeyPair sign]]
            [reagent.core :as reagent]
            [clojure.edn :as edn]))

(def web3 (Web3. "ws://127.0.0.1:8546"))

(def keypair (createKeyPair))
(def user (pubKeyToAddress (-> keypair .getPublic .encode)))

(defn sign-bytes [bytes]
  (js/Promise. (fn [bytes]
                 (sign bytes (.getPrivate keypair)))))

(def bzz (SwarmClient/BzzAPI. #js {:url "http://localhost:8500"
                                   :signBytes sign-bytes}))

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

(def feedhash (atom nil))


#_(defn upload-feed-value [feedhash data]
    (.then (.uploadFeedValue bzz
                             feedhash
                             #js {"index.htlm" {:contentType "text/html"
                                                :data data}}
                             #js {:defaultPath "index.html"})
           (fn [res]
             (.log js/console res))))

#_(defn update-feed-value [feedhash data]
    (.then (.updateFeedValue bzz
                             feedhash
                             data)
           (fn [res]
             (.log js/console res))))

(defn upload-feed-value [feedhash data]
  (.then (.uploadFeedValue bzz
                           feedhash
                           data)
         (fn [res]
           (.log js/console res))))

(defn create-feed [name]
  (.then (.createFeedManifest bzz
                              #js {:name name
                                   :user user})
         (fn [hash]
           (reset! feedhash hash)
           (println "feedhash: " (.getFeedURL bzz hash))
           (upload-feed-value hash "potatoe potatoe"))))

(println user)
(create-feed "hello")

#_(.then (-> web3 .-eth .-personal (.sign "hello" "0x63410f8acabd08648c9230be91f87a24e7871616" "password")) println)
