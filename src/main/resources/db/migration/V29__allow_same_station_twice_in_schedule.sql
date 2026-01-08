-- Makni krivi unique (zabranjuje istu stanicu 2x u schedule-u)
ALTER TABLE schedule_stops
  DROP CONSTRAINT IF EXISTS uq_schedule_station;

-- Osiguraj da unique po (schedule_id, stop_sequence) postoji (ako je već tu, neće se duplat)
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1
    FROM pg_constraint
    WHERE conname = 'uq_schedule_stop_sequence'
  ) THEN
    ALTER TABLE schedule_stops
      ADD CONSTRAINT uq_schedule_stop_sequence
      UNIQUE (schedule_id, stop_sequence);
  END IF;
END $$;

-- (opcionalno) indeks ti već postoji u V8, ali ovo je safe:
CREATE INDEX IF NOT EXISTS idx_schedule_stops_schedule_seq
  ON schedule_stops(schedule_id, stop_sequence);
