INSERT INTO user_accounts (
    user_id,
    role,
    nickname,
    status_message,
    profile_image_url,
    last_login_date,
    created_at,
    updated_at,
    deleted_at
) VALUES
      (1, 'ADMIN', 'admin', '관리자 계정', NULL, CURRENT_DATE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
      (2, 'USER',  'alice', '안녕하세요, Alice입니다!', '/images/alice.png', CURRENT_DATE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
      (3, 'USER',  'bob',   'Bob the builder', NULL, CURRENT_DATE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL);

INSERT INTO user_stats (
    user_id,
    rating,
    current_streak,
    longest_streak,
    solved_problems_count,
    rival_count,
    created_at,
    updated_at,
    deleted_at
) VALUES
      (1, 2500, 10, 15, 200, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
      (2, 1500,  3,  8,  75, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
      (3, 1200,  0,  0,  10, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL);

COMMIT;