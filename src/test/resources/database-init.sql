TRUNCATE TABLE STATION;
ALTER TABLE STATION ALTER COLUMN ID RESTART WITH 1;

TRUNCATE TABLE LINE;
ALTER TABLE LINE ALTER COLUMN ID RESTART WITH 1;
