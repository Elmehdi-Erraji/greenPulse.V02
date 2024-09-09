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



/*GP1 db sql */

-- 1. Enable uuid-ossp extension (for UUIDs if needed)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 2. Create enum types
CREATE TYPE consumption_type AS ENUM ('TRANSPORT', 'LOGEMENT', 'ALIMENTATION');
CREATE TYPE vehicule_type AS ENUM ('voiture', 'train');
CREATE TYPE energie_type AS ENUM ('electricité', 'gaz');
CREATE TYPE aliment_type AS ENUM ('viande', 'légume');

-- 3. Create users table with SERIAL id
CREATE TABLE users (
                       id SERIAL PRIMARY KEY,  -- Auto-incrementing id
                       name VARCHAR(100) NOT NULL,
                       age INT NOT NULL,
                       date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. Create consumption table with SERIAL id
CREATE TABLE consumption (
                             id SERIAL PRIMARY KEY,  -- Auto-incrementing id
                             user_id INT REFERENCES users(id),
                             start_date DATE NOT NULL,
                             end_date DATE NOT NULL,
                             amount DOUBLE PRECISION NOT NULL,
                             type consumption_type NOT NULL,  -- Using the consumption_type enum
                             impact DOUBLE PRECISION NOT NULL,
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 5. Create transport-specific consumption table
CREATE TABLE transport (
                           consumption_id INT PRIMARY KEY REFERENCES consumption(id),
                           distance_parcourue DOUBLE PRECISION NOT NULL,
                           type_de_vehicule vehicule_type NOT NULL  -- Using the vehicule_type enum
);

-- 6. Create logement-specific consumption table
CREATE TABLE logement (
                          consumption_id INT PRIMARY KEY REFERENCES consumption(id),
                          consommation_energie DOUBLE PRECISION NOT NULL,
                          type_energie energie_type NOT NULL  -- Using the energie_type enum
);

-- 7. Create alimentation-specific consumption table
CREATE TABLE alimentation (
                              consumption_id INT PRIMARY KEY REFERENCES consumption(id),
                              type_aliment aliment_type NOT NULL,  -- Using the aliment_type enum
                              poids DOUBLE PRECISION NOT NULL
);


