UPDATE 
    mesh 
SET 
    positions = ?, 
    texture_coordinates = ?, 
    normals = ?, 
    indices = ?,
    tangents = ?
WHERE 
    id = ?;