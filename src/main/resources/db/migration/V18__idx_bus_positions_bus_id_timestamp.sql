CREATE INDEX IF NOT EXISTS idx_bus_positions_bus_id_timestamp
ON bus_positions (bus_id, timestamp DESC);
