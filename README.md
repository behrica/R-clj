R-clj
=====

This poject has the goal to develop a common protocoll which allows to use R from Clojure.

The initial focus should be on defining an abstrcat Clojure protocol, how to call a function existing in any R package.

The goal is to be able to access the different approaches, on how to use R from Clojure, such as:

- jvmr : https://github.com/daslu/jvmr
- opencpu-clj : https://github.com/behrica/opencpu-clj

These two differ radically in their approach, but it should be possible to define a common protocol to hide their differences as implementation details.


### Initial proposal

The protocol should have the following functions:

#### Call a function in an R package

````clojure
(call-R [execution-context package-name function-name parameters]
````


#### Representation of data types

The data types of the paramters of R functions ar represented like this:

