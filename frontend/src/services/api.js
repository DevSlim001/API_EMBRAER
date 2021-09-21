
import Axios from 'axios';
const API_BASE_URL = process.env.API_BASE_URL || 'http://localhost:8080';
const api = Axios.create({
    baseURL:API_BASE_URL,
    transformResponse: [function (data) {
        //verifica se a pessoa pode fazer aquela requisição e volta um erro
        if(data){
            let status = JSON.parse(data).status
            if(status===403){
                data = {errors:[]}
                data.errors.push("Sessão expirada. Faça login novamente.")
            } else{
                data = JSON.parse(data)
            }
        }
        return data;
    }]
})

export default api;