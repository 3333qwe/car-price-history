(ns auto.test-runner
  (:require
   [doo.runner :refer-macros [doo-tests]]
   [auto.core-test]))

(enable-console-print!)

(doo-tests 'auto.core-test)
