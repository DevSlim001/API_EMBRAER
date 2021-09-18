
import Axios from 'axios';
const api = Axios.create({
    baseURL:"http://localhost:8080",
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