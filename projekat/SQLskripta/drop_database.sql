USE master;
GO
ALTER DATABASE db_name SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
GO

DROP DATABASE db_name;