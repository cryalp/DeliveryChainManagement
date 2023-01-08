USE master;
GO
IF DB_ID(N'DeliveryChain') IS NOT NULL
    DROP DATABASE DeliveryChain;
GO
CREATE DATABASE DeliveryChain
GO
USE DeliveryChain
GO

SELECT collation_name
FROM sys.databases
WHERE name = 'DeliveryChain';
ALTER DATABASE DeliveryChain COLLATE Turkish_CI_AS
GO

USE master
DROP LOGIN DeliveryChain
USE DeliveryChain
DROP USER DeliveryChain

IF NOT EXISTS(
        SELECT name
FROM master.sys.server_principals
WHERE name = 'DeliveryChain'
    )
    BEGIN
    CREATE LOGIN DeliveryChain WITH PASSWORD = '123x_123x_',
            DEFAULT_DATABASE = DeliveryChain
    CREATE USER DeliveryChain FOR LOGIN DeliveryChain
    EXEC sp_addrolemember N'db_owner',
             N'DeliveryChain'
END
GO

/*
DROP TABLE IF EXISTS SPRING_SESSION_ATTRIBUTES
GO
DROP TABLE IF EXISTS SPRING_SESSION
GO

CREATE TABLE SPRING_SESSION
(
    PRIMARY_ID            NVARCHAR(36) PRIMARY KEY,
    SESSION_ID            NVARCHAR(36) NOT NULL,
    CREATION_TIME         BIGINT       NOT NULL,
    LAST_ACCESS_TIME      BIGINT       NOT NULL,
    MAX_INACTIVE_INTERVAL INT          NOT NULL,
    EXPIRY_TIME           BIGINT       NOT NULL,
    PRINCIPAL_NAME        NVARCHAR(255)
)
GO

CREATE TABLE SPRING_SESSION_ATTRIBUTES
(
    SESSION_PRIMARY_ID NVARCHAR(36),
    ATTRIBUTE_NAME     NVARCHAR(255),
    ATTRIBUTE_BYTES    NVARCHAR(255)
)
GO
*/