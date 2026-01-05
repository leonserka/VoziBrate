-- 1) Osiguraj da sesija zna da su postojeći timestampi u Europe/Zagreb
SET TIME ZONE 'Europe/Zagreb';

ALTER TABLE bus_positions
ALTER COLUMN timestamp
TYPE timestamptz
USING timestamp AT TIME ZONE 'Europe/Zagreb';
