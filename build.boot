(def project 'boot-cljfmt)

(set-env! :resource-paths #{"resources" "src"}
          :source-paths   #{"test"}
          :dependencies   '[[org.clojure/clojure "1.9.0"]
                            [cljfmt "0.5.7"]
                            [lein-cljfmt "0.5.7"]
                            [adzerk/bootlaces "0.1.13" :scope "test"]
                            [adzerk/boot-test "RELEASE" :scope "test"]
                            [metosin/bat-test "0.4.0" :scope "test"]]
          :repositories  (partial map (fn [[k v]] [k (cond-> v (#{"clojars"} k) (assoc :username (System/getenv "CLOJARS_USER"),
                                                                                       :password (System/getenv "CLOJARS_PASS")))])))

(require '[boot-cljfmt.core :as fmt]
         '[metosin.bat-test :refer (bat-test)]
         '[adzerk.bootlaces :refer :all]) ; Redefine a variation of this task here

(def version "0.1.0")
(bootlaces! version)

(task-options!
 pom {:project     project
      :version     version
      :description "A Boot port of lein-cljfmt"
      :url         "http://github.com/siilisolutions/boot-cljfmt"
      :scm         {:url "https://github.com/siilisolutions/boot-cljfmt"}
      :license     {"Eclipse Public License"
                    "http://www.eclipse.org/legal/epl-v10.html"}})

(deftask build
  "Build and install the project locally."
  []
  (comp (pom) (jar) (install)))

(deftask check
  "Run a check for files, folders or the whole project, print the results."
  [f folder FOLDER str "The file or folder to check"]
  (fmt/check folder))

(deftask fix
  "Run a fix for files, folders or the whole project."
  [f folder FOLDER str "The file or folder to fix"]
  (fmt/fix folder))

(deftask deploy
         []
         (comp (build)
               (push :repo
                     "clojars"
                     :gpg-sign
                     false)))

(require '[adzerk.boot-test :refer [test]])
