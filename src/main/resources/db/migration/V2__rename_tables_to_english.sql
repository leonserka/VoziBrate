-- V2__rename_tables_to_english.sql
-- Migration script to rename all tables and columns from Croatian to English


ALTER TABLE korisnici RENAME TO users;
ALTER TABLE linije RENAME TO lines;
ALTER TABLE stanice RENAME TO stations;
ALTER TABLE ruta_stanice RENAME TO route_stations;
ALTER TABLE autobusi RENAME TO buses;
ALTER TABLE pozicije_autobusa RENAME TO bus_positions;
ALTER TABLE raspored RENAME TO schedules;
ALTER TABLE karte RENAME TO tickets;
ALTER TABLE favoriti RENAME TO favorites;
ALTER TABLE prodajna_mjesta RENAME TO sales_points;


ALTER TABLE users RENAME COLUMN ime TO name;
ALTER TABLE users RENAME COLUMN lozinka TO password;
ALTER TABLE users RENAME COLUMN uloga TO role;


ALTER TABLE lines RENAME COLUMN broj_linije TO line_number;
ALTER TABLE lines RENAME COLUMN naziv TO name;
ALTER TABLE lines RENAME COLUMN smjer TO direction;


ALTER TABLE stations RENAME COLUMN naziv TO name;
ALTER TABLE stations RENAME COLUMN lokacija TO location;


ALTER TABLE route_stations RENAME COLUMN linija_id TO line_id;
ALTER TABLE route_stations RENAME COLUMN stanica_id TO station_id;
ALTER TABLE route_stations RENAME COLUMN redoslijed TO order_number;


ALTER TABLE buses RENAME COLUMN broj_autobusa TO bus_number;
ALTER TABLE buses RENAME COLUMN registracija TO registration;
ALTER TABLE buses RENAME COLUMN linija_id TO line_id;


ALTER TABLE bus_positions RENAME COLUMN autobus_id TO bus_id;
ALTER TABLE bus_positions RENAME COLUMN vrijeme TO timestamp;


ALTER TABLE schedules RENAME COLUMN linija_id TO line_id;
ALTER TABLE schedules RENAME COLUMN polazak TO departure;
ALTER TABLE schedules RENAME COLUMN dolazak TO arrival;
ALTER TABLE schedules RENAME COLUMN dan_u_tjednu TO day_of_week;
ALTER TABLE schedules RENAME COLUMN smjer TO direction;


ALTER TABLE tickets RENAME COLUMN korisnik_id TO user_id;
ALTER TABLE tickets RENAME COLUMN linija_id TO line_id;
ALTER TABLE tickets RENAME COLUMN vrijeme_kupnje TO purchase_time;
ALTER TABLE tickets RENAME COLUMN cijena TO price;


ALTER TABLE favorites RENAME COLUMN korisnik_id TO user_id;
ALTER TABLE favorites RENAME COLUMN linija_id TO line_id;
ALTER TABLE favorites RENAME COLUMN stanica_id TO station_id;


ALTER TABLE sales_points RENAME COLUMN naziv TO name;
ALTER TABLE sales_points RENAME COLUMN adresa TO address;
