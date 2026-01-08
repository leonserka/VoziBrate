ALTER TABLE route_stations
ADD COLUMN IF NOT EXISTS minutes_from_start INT NOT NULL DEFAULT 0;

CREATE INDEX IF NOT EXISTS idx_route_stations_line_order
ON route_stations(line_id, order_number);
