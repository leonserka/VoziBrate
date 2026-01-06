-- BILJETARNICA - Trogir
UPDATE sales_points
SET opening_hours = 'svaki dan 06:30 - 20:00'
WHERE name = 'BILJETARNICA - Trogir';

-- Kastel Stari
UPDATE sales_points
SET opening_hours = 'od Ponedjeljka do Petka 07:00 - 14:00
Prva Subota u mjesecu 07:00 - 12:30'
WHERE name = 'Kastel Stari';

-- Kastel Sucurac
UPDATE sales_points
SET opening_hours = 'od Ponedjeljka do Petka 07:00 - 13:30
Prva Subota u mjesecu 07:00 - 12:30'
WHERE name = 'Kastel Sucurac';

-- BILJETARNICA - Sukoisan
UPDATE sales_points
SET opening_hours = 'radnim danom 06:30 - 20:00
subotom 06:30 - 12:30'
WHERE name = 'BILJETARNICA - Sukoisan';

-- Kartomati (0-24)
UPDATE sales_points
SET opening_hours = '0 - 24'
WHERE name ILIKE 'Kartomat%';
