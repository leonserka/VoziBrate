-- Minimalna dorada baze prije Schedule featurea

BEGIN;

-- 1 = Monday, 7 = Sunday (EU standard)

ALTER TABLE schedules
    ALTER COLUMN day_of_week TYPE INTEGER
    USING CASE lower(day_of_week)
        WHEN 'monday' THEN 1
        WHEN 'mon' THEN 1
        WHEN 'tuesday' THEN 2
        WHEN 'tue' THEN 2
        WHEN 'wednesday' THEN 3
        WHEN 'wed' THEN 3
        WHEN 'thursday' THEN 4
        WHEN 'thu' THEN 4
        WHEN 'friday' THEN 5
        WHEN 'fri' THEN 5
        WHEN 'saturday' THEN 6
        WHEN 'sat' THEN 6
        WHEN 'sunday' THEN 7
        WHEN 'sun' THEN 7
        ELSE NULL
    END;

-- Indeksi za Schedule Overview
CREATE INDEX IF NOT EXISTS idx_schedules_line_day
    ON schedules(line_id, day_of_week);

-- Indeksi za ETA i timetable po stanicama
CREATE INDEX IF NOT EXISTS idx_schedule_stops_schedule_seq
    ON schedule_stops(schedule_id, stop_sequence);

CREATE INDEX IF NOT EXISTS idx_schedule_stops_station
    ON schedule_stops(station_id);

-- Osiguraj jedinstveni redoslijed po scheduleu
ALTER TABLE schedule_stops
    ADD CONSTRAINT uq_schedule_stop_sequence
    UNIQUE (schedule_id, stop_sequence);

COMMIT;
