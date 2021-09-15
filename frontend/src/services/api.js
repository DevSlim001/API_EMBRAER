
import Axios from 'axios';
const host = process.env.HOST

const api = Axios.create({
    baseURL:"http://localhost:8080"
})

export default api;