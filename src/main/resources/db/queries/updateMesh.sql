UPDATE 
    mesh 
SET 
    name = ?,
    positions = ?, 
    texture_coordinates = ?, 
    normals = ?, 
    indices = ?,
    tangents = ?
WHERE 
    id = ?;