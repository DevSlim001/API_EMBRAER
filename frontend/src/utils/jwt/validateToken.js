import jwt from 'jsonwebtoken'
import dotenv from 'dotenv';

dotenv.config()

const validateToken = (token) => {
    return jwt.verify(token, process.env.JWT_SECRET,(err,decoded) => {
        if(err)
            return false
        else
            return true
    })
}

export default validateToken


