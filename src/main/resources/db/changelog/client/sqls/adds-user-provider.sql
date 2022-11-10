alter table if exists auth."user"
    rename column "full_name" to "name";

ALTER TABLE IF EXISTS auth.user
    ADD COLUMN IF NOT EXISTS provider VARCHAR
        CHECK ( provider IN ('google', 'github', 'local') ) DEFAULT 'local' NOT NULL;

ALTER TABLE auth."user"
    DROP CONSTRAINT IF EXISTS client_email_unique;

ALTER TABLE auth."user"
    DROP CONSTRAINT IF EXISTS user_email_provider_unique;
ALTER TABLE auth."user"
    ADD CONSTRAINT user_email_provider_unique UNIQUE (email, provider);