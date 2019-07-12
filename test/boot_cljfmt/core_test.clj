(ns boot-cljfmt.core-test
  (:require [clojure.test :refer :all]
            [boot-cljfmt.core :refer :all]
            [clojure.java.io :as io]
            [clojure.string :as s]))

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

(def mockfilename
  "./resources/mock_namespace.clj")

(defn create-mockfile! []
  (->> "./resources/mock_namespace.txt" slurp (spit mockfilename)))

(defn delete-mockfile! []
  (io/delete-file mockfilename))

#_(deftest test_check-file
  (testing "Whether the function reports errors in an incorrectly formatted file "
    (let [_ (create-mockfile!)
          res (check-file mockfilename)
          _ (delete-mockfile!)]
      (is (record? res))
      (is (:errored? res))
      (is (s/includes?
           (:report res)
           (str mockfilename " has incorrect formatting")))))
  (testing "Whether no such error are found in correctly formatted files"
    (let [res (check-file "./build.boot")]
      (is (record? res))
      (is (false? (:errored? res)))
      (is (= "" (:report res))))))

#_(deftest test_check
  (testing "Whether it prints a correct report if a non-existing filename is given"
    (is (= "File or directory does not exist or does not contain Clojure files.\n"
           (with-out-str (check-dir "Bama lama")))))
  (testing "Whether a correct report is printed if no errors are found"
    (is (= "All files formatted correctly.\n" (with-out-str (check-dir ".")))))
  (testing "That the function reports errors when an invalid file is added"
      (let [_ (create-mockfile!)]
        (is (true? (s/includes?
                    (with-out-str (check-dir "."))
                    ("1 file(s) formatted incorrectly"))))))
  (testing "That the errors go away when the erroring file is deleted"
      (let [_ (delete-mockfile!)]
        (is (= "All files formatted correctly.\n" (with-out-str (check-dir ".")))))))
