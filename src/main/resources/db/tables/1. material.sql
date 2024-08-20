CREATE TABLE IF NOT EXISTS material (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL,
    ambient BLOB NOT NULL,
    diffuse BLOB NOT NULL,
    specular BLOB NOT NULL,
    shininess REAL NOT NULL
);