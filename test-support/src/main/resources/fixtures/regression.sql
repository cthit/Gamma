BEGIN;

INSERT INTO g_user (
    user_id, cid, password, nick, first_name, last_name, email, language,
    user_agreement_accepted, acceptance_year, version, locked, created_at, updated_at
)
VALUES
    ('88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f', 'mscott',    '{bcrypt}$2y$10$cMGfichgOT2zp8gfoS5wUOYvjQmqfXUYY8makiyyv.OqSNxdEK8bS', 'Boss',              'Michael',  'Scott',    'mscott@example.org',    'EN', '2026-01-01T00:00:00Z', 2005, 0, FALSE, '2026-01-01T00:00:00Z', '2026-01-01T00:00:00Z'),
    ('bc605869-9a4d-46ec-8a29-d00819d4c195', 'jhalpert',   '{bcrypt}$2y$10$cMGfichgOT2zp8gfoS5wUOYvjQmqfXUYY8makiyyv.OqSNxdEK8bS', 'Big Tuna',          'Jim',      'Halpert',  'jhalpert@example.org',   'EN', '2026-01-01T00:00:00Z', 2002, 0, FALSE, '2026-01-01T00:00:00Z', '2026-01-01T00:00:00Z'),
    ('ec8987d7-4087-461d-bed5-9365086b6e3b', 'pbeesly',    '{bcrypt}$2y$10$cMGfichgOT2zp8gfoS5wUOYvjQmqfXUYY8makiyyv.OqSNxdEK8bS', 'Pam-Pam',           'Pam',      'Beesly',   'pbeesly@example.org',    'EN', '2026-01-01T00:00:00Z', 2003, 0, FALSE, '2026-01-01T00:00:00Z', '2026-01-01T00:00:00Z'),
    ('0c67c90b-dfdf-473a-98e3-b551e2f2f0f1', 'dschrute',   '{bcrypt}$2y$10$cMGfichgOT2zp8gfoS5wUOYvjQmqfXUYY8makiyyv.OqSNxdEK8bS', 'Assistant Manager', 'Dwight',   'Schrute',  'dschrute@example.org',   'EN', '2026-01-01T00:00:00Z', 2001, 0, FALSE, '2026-01-01T00:00:00Z', '2026-01-01T00:00:00Z'),
    ('858e5acc-c289-40d3-9422-d6d317f40299', 'amartin',    '{bcrypt}$2y$10$cMGfichgOT2zp8gfoS5wUOYvjQmqfXUYY8makiyyv.OqSNxdEK8bS', 'Pumpkin',           'Angela',   'Martin',   'amartin@example.org',    'EN', '2026-01-01T00:00:00Z', 2008, 0, FALSE, '2026-01-01T00:00:00Z', '2026-01-01T00:00:00Z'),
    ('9ad8946d-cfef-4f6f-8b48-cfb536d0c9eb', 'shudson',    '{bcrypt}$2y$10$cMGfichgOT2zp8gfoS5wUOYvjQmqfXUYY8makiyyv.OqSNxdEK8bS', 'Papa Bear',         'Stanley',  'Hudson',   'shudson@example.org',    'EN', '2026-01-01T00:00:00Z', 2002, 0, FALSE, '2026-01-01T00:00:00Z', '2026-01-01T00:00:00Z'),
    ('4efb340f-540c-4b15-a362-d402aab10195', 'kmalone',    '{bcrypt}$2y$10$cMGfichgOT2zp8gfoS5wUOYvjQmqfXUYY8makiyyv.OqSNxdEK8bS', 'Chili King',        'Kevin',    'Malone',   'kmalone@example.org',    'EN', '2026-01-01T00:00:00Z', 2006, 0, FALSE, '2026-01-01T00:00:00Z', '2026-01-01T00:00:00Z'),
    ('08e4abb5-e4d6-413b-94f2-6e1aa63716e7', 'omartinez',  '{bcrypt}$2y$10$cMGfichgOT2zp8gfoS5wUOYvjQmqfXUYY8makiyyv.OqSNxdEK8bS', 'C-Span',            'Oscar',    'Martinez', 'omartinez@example.org',  'EN', '2026-01-01T00:00:00Z', 2008, 0, FALSE, '2026-01-01T00:00:00Z', '2026-01-01T00:00:00Z'),
    ('43af2838-43b9-4665-b3d7-9c615f5038fb', 'abernard',   '{bcrypt}$2y$10$cMGfichgOT2zp8gfoS5wUOYvjQmqfXUYY8makiyyv.OqSNxdEK8bS', 'Nard Dog',          'Andy',     'Bernard',  'abernard@example.org',   'EN', '2026-01-01T00:00:00Z', 2007, 0, FALSE, '2026-01-01T00:00:00Z', '2026-01-01T00:00:00Z'),
    ('4542ab3d-7996-4097-ae4a-4fe61eaf2f20', 'pvance',     '{bcrypt}$2y$10$cMGfichgOT2zp8gfoS5wUOYvjQmqfXUYY8makiyyv.OqSNxdEK8bS', 'Mama Goose',        'Phyllis',  'Vance',    'pvance@example.org',     'EN', '2026-01-01T00:00:00Z', 2006, 0, FALSE, '2026-01-01T00:00:00Z', '2026-01-01T00:00:00Z'),
    ('0a799f6d-c65a-4d20-8588-2ff5375d6cce', 'mpalmer',    '{bcrypt}$2y$10$cMGfichgOT2zp8gfoS5wUOYvjQmqfXUYY8makiyyv.OqSNxdEK8bS', 'Einstein',          'Meredith', 'Palmer',   'mpalmer@example.org',    'EN', '2026-01-01T00:00:00Z', 2003, 0, FALSE, '2026-01-01T00:00:00Z', '2026-01-01T00:00:00Z'),
    ('4fcf6566-45d8-4d5d-b7d4-4f6f52bb0ac2', 'kkapoor',    '{bcrypt}$2y$10$cMGfichgOT2zp8gfoS5wUOYvjQmqfXUYY8makiyyv.OqSNxdEK8bS', 'Business Woman',    'Kelly',    'Kapoor',   'kkapoor@example.org',    'EN', '2026-01-01T00:00:00Z', 2004, 0, FALSE, '2026-01-01T00:00:00Z', '2026-01-01T00:00:00Z'),
    ('e6a76e6a-3499-4611-ae28-e1281ffa6e80', 'cbratton',   '{bcrypt}$2y$10$cMGfichgOT2zp8gfoS5wUOYvjQmqfXUYY8makiyyv.OqSNxdEK8bS', 'Papa Smurf',        'Creed',    'Bratton',  'cbratton@example.org',   'EN', '2026-01-01T00:00:00Z', 2007, 0, FALSE, '2026-01-01T00:00:00Z', '2026-01-01T00:00:00Z');

