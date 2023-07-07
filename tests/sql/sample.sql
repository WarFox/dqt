SELECT COUNT(*)
  FROM employees
 LIMIT 10;

---
SELECT
  COUNT(*),
  COUNT(CASE WHEN NOT ("employee_id" IS NULL) THEN 1 END),
  COUNT(CASE WHEN NOT ("employee_id" IS NULL) THEN 1 END),
  MIN("employee_id"),
  MAX("employee_id"),
  AVG("employee_id"),
  SUM("employee_id"),
  VARIANCE("employee_id"),
  STDDEV("employee_id"),
  COUNT(CASE WHEN NOT ("first_name" IS NULL) THEN 1 END),
  COUNT(CASE WHEN NOT ("first_name" IS NULL) THEN 1 END),
  AVG(CASE WHEN NOT ("first_name" IS NULL) THEN LENGTH("first_name") END),
  MIN(CASE WHEN NOT ("first_name" IS NULL) THEN LENGTH("first_name") END),
  MAX(CASE WHEN NOT ("first_name" IS NULL) THEN LENGTH("first_name") END),
  COUNT(CASE WHEN NOT ("last_name" IS NULL) THEN 1 END),
  COUNT(CASE WHEN NOT ("last_name" IS NULL) THEN 1 END),
  AVG(CASE WHEN NOT ("last_name" IS NULL) THEN LENGTH("last_name") END),
  MIN(CASE WHEN NOT ("last_name" IS NULL) THEN LENGTH("last_name") END),
  MAX(CASE WHEN NOT ("last_name" IS NULL) THEN LENGTH("last_name") END),
  COUNT(CASE WHEN NOT ("email" IS NULL) THEN 1 END),
  COUNT(CASE WHEN NOT ("email" IS NULL) THEN 1 END),
  AVG(CASE WHEN NOT ("email" IS NULL) THEN LENGTH("email") END),
  MIN(CASE WHEN NOT ("email" IS NULL) THEN LENGTH("email") END),
  MAX(CASE WHEN NOT ("email" IS NULL) THEN LENGTH("email") END),
  COUNT(CASE WHEN NOT ("phone_number" IS NULL) THEN 1 END),
  COUNT(CASE WHEN NOT ("phone_number" IS NULL) THEN 1 END),
  AVG(CASE WHEN NOT ("phone_number" IS NULL) THEN LENGTH("phone_number") END),
  MIN(CASE WHEN NOT ("phone_number" IS NULL) THEN LENGTH("phone_number") END),
  MAX(CASE WHEN NOT ("phone_number" IS NULL) THEN LENGTH("phone_number") END),
  COUNT(CASE WHEN NOT ("hire_date" IS NULL) THEN 1 END),
  COUNT(CASE WHEN NOT ("hire_date" IS NULL) THEN 1 END),
  COUNT(CASE WHEN NOT ("job_id" IS NULL) THEN 1 END),
  COUNT(CASE WHEN NOT ("job_id" IS NULL) THEN 1 END),
  MIN("job_id"),
  MAX("job_id"),
  AVG("job_id"),
  SUM("job_id"),
  VARIANCE("job_id"),
  STDDEV("job_id"),
  COUNT(CASE WHEN NOT ("salary" IS NULL) THEN 1 END),
  COUNT(CASE WHEN NOT ("salary" IS NULL) THEN 1 END),
  MIN("salary"),
  MAX("salary"),
  AVG("salary"),
  SUM("salary"),
  VARIANCE("salary"),
  STDDEV("salary"),
  COUNT(CASE WHEN NOT ("manager_id" IS NULL) THEN 1 END),
  COUNT(CASE WHEN NOT ("manager_id" IS NULL) THEN 1 END),
  MIN("manager_id"),
  MAX("manager_id"),
  AVG("manager_id"),
  SUM("manager_id"),
  VARIANCE("manager_id"),
  STDDEV("manager_id"),
  COUNT(CASE WHEN NOT ("department_id" IS NULL) THEN 1 END),
  COUNT(CASE WHEN NOT ("department_id" IS NULL) THEN 1 END),
  MIN("department_id"),
  MAX("department_id"),
  AVG("department_id"),
  SUM("department_id"),
  VARIANCE("department_id"),
  STDDEV("department_id")
FROM "public"."employees";



/*
table_name: employees
  metrics:
  - row_count
  - missing_count
  - missing_percentage
  - values_count
  - values_percentage
  - invalid_count
  - invalid_percentage
  - valid_count
  - valid_percentage
  - avg_length
  - max_length
  - min_length
  - avg
  - sum
  - max
  - min
  - stddev
  - variance
  tests:
  - row_count > 0
  # columns:
  #   dob:
  #     valid_format: date
  #     tests:
  #       - invalid_percentage == 0
  #   first_name:
  #     valid_format: number_percentage
  #     tests:
  #       - invalid_percentage == 0
*/

--
WITH group_by_value AS (
  SELECT
    "manager_id" AS value,
    COUNT(*) AS frequency
  FROM "public"."employees"
  WHERE NOT ("manager_id" IS NULL)
  GROUP BY "manager_id"
)
SELECT COUNT(*),
       COUNT(CASE WHEN frequency = 1 THEN 1 END),
       SUM(frequency)
FROM group_by_value;

  -- | SQL took 0:00:00.002524
  -- | Query measurement: distinct(manager_id) = 10
  -- | Query measurement: unique_count(manager_id) = 4
  -- | Derived measurement: duplicate_count(manager_id) = 6
  -- | Derived measurement: uniqueness(manager_id) = 23.68421052631578947368421053
  -- | Executing SQL query:

WITH group_by_value AS (
  SELECT
    "department_id" AS value,
    COUNT(*) AS frequency
  FROM "public"."employees"
  WHERE NOT ("department_id" IS NULL)
  GROUP BY "department_id"
)
SELECT COUNT(*),
       COUNT(CASE WHEN frequency = 1 THEN 1 END),
       SUM(frequency)
FROM group_by_value;

  -- | SQL took 0:00:00.002179
  -- | Query measurement: distinct(department_id) = 11
  -- | Query measurement: unique_count(department_id) = 3
  -- | Derived measurement: duplicate_count(department_id) = 8
  -- | Derived measurement: uniqueness(department_id) = 25.64102564102564102564102564
  -- | Test test(row_count > 0) passed with measurements {"expression_result": 40, "row_count": 40}
  -- | Test column(first_name) test(invalid_percentage == 0) failed with measurements {"expression_result": 100.0, "invalid_percentage": 100.0}
  -- | Executed 12 queries in 0:00:00.326230
  -- | Scan summary ------
  -- | 169 measurements computed
  -- | 2 tests executed
  -- | 1 of 2 tests failed:
  -- |   Test column(first_name) test(invalid_percentage == 0) failed with measurements {"expression_result": 100.0, "invalid_percentage": 100.0}
  -- | Exiting with code 1
