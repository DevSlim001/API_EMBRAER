import api from "./../../services/api";

export default async function createUser(usuario){
    let data = {...usuario}
    return await api.post("/usuarios",data)
    .then((res)=>{
        return res
    })
    .catch((err)=> {return err.response})
};