(ns remembrall-bot.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.json :refer [wrap-json-body]]
            [clojure.data.json :as json]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.adapter.jetty :as ring]
            [clojure.core.match :refer [match]]
            [environ.core :refer [env]]
            [clj-http.client :as client]))

(def access-token (env :access-token))
(def verify-token (env :verify-token))
(def messages-endpoint "https://graph.facebook.com/v2.6/me/messages")

(defn send-message [recipient message]
  (client/post messages-endpoint
               {:query-params {"access_token" access-token}
                :body (json/write-str {:recipient {:id recipient}
                                       :message message})
                :content-type :json
                :throw-exceptions false}))

(defn send-text [recipient text]
  (send-message recipient {:text text}))

(defn respond-message [{message :message sender :sender}]
  (send-text (:id sender) (:text message)))

(defroutes app-routes
  (GET "/" [] "Hello World")

  (GET "/webhook" {params :params}
    (if
        (and
         (= (get params "hub.mode") "subscribe")
         (= (get params "hub.verify_token") verify-token))
      {:status 200 :body (get params "hub.challenge")}
      {:status 403 :body "Failed validation."}))

  (POST "/webhook" {body :body}
    (match [body]
           [{:message _}] (str (respond-message body))
           :else {:status 400 :body "Invalid body"}))

  (route/not-found "Not Found"))

(def app
  (-> app-routes
      (handler/api)
      (wrap-defaults api-defaults)
      (wrap-json-body {:keywords? true :bigdecimals? true})))

(defn start [port]
  (ring/run-jetty app {:port port
                               :join? false}))

(defn -main []
  (let [port (Integer. (or (System/getenv "PORT") "3000"))]
    (start port)))
