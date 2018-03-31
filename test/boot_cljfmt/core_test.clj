(ns boot-cljfmt.core-test
  (:require [clojure.test :refer :all]
            [boot-cljfmt.core :refer :all]
            [clojure.java.io :as io]
            [clojure.string :as string]))

(deftest test_clj-file?
  (testing "It should return true for all existing files"
    (is (true? (clj-file? (io/file "./src/boot_cljfmt/core.clj")))))
  (testing "Whether the function clj-file? returns false for non-existing files."
    (is (false? (clj-file? (io/file "autoexec.bat")))))
  (testing "Whether an error is thrown for string inputs."
    (is (thrown? Exception (clj-file? "autoexec.bat")))))

(deftest test_get-project-filenames
  (testing "It should return a vector of valid Clojure filenames"
    (let [res (get-project-filenames ".")]
      (is (vector? res))
      (is (every? true? (map (comp clj-file? io/file) res)))))
  (testing "Whether an empty vector is returned for invalid inputs"
    (is (empty? (get-project-filenames "lentävä kalakukko")))))

(def mockfilename
  "./resources/mock_namespace.clj")

(defn create-mockfile! []
  (->> "./resources/mock_namespace.txt" slurp (spit mockfilename)))

(defn delete-mockfile! []
  (io/delete-file mockfilename))

(deftest test_check-file
  (testing "Whether the function reports errors in an incorrectly formatted file "
    (let [_ (create-mockfile!)
          res (check-file mockfilename)
          _ (delete-mockfile!)]
      (is (record? res))
      (is (true? (:errored? res)))
      (is (string/includes?
           (:report res)
           (str mockfilename " has incorrect formatting")))))
  (testing "Whether no such error are found in correctly formatted files"
    (let [res (check-file "./build.boot")]
      (is (record? res))
      (is (false? (:errored? res)))
      (is (= "" (:report res))))))

(deftest test_check
  (testing "Whether it prints a correct report if a non-existing filename is given"
    (is (= "File or directory does not exist or does not contain Clojure files.\n"
           (with-out-str (check "Bama lama")))))
  #_(testing "Whether a correct report is printed if no errors are found"
      (is (= "All files formatted correctly.\n" (with-out-str (check "."))))))

(deftest test_fix
  (testing "That no output is printed when there's nothing to fix"
    (is (= "" (with-out-str (fix ".")))))
  (testing "That the fix function formats an invalid file correctly"
    (let [_ (create-mockfile!)]
      (is (= (str "Reformatting " mockfilename "\n")
             (with-out-str (fix "."))))))
  (testing "That the reformatted file differs from the invalid original"
    (is (not= (slurp mockfilename) (slurp "./resources/mock_namespace.txt"))))
  (delete-mockfile!))
