-- Sample inserts for student table
INSERT INTO student (id, last_name, first_name, given_name, chinese_last_name, chinese_first_name, phone_number, date_of_birth, sex, organiser_id, class_id, guardian_id, join_date, active, last_modified_date, created_date)
VALUES ('S001', 'Doe', 'John', 'Johnny', '杜', '約翰', '123-456-7890', '2005-01-01T00:00:00Z', 'MALE', 'org-001', 'class-001', 'guard-001', '2025-01-01T00:00:00Z', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO student (id, last_name, first_name, given_name, chinese_last_name, chinese_first_name, phone_number, date_of_birth, sex, organiser_id, class_id, guardian_id, join_date, active, last_modified_date, created_date)
VALUES ('S002', 'Smith', 'Jane', 'Janie', '史密斯', '簡', '987-654-3210', '2006-02-02T00:00:00Z', 'FEMALE', 'org-002', 'class-002', 'guard-002', '2025-02-01T00:00:00Z', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Sample inserts for student_profile table (IDs will be auto-generated)
INSERT INTO student_profile (student_id, active, last_modified_date, created_date)
VALUES ('S001', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO student_profile (student_id, active, last_modified_date, created_date)
VALUES ('S002', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Sample inserts for topic_mastery table (assuming profile_id 1 from previous student_profile insert; adjust if needed based on actual generated IDs)

INSERT INTO topic_mastery (profile_id, topic_id, theta, mastery_score, active, last_modified_date, created_date)
VALUES (1, 1, -0.5581634675647462, 24, TRUE, '2025-08-29 08:37:19.404366+00', '2025-08-29 08:37:19.404367+00');

INSERT INTO topic_mastery (profile_id, topic_id, theta, mastery_score, active, last_modified_date, created_date)
VALUES (1, 4, -1.4425933641094373, 5, TRUE, '2025-08-29 08:37:19.423545+00', '2025-08-29 08:37:19.423546+00');

INSERT INTO topic_mastery (profile_id, topic_id, theta, mastery_score, active, last_modified_date, created_date)
VALUES (1, 5, 1.931122675584979, 97, TRUE, '2025-08-29 08:37:19.430867+00', '2025-08-29 08:37:19.430868+00');

INSERT INTO topic_mastery (profile_id, topic_id, theta, mastery_score, active, last_modified_date, created_date)
VALUES (1, 3, 0.2622695964410195, 62, TRUE, '2025-08-29 08:37:19.444621+00', '2025-08-29 08:37:19.444621+00');

INSERT INTO topic_mastery (profile_id, topic_id, theta, mastery_score, active, last_modified_date, created_date)
VALUES (1, 2, 0.5581634580351905, 75, TRUE, '2025-08-29 08:37:19.466987+00', '2025-08-29 08:37:19.466989+00');