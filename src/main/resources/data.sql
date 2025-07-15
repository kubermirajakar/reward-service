-- --------------------------------------
-- Seed initial customer data
-- --------------------------------------
INSERT INTO customer (name) VALUES
('Ananya Sharma'),
('Rahul Mehta'),
('Priya Verma');

-- --------------------------------------
-- Seed transaction data with numeric customer IDs
-- Note: Assumes auto-generated customer IDs are 1, 2, and 3
-- --------------------------------------
INSERT INTO transaction (amount, transaction_date, customer_id) VALUES
(120.00, '2025-06-15', 1),  -- Transaction for Ananya Sharma
(90.00,  '2025-05-20', 1),
(60.00,  '2025-04-10', 1),
(150.00, '2025-06-05', 2),  -- Transaction for Rahul Mehta
(55.00,  '2025-06-22', 2),
(40.00,  '2025-05-18', 2),
(200.00, '2025-06-25', 3),  -- Transaction for Priya Verma
(100.00, '2025-05-14', 3),
(75.00,  '2025-04-30', 3);