//creating 3 tables 
CREATE TABLE employees (
    id SERIAL PRIMARY KEY,
    emp_id VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    department VARCHAR(50) NOT NULL,
    address VARCHAR(200) NOT NULL,
    email VARCHAR(100) NOT NULL
);

CREATE TABLE employee_login (
    emp_id VARCHAR(20) PRIMARY KEY,
    password VARCHAR(256) NOT NULL,
    first_login BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_employee_login
        FOREIGN KEY (emp_id)
        REFERENCES employees(emp_id)
        ON DELETE CASCADE
); 
CREATE TABLE employee_roles (
    emp_id VARCHAR(20) NOT NULL,
    role VARCHAR(20) NOT NULL,
    CONSTRAINT fk_employee_roles
        FOREIGN KEY (emp_id)
        REFERENCES employees(emp_id)
        ON DELETE CASCADE,
    CONSTRAINT unique_employee_role UNIQUE (emp_id, role)
);
