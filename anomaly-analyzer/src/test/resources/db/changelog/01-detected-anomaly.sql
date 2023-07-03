create table Detected_Anomaly
(
    id           serial primary key,
    thermometer_id text,
    room_id    text,
    temperature  DECIMAL(11,2),
    timestamp    TIMESTAMP,
    temperature_unit text
);

CREATE OR REPLACE FUNCTION notify_anomaly_saved()
    RETURNS TRIGGER
AS $$
BEGIN
    PERFORM pg_notify('ANOMALY_SAVED',  row_to_json(NEW)::text);
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER anomaly_saved_trigger
    AFTER INSERT OR UPDATE
    ON Detected_Anomaly
    FOR EACH ROW
EXECUTE PROCEDURE notify_anomaly_saved();