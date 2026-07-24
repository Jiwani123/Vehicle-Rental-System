-- 001-migrate-vehicles-to-identity.sql
-- Safe migration script to make dbo.vehicles.id an IDENTITY column
-- Usage: open in SSMS or run with sqlcmd. Review, BACKUP your database first.
-- Set @preserveIds = 1 if you want to keep the existing id values. Set to 0 to let new IDs be generated.

USE [projectvehicle];
GO

SET NOCOUNT ON;

DECLARE @preserveIds BIT = 1; -- 1 = preserve existing ids, 0 = let new ids be generated

BEGIN TRY
    -- Basic checks
    IF OBJECT_ID('dbo.vehicles', 'U') IS NULL
    BEGIN
        THROW 51000, 'Source table dbo.vehicles does not exist in database projectvehicle. Aborting.', 1;
    END

    IF OBJECT_ID('dbo.vehicles_new', 'U') IS NOT NULL
    BEGIN
        THROW 51001, 'Temporary table dbo.vehicles_new already exists. Drop or rename it before running this script.', 1;
    END

    -- Create new table with IDENTITY
    PRINT 'Creating dbo.vehicles_new with IDENTITY column...';
    CREATE TABLE dbo.vehicles_new (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        brand_name NVARCHAR(50) NOT NULL,
        model_name NVARCHAR(50) NOT NULL,
        vehicle_type NVARCHAR(50) NOT NULL,
        is_available BIT NULL,
        rental_price_per_day FLOAT NOT NULL,
        image_url NVARCHAR(255) NULL
    );

    -- Copy data
    IF @preserveIds = 1
    BEGIN
        PRINT 'Preserving original ids (IDENTITY_INSERT ON for vehicles_new)...';
        SET IDENTITY_INSERT dbo.vehicles_new ON;
        INSERT INTO dbo.vehicles_new (id, brand_name, model_name, vehicle_type, is_available, rental_price_per_day, image_url)
        SELECT id, brand_name, model_name, vehicle_type, is_available, rental_price_per_day, image_url FROM dbo.vehicles;
        SET IDENTITY_INSERT dbo.vehicles_new OFF;

        -- Reseed identity to max(id)
        DECLARE @maxid BIGINT;
        SELECT @maxid = MAX(id) FROM dbo.vehicles_new;
        IF @maxid IS NULL SET @maxid = 0;
        DBCC CHECKIDENT ('dbo.vehicles_new', RESEED, @maxid);
    END
    ELSE
    BEGIN
        PRINT 'Inserting data without preserving ids (new ids will be assigned)...';
        INSERT INTO dbo.vehicles_new (brand_name, model_name, vehicle_type, is_available, rental_price_per_day, image_url)
        SELECT brand_name, model_name, vehicle_type, is_available, rental_price_per_day, image_url FROM dbo.vehicles;
    END

    -- Safety checks before renaming
    IF OBJECT_ID('dbo.vehicles_backup', 'U') IS NOT NULL
    BEGIN
        THROW 51002, 'Backup table dbo.vehicles_backup already exists. Drop or rename it and re-run the script.', 1;
    END

    -- Rename original to backup and new to original name
    PRINT 'Renaming original table to dbo.vehicles_backup...';
    EXEC sp_rename 'dbo.vehicles', 'vehicles_backup';

    PRINT 'Renaming dbo.vehicles_new to dbo.vehicles...';
    EXEC sp_rename 'dbo.vehicles_new', 'vehicles';

    PRINT 'Migration complete. Verify data in dbo.vehicles. If good, you can drop dbo.vehicles_backup.';

END TRY
BEGIN CATCH
    DECLARE @errMsg NVARCHAR(4000) = ERROR_MESSAGE();
    PRINT 'ERROR: ' + @errMsg;

    -- cleanup: drop the new table if present to avoid leaving a half-applied migration
    IF OBJECT_ID('dbo.vehicles_new', 'U') IS NOT NULL
    BEGIN
        PRINT 'Dropping temporary table dbo.vehicles_new (cleanup)...';
        DROP TABLE dbo.vehicles_new;
    END

    THROW; -- re-raise
END CATCH
GO

-- After running, consider running:
-- SELECT TOP 10 * FROM dbo.vehicles ORDER BY id DESC;
-- to validate rows. When satisfied you can remove the backup with:
-- DROP TABLE dbo.vehicles_backup; -- only after verifying everything works
