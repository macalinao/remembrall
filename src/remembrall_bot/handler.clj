(ns remembrall-bot.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.adapter.jetty :as ring]))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (POST "/webhook" [] "Hello my little friend")
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes api-defaults))

(defn start [port]
  (ring/run-jetty app {:port port
                               :join? false}))

(defn -main []
  (let [port (Integer. (or (System/getenv "PORT") "3000"))]
    (start port)))
