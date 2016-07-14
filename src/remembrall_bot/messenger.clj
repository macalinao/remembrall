(ns remembrall-bot.messenger
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]))

(def messages-endpoint "https://graph.facebook.com/v2.6/me/messages")

(defn send-message [& {:keys [access-token sender msg]}]
  (client/post messages-endpoint
               {:query-params {"access_token" access-token}
                :body (json/write-str msg)
                :content-type :json}))

(defn send-text [& {:keys [text] :as params}]
  (send-message (assoc params :msg {:text text})))
