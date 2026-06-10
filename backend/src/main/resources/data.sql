INSERT INTO resources (name, role, task, project, story_status, end_date, days_until_free, availability_status)
SELECT * FROM (VALUES
  ('Akshay Khobragade', 'Module Lead', 'TFR-2463- Pan and CUST changes', 'Kanban', 'In Progress', '2026-06-19'::date, 9, 'Busy'),
  ('Binson Selvin', 'Senior Software Engineer', 'TFR-2463- Pan and CUST changes', 'Kanban', 'In Progress', '2026-06-19'::date, 9, 'Busy'),
  ('Arbaz Shaikh', 'Frontend Dev', 'TFR-2486- White label', 'Kanban', 'In Progress', '2026-06-12'::date, 2, 'Almost Free'),
  ('Anis Shah', 'Associate Software Engineer', 'TEPM-17331', 'Kanban', 'In Progress', '2026-06-04'::date, 2, 'Almost Free'),
  ('Sheeba Shaikh', 'Intern', 'TFR-2463- Pan and CUST changes', 'Kanban', 'In Progress', '2026-06-19'::date, 9, 'Busy'),
  ('Mohit Jangale', 'Intern', 'TFR-2486- White label', 'Kanban', 'In Progress', '2026-06-12'::date, 2, 'Almost Free'),
  ('Akshay Pokale', 'Associate Software Engineer', 'TRAN-4152- DB Credit walkthrough', 'Kanban', 'In Progress', '2026-06-16'::date, 6, 'Busy'),
  ('Shailee Baxi', 'Lead QA', 'TFR-2486- White label', 'Kanban', 'In Progress', '2026-06-16'::date, 6, 'Busy'),
  ('Qurratul Aein', 'QA Engineer', 'TFR-998', 'Kanban', 'In Progress', '2026-06-10'::date, 1, 'Almost Free'),
  ('Karishma Wankhade', 'QA Engineer', 'TFR-2913', 'Kanban', 'In Progress', '2026-06-10'::date, 1, 'Almost Free'),
  ('Yogita Dhondge', 'QA Engineer', 'Escrow Automation', 'Kanban', 'In Progress', '2026-06-19'::date, 9, 'Busy')
) AS v(name, role, task, project, story_status, end_date, days_until_free, availability_status)
WHERE NOT EXISTS (SELECT 1 FROM resources LIMIT 1);
