-- 1) dodaj variant (A/B)
ALTER TABLE lines
ADD COLUMN IF NOT EXISTS variant VARCHAR(1) NOT NULL DEFAULT 'A';

-- 2) svima A
UPDATE lines SET variant = 'A';

-- 3) za duplikate po line_number: po ID-u dodijeli A/B
WITH ranked AS (
  SELECT
    id,
    line_number,
    ROW_NUMBER() OVER (PARTITION BY line_number ORDER BY id ASC) AS rn,
    COUNT(*)    OVER (PARTITION BY line_number)                AS cnt
  FROM lines
)
UPDATE lines l
SET variant =
  CASE
    WHEN r.cnt = 1 THEN 'A'
    WHEN r.rn  = 1 THEN 'A'
    WHEN r.rn  = 2 THEN 'B'
    ELSE 'X'
  END
FROM ranked r
WHERE l.id = r.id;

-- 4) obriši višak (ako imaš više od 2 reda za isti line_number)
-- Ako ti ovdje pukne zbog FK-ova, javi i dam ti "safe merge" varijantu.
DELETE FROM lines
WHERE variant = 'X';

-- 5) UNIQUE constraint na (line_number, variant)
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1
    FROM pg_constraint
    WHERE conname = 'uq_lines_number_variant'
  ) THEN
    ALTER TABLE lines
      ADD CONSTRAINT uq_lines_number_variant UNIQUE (line_number, variant);
  END IF;
END $$;
