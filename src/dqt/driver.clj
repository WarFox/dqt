(ns dqt.driver
  (:import
   (java.sql
    Driver
    DriverManager)))

;; (defn register
;;   [driver]

;;   )
;; Class drvClass = driverLoader.loadClass(driverClassName);
;; Driver driver = drvClass.newInstance();
;; (DriverManager/registerDriver)

(comment
  ;; for (Enumeration<Driver> e = DriverManager.getDrivers(); e.hasMoreElements();) {
  ;;                          Driver driver = e.nextElement();
  ;;                          drivers.add(driver.getClass().getName());
                         ;; }
  )
(defn- classes
  [driver]
  (-> driver .getClass .getName))

(defn drivers
  []
  (map classes (enumeration-seq (DriverManager/getDrivers))))
