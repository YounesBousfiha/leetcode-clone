-- User Service Initial Schema
-- V1__initial_schema.sql

-- Create user_profiles table
CREATE TABLE IF NOT EXISTS user_profiles (
    id UUID PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    display_name VARCHAR(255) NOT NULL,
    bio TEXT,
    github_url VARCHAR(255),
    linkedin_url VARCHAR(255),
    score BIGINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for faster lookups
CREATE INDEX idx_user_profiles_email ON user_profiles(email);
CREATE INDEX idx_user_profiles_score ON user_profiles(score DESC);

-- Create user_statistics table
CREATE TABLE IF NOT EXISTS user_statistics (
    user_id UUID PRIMARY KEY,
    total_problems_solved INTEGER DEFAULT 0,
    easy_solved INTEGER DEFAULT 0,
    medium_solved INTEGER DEFAULT 0,
    hard_solved INTEGER DEFAULT 0,
    total_submissions INTEGER DEFAULT 0,
    accepted_submissions INTEGER DEFAULT 0,
    current_streak INTEGER DEFAULT 0,
    longest_streak INTEGER DEFAULT 0,
    last_solved_date DATE,
    created_at DATE DEFAULT CURRENT_DATE,
    CONSTRAINT fk_user_statistics_profile FOREIGN KEY (user_id) REFERENCES user_profiles(id) ON DELETE CASCADE
);

-- Create indexes for statistics queries
CREATE INDEX idx_user_statistics_total_solved ON user_statistics(total_problems_solved DESC);
CREATE INDEX idx_user_statistics_streak ON user_statistics(current_streak DESC);

