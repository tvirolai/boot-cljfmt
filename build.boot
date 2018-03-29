(def project 'boot-cljfmt)
(def version "0.1.0-SNAPSHOT")

(set-env! :resource-paths #{"resources" "src"}
          :source-paths   #{"test"}
          :dependencies   '[[org.clojure/clojure "1.9.0"]
                            [cljfmt "0.5.7"]
                            [lein-cljfmt "0.5.7"]
                            [adzerk/boot-test "RELEASE" :scope "test"]])

(require '[boot-cljfmt.core :as fmt])

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

(deftask fmt
  "Run a check for files, folders or the whole project, print the results."
  [f file FILE str "The file or folder to check"]
  (fmt/check file))

(deftask fix
  ;; TODO
  [])

(require '[adzerk.boot-test :refer [test]])
