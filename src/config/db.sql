CREATE TABLE users (
  id SERIAL PRIMARY KEY,                    -- Auto-incrementing user ID
  name VARCHAR(100) NOT NULL,
  age INT NOT NULL
);

CREATE TABLE carbonRecords (
    id SERIAL PRIMARY KEY,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    type VARCHAR(50) NOT NULL,
    user_id integer NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE transports (
  record_id INT PRIMARY KEY,
  distance_parcourue DECIMAL(10, 2),
  type_de_vehicule VARCHAR(100),
  FOREIGN KEY (record_id) REFERENCES carbonRecords(id) ON DELETE CASCADE
);

CREATE TABLE logements (
   record_id INT PRIMARY KEY,
   consommation_energie DECIMAL(10, 2),
   type_energie VARCHAR(100),
   FOREIGN KEY (record_id) REFERENCES carbonRecords(id) ON DELETE CASCADE
);

CREATE TABLE alimentations (
    record_id INT PRIMARY KEY,
    type_aliment VARCHAR(100),
    poids DECIMAL(10, 2),
    FOREIGN KEY (record_id) REFERENCES carbonRecords(id) ON DELETE CASCADE
);

ALTER TABLE carbonRecords
    ADD CONSTRAINT chk_type
    CHECK (type IN ('TRANSPORT', 'LOGEMENT', 'ALIMENTATION'));
