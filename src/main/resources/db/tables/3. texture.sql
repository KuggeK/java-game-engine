CREATE TABLE IF NOT EXISTS texture (
    id INTEGER PRIMARY KEY,
    file_name TEXT,
    width INTEGER NOT NULL,
    height INTEGER NOT NULL,
    pixels BLOB NOT NULL
);