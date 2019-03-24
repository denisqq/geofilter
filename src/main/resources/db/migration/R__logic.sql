drop function if exists to_seconds(interval);
drop function if exists get_locations();

CREATE OR REPLACE FUNCTION to_seconds(t interval)
  RETURNS integer AS
$BODY$
DECLARE
    hs INTEGER;
    ms INTEGER;
    s INTEGER;
BEGIN
    SELECT (EXTRACT( HOUR FROM  t::time) * 60*60) INTO hs;
    SELECT (EXTRACT (MINUTES FROM t::time) * 60) INTO ms;
    SELECT (EXTRACT (SECONDS from t::time)) INTO s;
    SELECT (hs + ms + s) INTO s;
    RETURN s;
END;
$BODY$
  LANGUAGE 'plpgsql';

