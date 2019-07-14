(ns boot-cljfmt.core-test
  (:require [clojure.test :refer :all]
            [boot-cljfmt.core :refer :all]
            [clojure.java.io :as io]
            [clojure.string :as string]))

(def test-file-name
  "./mock_namespace.clj")

(def test-namespace
  (string/join "\n" ["(ns incorrectly.formatted.namespace )" "( defn we-care [alot]" "(+ 1 alot))"]))

(defn create-test-file! [filename]
  (spit test-file-name test-namespace))

(defn delete-test-file! [filename]
  (when (.exists (io/file test-file-name))
    (io/delete-file test-file-name)))

(defn with-test-file [f]
  (create-test-file! test-file-name)
  (f)
  (delete-test-file! test-file-name))

(delete-test-file! test-file-name)

(use-fixtures :each with-test-file)

(deftest clj-file-detection
  (testing "It should return true for all existing files"
    (is (clj-file? (io/file "./src/boot_cljfmt/core.clj"))))
  (testing "Whether the function clj-file? returns false for non-existing files."
    (is (false? (clj-file? (io/file "autoexec.bat")))))
  (testing "Whether an error is thrown for string inputs."
    (is (thrown? Exception (clj-file? "autoexec.bat")))))

(deftest reading-clj-files
  (testing "It should return a vector of valid Clojure filenames"
    (let [res (get-project-filenames ".")]
      (is (vector? res))
      (is (every? true? (map (comp clj-file? io/file) res)))))
  (testing "Whether an empty vector is returned for invalid inputs"
    (is (empty? (get-project-filenames "lentävä kalakukko")))))

(deftest checking-files
  (testing "Whether the function reports errors in an incorrectly formatted file "
    (let [res (check-file test-file-name)]
      (is (record? res))
      (is (:errored? res))
      (is (string/includes? (:report res) (str test-file-name " has incorrect formatting")))))
  (testing "Whether no such error are found in correctly formatted files"
    (let [res (check-file "./build.boot")]
      (is (record? res))
      (is (false? (:errored? res)))
      (is (= "" (:report res))))))

(deftest project-level-checking
  (with-redefs [exit-now! (constantly nil)]
    (testing "Whether it prints a correct report if a non-existing filename is given"
      (is (= "File or directory does not exist or does not contain Clojure files.\n"
             (with-out-str (check-dir! "Bama lama")))))
    (testing "That the function reports errors when an invalid file is added"
      (is (true? (string/includes?
                  (with-out-str (check-dir! "."))
                  "1 file(s) formatted incorrectly"))))
    (testing "That the errors go away when the erroring file is deleted"
      (do
        (delete-test-file! test-file-name)
        (is (= "All files formatted correctly.\n" (with-out-str (check-dir! "."))))))))
