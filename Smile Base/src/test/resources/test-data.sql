-- Insert Users (Doctors) + 1 Receptionist
INSERT INTO users (username, password, name, email, role, active) VALUES
('smith.j', 'password', 'Dr. Smith', 'smith@example.com', 1, true),
('johnson.m', 'password', 'Dr. Johnson', 'johnson@example.com', 1, true),
('williams.d', 'password', 'Dr. Williams', 'williams@example.com', 1, true),
('max.m', 'password', 'Max Mustermann', 'max@example.com', 0, true);

-- Insert Patients
INSERT INTO patients (name, birthdate, insurance_number, insurance_provider, email) VALUES
('John Doe', '1980-01-01', 'INS001', 'Provider A', 'john@example.com'),
('Jane Smith', '1985-05-15', 'INS002', 'Provider B', 'jane@example.com'),
('Bob Johnson', '1990-10-20', 'INS003', 'Provider A', 'bob@example.com'),
('Alice Brown', '1975-03-30', 'INS004', 'Provider C', 'alice@example.com'),
('Charlie Davis', '1988-07-07', 'INS005', 'Provider B', 'charlie@example.com'),
('Eva Wilson', '1992-12-25', 'INS006', 'Provider A', 'eva@example.com'),
('Frank Miller', '1970-06-18', 'INS007', 'Provider C', 'frank@example.com'),
('Grace Lee', '1995-09-03', 'INS008', 'Provider B', 'grace@example.com'),
('Henry Taylor', '1982-11-11', 'INS009', 'Provider A', 'henry@example.com'),
('Ivy Clark', '1987-04-22', 'INS010', 'Provider C', 'ivy@example.com');

-- Common Table Expression for generating weekdays
WITH RECURSIVE weekdays(day_num, date) AS (
    SELECT 0, PARSEDATETIME('2024-01-08', 'yyyy-MM-dd')
    UNION ALL
    SELECT day_num + 1, DATEADD('DAY', 1, date)
    FROM weekdays
    WHERE day_num < 9
),
-- Generate all possible appointment slots
generate_appointments AS (
    SELECT
        d.id AS doctor_id,
        w.date,
        DATEADD('MINUTE', (slot - 1) * 30 + 480, w.date) AS start_time,
        FLOOR(RAND() * 3) AS appointment_type
    FROM weekdays w
    CROSS JOIN (SELECT id FROM users WHERE role = 1) d
    CROSS JOIN (
        SELECT 1 AS slot UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
        UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8
        UNION ALL SELECT 9 UNION ALL SELECT 10 UNION ALL SELECT 11 UNION ALL SELECT 12
        UNION ALL SELECT 13 UNION ALL SELECT 14 UNION ALL SELECT 15 UNION ALL SELECT 16
    ) slots
)

-- Insert Appointments for all doctors
INSERT INTO appointments (title, start, appointment_type, "end", doctor_id, patient_id)
SELECT
    'Appointment ' || CAST(FLOOR(RAND() * 1000) AS INT),
    start_time,
    appointment_type,
    CASE
        WHEN appointment_type = 0 THEN DATEADD('MINUTE', 30, start_time)
        WHEN appointment_type = 1 THEN DATEADD('MINUTE', 60, start_time)
        WHEN appointment_type = 2 THEN DATEADD('MINUTE', 120, start_time)
    END,
    doctor_id,
    CAST(FLOOR(RAND() * (SELECT COUNT(*) FROM patients)) + 1 AS INT)
FROM (
    SELECT *,
           ROW_NUMBER() OVER (PARTITION BY doctor_id, date ORDER BY RAND()) as rn
    FROM generate_appointments
    WHERE start_time < DATEADD('HOUR', 17, TRUNC(start_time))
      AND CASE
            WHEN appointment_type = 0 THEN DATEADD('MINUTE', 30, start_time)
            WHEN appointment_type = 1 THEN DATEADD('MINUTE', 60, start_time)
            WHEN appointment_type = 2 THEN DATEADD('MINUTE', 120, start_time)
          END <= DATEADD('HOUR', 17, TRUNC(start_time))
) subquery
WHERE rn <= 5
ORDER BY doctor_id, start_time;