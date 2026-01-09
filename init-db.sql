-- Create databases
CREATE DATABASE IF NOT EXISTS energyDB;
CREATE DATABASE IF NOT EXISTS eauDB;

-- Use energyDB
USE energyDB;

-- Grant privileges (optional, for security)
GRANT ALL PRIVILEGES ON energyDB.* TO 'root'@'%';

-- Use eauDB
USE eauDB;

-- Grant privileges (optional, for security)
GRANT ALL PRIVILEGES ON eauDB.* TO 'root'@'%';

FLUSH PRIVILEGES;
