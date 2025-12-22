-- Testni ciklusi vožnje za liniju 3
-- Ponedjeljak (1)

INSERT INTO schedules (line_id, departure, arrival, day_of_week)
VALUES
    (3, '06:00', '06:45', 1),
    (3, '07:00', '07:45', 1),
    (3, '08:00', '08:45', 1),
    (3, '09:00', '09:45', 1),
    (3, '10:00', '10:45', 1);

-- Utorak (2)

INSERT INTO schedules (line_id, departure, arrival, day_of_week)
VALUES
    (3, '06:30', '07:15', 2),
    (3, '07:30', '08:15', 2),
    (3, '08:30', '09:15', 2);

-- Subota (6)

INSERT INTO schedules (line_id, departure, arrival, day_of_week)
VALUES
    (3, '08:00', '08:50', 6),
    (3, '09:30', '10:20', 6);

-- Nedjelja (7)

INSERT INTO schedules (line_id, departure, arrival, day_of_week)
VALUES
    (3, '09:00', '09:50', 7),
    (3, '11:00', '11:50', 7);
