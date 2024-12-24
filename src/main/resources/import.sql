-- This file allow to write SQL commands that will be emitted in test and dev.
-- The commands are commented as their support depends of the database
-- insert into myentity (id, field) values(1, 'field-1');
-- insert into myentity (id, field) values(2, 'field-2');
-- insert into myentity (id, field) values(3, 'field-3');
-- alter sequence myentity_seq restart with 4;

INSERT INTO public."user" (sex, when_created, when_deleted, when_modified, id, phone_number, username, avatar, password, role) VALUES (null, '2024-05-04 10:38:01.699416 +00:00', null, '2024-05-04 10:38:01.699467 +00:00', '36e149e3-ed38-42ef-83ca-fad1f9f10303', '16631132230', '16631132230', null, '$2a$10$q7ItLTa4RRHMjlYeUw98Bejt3CzQNW17lGmpx/.k3FoFXWZKpcKpG', null);
INSERT INTO "public"."user_token" ("when_created", "when_deleted", "when_modified", "id", "user_id", "token") VALUES (NULL, NULL, NULL, '36e149e3-ed38-42ef-83ca-fad1f9910503', '36e149e3-ed38-42ef-83ca-fad1f9f10303', 'eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwczovL3Zpc2lvbmJhZ2VsLmNvbSIsInVwbiI6Im9wZW5zcG9uc29yIiwiZ3JvdXBzIjpbIlVzZXIiXSwiYmlydGhkYXRlIjoiMjAyNS0xMC0yOSIsImlhdCI6MTczMDIwMTAyNCwiZXhwIjoxNzMyNzkzMDI0LCJqdGkiOiIxY2NlZGNhOS1mODU3LTRlNzQtYjNlMy05MTlmM2VmNmE5OTkifQ.HsAJcjgU75m4oviayTOFMgSnKAnyozxy7rHzChMebqe3FIi_7yBAhPAcF2WMXfB-IjjDXQlfprhKVzFOYyV3QOUy_dLrfQLD-Nhr0QUhFYKm0w9dqtkoIbNxeMGIufTxLsNaKiue__UAejFjIphoJ7HzZayBonI0h2bESU0Wsl_BVBrT407m_lZ3Q7CC2fUOxHueQBpCSfRKUR5eKfMRcX8hf0nGvPrywMG40zwEkJVFLZmUYYLr1KRGdd3JevZcpWCFE42wEFQn_vEkqYtNBNWXgLy6GAB7-8JkwKeRXRuhlghsJkQBsdRgMAD3ZM6HmkgyClT5D7ke8mQDrcSKgg');

INSERT INTO "public"."wallet" ("balance", "when_created", "when_deleted", "when_modified", "id", "user_id") VALUES (10, '2024-11-02 16:55:30.397166+08', NULL, '2024-11-02 16:55:30.397189+08', 'f850bc51-36c5-4a50-8a69-f4e9bc952d65', '36e149e3-ed38-42ef-83ca-fad1f9f10303');

-- gen history
INSERT INTO "public"."history" ("when_created", "when_deleted", "when_modified", "id", "user_id", "request_id", "prompt") VALUES ('2024-12-18 19:48:12.635003+08', NULL, '2024-12-18 19:48:12.635011+08', '4301d95a-6f89-48a5-b43f-f637ae87e795', '36e149e3-ed38-42ef-83ca-fad1f9f10303', 'a70744f5-f142-4685-8046-ee11b883dfea', 'cat, print style');
INSERT INTO "public"."fal_image" ("height", "when_created", "when_deleted", "when_modified", "width", "content_type", "id", "request_id", "url") VALUES (768, '2024-12-18 19:48:12.636208+08', NULL, '2024-12-18 19:48:12.636216+08', 1024, 'image/png', '65f249fb-a4d6-4fab-8851-b24eb51e6f81', 'a70744f5-f142-4685-8046-ee11b883dfea', 'http://visionbagel.media.microprofile.cn/a598bc7b-9223-4e7c-b0bc-66ea9233a040.png');
-- end gen history

-- sms
INSERT INTO "public"."sms_code" ("code", "effective", "when_created", "when_deleted", "when_modified", "mobile", "id") VALUES ('1031', 't', '2024-11-12 16:13:04.195817+08', NULL, '2024-11-12 16:13:04.195827+08', '16631132230', '22ee50b8-d247-4424-98aa-20aced67d85d');


-- trade
INSERT INTO "public"."trade" ("money", "pay_status", "when_created", "when_deleted", "when_modified", "id", "user_id", "trade_no") VALUES ('1.00', 'f', '2024-11-15 14:52:53.950117+08', NULL, '2024-11-15 14:52:53.950142+08', '82550604-c50c-46ea-8b73-14a407e5ad1a', NULL, '20241115865683000');
