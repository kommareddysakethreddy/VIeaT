-- This script runs automatically the first time the PostgreSQL container starts.
-- It creates one database per microservice, which keeps the beginner setup clean:
-- customer-service  -> customerdb
-- inventory-service -> inventorydb
-- order-service     -> orderdb
--
-- We do this in one PostgreSQL container because that is simpler than maintaining
-- three separate database containers while still showing good microservice separation.

CREATE DATABASE customerdb OWNER food_user;
CREATE DATABASE inventorydb OWNER food_user;
CREATE DATABASE orderdb OWNER food_user;
CREATE DATABASE authdb OWNER food_user;
CREATE DATABASE paymentdb OWNER food_user;
CREATE DATABASE notificationdb OWNER food_user;
