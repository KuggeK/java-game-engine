UPDATE 
    mesh 
SET 
    positions = ?, 
    texture_coordinates = ?, 
    normals = ?, 
    indices = ?  
WHERE 
    id = ?;