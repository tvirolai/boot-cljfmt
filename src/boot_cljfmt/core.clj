(ns boot-cljfmt.core
  {:boot/export-tasks true}
  (:require [clojure.java.io :as io]
            [cljfmt.core :as cljfmt]
            [boot.core :as bc]
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

(defn exit-now! [code]
  (System/exit code))

(defn check-dir
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
                 (exit-now! 1)))))

(defn fix-dir
  [dir]
  (let [checks (map check-file (get-project-filenames dir))
        errored-checks (filter #(true? (:errored? %)) checks)]
    (try
      (doseq [file (map :file errored-checks)]
        (try
          (let [original (slurp file)
                revised (cljfmt/reformat-string original)]
            (when (not= original revised)
              (println (str "Reformatting " file))
              (spit file revised)))
          (catch Exception e
            (println (str "Failed to format file: " file))
            (println (.getMessage e))))))))

(bc/deftask check
  "Run a check for files, folders or the whole project, print the results."
  [f folder FOLDER str "The file or folder to check"]
  (check-dir folder))

(bc/deftask fix
  "Run a fix for files, folders or the whole project."
  [f folder FOLDER str "The file or folder to fix"]
  (fix-dir folder))
