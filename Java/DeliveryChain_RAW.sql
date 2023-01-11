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
    /* USER_COMMENT WILL BE ADDED */
)
GO
INSERT INTO Product
    (UserAccountId, Header, Description, Price, Quantity, AdditionDate, Photo, IsActive, UniqueId)
VALUES
    (1, 'Test Ürünü', 'test açıklama', '1', 1, '2023-01-07 01:07:01', 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAADbUlEQVRYhe2VUWhbZRTHf+c2XSUxsWt6k9skrYU9zFmsPhTclCH4tD4IDpxDkcnwRXD45pMgKiL6JnsU8UHfHMOBWDaGMGG+OBC0rA5EiduNzb2pbeySNU1zv+NDkxmTrLnB+iDu/3Tvd/73f/7fuec7H9zF/x0yCNm2s4+IJZeAQJFvRPUaltZQcVAeQHhY0YWSV3juXzGQSuUWEOabr5ugyyBVYBTItonOep67GEbTCpvccaanEY60LY2AFIFKe3IAVV4IqxvagDGNE3RX7CDwaA/682G1QxtoioaDkLMnJh/fNQMTE/cfQNgf2gBgmdu9shOGIqnU5NMimhwe5nPXdVd7sYLAzA/WrqCYuTslte2pQyLmKbX4OuL7N87ZTvaZ+pZcTTm570X1vDFcKpUKPwAGQMQYRerAnvAW5KHmg+U4UweMCQ4jchg4gphfRXnRL7qLt/eVyWTGg8B6X+EkIAJrBrkMLIrqjyK6bETmRDkEPAbY/T1wHuUg28cU4A9U3vb9G6eBxjalA44z9WCgwRuCHKN3jxhgtRkbC1ULWFPhdL0W+aBczpc7Yr0xls3mIlvWSUTngVkgFiZZm8lbwFeono1Gh8/k8/naHcz1h53OfWQhFxXzHsheIE7v6mwCqwJnDRQsrM887/ovO2lHwhgQkS0T1L5U1YWVlZWbzeWh0dHpeItTLucrQCOTyUSBqNmy5lR0FvhnBtLpdEwxVbFGfhJwUulcA6iC1qBRBCmBzqTSuShwTyNgBABLXkI02U+/7yDyPG8TLFtoCm+bjgOo6DuWmHdR1oH7+ItjRPRZjF7upz/UjwCYvaOJC0FAAqHWTBIHiQsyoyIO8GQbv4ryrbGC10reb1f6iYcaxa7rbmCxD3iC7Zuv9d0M2jWiYwije0Suh9EOfRmpsgZcBeodoX1dZKHQCKzldHry+K4ZKHnuy8DvdI/j7kYzfAhEFH111wwAAcJ3IXhLqpsXgarAx7tpgPGxxOsIF3ag1E2gR0ulUgXVM8lk4tN+mgNespBM5rJDEa4B93YFlTd9330LwHEcu1gslvrphTmGf8PGxvrNWCxeQORoR+ic77unAAWoVCq3wugN9Ata8P3CJ4i80kqmyBfjycRxIBhUa+AKtFCtrF+JxRI/Ixqxk4ljS0tLncfzLv4b+BP4mzL9+mAIZwAAAABJRU5ErkJggg==', 1, '5B6C6E1E-E0A0-48BF-AC48-1E7E88CCA975')
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