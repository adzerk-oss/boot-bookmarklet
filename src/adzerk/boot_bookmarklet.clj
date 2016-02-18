(ns adzerk.boot-bookmarklet
  {:boot/export-tasks true}
  (:require [adzerk.boot-cljs :refer (cljs)]
            [boot.core        :as    core]
            [boot.util        :as    util]
            [clojure.java.io  :as    io]
            [clojure.string   :as    str]
            [cemerick.url     :refer (url-encode)]))

(defn cljs-files-by-id
  "(stolen from boot-cljs, modified 'main-files')"
  [fileset ids]
  (let [re-pat #(re-pattern (str "\\Q" % "\\E\\.cljs$"))]
    (->> fileset
         core/input-files
         (core/by-re (map re-pat ids))
         (sort-by :path))))

(defn path->js
  "Given a path to a cljs namespace source file, returns the corresponding
   Google Closure namespace name for goog.provide() or goog.require().

   (stolen from boot-cljs)"
  [path]
  (-> path
      (str/replace #"\.clj([s|c])?$" "")
      (str/replace #"[/\\]" ".")))

(defn- path->ns
  "Given a path to a cljs namespace source file, returns the corresponding
   cljs namespace name.

   (stolen from boot-cljs)"
  [path]
  (-> (path->js path) (str/replace #"_" "-")))

(defn- all-cljs-src-files
  [fileset]
  (->> fileset core/input-files (core/by-ext ["cljs"])))

(defn- cljs-edn-for
  [cljs-file]
  {:require          (mapv (comp symbol path->ns core/tmp-path) [cljs-file])
   :compiler-options {:optimizations :advanced}})

(core/deftask ^:private generate-cljs-edn
  [i ids IDS #{str} "The cljs namespaces for which to generate .cljs.edn files."]
  (let [tmp-main (core/tmp-dir!)]
    (core/with-pre-wrap fileset
      (core/empty-dir! tmp-main)
      (let [cljs-files (if ids
                         (cljs-files-by-id fileset ids)
                         (->> fileset
                              core/input-files
                              (core/by-ext ["cljs"])
                              (sort-by :path)))]
        (doseq [cljs cljs-files
                :let [out-main (str (.getName (core/tmp-file cljs)) ".edn")
                      out-file (io/file tmp-main out-main)]]
          (util/info "Writing %s...\n" (.getName out-file))
          (doto out-file
            (io/make-parents)
            (spit (cljs-edn-for cljs))))
        (-> fileset (core/add-source tmp-main) core/commit!)))))

(defn bookmarklet-link
  [js-file]
  (let [js-code (url-encode (slurp (core/tmp-file js-file)))]
    (format "<div>\n<a href=\"javascript: %s\">\n<h1>%s</h1>\n</a>\n</div>\n"
            js-code
            (.getName (core/tmp-file js-file)))))

(core/deftask ^:private generate-html
  []
  (let [tmp-main (core/tmp-dir!)]
    (core/with-pre-wrap fileset
      (core/empty-dir! tmp-main)
      (let [cljs-edn-paths (->> fileset
                                core/input-files
                                (core/by-ext ["cljs.edn"])
                                (sort-by :path)
                                (map (comp #(str/replace % #"\.cljs\.edn" ".js")
                                           core/tmp-path)))
            js-files   (->> fileset
                            core/output-files
                            (filter (comp (set cljs-edn-paths) core/tmp-path))
                            (sort-by :path))
            html-file (io/file tmp-main "bookmarklets.html")]
        (util/info "Writing %s...\n" (.getName html-file))
        (doto html-file
          (io/make-parents)
          (spit "<html>\n<head>\n<title>Bookmarklets</title>\n</head>\n<body>\n"))
        (doseq [js js-files]
          (spit html-file (bookmarklet-link js) :append true))
        (spit html-file "</body>\n</html>" :append true)
        (-> fileset (core/add-resource tmp-main) core/commit!)))))

(core/deftask bookmarklet
  [i ids IDS #{str} "The cljs namespaces to turn into bookmarklets."]
  (comp
    (generate-cljs-edn :ids ids)
    (cljs)
    (generate-html)))

