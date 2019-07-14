(ns boot-cljfmt.core
  {:boot/export-tasks true}
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [cljfmt.core :as cljfmt]
            [cljfmt.diff :as diff]
            [schema.core :as s]
            [boot.core :as bc]))

(def File #(.isFile %))

(s/defn clj-file? :- s/Bool
  [f :- File]
  (let [extension (-> (.getName f) string/lower-case (string/split #"\.") last)]
    (and (.exists f) (.isFile f) (not (.isHidden f))
         (contains? #{"clj" "cljs" "cljc" "cljx" "boot"} extension))))

(s/defn get-project-filenames :- [File]
  [dir :- s/Str]
  (->> dir
       io/file
       file-seq
       (filter clj-file?)
       (mapv str)))

(s/defrecord Checkerror
             [errored? :- s/Bool
              file     :- File
              report   :- s/Str])

(s/defrecord Checks
             [checks   :- [Checkerror]
              errored-checks :- [Checkerror]])

(s/defn check-file :- Checkerror
  [file :- File]
  (let [original (slurp file)
        formatted (cljfmt/reformat-string original)]
    (if (= original formatted)
      (->Checkerror false file "")
      (let [diff (diff/unified-diff file original formatted)]
        (->Checkerror true file (str file " has incorrect formatting\n" (diff/colorize-diff diff)))))))

(s/defn check-dir :- Checks
  [dir :- s/Str]
  (let [checks (->> dir get-project-filenames (map check-file))]
    (->Checks checks (filter :errored? checks))))

(s/defn check-dir! :- nil
  [dir :- s/Str]
  (let [{:keys [checks errored-checks]} (check-dir dir)]
    (cond
      (zero? (count checks)) (println "File or directory does not exist or does not contain Clojure files.")
      (zero? (count errored-checks)) (println "All files formatted correctly.")
      :default (do
                 (doseq [err errored-checks]
                   (println (:report err)))
                 (println (str (count errored-checks) " file(s) formatted incorrectly"))
                 (System/exit 1)))))

(s/defn fix-dir! :- nil
  [dir :- s/Str]
  (let [{:keys [checks errored-checks]} (check-dir dir)]
    (doseq [file (map :file errored-checks)
            :let [original (slurp file)
                  revised (cljfmt/reformat-string original)]
            :when (not= original revised)]
      (do
        (println (str "Reformatting " file))
        (spit file revised)))))

(bc/deftask check
  "Run a check for files, folders or the whole project, print the results."
  [f folder FOLDER str "The file or folder to check"]
  (check-dir! folder))

(bc/deftask fix
  "Run a fix for files, folders or the whole project."
  [f folder FOLDER str "The file or folder to fix"]
  (fix-dir! folder))
