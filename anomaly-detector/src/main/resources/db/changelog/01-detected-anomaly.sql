create table Detected_Anomaly
(
    id           serial primary key,
    temperature  double(11,2),
    timestamp    TIMESTAMP,
    roomId    text,
    thermometerId timestamp 
);