INSERT INTO g_admin_user (created_at, user_id)
VALUES ('2026-01-01T00:00:00Z', '88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f');

INSERT INTO g_gdpr_trained (created_at, user_id)
VALUES ('2026-01-01T00:00:00Z', '88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f');

INSERT INTO g_super_group_type (super_group_type_name, created_at)
VALUES
    ('committee',     '2026-01-01T00:00:00Z'),
    ('board',         '2026-01-01T00:00:00Z'),
    ('society',       '2026-01-01T00:00:00Z'),
    ('alumni',        '2026-01-01T00:00:00Z'),
    ('functionaries', '2026-01-01T00:00:00Z');

INSERT INTO g_text (text_id, sv, en, created_at)
VALUES
    ('10000000-0000-0000-0000-000000000001', '', '', '2026-01-01T00:00:00Z'),
    ('10000000-0000-0000-0000-000000000002', '', '', '2026-01-01T00:00:00Z'),
    ('10000000-0000-0000-0000-000000000003', '', '', '2026-01-01T00:00:00Z'),
    ('10000000-0000-0000-0000-000000000004', '', '', '2026-01-01T00:00:00Z'),
    ('10000000-0000-0000-0000-000000000005', '', '', '2026-01-01T00:00:00Z'),
    ('10000000-0000-0000-0000-000000000006', '', '', '2026-01-01T00:00:00Z'),
    ('10000000-0000-0000-0000-000000000007', '', '', '2026-01-01T00:00:00Z'),
    ('10000000-0000-0000-0000-000000000008', '', '', '2026-01-01T00:00:00Z'),
    ('10000000-0000-0000-0000-000000000009', '', '', '2026-01-01T00:00:00Z'),
    ('20000000-0000-0000-0000-000000000001', 'Ordförande',        'Chairman',      '2026-01-01T00:00:00Z'),
    ('20000000-0000-0000-0000-000000000002', 'Kassör',            'Treasurer',     '2026-01-01T00:00:00Z'),
    ('20000000-0000-0000-0000-000000000003', 'Ledamot',           'Member',        '2026-01-01T00:00:00Z'),
    ('20000000-0000-0000-0000-000000000004', 'Vice Ordförande',   'Vice-chairman', '2026-01-01T00:00:00Z'),
    ('30000000-0000-0000-0000-000000000001', '', '', '2026-01-01T00:00:00Z'),
    ('30000000-0000-0000-0000-000000000002', '', '', '2026-01-01T00:00:00Z'),
    ('30000000-0000-0000-0000-000000000003', '', '', '2026-01-01T00:00:00Z');

