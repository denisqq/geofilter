drop function if exists filter.to_seconds(interval);
DROP FUNCTION if exists filter.abs(interval);

CREATE OR REPLACE FUNCTION filter.to_seconds(t interval)
  RETURNS integer AS
$BODY$
DECLARE
  hs INTEGER;
  ms INTEGER;
  s  INTEGER;
BEGIN
  SELECT (EXTRACT(HOUR FROM t::time) * 60 * 60) INTO hs;
  SELECT (EXTRACT(MINUTES FROM t::time) * 60) INTO ms;
  SELECT (EXTRACT(SECONDS from t::time)) INTO s;
  SELECT (abs(hs) + abs(ms) + abs(s)) INTO s;
  RETURN s;
END;
$BODY$
  LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION filter.abs(IN t interval, OUT p_res interval)
  RETURNS interval AS
$BODY$
DECLARE
  day integer;
BEGIN
  day := extract(day from t);
  if day < 0 then
    p_res := make_interval(days := -day);
  else
    p_res := make_interval(days := day);
  end if;

END;
$BODY$
  LANGUAGE 'plpgsql';


-- create extension earthdistance with schema filter;
-- create extension cube with schema filter
