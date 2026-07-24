-- Migration script to add priority column to inquiries table
-- Execute this script on your SQL Server database

-- Add priority column to inquiries table
ALTER TABLE inquiries 
ADD priority VARCHAR(50) NULL;

-- Update existing records to have MEDIUM priority
UPDATE inquiries 
SET priority = 'MEDIUM' 
WHERE priority IS NULL;

-- Optionally, make the column NOT NULL after setting default values
-- ALTER TABLE inquiries 
-- ALTER COLUMN priority VARCHAR(50) NOT NULL;

PRINT 'Priority column added to inquiries table successfully';