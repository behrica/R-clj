A complete example of such a protocol is this:

````clojure
(ns r-clj.r
  (:require [opencpu-clj.ocpu :as ocpu :refer[call-R-function] ])
  )

(defprotocol R
  (call-R [_ execution-context package-name function-name parameters]))

;implementation of protocol R via opencpu-clj
(defrecord ocpu []
    R
  (call-R [_ execution-context package-name function-name parameters]
    (ocpu/call-R-function (:server execution-context) package-name function-name parameters :json)))



;implementation of protocol R via jvmr
(defrecord jvmr []
           R
  (call-R [_ execution-context package-name function-name parameters]
    (println "call to jvmr of function: " function-name)))




;; define a function, which can be called by passing an implementor of the R protocoll
(defn r-seq [ctx from to]
  (call-R (:r ctx) ctx "base" "seq" {:from from :to to}))


;; example usage of the "seq" function
;; caller needs to decide on each call, which implemntor to use and give the calling context
;; maybe set this globaly ??

(defn test-call []
  (let [R-ocpu (ocpu.)
        R-jvmr (jvmr.)]
    (do
      (println (r-seq {:r R-ocpu :server "http://public.opencpu.org"} 1 10))
      (println (r-seq {:r R-jvmr :dont-know "something"} 1 10)))))
````
