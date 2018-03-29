(ns boot-cljfmt.core
  (:require [clojure.java.io :as io]
            [cljfmt.core :as cljfmt]
            [leiningen.cljfmt.diff :as diff]))

(defn clj-file?
  "Implementation from https://gist.github.com/bartojs/83a096ecb1221885ddd1"
  [f]
  (and (.exists f) (.isFile f) (not (.isHidden f))
       (contains? #{"clj" "cljs" "cljc" "cljx" "boot"}
                  (last (.split (.toLowerCase (.getName f)) "\\.")))))

(defn get-project-filenames
  "Takes a directory (as string),
  returns a vector containing names of all Clojure files inside it."
  [dir]
  (->> dir
       io/file
       file-seq
       (filter clj-file?)
       (mapv str)))

(defrecord Checkerror [errored? file report])

(defn check-file
  "Takes a filename (string), returns a record containing properties :errored? (boolean), file (string), report (string)."
  [file]
  (let [original (slurp file)
        formatted (cljfmt/reformat-string original)]
    (if (not= original formatted)
      (let [diff (diff/unified-diff file original formatted)
            colordiff (diff/colorize-diff diff)]
        (Checkerror. true file (str file " has incorrect formatting\n" colordiff)))
      (Checkerror. false file ""))))

(defn check
  [dir]
  (let [checks (map check-file (get-project-filenames dir))
        errored-checks (filter #(true? (:errored? %)) checks)]
    (cond
      (zero? (count checks)) (println "File or directory does not exist or does not contain Clojure files.")
      (zero? (count errored-checks)) (println "All files formatted correctly.")
      :default (do
                 (doseq [err errored-checks]
                   (println (:report err)))
                 (println (str (count errored-checks) " file(s) formatted incorrectly"))
                 (System/exit 1)))))