INSERT INTO g_super_group (
    super_group_id, e_name, pretty_name, super_group_type_name,
    created_at, updated_at, description, version
)
VALUES
    ('aed27030-ad90-4526-855c-1e909b1dcecb', 'digit',             'digIT',             'committee',     '2026-01-01T00:00:00Z', '2026-01-01T00:00:00Z', '10000000-0000-0000-0000-000000000001', 0),
    ('2157ee72-04cd-4029-8d57-77142d3ef5fa', 'styrit',            'styrIT',            'board',         '2026-01-01T00:00:00Z', '2026-01-01T00:00:00Z', '10000000-0000-0000-0000-000000000002', 0),
    ('b8dbca3a-52e7-4299-9499-e58ec93a0c2c', 'drawit',            'DrawIT',            'society',       '2026-01-01T00:00:00Z', '2026-01-01T00:00:00Z', '10000000-0000-0000-0000-000000000003', 0),
    ('b3bcbbcc-0b93-4c41-a3c7-1792448c6fc1', 'prit',              'P.R.I.T.',          'committee',     '2026-01-01T00:00:00Z', '2026-01-01T00:00:00Z', '10000000-0000-0000-0000-000000000004', 0),
    ('364a359a-f9eb-4d81-bb99-25cc5adf176d', 'didit',             'didIT',             'alumni',        '2026-01-01T00:00:00Z', '2026-01-01T00:00:00Z', '10000000-0000-0000-0000-000000000005', 0),
    ('30c2ee3b-b761-46d0-9029-215a9b484f7a', 'emeritus',          'EmerITus',          'alumni',        '2026-01-01T00:00:00Z', '2026-01-01T00:00:00Z', '10000000-0000-0000-0000-000000000006', 0),
    ('5a427d4d-adb7-4de7-9c87-a569014c7b58', 'dragit',            'DragIT',            'alumni',        '2026-01-01T00:00:00Z', '2026-01-01T00:00:00Z', '10000000-0000-0000-0000-000000000007', 0),
    ('326807b4-ae68-4626-8382-919a15a8e23c', 'sprit',             'S.P.R.I.T.',        'alumni',        '2026-01-01T00:00:00Z', '2026-01-01T00:00:00Z', '10000000-0000-0000-0000-000000000008', 0),
    ('712e21f5-f3c6-49fc-a9e7-5b7ec3ff31ab', 'kandidatmiddagen',  'Kandidatmiddagen',  'functionaries', '2026-01-01T00:00:00Z', '2026-01-01T00:00:00Z', '10000000-0000-0000-0000-000000000009', 0);

INSERT INTO g_post (post_id, post_name, email_prefix, version, created_at, updated_at, post_order)
VALUES
    ('7bb1db15-730d-4864-bfc3-99abe7c0ccf8', '20000000-0000-0000-0000-000000000001', '', 0, '2026-01-01T00:00:00Z', '2026-01-01T00:00:00Z', 0),
    ('844067b3-e95d-4a28-a586-7388f155b8fb', '20000000-0000-0000-0000-000000000002', '', 0, '2026-01-01T00:00:00Z', '2026-01-01T00:00:00Z', 1),
    ('08efcf3a-1805-4b5f-a60e-da6ce0d33f58', '20000000-0000-0000-0000-000000000003', '', 0, '2026-01-01T00:00:00Z', '2026-01-01T00:00:00Z', 2),
    ('524db9a7-e8be-403e-a07c-a41803ea5ee7', '20000000-0000-0000-0000-000000000004', '', 0, '2026-01-01T00:00:00Z', '2026-01-01T00:00:00Z', 3);

