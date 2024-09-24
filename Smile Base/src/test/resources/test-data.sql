-- Insert Users (Doctors) + 1 Receptionist
INSERT INTO users (username, password, name, email, role, active) VALUES
('smith.j', 'password', 'Dr. Smith', 'smith@example.com', 1, true),
('johnson.m', 'password', 'Dr. Johnson', 'johnson@example.com', 1, true),
('williams.d', 'password', 'Dr. Williams', 'williams@example.com', 1, true),
('max.m', 'password', 'Max Mustermann', 'williams@example.com', 0, true);

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

-- Insert Appointments for doctor1
INSERT INTO appointments (title, start, appointment_type, "end", doctor_id, patient_id)
SELECT
    'Appointment ' || CAST(FLOOR(RAND() * 1000) AS INT),
    DATEADD('DAY', n, CURRENT_DATE()) + DATEADD('HOUR', 8 + MOD(n, 8), TIME '00:00:00'),
    FLOOR(RAND() * 3),
    DATEADD('DAY', n, CURRENT_DATE()) + DATEADD('HOUR', 9 + MOD(n, 8), TIME '00:00:00'),
    (SELECT id FROM users WHERE username = 'smith.j'),
    CAST(FLOOR(RAND() * (SELECT COUNT(*) FROM patients)) + 1 AS INT)
FROM (
    SELECT ROW_NUMBER() OVER () - 1 AS n
    FROM INFORMATION_SCHEMA.COLUMNS
    LIMIT 14
) AS numbers
WHERE MOD(n, 7) < 5
LIMIT 35;

-- Insert Appointments for doctor2
INSERT INTO appointments (title, start, appointment_type, "end", doctor_id, patient_id)
SELECT
    'Appointment ' || CAST(FLOOR(RAND() * 1000) AS INT),
    DATEADD('DAY', n, CURRENT_DATE()) + DATEADD('HOUR', 8 + MOD(n, 8), TIME '00:00:00'),
    FLOOR(RAND() * 3),
    DATEADD('DAY', n, CURRENT_DATE()) + DATEADD('HOUR', 9 + MOD(n, 8), TIME '00:00:00'),
    (SELECT id FROM users WHERE username = 'johnson.m'),
    CAST(FLOOR(RAND() * (SELECT COUNT(*) FROM patients)) + 1 AS INT)
FROM (
    SELECT ROW_NUMBER() OVER () - 1 AS n
    FROM INFORMATION_SCHEMA.COLUMNS
    LIMIT 14
) AS numbers
WHERE MOD(n, 7) < 5
LIMIT 35;

-- Insert Appointments for doctor3
INSERT INTO appointments (title, start, appointment_type, "end", doctor_id, patient_id)
SELECT
    'Appointment ' || CAST(FLOOR(RAND() * 1000) AS INT),
    DATEADD('DAY', n, CURRENT_DATE()) + DATEADD('HOUR', 8 + MOD(n, 8), TIME '00:00:00'),
    FLOOR(RAND() * 3),
    DATEADD('DAY', n, CURRENT_DATE()) + DATEADD('HOUR', 9 + MOD(n, 8), TIME '00:00:00'),
    (SELECT id FROM users WHERE username = 'williams.d'),
    CAST(FLOOR(RAND() * (SELECT COUNT(*) FROM patients)) + 1 AS INT)
FROM (
    SELECT ROW_NUMBER() OVER () - 1 AS n
    FROM INFORMATION_SCHEMA.COLUMNS
    LIMIT 14
) AS numbers
WHERE MOD(n, 7) < 5
LIMIT 35;