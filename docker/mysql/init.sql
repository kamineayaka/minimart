-- Run once on the host MySQL (not via Docker):
--   mysql -u root -p < docker/mysql/init.sql
-- Table migrations belong in each service's Flyway, not here.

CREATE DATABASE IF NOT EXISTS minimart_member CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS minimart_product CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS minimart_order CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS minimart_payment CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'minimart'@'%' IDENTIFIED BY 'minimart';
GRANT ALL PRIVILEGES ON minimart_member.* TO 'minimart'@'%';
GRANT ALL PRIVILEGES ON minimart_product.* TO 'minimart'@'%';
GRANT ALL PRIVILEGES ON minimart_order.* TO 'minimart'@'%';
GRANT ALL PRIVILEGES ON minimart_payment.* TO 'minimart'@'%';
FLUSH PRIVILEGES;
