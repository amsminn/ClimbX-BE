INSERT INTO user_accounts (user_id,
                           role,
                           nickname,
                           status_message,
                           profile_image_url,
                           last_login_date,
                           created_at,
                           updated_at,
                           deleted_at)
VALUES (1, 'ADMIN', 'admin', '관리자 계정', NULL, CURRENT_DATE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
        NULL),
       (2, 'USER', 'alice', '안녕하세요, Alice입니다!', '/images/alice.png', CURRENT_DATE,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
       (3, 'USER', 'bob', 'Bob the builder', NULL, CURRENT_DATE, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP, NULL);

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
INSERT INTO gyms (gym_id, name, latitude, longitude, address, phone_number, description, open_time,
                  close_time, 2d_map_url, created_at, updated_at)
VALUES (1, 'ClimbX Seoul', 37.5665, 126.9780, '123 Seoul St', '02-1234-5678', 'Best gym in Seoul',
        '09:00', '22:00', 'http://map1.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (2, 'Boulder Base', 37.5796, 126.9770, '456 Mapo-gu', '010-2345-6789', 'Bouldering only',
        '10:00', '21:00', 'http://map2.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (3, 'Peak Gym', 37.5700, 126.9820, '789 Jongno-gu', '031-3456-7890', 'Family friendly',
        '08:00', '23:00', 'http://map3.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (4, 'Summit Climb', 37.5610, 126.9950, '101 Gangnam-daero', '02-4567-8901',
        'Advanced routes', '07:00', '20:00', 'http://map4.com', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       (5, 'Urban Grip', 37.5550, 126.9700, '202 Itaewon-ro', '010-5678-9012', 'Downtown location',
        '11:00', '22:30', 'http://map5.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

COMMIT;