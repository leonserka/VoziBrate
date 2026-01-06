ALTER TABLE sales_points
    ADD COLUMN IF NOT EXISTS gps_lat DOUBLE PRECISION;

ALTER TABLE sales_points
    ADD COLUMN IF NOT EXISTS gps_lng DOUBLE PRECISION;

CREATE INDEX IF NOT EXISTS idx_sales_points_gps
ON sales_points (gps_lat, gps_lng);
