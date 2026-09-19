CREATE DATABASE IF NOT EXISTS minimart_member CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS minimart_product CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS minimart_order CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'minimart'@'%' IDENTIFIED BY 'minimart';
GRANT ALL PRIVILEGES ON minimart_member.* TO 'minimart'@'%';
GRANT ALL PRIVILEGES ON minimart_product.* TO 'minimart'@'%';
GRANT ALL PRIVILEGES ON minimart_order.* TO 'minimart'@'%';
FLUSH PRIVILEGES;
