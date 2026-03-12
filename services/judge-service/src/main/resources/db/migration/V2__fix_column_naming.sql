-- Fix column naming inconsistency
-- V2__fix_column_naming.sql

-- Drop the old table if userid column exists (wrong naming)
-- This is safe in dev environment as we're using seeders
DO $$
BEGIN
    -- Check if the wrong column name exists
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name='submissions'
        AND column_name='userid'
    ) THEN
        -- Drop and recreate with correct naming
        DROP TABLE IF EXISTS submission_results CASCADE;
        DROP TABLE IF EXISTS submissions CASCADE;

        -- Recreate submissions table with correct naming
        CREATE TABLE submissions (
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

        -- Create indexes
        CREATE INDEX idx_submissions_user_id ON submissions(user_id);
        CREATE INDEX idx_submissions_problem_id ON submissions(problem_id);
        CREATE INDEX idx_submissions_user_problem ON submissions(user_id, problem_id);
        CREATE INDEX idx_submissions_status ON submissions(status);
        CREATE INDEX idx_submissions_created_at ON submissions(created_at DESC);

        -- Recreate submission_results table
        CREATE TABLE submission_results (
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

        -- Create indexes
        CREATE INDEX idx_submission_results_submission_id ON submission_results(submission_id);
        CREATE INDEX idx_submission_results_passed ON submission_results(passed);
    END IF;
END $$;

