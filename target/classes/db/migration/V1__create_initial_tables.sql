-- V4__create_vozi_brate_schema.sql
-- Glavna shema za VoziBrate aplikaciju (PostgreSQL)

CREATE TABLE korisnici (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ime VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    lozinka VARCHAR(255) NOT NULL,
    uloga VARCHAR(20) DEFAULT 'korisnik'
);

CREATE TABLE linije (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    broj_linije VARCHAR(10) NOT NULL,
    naziv VARCHAR(100) NOT NULL,
    smjer VARCHAR(50)
);

CREATE TABLE stanice (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    naziv VARCHAR(100) NOT NULL,
    lokacija VARCHAR(100),
    gps_lat DECIMAL(9,6),
    gps_lng DECIMAL(9,6)
);

CREATE TABLE ruta_stanice (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    linija_id INT NOT NULL,
    stanica_id INT NOT NULL,
    redoslijed INT,
    FOREIGN KEY (linija_id) REFERENCES linije(id),
    FOREIGN KEY (stanica_id) REFERENCES stanice(id)
);

CREATE TABLE autobusi (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    broj_autobusa VARCHAR(20),
    registracija VARCHAR(20),
    linija_id INT,
    gps_id VARCHAR(50),
    FOREIGN KEY (linija_id) REFERENCES linije(id)
);

CREATE TABLE pozicije_autobusa (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    autobus_id INT NOT NULL,
    gps_lat DECIMAL(9,6),
    gps_lng DECIMAL(9,6),
    vrijeme TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (autobus_id) REFERENCES autobusi(id)
);

CREATE TABLE raspored (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    linija_id INT NOT NULL,
    polazak TIME NOT NULL,
    dolazak TIME NOT NULL,
    dan_u_tjednu VARCHAR(15),
    smjer VARCHAR(50),
    FOREIGN KEY (linija_id) REFERENCES linije(id)
);

CREATE TABLE karte (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    korisnik_id INT NOT NULL,
    linija_id INT NOT NULL,
    vrijeme_kupnje TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    cijena DECIMAL(5,2),
    status VARCHAR(20) DEFAULT 'aktivna',
    FOREIGN KEY (korisnik_id) REFERENCES korisnici(id),
    FOREIGN KEY (linija_id) REFERENCES linije(id)
);

CREATE TABLE favoriti (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    korisnik_id INT NOT NULL,
    linija_id INT,
    stanica_id INT,
    FOREIGN KEY (korisnik_id) REFERENCES korisnici(id),
    FOREIGN KEY (linija_id) REFERENCES linije(id),
    FOREIGN KEY (stanica_id) REFERENCES stanice(id)
);

CREATE TABLE prodajna_mjesta (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    naziv VARCHAR(100) NOT NULL,
    adresa VARCHAR(150),
    gps_lat DECIMAL(9,6),
    gps_lng DECIMAL(9,6)
);
