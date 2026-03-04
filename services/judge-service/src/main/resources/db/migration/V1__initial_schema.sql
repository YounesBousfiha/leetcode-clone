-- Judge Service Initial Schema
-- V1__initial_schema.sql

-- Create submissions table
CREATE TABLE IF NOT EXISTS submissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    problem_id VARCHAR(255) NOT NULL,
    code TEXT NOT NULL,
    language VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    execution_time DOUBLE PRECISION,
    memory_used BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    CHECK (language IN ('JAVA', 'PYTHON', 'JAVASCRIPT', 'CPP', 'GO')),
    CHECK (status IN ('PENDING', 'PROCESSING', 'ACCEPTED', 'WRONG_ANSWER',
                     'TIME_LIMIT_EXCEEDED', 'MEMORY_LIMIT_EXCEEDED',
                     'COMPILATION_ERROR', 'RUNTIME_ERROR', 'INTERNAL_ERROR'))
);

-- Create indexes for faster queries
CREATE INDEX idx_submissions_user_id ON submissions(user_id);
CREATE INDEX idx_submissions_problem_id ON submissions(problem_id);
CREATE INDEX idx_submissions_user_problem ON submissions(user_id, problem_id);
CREATE INDEX idx_submissions_status ON submissions(status);
CREATE INDEX idx_submissions_created_at ON submissions(created_at DESC);

-- Create submission_results table
CREATE TABLE IF NOT EXISTS submission_results (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    test_case_id VARCHAR(255),
    passed BOOLEAN DEFAULT FALSE,
    output TEXT,
    expected_output TEXT,
    execution_time DOUBLE PRECISION,
    memory_used BIGINT,
    error_message TEXT,
    submission_id UUID NOT NULL,
    CONSTRAINT fk_submission_results_submission FOREIGN KEY (submission_id) REFERENCES submissions(id) ON DELETE CASCADE
);

-- Create index for faster lookups
CREATE INDEX idx_submission_results_submission_id ON submission_results(submission_id);
CREATE INDEX idx_submission_results_passed ON submission_results(passed);

