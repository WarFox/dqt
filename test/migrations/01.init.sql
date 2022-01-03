--- Employee gender type

DROP TYPE if EXISTS employee_gender CASCADE;

CREATE TYPE employee_gender AS ENUM (
  'M',
  'F'
);

--- Department

DROP TABLE if EXISTS department;

CREATE TABLE department (
  id CHARACTER(4) NOT NULL,
  dept_name CHARACTER VARYING(40) NOT NULL
);

--- Employees

DROP TABLE if EXISTS employees;

CREATE TABLE employees (
  id            serial PRIMARY KEY,
  first_name    VARCHAR(40),
  last_name     VARCHAR(40),
  dob           DATE,
  gender        employee_gender NOT NULL,
  hired_date    DATE
);

--- Employee Department

DROP TABLE if EXISTS employee_department;

CREATE TABLE employee_department (
  employee_id bigint NOT NULL,
  department_id CHARACTER(4) NOT NULL,
  from_date DATE NOT NULL,
  to_date DATE NOT NULL
);

--- Data

INSERT INTO employees(first_name, last_name, dob, gender, hired_date)
VALUES ('deepu', 'puthrote', to_date('17/10/1986', 'DD/MM/YYY'), 'M', CURRENT_DATE);
