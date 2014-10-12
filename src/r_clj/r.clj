(ns r-clj.r
  (:import (org.ddahl.jvmr RInJava))
  (:require [opencpu-clj.ocpu :as ocpu :refer[call-R-function]]
            [com.evocomputing.rincanter :as rincanter :refer [r-eval]])
  )



(defn make-call-string [package-name function-name parameters]
  (format "%s::%s(%s=%s,%s=%s)"
          package-name
          function-name
          (name (first (keys parameters)))
          (first (vals parameters))
          (name (second (keys parameters)))
          (second (vals parameters)))

  )





(defprotocol R
  (R-call-function [_ execution-context package-name function-name parameters]))

;implementation of protocol R via opencpu-clj
(defrecord ocpu []
    R
  (R-call-function [_ execution-context package-name function-name parameters]
    (vec (ocpu/call-R-function (:server execution-context) package-name function-name parameters :json))))



;implementation of protocol R via rincanter
(defrecord rincanter []
           R
  (R-call-function [_ execution-context package-name function-name parameters]
    (r-eval (make-call-string package-name function-name parameters))))


(defrecord jvmr []
  R
  (R-call-function [_ execution-context package-name function-name parameters]
    (let [JVMR (RInJava.)]
      (do
        (.eval JVMR (format "result <- %s" (make-call-string package-name function-name parameters)))
        (vec (.toVectorInt JVMR "result"))))))



      ;; define a function, which can be called by passing an implementor of the R protocoll
(defn r-seq [ctx from to]
  (R-call-function (:r ctx) ctx "base" "seq" {:from from :to to}))


;; example usage of the "seq" function
;; caller needs to decide on each call, which implemntor to use and give the calling context
;; maybe set this globaly ??

(defn test-call []
  (let [R-ocpu (ocpu.)
        R-rincanter (rincanter.)
        R-jvmr (jvmr.)
        ]
    (do
      (println "ocpu (public): " (r-seq {:r R-ocpu :server "http://public.opencpu.org"} 1 10))
      (println "ocpu (local):  " (r-seq {:r R-ocpu :server "http://localhost:6124"} 1 10))
      (println "rincanter:     " (r-seq {:r R-rincanter} 1 10)))
      (println "jvmr:          " (r-seq {:r R-jvmr} 1 10))))
