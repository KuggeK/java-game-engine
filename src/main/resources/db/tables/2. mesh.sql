CREATE TABLE IF NOT EXISTS mesh (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL,
    positions BLOB NOT NULL,
    texture_coordinates BLOB NOT NULL,
    normals BLOB NOT NULL,
    indices BLOB NOT NULL,
    tangents BLOB
);