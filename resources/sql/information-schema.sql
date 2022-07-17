SELECT column_name, data_type, is_nullable
  FROM information_schema.COLUMNS
 WHERE TABLE_NAME = 'employees';
