-- Brisanje testnih polazaka za liniju 3 (unesenih u V9)
DELETE FROM schedules WHERE line_id = 3;

ALTER TABLE schedules
ADD COLUMN arrival_next_day BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE schedule_stops
ADD COLUMN next_day BOOLEAN NOT NULL DEFAULT FALSE;
