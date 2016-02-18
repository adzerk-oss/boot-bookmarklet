(set-env!
  :source-paths #{"src"}
  :dependencies '[[org.clojure/clojure       "1.7.0"]
                  [org.clojure/clojurescript "1.7.228"]
                  [adzerk/bootlaces          "0.1.13" :scope "test"]
                  [adzerk/boot-cljs          "1.7.228-1"]
                  [com.cemerick/url          "0.1.1"]])

(require '[adzerk.bootlaces :refer :all])

(def +version+ "0.1.1")

(task-options!
  pom {:project     'adzerk/boot-bookmarklet
       :version     +version+
       :description "A Boot task for generating bookmarklets."
       :url         "https://github.com/adzerk-oss/boot-bookmarklet"
       :scm         {:url "https://github.com/adzerk-oss/boot-bookmarklet"}
       :license     {"EPL" "http://www.eclipse.org/legal/epl-v10.html"}})

(bootlaces! +version+)

(deftask deploy
  "Builds uberjar, installs it to local Maven repo, and deploys it to Clojars."
  []
  (comp (build-jar) (push-release)))
