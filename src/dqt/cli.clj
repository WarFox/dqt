(ns dqt.cli
  (:require
   [clojure.tools.cli :as cli]
   [clojure.string :as string]))

(defonce cli-options
  [[nil "--fail-if-invalid" "Set to true if you want to fail the task when data is invalid"
    :default false]
   ["-t" "--table TABLE" "Table name" :default ""]
   ["-d" "--datasource DATASOURCE" "Configuration and credentials for datasource" :default ""]
   ["-h" "--help"]])

(defn usage [options-summary]
  (->> ["dqt - Data Quality Tool"
        "version: package-version-number"
        ""
        "Usage: dqt [options] action"
        ""
        "Options:"
        options-summary
        ""
        "Actions:"
        "  run      Run the job"
        ""
        "Please refer to the manual page for more information."]
       (string/join \newline)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))

(defn parse
  [args]
  (cli/parse-opts args cli-options))

(defn validate-options
  "Validate command line arguments. Either return a map indicating the program
  should exit (with an error message, and optional ok status), or a map
  indicating the action the program should take and the options provided."
  [opts]
  (let [{:keys [options arguments errors summary]} opts]
    (cond
      (:help options) ; help => exit OK with usage summary
      {:exit-message (usage summary) :ok? true}
      errors ; errors => exit with description of errors
      {:exit-message (error-msg errors)}
      ;; custom validation on arguments
      (#{"start" "stop" "status" "run"} (first arguments))
      {:action (first arguments) :options options}
      :else ; failed custom validation => exit with usage summary
      {:exit-message (usage summary)})))

(defn exit [status msg]
  (println msg)
  (System/exit status))

(defn pass-or-exit
  [{:keys [exit-message ok?] :as opts}]
  (if exit-message
    (exit (if ok? 0 1) exit-message)
    opts))

(defn init
  [args]
  (-> args
      parse
      validate-options
      pass-or-exit))
