import api from "./../../services/api";

export default async function updateSenha(data){
    let token = localStorage.getItem("token")
    let config = {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    }
    return await api.patch("/usuarios/senha",data,config)
    .then((res)=>{
        return res
    })
    .catch((err)=> {return err.response})
};