(ns api.server
  (:require [ring.adapter.jetty :as ring-jetty]
            [reitit.ring :as ring]
            [ring.util.response :as r]
            [muuntaja.core :as m]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [clojure.java.io :as io])
  (:gen-class))

;; https://github.com/alndvz/vid4

(defn index []
  (slurp (io/resource "public/index.html")))

(defn login [req]
  (prn "this is the req")
  (prn req)
  (prn "end req")
  (r/response 
    {:body {:ok true
            :username "token-login"}}))

(def app
  (ring/ring-handler
   (ring/router
    ["/"
     ["api/"
      ["login" {:post login}]]
     ["assets/*" (ring/create-resource-handler {:root "public/assets"})]
     ["" {:handler (fn [req] {:body (index) :status 200})}]]
    {:data {:muuntaja m/instance
            :middleware [muuntaja/format-middleware]}})))

(defn start []
  (ring-jetty/run-jetty #'app {:port  3001
                               :join? false}))

(defn -main []
  (println "starting app!")
  (start))

(comment
  (def server (start))
  (.stop server)
  )
