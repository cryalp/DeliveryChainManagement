DROP TABLE IF EXISTS Product
GO
DROP TABLE IF EXISTS UserAccount
GO

CREATE TABLE UserAccount
(
    Id           INT              PRIMARY KEY IDENTITY(1,1),
    Email        NVARCHAR(255)    NOT NULL,
    Username     NVARCHAR(255)    NOT NULL,
    Password     NVARCHAR(255)    NOT NULL,
    AccountType  NVARCHAR(255)    NOT NULL,
    CreationDate DATETIME         NOT NULL,
    Photo        NVARCHAR(MAX),
    IsActive     BIT              NOT NULL,
    UniqueId     UNIQUEIDENTIFIER NOT NULL,
)
GO

INSERT INTO UserAccount
    (Email, Username, Password, AccountType, CreationDate, Photo, IsActive, UniqueId)
VALUES
    ('cryalp@cryalp.com', 'cryalp', '$2a$10$PiFqHXFk0cConmDJ2gdx8.snlIPEWQqbFvOU5e8uM/z11QYOIkCr6', 'Supplier', '2023-01-07 00:17:01', 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAQAAAAAYLlVAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAAAAmJLR0QA/4ePzL8AAAAHdElNRQfkAxgQICe/pMQtAAADZklEQVRo3u2ZS0hUURjHf3dGHc1X4ytLkUxL3TgliUgJFm60jSCRgZtcRA8iCNpGFEi6qUVCRIuwhVEE7UqCqKiwjNRMkdJeYva0JsdRR53bIjHvODP3nDv3uvL/LWfu9/+dxz3nfOfCqlYVmRRskSYw9lQaLlzk4sSOh1EG6OY9syvT6jxO0YMXdUnMMUIbu4mx2jyWRgbxa8z/x29aybHS3smFgJYvj6eUWmW/livM69irqLymzAr7GFqYE7BXUeki33yAeiYE7VVU2ogz134DLyTsVbzsNRfgiNDoL40OEsyzj+eepL2KmwqR1GIL6SaKpaGT2GkeQAGpBvrNhd0sgByRVMuULfImiAEkG7CHBJGdIcLNNHKJAbgN5Z7EZxbACPMGAEaZMgvgDeMGAHpFsMUAhumTtp/gscjfxAA83EaVBHjOS2noMMqmW2ohnmK/mfYADUxKALQTbzaAg/PCO2I3hWbbA6TQJoQwyA4r7AFSaWVa9zhWLpNSb5NxUko1tZTzkx94ecAXinCG+LeHaxynD3BSzz62kc4ME9Jv0ILSOEYnEwtVwDBHSQIUCmiiP6An5hnjBtU4ABsV3GUWFRUf77iIK3QFFro0K6GFSk0PzXKHJrrwo5BJCSXk4sTGJKP008UQM0AmhzhMhibXCGe5Kle4bacv6AiP0Uwx0Yv4UUQtLmYKmRzgWdCJ6uGEzJkii0dhptkY7TRQSOKCtUIsWVTRzCt8IZ/6RZ3oENg4x0kdxDm+84ERxvGTwHo2ksUanWd6qOWjSPvL+Cp9BhaLMyL2Ni5ZZK8ytLxkW74bbqZGfLJIKo89+gCVZFsGADWBMyUQwM4ug9c2YiomNzxAhoEaSEbpuMIDbCTLUgA7W8MDbCHRUgAo0pYrgQD5ls4AgBxtE7UAirV3XACkkRIaIIZ1lgMkaE8TgQDGylAZBXhoAaLNvloKoijtedkW8GO0VDIjsoV7CxRDFxHyfRASQMW/AgCaklULMG3wJkBGPm2lrQXwMGw5wDifQgP4eWj5IPSGA4AOBiy193Fd796kUaoKlo2bJOkxOjit+1HCaNwnT6SbHBzkbcjPMkbDzeVgW13wzVchjzqqyCdZu2xIS8GPl890cosnzIgC/FMcGaTgiAgA5vnDN9wrssStyoj+AhbAtJAsggKZAAAAJXRFWHRkYXRlOmNyZWF0ZQAyMDIwLTAzLTI0VDE2OjMyOjM5KzAwOjAwFC32vAAAACV0RVh0ZGF0ZTptb2RpZnkAMjAyMC0wMy0yNFQxNjozMjozOSswMDowMGVwTgAAAAAASUVORK5CYII='
, 1, 'D1DD4FF9-790A-4FD1-8E34-5B90D625ECBB')
GO

