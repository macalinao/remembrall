(ns remembrall-bot.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.adapter.jetty :as ring]))

(def verify-token "orworse_exp3lled!")

(defroutes app-routes
  (GET "/" [] "Hello World")
  (GET "/webhook" {params :params}
    (if
        (and
         (= (get params "hub.mode") "subscribe")
         (= (get params "hub.verify_token") verify-token))
      {:status 200 :body (get params "hub.challenge")}
      {:status 403 :body "Failed validation."}))
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      (handler/api)
      (wrap-defaults api-defaults)))

(defn start [port]
  (ring/run-jetty app {:port port
                               :join? false}))

(defn -main []
  (let [port (Integer. (or (System/getenv "PORT") "3000"))]
    (start port)))
