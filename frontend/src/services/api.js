
import Axios from 'axios';
let token = localStorage.getItem("token")
const api = Axios.create({
    baseURL:"http://localhost:8080",
    headers: {'Authorization': `Bearer ${token}`},
    transformResponse: [function (data) {
        //verifica se a pessoa pode fazer aquela requisição e volta um erro
        let status = JSON.parse(data).status
        data = {errors:[]}
        if(status===403){
            data.errors.push("Faça login novamente.")
        }
        return data;
    }]
})

export default api;