INSERT INTO g_group (group_id, e_name, pretty_name, super_group_id, created_at, updated_at, version)
VALUES
    ('047ac437-a789-4cc5-bb6e-ba50efd7c509', 'digit2025',            'digIT2025',            '364a359a-f9eb-4d81-bb99-25cc5adf176d', '2026-01-01T00:00:00Z', '2026-01-01T00:00:00Z', 0),
    ('2abe2264-fd61-4899-ba46-851279d85229', 'digit2026',            'digIT2026',            'aed27030-ad90-4526-855c-1e909b1dcecb', '2026-01-01T00:00:00Z', '2026-01-01T00:00:00Z', 0),
    ('a2f06d3a-7432-4655-a778-69c9142912f1', 'styrit2025',           'styrIT2025',           '30c2ee3b-b761-46d0-9029-215a9b484f7a', '2026-01-01T00:00:00Z', '2026-01-01T00:00:00Z', 0),
    ('834651d1-34c1-4bac-b148-6546368a8454', 'styrit2026',           'styrIT2026',           '2157ee72-04cd-4029-8d57-77142d3ef5fa', '2026-01-01T00:00:00Z', '2026-01-01T00:00:00Z', 0),
    ('672db849-8afb-4160-9f12-7f8c1d379fcc', 'drawit2025',           'DrawIT2025',           '5a427d4d-adb7-4de7-9c87-a569014c7b58', '2026-01-01T00:00:00Z', '2026-01-01T00:00:00Z', 0),
    ('9b239de9-88a3-4992-96d1-b8dea2a637ec', 'drawit2026',           'DrawIT2026',           'b8dbca3a-52e7-4299-9499-e58ec93a0c2c', '2026-01-01T00:00:00Z', '2026-01-01T00:00:00Z', 0),
    ('5f26a10c-e668-4ec1-b072-a7dd8f11735c', 'prit2025',             'P.R.I.T.2025',         '326807b4-ae68-4626-8382-919a15a8e23c', '2026-01-01T00:00:00Z', '2026-01-01T00:00:00Z', 0),
    ('1ed91274-13c8-4d6d-ab75-37c9d732b51b', 'prit2026',             'P.R.I.T.2026',         'b3bcbbcc-0b93-4c41-a3c7-1792448c6fc1', '2026-01-01T00:00:00Z', '2026-01-01T00:00:00Z', 0),
    ('ee4153d5-830d-445f-acb3-ec09c53e7c0c', 'kandidatmiddagen2026', 'Kandidatmiddagen2026', '712e21f5-f3c6-49fc-a9e7-5b7ec3ff31ab', '2026-01-01T00:00:00Z', '2026-01-01T00:00:00Z', 0);

