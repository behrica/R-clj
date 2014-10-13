(ns r-clj.r-tests
  (:require [midje.sweet :refer [fact facts tabular]]
            [r-clj.r :refer :all]

            ))



(tabular "can call seq(1,10)"
       (fact (R-call-function ?r "base" "seq" {:from 1 :to 10} {:server "http://public.opencpu.org"}) => [1 2 3 4 5 6 7 8 9 10])
?r
R-ocpu
R-jvmr
R-rincanter)

(tabular "can call seq(1,10,by=0.1)"
         (fact (R-call-function ?r "base" "seq" {:from 1 :to 3 :by 0.5} {:server "http://public.opencpu.org"}) => ?expected)
         ?r           ?expected
         R-ocpu       [1 1.5 2 2.5 3]
         R-jvmr       [1.0 1.5 2.0 2.5 3.0]
         R-rincanter  [1.0 1.5 2.0 2.5 3.0]
         )