CREATE TABLE schedule_stops (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    schedule_id INT NOT NULL,
    station_id INT NOT NULL,

    stop_sequence INT NOT NULL,
    time TIME NOT NULL,

    CONSTRAINT fk_schedule_stops_schedule
        FOREIGN KEY (schedule_id)
        REFERENCES schedules(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_schedule_stops_station
        FOREIGN KEY (station_id)
        REFERENCES stations(id),

    CONSTRAINT uq_schedule_station
        UNIQUE (schedule_id, station_id)
);
