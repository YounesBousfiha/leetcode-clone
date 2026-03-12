-- Rename problem_id column to problem_slug to reflect it stores a slug, not a UUID
-- V3__rename_problem_id_to_problem_slug.sql

ALTER TABLE submissions RENAME COLUMN problem_id TO problem_slug;

-- Recreate indexes with correct naming
DROP INDEX IF EXISTS idx_submissions_problem_id;
DROP INDEX IF EXISTS idx_submissions_user_problem;

CREATE INDEX idx_submissions_problem_slug ON submissions(problem_slug);
CREATE INDEX idx_submissions_user_problem_slug ON submissions(user_id, problem_slug);

