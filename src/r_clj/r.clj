(ns r-clj.r
  (:import (org.ddahl.jvmr RInJava))
  (:require [opencpu-clj.ocpu :as ocpu :refer[call-R-function]]
            [com.evocomputing.rincanter :as rincanter :refer [r-eval]])
  )



(defn make-call-string [package-name function-name parameters]

  (format "%s::%s(%s)"
          package-name
          function-name
          (clojure.string/join "," (map #(format "%s=%s" (name (first %)) (second %)) parameters))
  ))




;a protocoll on how to intercat with R from Clojure

(defprotocol R
  (R-call-function [_ package-name function-name parameters execution-context ]))



;implementation of protocol R via opencpu-clj
(defrecord ocpu []
    R
  (R-call-function [_ package-name function-name parameters execution-context ]
    (vec (ocpu/call-R-function (:server execution-context) package-name function-name parameters :json))))



;implementation of protocol R via rincanter
(defrecord rincanter []
           R
  (R-call-function [_ package-name function-name parameters execution-context ]
    (let [call-string (make-call-string package-name function-name parameters)]
      ;(println "rincanter: " call-string)
      (r-eval call-string))))



;implementation of protocol R via jvmr
(defrecord jvmr []
  R
  (R-call-function [_ package-name function-name parameters execution-context ]
    (let [JVMR (RInJava.)
          call-string (make-call-string package-name function-name parameters)]
      (do
        ;(println "jvmr: " call-string)
        (vec (.apply JVMR call-string))))))



 ; define a function, which can be called by passing an implementor of the R protocoll
(defn r-seq [r  from to ctx]
  (R-call-function r  "base" "seq" {:from from :to to} ctx))


;; example usage of the "seq" function
;; caller needs to decide on each call, which implemntor to use and give the calling context
;; maybe set this globaly ??

(defn test-call []
  (let [R-ocpu (ocpu.)
        R-rincanter (rincanter.)
        R-jvmr (jvmr.)
        ]
    (do
      (println "ocpu (public): " (r-seq  R-ocpu 1 10 {:server "http://public.opencpu.org"}))
      ;(println "ocpu (local):  " (r-seq R-ocpu 1 10 {:server "http://localhost:3861"}))
      (println "rincanter:     " (r-seq R-rincanter 1 10 {}))
      (println "jvmr:          " (r-seq R-jvmr 1 10 {})))))


(def R-ocpu (ocpu.))
(def R-rincanter (rincanter.))
(def R-jvmr (jvmr.))
