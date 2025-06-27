INSERT INTO user_accounts (role,
                           nickname,
                           status_message,
                           profile_image_url,
                           last_login_date,
                           created_at,
                           updated_at,
                           deleted_at)
VALUES ('ADMIN', 'admin', '관리자 계정', NULL, CURRENT_DATE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
       ('USER', 'alice', '안녕하세요, Alice입니다!', '/images/alice.png', CURRENT_DATE, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP, NULL),
       ('USER', 'bob', 'Bob the builder', NULL, CURRENT_DATE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
        NULL);

INSERT INTO user_stats (user_id,
                        rating,
                        current_streak,
                        longest_streak,
                        solved_problems_count,
                        rival_count,
                        created_at,
                        updated_at,
                        deleted_at)
VALUES (1, 2500, 10, 15, 200, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
       (2, 1500, 3, 8, 75, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
       (3, 1200, 0, 0, 10, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL);
-- Gyms
INSERT INTO gyms (gym_id, name, latitude, longitude, address, phone_number, description,
                  map_2d_url, created_at, updated_at)
VALUES (1, 'ClimbX Seoul', 37.5665, 126.9780, '123 Seoul St', '02-1234-5678', 'Best gym in Seoul',
        'http://map1.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (2, 'Boulder Base', 37.5796, 126.9770, '456 Mapo-gu', '010-2345-6789', 'Bouldering only',
        'http://map2.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (3, 'Peak Gym', 37.5700, 126.9820, '789 Jongno-gu', '031-3456-7890', 'Family friendly',
        'http://map3.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (4, 'Summit Climb', 37.5610, 126.9950, '101 Gangnam-daero', '02-4567-8901',
        'Advanced routes', 'http://map4.com', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       (5, 'Urban Grip', 37.5550, 126.9700, '202 Itaewon-ro', '010-5678-9012', 'Downtown location',
        'http://map5.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO problems (problem_rating,
                      created_at,
                      updated_at,
                      deleted_at)
VALUES (5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
       (1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
       (2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
       (1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
       (4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
       (3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL);

INSERT INTO videos (user_id,
                    created_at,
                    updated_at,
                    deleted_at)
VALUES (1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
       (2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
       (3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
       (2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
       (2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
       (2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
       (2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
       (2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
       (2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
       (2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL);

INSERT INTO submissions (video_id,
                         problem_id,
                         status,
                         reject_reason,
                         appeal_status,
                         created_at,
                         updated_at,
                         deleted_at)
VALUES (1,  3, 'ACCEPTED', NULL, 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
       (2,  2, 'REJECTED', 'Insufficient explanation', 'REJECTED', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP, NULL),
       (3, 1, 'PENDING', NULL, 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
       (4, 1, 'ACCEPTED', NULL, 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
       (5, 2, 'ACCEPTED', 'Duplicate submission', 'REJECTED', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP, NULL),
       (6, 3, 'ACCEPTED', NULL, 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
       (7, 1, 'ACCEPTED', NULL, 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
       (8, 2, 'REJECTED', 'Incorrect solution', 'REJECTED', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP, NULL),
       (9, 3, 'ACCEPTED', NULL, 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
       (10, 1, 'ACCEPTED', NULL, 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL);

COMMIT;