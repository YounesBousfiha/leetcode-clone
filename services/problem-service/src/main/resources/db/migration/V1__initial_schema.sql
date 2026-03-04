-- Problem Service Initial Schema
-- V1__initial_schema.sql

-- Create problems table
CREATE TABLE IF NOT EXISTS problems (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) UNIQUE NOT NULL,
    slug VARCHAR(255) UNIQUE NOT NULL,
    description TEXT NOT NULL,
    difficulty VARCHAR(50) NOT NULL,
    time_limit DOUBLE PRECISION,
    memory_limit INTEGER,
    CHECK (difficulty IN ('EASY', 'MEDIUM', 'HARD'))
);

-- Create indexes for faster queries
CREATE INDEX idx_problems_slug ON problems(slug);
CREATE INDEX idx_problems_difficulty ON problems(difficulty);
CREATE INDEX idx_problems_title ON problems(title);

-- Create tags table
CREATE TABLE IF NOT EXISTS tags (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) UNIQUE NOT NULL,
    slug VARCHAR(100) UNIQUE NOT NULL
);

-- Create indexes
CREATE INDEX idx_tags_slug ON tags(slug);
CREATE INDEX idx_tags_name ON tags(name);

-- Create problem_tags join table
CREATE TABLE IF NOT EXISTS problem_tags (
    problem_id UUID NOT NULL,
    tag_id UUID NOT NULL,
    PRIMARY KEY (problem_id, tag_id),
    CONSTRAINT fk_problem_tags_problem FOREIGN KEY (problem_id) REFERENCES problems(id) ON DELETE CASCADE,
    CONSTRAINT fk_problem_tags_tag FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);

-- Create test_cases table
CREATE TABLE IF NOT EXISTS test_cases (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    input TEXT NOT NULL,
    expected_output TEXT NOT NULL,
    is_public BOOLEAN DEFAULT FALSE,
    problem_id UUID NOT NULL,
    CONSTRAINT fk_test_cases_problem FOREIGN KEY (problem_id) REFERENCES problems(id) ON DELETE CASCADE
);

-- Create index for faster lookups
CREATE INDEX idx_test_cases_problem_id ON test_cases(problem_id);
CREATE INDEX idx_test_cases_public ON test_cases(is_public);

-- Create code_template table
CREATE TABLE IF NOT EXISTS code_template (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    language VARCHAR(50) NOT NULL,
    boilerplate_code TEXT NOT NULL,
    problem_id UUID NOT NULL,
    CONSTRAINT fk_code_template_problem FOREIGN KEY (problem_id) REFERENCES problems(id) ON DELETE CASCADE
);

-- Create index
CREATE INDEX idx_code_template_problem_id ON code_template(problem_id);
CREATE INDEX idx_code_template_language ON code_template(language);

-- Create problem_hints table
CREATE TABLE IF NOT EXISTS problem_hints (
    problem_id UUID NOT NULL,
    hints TEXT NOT NULL,
    CONSTRAINT fk_problem_hints_problem FOREIGN KEY (problem_id) REFERENCES problems(id) ON DELETE CASCADE
);

-- Insert sample tags
INSERT INTO tags (id, name, slug) VALUES
    (gen_random_uuid(), 'Array', 'array'),
    (gen_random_uuid(), 'String', 'string'),
    (gen_random_uuid(), 'Hash Table', 'hash-table'),
    (gen_random_uuid(), 'Dynamic Programming', 'dynamic-programming'),
    (gen_random_uuid(), 'Math', 'math'),
    (gen_random_uuid(), 'Sorting', 'sorting'),
    (gen_random_uuid(), 'Greedy', 'greedy'),
    (gen_random_uuid(), 'Depth-First Search', 'depth-first-search'),
    (gen_random_uuid(), 'Binary Search', 'binary-search'),
    (gen_random_uuid(), 'Tree', 'tree'),
    (gen_random_uuid(), 'Breadth-First Search', 'breadth-first-search'),
    (gen_random_uuid(), 'Two Pointers', 'two-pointers'),
    (gen_random_uuid(), 'Linked List', 'linked-list'),
    (gen_random_uuid(), 'Stack', 'stack'),
    (gen_random_uuid(), 'Recursion', 'recursion')
ON CONFLICT (slug) DO NOTHING;

