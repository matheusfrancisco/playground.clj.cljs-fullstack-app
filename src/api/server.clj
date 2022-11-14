(ns api.server
  (:require [muuntaja.core :as m]
            [org.httpkit.server :refer [run-server]]
            [schema.core :as s]
            [clojure.java.io :as io]
            [reitit.coercion.schema]
            [reitit.ring :as ring]
            [reitit.ring.coercion :refer [coerce-exceptions-middleware
                                          coerce-request-middleware
                                          coerce-response-middleware]]
            [reitit.ring.middleware.exception :refer [exception-middleware]]
            [reitit.ring.middleware.muuntaja :refer [format-negotiate-middleware
                                                     format-request-middleware
                                                     format-response-middleware]]
            [reitit.ring.middleware.parameters :refer [parameters-middleware]]
            [ring.middleware.cors :refer [wrap-cors]]))

(defonce server (atom nil))

(defn signin
  [{:keys [parameters]}]
  (let [data (:body parameters) ]
    (prn data)
    {:status 200
     :body "token-login"}))

;; routes
(def ping-routes
  ["/ping" {:name :ping
            :get (fn [_]
                   {:status 200
                    :body {:ping "pong"}})}])

(defn index []
  (slurp (io/resource "public/index.html")))

(def routes
  ["/sign-in"
   ["" {:post {:parameters {:body {:email s/Str}}
               :handler signin}}] ])

(def app
  (ring/ring-handler
    (ring/router
      [["/api"
        ["" {:handler (fn [req] {:body (index) :status 200})}]
        ["assets/*" (ring/create-resource-handler {:root "public/assets"})]
        ping-routes
        routes]]
      {:data {:coercion reitit.coercion.schema/coercion
              :muuntaja m/instance
              :middleware [[wrap-cors
                            :access-control-allow-origin [#"http://localhost:8000"]
                            :access-control-allow-methods [:get :post :put :delete]]
                           parameters-middleware
                           format-negotiate-middleware
                           format-response-middleware
                           exception-middleware
                           format-request-middleware
                           coerce-exceptions-middleware
                           coerce-request-middleware
                           coerce-response-middleware]}})
    (ring/routes
      (ring/redirect-trailing-slash-handler)
      (ring/create-default-handler
        {:not-found (constantly {:status 404 :body "Route not found"})}))))

(defn stop-server []
  (when-not (nil? @server)
    (@server :timeout 100)
    (reset! server nil)))

(defn -main []
  (println "Server started")
  (reset! server (run-server app {:port 3001})))

(defn restart-server []
  (stop-server)
  (-main))