INSERT INTO g_membership (created_at, user_id, group_id, post_id, unofficial_post_name)
VALUES
    ('2026-01-01T00:00:00Z', '88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f', '2abe2264-fd61-4899-ba46-851279d85229', '7bb1db15-730d-4864-bfc3-99abe7c0ccf8', 'Regional Manager'),
    ('2026-01-01T00:00:00Z', 'bc605869-9a4d-46ec-8a29-d00819d4c195', '047ac437-a789-4cc5-bb6e-ba50efd7c509', '7bb1db15-730d-4864-bfc3-99abe7c0ccf8', 'root'),
    ('2026-01-01T00:00:00Z', 'ec8987d7-4087-461d-bed5-9365086b6e3b', '047ac437-a789-4cc5-bb6e-ba50efd7c509', '844067b3-e95d-4a28-a586-7388f155b8fb', 'cache-chef'),
    ('2026-01-01T00:00:00Z', '0c67c90b-dfdf-473a-98e3-b551e2f2f0f1', '047ac437-a789-4cc5-bb6e-ba50efd7c509', '08efcf3a-1805-4b5f-a60e-da6ce0d33f58', 'dev-ooops'),
    ('2026-01-01T00:00:00Z', '858e5acc-c289-40d3-9422-d6d317f40299', '2abe2264-fd61-4899-ba46-851279d85229', '7bb1db15-730d-4864-bfc3-99abe7c0ccf8', 'root'),
    ('2026-01-01T00:00:00Z', '9ad8946d-cfef-4f6f-8b48-cfb536d0c9eb', '2abe2264-fd61-4899-ba46-851279d85229', '844067b3-e95d-4a28-a586-7388f155b8fb', 'cache-chef'),
    ('2026-01-01T00:00:00Z', '4efb340f-540c-4b15-a362-d402aab10195', '2abe2264-fd61-4899-ba46-851279d85229', '08efcf3a-1805-4b5f-a60e-da6ce0d33f58', 'dev-ooops'),
    ('2026-01-01T00:00:00Z', '9ad8946d-cfef-4f6f-8b48-cfb536d0c9eb', 'a2f06d3a-7432-4655-a778-69c9142912f1', '7bb1db15-730d-4864-bfc3-99abe7c0ccf8', 'Ordf'),
    ('2026-01-01T00:00:00Z', 'bc605869-9a4d-46ec-8a29-d00819d4c195', 'a2f06d3a-7432-4655-a778-69c9142912f1', '844067b3-e95d-4a28-a586-7388f155b8fb', 'Kassör'),
    ('2026-01-01T00:00:00Z', '4542ab3d-7996-4097-ae4a-4fe61eaf2f20', 'a2f06d3a-7432-4655-a778-69c9142912f1', '08efcf3a-1805-4b5f-a60e-da6ce0d33f58', 'IT-ansvarig'),
    ('2026-01-01T00:00:00Z', '0a799f6d-c65a-4d20-8588-2ff5375d6cce', 'a2f06d3a-7432-4655-a778-69c9142912f1', '524db9a7-e8be-403e-a07c-a41803ea5ee7', 'VO'),
    ('2026-01-01T00:00:00Z', '4fcf6566-45d8-4d5d-b7d4-4f6f52bb0ac2', '834651d1-34c1-4bac-b148-6546368a8454', '7bb1db15-730d-4864-bfc3-99abe7c0ccf8', 'Ordf'),
    ('2026-01-01T00:00:00Z', 'bc605869-9a4d-46ec-8a29-d00819d4c195', '834651d1-34c1-4bac-b148-6546368a8454', '844067b3-e95d-4a28-a586-7388f155b8fb', 'Kassör'),
    ('2026-01-01T00:00:00Z', 'ec8987d7-4087-461d-bed5-9365086b6e3b', '834651d1-34c1-4bac-b148-6546368a8454', '08efcf3a-1805-4b5f-a60e-da6ce0d33f58', 'IT-ansvarig'),
    ('2026-01-01T00:00:00Z', '0c67c90b-dfdf-473a-98e3-b551e2f2f0f1', '834651d1-34c1-4bac-b148-6546368a8454', '524db9a7-e8be-403e-a07c-a41803ea5ee7', 'VO'),
    ('2026-01-01T00:00:00Z', '858e5acc-c289-40d3-9422-d6d317f40299', '672db849-8afb-4160-9f12-7f8c1d379fcc', '7bb1db15-730d-4864-bfc3-99abe7c0ccf8', 'Ordf'),
    ('2026-01-01T00:00:00Z', '9ad8946d-cfef-4f6f-8b48-cfb536d0c9eb', '672db849-8afb-4160-9f12-7f8c1d379fcc', '844067b3-e95d-4a28-a586-7388f155b8fb', 'Kassör'),
    ('2026-01-01T00:00:00Z', '4efb340f-540c-4b15-a362-d402aab10195', '672db849-8afb-4160-9f12-7f8c1d379fcc', '08efcf3a-1805-4b5f-a60e-da6ce0d33f58', 'Knapansvarig'),
    ('2026-01-01T00:00:00Z', '4542ab3d-7996-4097-ae4a-4fe61eaf2f20', '9b239de9-88a3-4992-96d1-b8dea2a637ec', '7bb1db15-730d-4864-bfc3-99abe7c0ccf8', 'Ordf'),
    ('2026-01-01T00:00:00Z', '4542ab3d-7996-4097-ae4a-4fe61eaf2f20', '9b239de9-88a3-4992-96d1-b8dea2a637ec', '844067b3-e95d-4a28-a586-7388f155b8fb', 'Kassör'),
    ('2026-01-01T00:00:00Z', '4542ab3d-7996-4097-ae4a-4fe61eaf2f20', '9b239de9-88a3-4992-96d1-b8dea2a637ec', '08efcf3a-1805-4b5f-a60e-da6ce0d33f58', 'Knapansvarig'),
    ('2026-01-01T00:00:00Z', '0a799f6d-c65a-4d20-8588-2ff5375d6cce', '5f26a10c-e668-4ec1-b072-a7dd8f11735c', '7bb1db15-730d-4864-bfc3-99abe7c0ccf8', 'ChefChef'),
    ('2026-01-01T00:00:00Z', 'e6a76e6a-3499-4611-ae28-e1281ffa6e80', '5f26a10c-e668-4ec1-b072-a7dd8f11735c', '844067b3-e95d-4a28-a586-7388f155b8fb', 'Ka$$Chef'),
    ('2026-01-01T00:00:00Z', '4542ab3d-7996-4097-ae4a-4fe61eaf2f20', '5f26a10c-e668-4ec1-b072-a7dd8f11735c', '08efcf3a-1805-4b5f-a60e-da6ce0d33f58', 'BösChef'),
    ('2026-01-01T00:00:00Z', 'bc605869-9a4d-46ec-8a29-d00819d4c195', '1ed91274-13c8-4d6d-ab75-37c9d732b51b', '7bb1db15-730d-4864-bfc3-99abe7c0ccf8', 'ChefChef'),
    ('2026-01-01T00:00:00Z', 'ec8987d7-4087-461d-bed5-9365086b6e3b', '1ed91274-13c8-4d6d-ab75-37c9d732b51b', '844067b3-e95d-4a28-a586-7388f155b8fb', 'Ka$$Chef'),
    ('2026-01-01T00:00:00Z', '0c67c90b-dfdf-473a-98e3-b551e2f2f0f1', '1ed91274-13c8-4d6d-ab75-37c9d732b51b', '08efcf3a-1805-4b5f-a60e-da6ce0d33f58', 'BösChef'),
    ('2026-01-01T00:00:00Z', '858e5acc-c289-40d3-9422-d6d317f40299', 'ee4153d5-830d-445f-acb3-ec09c53e7c0c', '7bb1db15-730d-4864-bfc3-99abe7c0ccf8', NULL),
    ('2026-01-01T00:00:00Z', '9ad8946d-cfef-4f6f-8b48-cfb536d0c9eb', 'ee4153d5-830d-445f-acb3-ec09c53e7c0c', '08efcf3a-1805-4b5f-a60e-da6ce0d33f58', NULL),
    ('2026-01-01T00:00:00Z', '4efb340f-540c-4b15-a362-d402aab10195', 'ee4153d5-830d-445f-acb3-ec09c53e7c0c', '08efcf3a-1805-4b5f-a60e-da6ce0d33f58', NULL),
    ('2026-01-01T00:00:00Z', '4542ab3d-7996-4097-ae4a-4fe61eaf2f20', 'ee4153d5-830d-445f-acb3-ec09c53e7c0c', '08efcf3a-1805-4b5f-a60e-da6ce0d33f58', NULL);

