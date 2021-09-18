import api from "./../../services/api";

export default async function createUser(usuario){
    let data = {...usuario}
    let token = localStorage.getItem("token")
    let config = {
        headers: {'Authorization': `Bearer ${token}`}
    }
    return await api.post("/usuarios",data,config)
    .then((res)=>{
        return res
    })
    .catch((err)=> {return err.response})
};