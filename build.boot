(def project 'boot-cljfmt)

(set-env! :resource-paths #{"resources" "src"}
          :source-paths   #{"test"}
          :dependencies   '[[org.clojure/clojure "1.10.0"]
                            [cljfmt "0.6.4"]
                            [boot/core "RELEASE" :scope "test"]
                            [adzerk/bootlaces "0.1.13" :scope "test"]
                            [adzerk/boot-test "RELEASE" :scope "test"]
                            [metosin/bat-test "0.4.0" :scope "test"]]
          :repositories
          (partial map (fn [[k v]] [k (cond-> v (#{"clojars"} k) (assoc :username (System/getenv "CLOJARS_USER")
                                                                        :password (System/getenv "CLOJARS_PASS")))])))

(require '[boot-cljfmt.core :refer [check fix]]
         '[metosin.bat-test :refer (bat-test)]
         '[adzerk.bootlaces :refer :all]) ; Redefine a variation of this task here

(def version "0.1.2")
(bootlaces! version)

(task-options!
 pom {:project     project
      :version     version
      :description "A Boot port of lein-cljfmt"
      :url         "http://github.com/siili-core/boot-cljfmt"
      :scm         {:url "https://github.com/siili-core/boot-cljfmt"}
      :license     {"Eclipse Public License"
                    "http://www.eclipse.org/legal/epl-v10.html"}})

(deftask build
  "Build and install the project locally."
  []
  (comp (pom) (jar) (install)))

(deftask deploy
  []
  (comp (build)
        (push :repo
              "clojars"
              :gpg-sign
              false)))

(require '[adzerk.boot-test :refer [test]])
