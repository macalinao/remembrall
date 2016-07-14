(ns remembrall-bot.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.json :refer [wrap-json-body]]
            [remembrall-bot.messenger :refer :all]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.adapter.jetty :as ring]
            [clojure.core.match :refer [match]]
            [environ.core :refer [env]]))

(def access-token (env :access-token))
(def verify-token (env :verify-token))

(defn respond-message [{:keys [message sender recipient timestamp]}]
  (send-text {:access-token access-token
              :sender sender
              :text message}))


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
    (match body
           [{:message _}] (respond-message body)
           :else {:status 400 :body "Invalid "}))

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
