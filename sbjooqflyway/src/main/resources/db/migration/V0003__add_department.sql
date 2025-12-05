CREATE TABLE department (
    id    SERIAL PRIMARY KEY,
    no    CHAR(4)     NOT NULL,
    name  VARCHAR(40) NOT NULL
);

INSERT INTO department(no, name) VALUES 
  ('d001','Marketing'),
  ('d002','Finance'),
  ('d003','Human Resources'),
  ('d004','Production'),
  ('d005','Development'),
  ('d006','Quality Management'),
  ('d007','Sales'),
  ('d008','Research'),
  ('d009','Customer Service'),
  ('dddd','Unset');
  
ALTER TABLE employee ADD COLUMN department_id INTEGER; 
UPDATE employee SET department_id = (SELECT id FROM department where no = 'dddd');
ALTER TABLE employee ALTER COLUMN department_id SET NOT NULL;