INSERT INTO g_api_key (
    api_key_id, pretty_name, token, key_type, created_at, updated_at, version, description
)
VALUES
    ('11111111-1111-4111-8111-111111111111', 'info-regression',             '{bcrypt}$2y$10$43Xdh/xWiqXWBbJX8L3/H.3Kk0/Usl7uwZxQLKqz9UPYjaKmNNXcu', 'INFO',             '2026-01-01T00:00:00Z', '2026-01-01T00:00:00Z', 0, '30000000-0000-0000-0000-000000000001'),
    ('22222222-2222-4222-8222-222222222222', 'account-scaffold-regression', '{bcrypt}$2y$10$43Xdh/xWiqXWBbJX8L3/H.3Kk0/Usl7uwZxQLKqz9UPYjaKmNNXcu', 'ACCOUNT_SCAFFOLD', '2026-01-01T00:00:00Z', '2026-01-01T00:00:00Z', 0, '30000000-0000-0000-0000-000000000002'),
    ('33333333-3333-4333-8333-333333333333', 'allow-list-regression',       '{bcrypt}$2y$10$43Xdh/xWiqXWBbJX8L3/H.3Kk0/Usl7uwZxQLKqz9UPYjaKmNNXcu', 'ALLOW_LIST',       '2026-01-01T00:00:00Z', '2026-01-01T00:00:00Z', 0, '30000000-0000-0000-0000-000000000003');

INSERT INTO g_api_key_settings (settings_id, created_at, updated_at, version, api_key_id)
VALUES
    ('40000000-0000-4000-8000-000000000001', '2026-01-01T00:00:00Z', '2026-01-01T00:00:00Z', 0, '11111111-1111-4111-8111-111111111111'),
    ('40000000-0000-4000-8000-000000000002', '2026-01-01T00:00:00Z', '2026-01-01T00:00:00Z', 0, '22222222-2222-4222-8222-222222222222');

INSERT INTO g_api_key_to_super_group_type (settings_id, created_at, super_group_type_name)
VALUES ('40000000-0000-4000-8000-000000000002', '2026-01-01T00:00:00Z', 'committee');

INSERT INTO g_api_key_account_scaffold_requires_managed (settings_id, created_at, super_group_type_name)
VALUES ('40000000-0000-4000-8000-000000000002', '2026-01-01T00:00:00Z', 'committee');

COMMIT;
