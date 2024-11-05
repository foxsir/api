-- This file allow to write SQL commands that will be emitted in test and dev.
-- The commands are commented as their support depends of the database
-- insert into myentity (id, field) values(1, 'field-1');
-- insert into myentity (id, field) values(2, 'field-2');
-- insert into myentity (id, field) values(3, 'field-3');
-- alter sequence myentity_seq restart with 4;

INSERT INTO public."user" (sex, when_created, when_deleted, when_modified, id, phone_number, username, avatar, password, role) VALUES (null, '2024-05-04 10:38:01.699416 +00:00', null, '2024-05-04 10:38:01.699467 +00:00', '36e149e3-ed38-42ef-83ca-fad1f9f10303', null, 'opensponsor', null, '$2a$10$q7ItLTa4RRHMjlYeUw98Bejt3CzQNW17lGmpx/.k3FoFXWZKpcKpG', null);

INSERT INTO "public"."wallet" ("balance", "when_created", "when_deleted", "when_modified", "id", "user_id") VALUES (1, '2024-11-02 16:55:30.397166+08', NULL, '2024-11-02 16:55:30.397189+08', 'f850bc51-36c5-4a50-8a69-f4e9bc952d65', '36e149e3-ed38-42ef-83ca-fad1f9f10303');


-- gen history
INSERT INTO "public"."history" ("when_created", "when_deleted", "when_modified", "id", "user_id", "request_id", "prompt") VALUES ('2024-11-04 18:06:02.455234+08', NULL, '2024-11-04 18:06:02.455239+08', '5f74dc34-d9f9-4da5-b8cb-ae6beef2212e', '36e149e3-ed38-42ef-83ca-fad1f9f10303', 'c43ed5ed-78e1-45a9-a0b0-3ca0a06c6142', 'the black cat on table.');
INSERT INTO "public"."history" ("when_created", "when_deleted", "when_modified", "id", "user_id", "request_id", "prompt") VALUES ('2024-11-04 18:06:38.067484+08', NULL, '2024-11-04 18:06:38.067492+08', '453b8a78-11ce-42ab-a76c-7e7ac59966e3', '36e149e3-ed38-42ef-83ca-fad1f9f10303', '314a3157-c6c3-40e0-be66-d6d737aea600', 'the black cat on table.');
INSERT INTO "public"."history" ("when_created", "when_deleted", "when_modified", "id", "user_id", "request_id", "prompt") VALUES ('2024-11-04 19:05:56.156986+08', NULL, '2024-11-04 19:05:56.156992+08', 'fdfdfc85-06d1-4a84-8b81-a0e45ee59d47', '36e149e3-ed38-42ef-83ca-fad1f9f10303', '36be546a-9c17-4c48-8fde-0f8e7a462dd6', 'the black cat on table.');
INSERT INTO "public"."history" ("when_created", "when_deleted", "when_modified", "id", "user_id", "request_id", "prompt") VALUES ('2024-11-04 19:06:28.077001+08', NULL, '2024-11-04 19:06:28.077006+08', '0e5affe3-2df4-4fcb-b529-7904d894e1d3', '36e149e3-ed38-42ef-83ca-fad1f9f10303', 'f462d25b-17f5-415c-8e4b-f6c108c062b8', 'the black cat on table.');