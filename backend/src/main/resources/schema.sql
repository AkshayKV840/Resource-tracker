CREATE DATABASE IF NOT EXISTS resource_tracker;
USE resource_tracker;

CREATE TABLE IF NOT EXISTS resources (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    role VARCHAR(100),
    task VARCHAR(255),
    project VARCHAR(100),
    story_status VARCHAR(50) DEFAULT 'In Progress',
    end_date DATE,
    days_until_free INT,
    availability_status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