CREATE TABLE Product
(
    Id            INT              PRIMARY KEY IDENTITY (1,1),
    UserAccountId INT              FOREIGN KEY REFERENCES UserAccount(Id) NOT NULL,
    Header        NVARCHAR(255)    NOT NULL,
    Description   NVARCHAR(255)    NOT NULL,
    Price         MONEY            NOT NULL,
    Quantity      INT              NOT NULL,
    AdditionDate  DATETIME         NOT NULL,
    Photo         NVARCHAR(MAX),
    IsActive      BIT              NOT NULL,
    UniqueId      UNIQUEIDENTIFIER NOT NULL
)
GO
ALTER TABLE Product
ADD Discount FLOAT NOT NULL
DEFAULT 0
GO

INSERT INTO Product
    (UserAccountId, Header, Description, Price, Quantity, AdditionDate, Photo, IsActive, UniqueId)
VALUES
    (1, 'Test ??r??n??', 'test a????klama', '1', 1, '2023-01-07 01:07:01', 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAADbUlEQVRYhe2VUWhbZRTHf+c2XSUxsWt6k9skrYU9zFmsPhTclCH4tD4IDpxDkcnwRXD45pMgKiL6JnsU8UHfHMOBWDaGMGG+OBC0rA5EiduNzb2pbeySNU1zv+NDkxmTrLnB+iDu/3Tvd/73f/7fuec7H9zF/x0yCNm2s4+IJZeAQJFvRPUaltZQcVAeQHhY0YWSV3juXzGQSuUWEOabr5ugyyBVYBTItonOep67GEbTCpvccaanEY60LY2AFIFKe3IAVV4IqxvagDGNE3RX7CDwaA/682G1QxtoioaDkLMnJh/fNQMTE/cfQNgf2gBgmdu9shOGIqnU5NMimhwe5nPXdVd7sYLAzA/WrqCYuTslte2pQyLmKbX4OuL7N87ZTvaZ+pZcTTm570X1vDFcKpUKPwAGQMQYRerAnvAW5KHmg+U4UweMCQ4jchg4gphfRXnRL7qLt/eVyWTGg8B6X+EkIAJrBrkMLIrqjyK6bETmRDkEPAbY/T1wHuUg28cU4A9U3vb9G6eBxjalA44z9WCgwRuCHKN3jxhgtRkbC1ULWFPhdL0W+aBczpc7Yr0xls3mIlvWSUTngVkgFiZZm8lbwFeono1Gh8/k8/naHcz1h53OfWQhFxXzHsheIE7v6mwCqwJnDRQsrM887/ovO2lHwhgQkS0T1L5U1YWVlZWbzeWh0dHpeItTLucrQCOTyUSBqNmy5lR0FvhnBtLpdEwxVbFGfhJwUulcA6iC1qBRBCmBzqTSuShwTyNgBABLXkI02U+/7yDyPG8TLFtoCm+bjgOo6DuWmHdR1oH7+ItjRPRZjF7upz/UjwCYvaOJC0FAAqHWTBIHiQsyoyIO8GQbv4ryrbGC10reb1f6iYcaxa7rbmCxD3iC7Zuv9d0M2jWiYwije0Suh9EOfRmpsgZcBeodoX1dZKHQCKzldHry+K4ZKHnuy8DvdI/j7kYzfAhEFH111wwAAcJ3IXhLqpsXgarAx7tpgPGxxOsIF3ag1E2gR0ulUgXVM8lk4tN+mgNespBM5rJDEa4B93YFlTd9330LwHEcu1gslvrphTmGf8PGxvrNWCxeQORoR+ic77unAAWoVCq3wugN9Ata8P3CJ4i80kqmyBfjycRxIBhUa+AKtFCtrF+JxRI/Ixqxk4ljS0tLncfzLv4b+BP4mzL9+mAIZwAAAABJRU5ErkJggg==', 1, '5B6C6E1E-E0A0-48BF-AC48-1E7E88CCA975')
GO

DROP TABLE IF EXISTS BillProduct
GO
DROP TABLE IF EXISTS Bill
GO
CREATE TABLE Bill
(
    Id           INT              PRIMARY KEY IDENTITY(1,1),
    BuyerId      INT              FOREIGN KEY REFERENCES UserAccount(Id) NOT NULL,
    TotalPrice   MONEY            NOT NULL,
    CreationDate DATETIME         NOT NULL,
    IsApproved   BIT              NOT NULL,
    UniqueId     UNIQUEIDENTIFIER NOT NULL
)
GO

INSERT INTO Bill
    (BuyerId, TotalPrice, CreationDate, IsApproved, UniqueId)
VALUES
    (1, 2, '2023-01-09 03:12:12', 1, 'B515E9D0-0BAB-4E77-AF87-BFB329ADCEC1')
GO

CREATE TABLE BillProduct
(
    Id           INT   PRIMARY KEY IDENTITY(1,1),
    SupplierId   INT   FOREIGN KEY REFERENCES UserAccount(Id) NOT NULL,
    BillId       INT   FOREIGN KEY REFERENCES Bill(Id) NOT NULL,
    ProductId    INT   FOREIGN KEY REFERENCES Product(Id) NOT NULL,
    Quantity     INT   NOT NULL,
    CurrentPrice MONEY NOT NULL,
)
GO

INSERT INTO BillProduct
    (SupplierId, BillId, ProductId, Quantity, CurrentPrice)
VALUES
    (1, 1, 1, 1, 2)
GO

DROP TABLE IF EXISTS Cart
GO
CREATE TABLE Cart
(
    Id        INT              PRIMARY KEY IDENTITY(1,1),
    BuyerId   INT              FOREIGN KEY REFERENCES UserAccount(Id) NOT NULL,
    ProductId INT              FOREIGN KEY REFERENCES Product(Id) NOT NULL,
    Quantity  INT              NOT NULL,
    UniqueId  UNIQUEIDENTIFIER NOT NULL
)
GO

INSERT INTO Cart
    (BuyerId, ProductId, Quantity, UniqueId)
VALUES
    (1, 1, 1, 'FE7C556E-595B-4E8E-80BD-83B6B6171B80')
GO

DROP TABLE IF EXISTS ProductPhoto
GO
CREATE TABLE ProductPhoto
(
    Id        INT              PRIMARY KEY IDENTITY(1,1),
    ProductId INT              FOREIGN KEY REFERENCES Product(Id) NOT NULL,
    Photo     NVARCHAR(MAX)    NOT NULL,
    UniqueId  UNIQUEIDENTIFIER NOT NULL
)
GO

INSERT INTO ProductPhoto
    (ProductId, Photo, UniqueId)
VALUES
    (1, 'data:image/png;base64,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', '4B2CC5AB-5A98-492B-9403-7F52E1E25A03')
GO

INSERT INTO ProductPhoto
    (ProductId, Photo, UniqueId)
VALUES
    (1, 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAADbUlEQVRYhe2VUWhbZRTHf+c2XSUxsWt6k9skrYU9zFmsPhTclCH4tD4IDpxDkcnwRXD45pMgKiL6JnsU8UHfHMOBWDaGMGG+OBC0rA5EiduNzb2pbeySNU1zv+NDkxmTrLnB+iDu/3Tvd/73f/7fuec7H9zF/x0yCNm2s4+IJZeAQJFvRPUaltZQcVAeQHhY0YWSV3juXzGQSuUWEOabr5ugyyBVYBTItonOep67GEbTCpvccaanEY60LY2AFIFKe3IAVV4IqxvagDGNE3RX7CDwaA/682G1QxtoioaDkLMnJh/fNQMTE/cfQNgf2gBgmdu9shOGIqnU5NMimhwe5nPXdVd7sYLAzA/WrqCYuTslte2pQyLmKbX4OuL7N87ZTvaZ+pZcTTm570X1vDFcKpUKPwAGQMQYRerAnvAW5KHmg+U4UweMCQ4jchg4gphfRXnRL7qLt/eVyWTGg8B6X+EkIAJrBrkMLIrqjyK6bETmRDkEPAbY/T1wHuUg28cU4A9U3vb9G6eBxjalA44z9WCgwRuCHKN3jxhgtRkbC1ULWFPhdL0W+aBczpc7Yr0xls3mIlvWSUTngVkgFiZZm8lbwFeono1Gh8/k8/naHcz1h53OfWQhFxXzHsheIE7v6mwCqwJnDRQsrM887/ovO2lHwhgQkS0T1L5U1YWVlZWbzeWh0dHpeItTLucrQCOTyUSBqNmy5lR0FvhnBtLpdEwxVbFGfhJwUulcA6iC1qBRBCmBzqTSuShwTyNgBABLXkI02U+/7yDyPG8TLFtoCm+bjgOo6DuWmHdR1oH7+ItjRPRZjF7upz/UjwCYvaOJC0FAAqHWTBIHiQsyoyIO8GQbv4ryrbGC10reb1f6iYcaxa7rbmCxD3iC7Zuv9d0M2jWiYwije0Suh9EOfRmpsgZcBeodoX1dZKHQCKzldHry+K4ZKHnuy8DvdI/j7kYzfAhEFH111wwAAcJ3IXhLqpsXgarAx7tpgPGxxOsIF3ag1E2gR0ulUgXVM8lk4tN+mgNespBM5rJDEa4B93YFlTd9330LwHEcu1gslvrphTmGf8PGxvrNWCxeQORoR+ic77unAAWoVCq3wugN9Ata8P3CJ4i80kqmyBfjycRxIBhUa+AKtFCtrF+JxRI/Ixqxk4ljS0tLncfzLv4b+BP4mzL9+mAIZwAAAABJRU5ErkJggg==', '6895E16F-456F-421F-BBDB-439362075CAE')
GO